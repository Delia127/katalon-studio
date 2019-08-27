import sys
from string import Template
import build_utils

def generate_scan_info_file(file_path, version, build_dir, dest_dir):
    updateInfo = {
        "buildDir": build_dir,
        "destDir": dest_dir,
        "version": version
    }
    update_info_string = json.dumps(updateInfo)
    write_file(file_path = file_path, text = update_info_string)
    update_info_from_file = read_file(file_path = file_path)
    print(update_info_from_file)

file_path = sys.argv[1]
version = sys.argv[2]
build_dir = sys.argv[3]
dest_dir = sys.argv[4]

generate_scan_info_file(file_path, version, build_dir, dest_dir)