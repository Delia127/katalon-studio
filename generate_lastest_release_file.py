import sys
from string import Template
from build_utils import write_file, read_file

def generate_lastest_release_file(file_path, version):
    latest_release_template = Template("""
[
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_32-${version}.zip",
        "file": "win_32",
        "type": "kse"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_64-${version}.zip",
        "file": "win_64",
        "type": "kse"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon%20Studio.dmg",
        "file": "mac_64",
        "type": "kse"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Linux_64-${version}.tar.gz",
        "file": "linux_64",
        "type": "kse"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Windows_32-${version}.zip",
        "file": "win_32",
        "type": "re"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Windows_64-${version}.zip",
        "file": "win_64",
        "type": "re"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_MacOS-${version}.tar.gz",
        "file": "mac_64",
        "type": "re"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Linux_64-${version}.tar.gz",
        "file": "linux_64",
        "type": "re"
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