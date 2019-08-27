import sys
from string import Template
import build_utils

def generate_lastest_release_file(file_path, version):
    latest_release_template = Template("""
[
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_32-${version}.zip",
        "file": "win_32"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_64-${version}.zip",
        "file": "win_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon%20Studio.dmg",
        "file": "mac_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Linux_64-${version}.tar.gz",
        "file": "linux_64"
    }
]
""")
    latest_release = latest_release_template.substitute(version = version)
    write_file(file_path = file_path, text = latest_release)
    latest_release_from_file = read_file(file_path = file_path)
    print(latest_release_from_file)

file_path = sys.argv[1]
version = sys.argv[2]

generate_lastest_release_file(file_path, version)