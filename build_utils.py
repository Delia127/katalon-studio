import sys
from string import Template

def write_file(file_path, text):
    f = open(file_path, "w", encoding='utf8')
    f.write(text)
    f.close()

def read_file(file_path):
    f = open(file_path, "r", encoding='utf8')
    return f.read()