const ghRelease = require('gh-release');

const username = process.argv[2];
const password = process.argv[3];
const name = process.argv[4];
const argsLength = process.argv.length;

const files = [];
for (let i = 5; i < argsLength; i++) {
  const file = process.argv[i];
  files.push(file);
}

const options = {
  tag_name: name,
  target_commitish: 'master',
  name: name,
  body: name,
  assets: files,
  draft: true,
  prerelease: true,
  repo: 'katalon-studio',
  owner: 'katalon-studio',
  endpoint: 'https://api.github.com'
}

console.log(options);

options.auth = {
  username: username,
  password: password
}

ghRelease(options, function (err, result) {
  if (err) throw err
  console.log(result);
})