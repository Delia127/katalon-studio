import sys
from string import Template
from build_utils import write_file, read_file

def generate_release_json_file(file_path, version):
    releases_template = Template(
"""
{
    "os": "macOS (app)",
    "version": "${version}",
    "filename": "Katalon.Studio.app.zip",
    "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon.Studio.app.zip"
},
{
    "os": "macOS (dmg)",
    "version": "${version}",
    "filename": "Katalon.Studio.dmg",
    "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon.Studio.dmg"
},
{
    "os": "Linux",
    "version": "${version}",
    "filename": "Katalon_Studio_Linux_64-${version}.tar.gz",
    "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Linux_64-${version}.tar.gz"
},
{
    "os": "Windows 32",
    "version": "${version}",
    "filename": "Katalon_Studio_Windows_32-${version}.zip",
    "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Windows_32-${version}.zip"
},
{
    "os": "Windows 64",
    "version": "${version}",
    "filename": "Katalon_Studio_Windows_64-${version}.zip",
    "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Windows_64-${version}.zip"
},
""")
    releases = releases_template.substitute(version = version)
    write_file(file_path = file_path, text = releases)
    releases_from_file = read_file(file_path = file_path)
    print(releases_from_file)

file_path = sys.argv[1]
version = sys.argv[2]

generate_release_json_file(file_path, version)