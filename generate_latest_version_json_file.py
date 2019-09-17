import sys
from string import Template
from build_utils import write_file, read_file

def generate_latest_version_json_file(file_path, version):
    latest_version_template = Template("""
{
    "latestVersion": "${version}",
    "newMechanism": true,
    "latestUpdateLocation": "https://katalon.s3.amazonaws.com/update/${version}",
    "releaseNotesLink": "https://docs.katalon.com/katalon-studio/new/index.html",
    "quickRelease": true
}
""")
    latest_version = latest_version_template.substitute(version = version)
    write_file(file_path = file_path, text = latest_version)
    latest_releases_from_file = read_file(file_path = file_path)
    print(latest_releases_from_file)

file_path = sys.argv[1]
version = sys.argv[2]

generate_latest_version_json_file(file_path, version)