#!/usr/bin/env node

const QRCode = require('qrcode');
const fs = require('fs');
const path = require('path');
const readline = require('readline');

// Configuration
const API_BASE_URL = process.env.API_URL || 'http://localhost:8080';
const FRONTEND_URL = process.env.FRONTEND_URL || 'http://localhost:5173';
const OUTPUT_DIR = './qr-codes';

// Create output directory if it doesn't exist
if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

// Create readline interface for user input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function question(prompt) {
    return new Promise((resolve) => {
        rl.question(prompt, resolve);
    });
}

async function generateAccessToken(description, validityHours = 48) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/access/generate`, {
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
        return null;
    }
}

async function generateQRCode(token, description) {
    try {
        const url = `${FRONTEND_URL}?token=${encodeURIComponent(token)}`;
        const filename = description 
            ? `qr-${description.replace(/[^a-zA-Z0-9]/g, '_')}-${Date.now()}.png`
            : `qr-${token.slice(-8)}-${Date.now()}.png`;
        
        const filepath = path.join(OUTPUT_DIR, filename);

        // Generate QR code with wedding theme colors
        await QRCode.toFile(filepath, url, {
            width: 400,
            margin: 2,
            color: {
                dark: '#5d5550', // boho-brown
                light: '#FFFFFF'
            }
        });

        return { filepath, filename, url };
    } catch (error) {
        console.error('Error generating QR code:', error.message);
        return null;
    }
}

function displayWelcome() {
    console.log('\n' + '='.repeat(60));
    console.log('🌸  Barbara & Mikołaj - QR Code Generator  🌸');
    console.log('           Wedding Photo App Access');
    console.log('='.repeat(60));
    console.log('This tool generates QR codes for wedding guests');
    console.log('to access the photo sharing application.\n');
}

function displayTokenInfo(tokenData, qrData) {
    console.log('\n' + '✅ QR Code Generated Successfully!');
    console.log('-'.repeat(50));
    console.log(`Description: ${tokenData.description || 'No description'}`);
    console.log(`Token: ${tokenData.token}`);
    console.log(`Created: ${new Date(tokenData.createdAt).toLocaleString()}`);
    console.log(`Expires: ${new Date(tokenData.expiresAt).toLocaleString()}`);
    console.log(`QR Code saved: ${qrData.filename}`);
    console.log(`Access URL: ${qrData.url}`);
    console.log('-'.repeat(50));
}

async function generateSingleQR() {
    try {
        const description = await question('Enter description (optional, e.g., "Table 1", "Family"): ');
        const hours = await question('Enter validity period in hours (default: 48): ') || '48';

        console.log('\n📝 Generating access token...');
        const tokenData = await generateAccessToken(description.trim(), hours);
        
        if (!tokenData) {
            console.log('❌ Failed to generate access token');
            return false;
        }

        console.log('🎨 Creating QR code...');
        const qrData = await generateQRCode(tokenData.token, description.trim());
        
        if (!qrData) {
            console.log('❌ Failed to generate QR code');
            return false;
        }

        displayTokenInfo(tokenData, qrData);
        return true;
    } catch (error) {
        console.error('❌ Error:', error.message);
        return false;
    }
}

async function generateBatchQR() {
    try {
        const count = parseInt(await question('How many QR codes to generate: '));
        const baseDescription = await question('Base description (optional, will add numbers): ');
        const hours = await question('Enter validity period in hours (default: 48): ') || '48';

        if (isNaN(count) || count <= 0) {
            console.log('❌ Invalid count. Please enter a positive number.');
            return false;
        }

        console.log(`\n📝 Generating ${count} QR codes...`);
        const results = [];

        for (let i = 1; i <= count; i++) {
            const description = baseDescription.trim() 
                ? `${baseDescription.trim()} #${i}`
                : `Guest Access #${i}`;

            console.log(`  Generating ${i}/${count}: ${description}`);
            
            const tokenData = await generateAccessToken(description, hours);
            if (!tokenData) {
                console.log(`  ❌ Failed to generate token for ${description}`);
                continue;
            }

            const qrData = await generateQRCode(tokenData.token, description);
            if (!qrData) {
                console.log(`  ❌ Failed to generate QR code for ${description}`);
                continue;
            }

            results.push({ tokenData, qrData });
        }

        console.log(`\n✅ Successfully generated ${results.length}/${count} QR codes`);
        
        // Save summary report
        const reportPath = path.join(OUTPUT_DIR, `batch-report-${Date.now()}.txt`);
        const report = results.map((result, index) => {
            return [
                `QR Code #${index + 1}`,
                `Description: ${result.tokenData.description}`,
                `Token: ${result.tokenData.token}`,
                `File: ${result.qrData.filename}`,
                `URL: ${result.qrData.url}`,
                `Expires: ${new Date(result.tokenData.expiresAt).toLocaleString()}`,
                '-'.repeat(50)
            ].join('\n');
        }).join('\n\n');

        fs.writeFileSync(reportPath, `Barbara & Mikołaj Wedding QR Codes\nGenerated: ${new Date().toLocaleString()}\n\n${report}`);
        console.log(`📄 Batch report saved: ${path.basename(reportPath)}`);

        return true;
    } catch (error) {
        console.error('❌ Error:', error.message);
        return false;
    }
}

async function main() {
    displayWelcome();

    try {
        while (true) {
            console.log('\nOptions:');
            console.log('1. Generate single QR code');
            console.log('2. Generate batch QR codes');
            console.log('3. Exit');

            const choice = await question('\nSelect option (1-3): ');

            switch (choice.trim()) {
                case '1':
                    await generateSingleQR();
                    break;
                case '2':
                    await generateBatchQR();
                    break;
                case '3':
                    console.log('\n👋 Goodbye! Happy wedding! 🎉');
                    rl.close();
                    return;
                default:
                    console.log('❌ Invalid option. Please select 1, 2, or 3.');
            }

            const continueChoice = await question('\nGenerate more QR codes? (y/n): ');
            if (continueChoice.toLowerCase() !== 'y') {
                console.log('\n👋 Goodbye! Happy wedding! 🎉');
                break;
            }
        }
    } catch (error) {
        console.error('❌ Unexpected error:', error.message);
    } finally {
        rl.close();
    }
}

// Handle script interruption
process.on('SIGINT', () => {
    console.log('\n\n👋 Goodbye! Happy wedding! 🎉');
    rl.close();
    process.exit(0);
});

// Run the script
if (require.main === module) {
    main();
}