package com.kms.katalon.custom.generation

import groovy.text.GStringTemplateEngine
import java.io.File;

import com.kms.katalon.entity.global.GlobalVariableEntity;

class GlobalVariableTemplate {
	def static tpl = '''
class GlobalVariable {
	<% globalVariables.each { %> 
	def static <%= it.getName() %> = <%= it.getInitValue() %>
	<% } %> 
}
'''
	def generateGlobalVarialbeFile(File file, List<GlobalVariableEntity> globalVariables) {
		
		def binding = [
			"globalVariables" : globalVariables
		]
		
		def engine = new GStringTemplateEngine()
		def tpl = engine.createTemplate(tpl).make(binding)
		if (file.canWrite()) {
			file.write(tpl.toString());
		}
	}
}
