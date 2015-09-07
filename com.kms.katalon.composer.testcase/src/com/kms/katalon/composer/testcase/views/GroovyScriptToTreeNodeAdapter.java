package com.kms.katalon.composer.testcase.views;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;

@SuppressWarnings("deprecation")
public class GroovyScriptToTreeNodeAdapter {

	static Properties overrides = new Properties(){
		private static final long serialVersionUID = 1L;
		{
			put("org.codehaus.groovy.ast.MethodNode", "Function - $expression.name");
		}
	};
	static Properties classNameToStringForm;
	static {
		try {
			URL url =  ClassLoader.getSystemResource("groovy/inspect/swingui/AstBrowserProperties.groovy");
			if (url == null) {
				url = GroovyScriptToTreeNodeAdapter.class.getClassLoader().getResource("groovy/inspect/swingui/AstBrowserProperties.groovy");
			}
	
			ConfigObject config = new ConfigSlurper().parse(url);
			classNameToStringForm = config.toProperties();
			classNameToStringForm.putAll(overrides);
			
			String home = System.getProperty("user.home");
			if (home != null && !home.equals("")) {
				File userFile = new File(home + File.separator + ".groovy/AstBrowserProperties.groovy");
				if (userFile.exists()) {
					ConfigObject customConfig = new ConfigSlurper().parse(userFile.toURL());
					// layer custom string forms onto defaults with putAll, do not replace them
					classNameToStringForm.putAll(customConfig.toProperties());
				}
			}
		}catch(Exception ex) {
			classNameToStringForm = new Properties();
		}
	}
	
	public GroovyScriptToTreeNodeAdapter(){
	}
	
	public TheAstTreeNode make(Object node) {
		TheAstTreeNode treeNode = null;
		if(node instanceof ASTNode){
			String friendlyName = getStringForm((ASTNode)node);
			treeNode = new TheAstTreeNode(null, friendlyName);
			treeNode.ast = (ASTNode)node;
		}
		return treeNode;
	}

	public TheAstTreeNode make(MethodNode node) {
		String friendlyName = getStringForm(node);
		TheAstTreeNode treeNode = new TheAstTreeNode(null, friendlyName);
		treeNode.ast = (ASTNode)node;
		return treeNode;
	}
	
	private String getStringForm(ASTNode node){
		try{
			String templateTextForNode = classNameToStringForm.getProperty(node.getClass().getName());
			if (templateTextForNode != null) {
				GStringTemplateEngine engine = new GStringTemplateEngine();
				Template template = engine.createTemplate(templateTextForNode);
				Map<Object, Object> bindings = new HashMap<>();
				bindings.put("expression", node);
				Writable writable = template.make(bindings);
				StringWriter result = new StringWriter();
				writable.writeTo(result);
				return result.toString();
			} else {
				return node.getClass().getSimpleName();
			}
		}
		catch(Exception ex){
		}
		return null;
	}
		
	void extendMethodNodePropertyTable(List<List<String>> table, final MethodNode node) {
		BytecodeHelper.getMethodDescriptor(node);
		table.add(new ArrayList<String>(){
			private static final long serialVersionUID = 1L;
			{
				add("descriptor");
				add(BytecodeHelper.getMethodDescriptor(node));
				add("String");
			}
		});
	}

}
