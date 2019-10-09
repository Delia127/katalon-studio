# This build script assumes the version in manifest.json is already lifted
rsync -rv --exclude=./build . ./build/chrome &&
rsync -rv --exclude=./build . ./build/firefox

wait

# Build Firefox extension
cd build/firefox
javascript-obfuscator ./wait-temp.js --output ./content/wait.js
rm ./wait-temp.js
rm -rf ./build
rm -rf .git
rm -rf .content
# web-ext sign --api-key user:12712544:226 --api-secret 549f8d261cce3f1a713ebd391464907bf5791a3a8dd12f62381df6a6760c7293

# Build Chrome extension
cd ..
rm -rf .git
rm -rf .content


cd chrome
javascript-obfuscator ./wait-temp.js --output ./content/wait.js
rm ./wait-temp.js
rm -rf ./build
rm -rf .git
rm -rf .content