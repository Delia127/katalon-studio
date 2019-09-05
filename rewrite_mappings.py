import sys
import re
from build_utils import write_file, read_file

def rewrite_mappings(file_path, commit_id, tag):
    title_version = '{0}-{1}'.format(tag, commit_id)
    version_mapping = read_file(file_path = file_path)
    version_mapping = re.sub('3=.*', '3={0}'.format(title_version), version_mapping)
    write_file(file_path = file_path, text = version_mapping)
    print(read_file(file_path = file_path))

file_path = sys.argv[1]
commit_id = sys.argv[2]
tag = sys.argv[3]

rewrite_mappings(file_path, commit_id, tag)