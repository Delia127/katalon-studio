import sys
from string import Template
from build_utils import write_file, read_file

def generate_links_file(file_path, version, tag, is_beta):
    second_arg = version;
    if is_beta:
        release_beta = "release-beta/"
        first_arg = tag
    else:
        release_beta = ""
        first_arg = version
    
    template = Template("""https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon+Studio.app.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon+Studio.dmg
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Linux_64-${second_arg}.tar.gz
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Windows_32-${second_arg}.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Windows_64-${second_arg}.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Engine_MacOS-${second_arg}.tar.gz
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Engine_Linux_64-${second_arg}.tar.gz
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Engine_Windows_32-${second_arg}.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/Katalon_Studio_Engine_Windows_64-${second_arg}.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/apidocs.zip
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/changeLogs.txt
https://s3.amazonaws.com/katalon/${release_beta}${first_arg}/commit.txt""")
    template_string = template.substitute(release_beta = release_beta, first_arg = first_arg, second_arg = second_arg)
  
    write_file(file_path = file_path, text = template_string)
    links_from_file = read_file(file_path = file_path)
    print(links_from_file)

file_path = sys.argv[1]
version = sys.argv[2]
tag = sys.argv[3]
is_beta = sys.argv[4]

generate_links_file(file_path, version, tag, is_beta)