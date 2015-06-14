# -*- coding: utf-8 -*-

import sys
import xml.etree.ElementTree as ET
import subprocess

def print_command(command):
	for str in command:
		print(str, end=" ")
	print()

def run_prepare_ectec(config, jar_name):
	mem_str = "-Xmx" + config["xmx"]
	db_path = config["database"]
	threads = config["threads"]
	csv_file = config["csv"]
	
	command = []
	
	if jar_name == "2-repositoryregisterer.jar":
		command = ["java", "-jar", mem_str, jar_name, "-d", db_path, "-th", threads, "-i", csv_file]
	elif jar_name == "10-clonegenealogydetector.jar":
		command = ["java", "-jar", mem_str, jar_name, "-d", db_path, "-th", threads, "-gm", "c"]
	elif jar_name == "11-fragmentgenealogydetector.jar":
		command = ["java", "-jar", mem_str, jar_name, "-d", db_path, "-th", threads, "-gm", "f"]
	else:
		command = ["java", "-jar", mem_str, jar_name, "-d", db_path, "-th", threads]
	
	print_command(command)
	subprocess.call(command)

def run_prepare_csv(config, config_file):
	mem_str = "-Xmx" + config["xmx"]
	csv_jar = config["csv-prepare"]
	csv_file = config["csv"]
	
	command = ["java", "-jar", mem_str, csv_jar, config_file, csv_file]
	print_command(command)
	subprocess.call(command)

def parse_xml(path):
	config = dict()
	tree = ET.parse(path)
	root = tree.getroot()
	
	for child in root:
		if child.tag == "general" or child.tag == "servlet" or child.tag == "jar":
			for grand_child in child:
				config[grand_child.tag] = grand_child.text
	
	return config

if __name__ == "__main__":
	param = sys.argv
	config_file = param[1]
	
	config = parse_xml(config_file)
	print(config)
	
	run_prepare_csv(config, config_file)
	
	run_prepare_ectec(config, "1-dbmaker.jar")
	run_prepare_ectec(config, "2-repositoryregisterer.jar")
	run_prepare_ectec(config, "3-revisiondetector.jar")
	run_prepare_ectec(config, "4-combiner.jar")
	run_prepare_ectec(config, "5-filedetector.jar")
	run_prepare_ectec(config, "6-fragmentdetector.jar")
	run_prepare_ectec(config, "7-clonedetector.jar")
	run_prepare_ectec(config, "8-fragmentlinker.jar")
	run_prepare_ectec(config, "9-clonelinker.jar")
	run_prepare_ectec(config, "10-clonegenealogydetector.jar")
	run_prepare_ectec(config, "11-fragmentgenealogydetector.jar")
