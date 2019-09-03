import sys
from build_utils import write_file, read_file

def generate_commmit_file(file_path, commit_id):
    write_file(file_path = file_path, text = commit_id)
    latest_commmit_from_file = read_file(file_path = file_path)
    print(latest_commmit_from_file)

file_path = sys.argv[1]
commit_id = sys.argv[2]

generate_commmit_file(file_path, commit_id)