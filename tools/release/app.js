const ghRelease = require('publish-release');

const token = process.argv[2];
const name = process.argv[3];
const argsLength = process.argv.length;

const files = [];
for (let i = 4; i < argsLength; i++) {
  const file = process.argv[i];
  files.push(file);
}

const options = {
  owner: 'katalon-studio',
  repo: 'katalon-studio',
  tag: name,
  name: name,
  notes: '',
  draft: true,
  prerelease: true,
  reuseRelease: true,
  skipIfPublished: true,
  target_commitish: 'master',
  assets: files
}

console.log(options);

options.token = token;

ghRelease(options, function (err, result) {
  if (err) throw err
  console.log(result);
})