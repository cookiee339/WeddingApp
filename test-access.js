#!/usr/bin/env node

const API_BASE_URL = process.env.API_URL || 'http://localhost:8080';

async function testAccessControl() {
    console.log('🧪 Testing Wedding App Access Control System');
    console.log('='.repeat(50));
    console.log(`API Base URL: ${API_BASE_URL}\n`);

    let allTestsPassed = true;

    // Test 1: Generate Access Token
    console.log('Test 1: Generate Access Token');
    try {
        const response = await fetch(`${API_BASE_URL}/api/access/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                description: 'Test Token',
                validityHours: 48
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const tokenData = await response.json();
        console.log('✅ Token generated successfully');
        console.log(`   Token: ${tokenData.token}`);
        console.log(`   Description: ${tokenData.description}`);
        console.log(`   Expires: ${tokenData.expiresAt}\n`);

        // Test 2: Validate Valid Token
        console.log('Test 2: Validate Valid Token');
        const validateResponse = await fetch(`${API_BASE_URL}/api/access/validate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                token: tokenData.token
            })
        });

        if (!validateResponse.ok) {
            throw new Error(`HTTP ${validateResponse.status}: ${validateResponse.statusText}`);
        }

        const validateResult = await validateResponse.json();
        if (validateResult.valid === true) {
            console.log('✅ Valid token correctly validated\n');
        } else {
            console.log('❌ Valid token incorrectly rejected\n');
            allTestsPassed = false;
        }

        // Test 3: Validate Invalid Token
        console.log('Test 3: Validate Invalid Token');
        const invalidResponse = await fetch(`${API_BASE_URL}/api/access/validate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                token: 'invalid-token-12345'
            })
        });

        if (!invalidResponse.ok) {
            throw new Error(`HTTP ${invalidResponse.status}: ${invalidResponse.statusText}`);
        }

        const invalidResult = await invalidResponse.json();
        if (invalidResult.valid === false) {
            console.log('✅ Invalid token correctly rejected\n');
        } else {
            console.log('❌ Invalid token incorrectly accepted\n');
            allTestsPassed = false;
        }

        // Test 4: Get All Tokens
        console.log('Test 4: Get All Generated Tokens');
        const allTokensResponse = await fetch(`${API_BASE_URL}/api/access/codes`);

        if (!allTokensResponse.ok) {
            throw new Error(`HTTP ${allTokensResponse.status}: ${allTokensResponse.statusText}`);
        }

        const allTokens = await allTokensResponse.json();
        console.log(`✅ Retrieved ${allTokens.length} token(s)`);
        console.log(`   Latest token: ${allTokens[0]?.description || 'No description'}\n`);

        // Test 5: Frontend URL Generation
        console.log('Test 5: Frontend URL Generation');
        const frontendUrl = `http://localhost:5173?token=${encodeURIComponent(tokenData.token)}`;
        console.log('✅ Frontend URL generated:');
        console.log(`   ${frontendUrl}\n`);

        // Test 6: Token Cleanup (Optional)
        console.log('Test 6: Cleanup Expired Tokens');
        const cleanupResponse = await fetch(`${API_BASE_URL}/api/access/cleanup`, {
            method: 'POST'
        });

        if (!cleanupResponse.ok) {
            throw new Error(`HTTP ${cleanupResponse.status}: ${cleanupResponse.statusText}`);
        }

        const cleanupResult = await cleanupResponse.json();
        console.log(`✅ Cleanup completed: ${cleanupResult.message}\n`);

    } catch (error) {
        console.log(`❌ Test failed: ${error.message}\n`);
        allTestsPassed = false;
    }

    // Summary
    console.log('='.repeat(50));
    if (allTestsPassed) {
        console.log('🎉 All tests passed! Access control system is working correctly.');
        console.log('\nNext steps:');
        console.log('1. Start your frontend: cd frontend && npm run dev');
        console.log('2. Generate QR codes: npm run generate');
        console.log('3. Test the complete flow by accessing the generated URL');
    } else {
        console.log('❌ Some tests failed. Please check your backend configuration.');
        console.log('\nTroubleshooting:');
        console.log('1. Ensure backend is running: cd backend && ./gradlew run');
        console.log('2. Check database connection');
        console.log('3. Verify API endpoints are properly configured');
    }
    console.log('='.repeat(50));
}

// Handle errors gracefully
process.on('unhandledRejection', (error) => {
    console.error('❌ Unhandled error:', error.message);
    process.exit(1);
});

// Run tests
if (require.main === module) {
    testAccessControl().catch(error => {
        console.error('❌ Test suite failed:', error.message);
        process.exit(1);
    });
}

module.exports = { testAccessControl };