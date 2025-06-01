const express = require('express');
const QRCode = require('qrcode');
const fs = require('fs').promises;
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;
const API_BASE_URL = process.env.API_URL || 'http://backend:8080';
const FRONTEND_URL = process.env.FRONTEND_URL || 'http://localhost';
const OUTPUT_DIR = '/app/qr-codes';

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));
app.use('/qr-codes', express.static(OUTPUT_DIR));

// Ensure output directory exists
async function ensureOutputDir() {
    try {
        await fs.access(OUTPUT_DIR);
    } catch {
        await fs.mkdir(OUTPUT_DIR, { recursive: true });
    }
}

// Generate access token from backend
async function generateAccessToken(description, validityHours = 48) {
    try {
        const response = await fetch(`${API_BASE_URL}/access/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                description: description || null,
                validityHours: parseInt(validityHours)
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error generating access token:', error.message);
        throw error;
    }
}

// Generate QR code image
async function generateQRCodeImage(token, description) {
    try {
        const url = `${FRONTEND_URL}?token=${encodeURIComponent(token)}`;
        const filename = description 
            ? `qr-${description.replace(/[^a-zA-Z0-9]/g, '_')}-${Date.now()}.png`
            : `qr-${token.slice(-8)}-${Date.now()}.png`;
        
        const filepath = path.join(OUTPUT_DIR, filename);

        await QRCode.toFile(filepath, url, {
            width: 400,
            margin: 2,
            color: {
                dark: '#5d5550',
                light: '#FFFFFF'
            }
        });

        return { filepath, filename, url };
    } catch (error) {
        console.error('Error generating QR code:', error.message);
        throw error;
    }
}

// API Routes

// Health check
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'healthy', service: 'qr-generator', timestamp: new Date().toISOString() });
});

// Generate single QR code
app.post('/api/generate', async (req, res) => {
    try {
        const { description, validityHours = 48 } = req.body;

        console.log(`Generating QR code: ${description || 'No description'}`);
        
        const tokenData = await generateAccessToken(description, validityHours);
        const qrData = await generateQRCodeImage(tokenData.token, description);
        
        res.json({
            success: true,
            token: tokenData,
            qr: {
                filename: qrData.filename,
                url: qrData.url,
                downloadUrl: `/qr-codes/${qrData.filename}`
            }
        });
    } catch (error) {
        console.error('Generate QR error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// Generate batch QR codes
app.post('/api/generate-batch', async (req, res) => {
    try {
        const { count, baseDescription, validityHours = 48 } = req.body;
        
        if (!count || count <= 0 || count > 100) {
            return res.status(400).json({
                success: false,
                error: 'Count must be between 1 and 100'
            });
        }

        console.log(`Generating ${count} QR codes...`);
        
        const results = [];
        const errors = [];

        for (let i = 1; i <= count; i++) {
            const description = baseDescription 
                ? `${baseDescription} #${i}`
                : `Guest Access #${i}`;

            try {
                const tokenData = await generateAccessToken(description, validityHours);
                const qrData = await generateQRCodeImage(tokenData.token, description);
                
                results.push({
                    index: i,
                    token: tokenData,
                    qr: {
                        filename: qrData.filename,
                        url: qrData.url,
                        downloadUrl: `/qr-codes/${qrData.filename}`
                    }
                });
            } catch (error) {
                errors.push({ index: i, description, error: error.message });
            }
        }

        // Generate batch report
        const reportPath = path.join(OUTPUT_DIR, `batch-report-${Date.now()}.txt`);
        const report = results.map((result, index) => {
            return [
                `QR Code #${result.index}`,
                `Description: ${result.token.description}`,
                `Token: ${result.token.token}`,
                `File: ${result.qr.filename}`,
                `URL: ${result.qr.url}`,
                `Expires: ${new Date(result.token.expiresAt).toLocaleString()}`,
                '-'.repeat(50)
            ].join('\n');
        }).join('\n\n');

        await fs.writeFile(reportPath, `Barbara & Mikołaj Wedding QR Codes\nGenerated: ${new Date().toLocaleString()}\n\n${report}`);

        res.json({
            success: true,
            generated: results.length,
            total: count,
            errors: errors.length,
            results,
            errors,
            reportFile: path.basename(reportPath)
        });
    } catch (error) {
        console.error('Batch generate error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// List generated QR codes
app.get('/api/qr-codes', async (req, res) => {
    try {
        const files = await fs.readdir(OUTPUT_DIR);
        const qrFiles = files.filter(file => file.startsWith('qr-') && file.endsWith('.png'));
        
        const fileDetails = await Promise.all(
            qrFiles.map(async (filename) => {
                try {
                    const filepath = path.join(OUTPUT_DIR, filename);
                    const stats = await fs.stat(filepath);
                    return {
                        filename,
                        downloadUrl: `/qr-codes/${filename}`,
                        size: stats.size,
                        created: stats.birthtime,
                        modified: stats.mtime
                    };
                } catch (error) {
                    return null;
                }
            })
        );

        res.json({
            success: true,
            count: fileDetails.filter(Boolean).length,
            files: fileDetails.filter(Boolean).sort((a, b) => b.created - a.created)
        });
    } catch (error) {
        console.error('List QR codes error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// Get backend tokens
app.get('/api/tokens', async (req, res) => {
    try {
        const response = await fetch(`${API_BASE_URL}/access/codes`);
        if (!response.ok) {
            throw new Error(`Backend API error: ${response.status}`);
        }
        const tokens = await response.json();
        res.json({ success: true, tokens });
    } catch (error) {
        console.error('Get tokens error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// Delete QR code file
app.delete('/api/qr-codes/:filename', async (req, res) => {
    try {
        const { filename } = req.params;
        
        if (!filename.startsWith('qr-') || !filename.endsWith('.png')) {
            return res.status(400).json({
                success: false,
                error: 'Invalid filename'
            });
        }

        const filepath = path.join(OUTPUT_DIR, filename);
        await fs.unlink(filepath);
        
        res.json({
            success: true,
            message: `File ${filename} deleted`
        });
    } catch (error) {
        console.error('Delete QR code error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// Serve simple web interface
app.get('/', (req, res) => {
    res.send(`
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Wedding QR Generator</title>
            <style>
                body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
                .header { text-align: center; color: #5d5550; margin-bottom: 30px; }
                .form-group { margin-bottom: 15px; }
                label { display: block; margin-bottom: 5px; font-weight: bold; }
                input, select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
                button { background: #d4906c; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
                button:hover { background: #c17d56; }
                .result { margin-top: 20px; padding: 15px; background: #f9f9f9; border-radius: 4px; }
                .qr-list { margin-top: 30px; }
                .qr-item { padding: 10px; border: 1px solid #ddd; margin: 5px 0; border-radius: 4px; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>🌸 Barbara & Mikołaj 🌸</h1>
                <h2>Wedding QR Code Generator</h2>
            </div>
            
            <form id="generateForm">
                <div class="form-group">
                    <label for="description">Description:</label>
                    <input type="text" id="description" placeholder="e.g., Table 5, Family Smith...">
                </div>
                <div class="form-group">
                    <label for="validityHours">Validity (hours):</label>
                    <select id="validityHours">
                        <option value="24">24 hours</option>
                        <option value="48" selected>48 hours</option>
                        <option value="72">72 hours</option>
                        <option value="168">1 week</option>
                    </select>
                </div>
                <button type="submit">Generate QR Code</button>
            </form>
            
            <div id="result" class="result" style="display: none;"></div>
            
            <div class="qr-list">
                <h3>Generated QR Codes</h3>
                <div id="qrList">Loading...</div>
            </div>

            <script>
                document.getElementById('generateForm').addEventListener('submit', async (e) => {
                    e.preventDefault();
                    
                    const description = document.getElementById('description').value;
                    const validityHours = document.getElementById('validityHours').value;
                    
                    try {
                        const response = await fetch('/api/generate', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ description, validityHours: parseInt(validityHours) })
                        });
                        
                        const result = await response.json();
                        
                        if (result.success) {
                            document.getElementById('result').innerHTML = \`
                                <h3>✅ QR Code Generated!</h3>
                                <p><strong>Description:</strong> \${result.token.description || 'No description'}</p>
                                <p><strong>Token:</strong> \${result.token.token}</p>
                                <p><strong>Expires:</strong> \${new Date(result.token.expiresAt).toLocaleString()}</p>
                                <p><strong>File:</strong> <a href="\${result.qr.downloadUrl}" target="_blank">\${result.qr.filename}</a></p>
                                <p><strong>Access URL:</strong> <code>\${result.qr.url}</code></p>
                            \`;
                            document.getElementById('result').style.display = 'block';
                            loadQRList();
                        } else {
                            document.getElementById('result').innerHTML = \`<h3>❌ Error:</h3><p>\${result.error}</p>\`;
                            document.getElementById('result').style.display = 'block';
                        }
                    } catch (error) {
                        document.getElementById('result').innerHTML = \`<h3>❌ Error:</h3><p>\${error.message}</p>\`;
                        document.getElementById('result').style.display = 'block';
                    }
                });
                
                async function loadQRList() {
                    try {
                        const response = await fetch('/api/qr-codes');
                        const result = await response.json();
                        
                        if (result.success) {
                            const listHtml = result.files.map(file => \`
                                <div class="qr-item">
                                    <strong>\${file.filename}</strong>
                                    <br>Created: \${new Date(file.created).toLocaleString()}
                                    <br><a href="\${file.downloadUrl}" target="_blank">Download</a>
                                </div>
                            \`).join('');
                            
                            document.getElementById('qrList').innerHTML = listHtml || '<p>No QR codes generated yet.</p>';
                        }
                    } catch (error) {
                        document.getElementById('qrList').innerHTML = '<p>Error loading QR codes.</p>';
                    }
                }
                
                // Load QR list on page load
                loadQRList();
            </script>
        </body>
        </html>
    `);
});

// Error handling middleware
app.use((error, req, res, next) => {
    console.error('Unhandled error:', error);
    res.status(500).json({
        success: false,
        error: 'Internal server error'
    });
});

// Start server
async function startServer() {
    try {
        await ensureOutputDir();
        
        app.listen(PORT, () => {
            console.log(`🌸 Wedding QR Generator Service running on port ${PORT}`);
            console.log(`Web interface: http://localhost:${PORT}`);
            console.log(`API Base URL: ${API_BASE_URL}`);
            console.log(`Frontend URL: ${FRONTEND_URL}`);
            console.log(`Output Directory: ${OUTPUT_DIR}`);
        });
    } catch (error) {
        console.error('Failed to start server:', error);
        process.exit(1);
    }
}

startServer();
