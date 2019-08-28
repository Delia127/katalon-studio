import sys
from build_utils import write_file, read_file

file_path = sys.argv[1]
commit_id = sys.argv[2]
tag = sys.argv[3]

title_version = "{0}-{1}".format(tag, commit_id)
version_mapping = read_file(file_path = file_path)
version_mapping = version_mapping.replaceAll(/3=.*/, "3={0}".format(title_version))
write_file(file_path = file_path, text = version_mapping)