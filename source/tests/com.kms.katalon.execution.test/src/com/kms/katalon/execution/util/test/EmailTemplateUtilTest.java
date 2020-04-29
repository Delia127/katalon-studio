package com.kms.katalon.execution.util.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.kms.katalon.execution.util.EmailTemplateUtil;

public class EmailTemplateUtilTest {

	@Test
	public void getTinyMCETemplateTest() throws Exception{
		String template = EmailTemplateUtil.getTinyMCETemplate();
		
		assertTrue(!template.isEmpty());
		assertEquals(template.contains("html"), true);
	}
	
	@Test
	public void getHTMLTemplateForTestSuiteKatalonSignatureTest() throws Exception{
		String template = EmailTemplateUtil.getHTMLTemplateForTestSuite();
		
		assertTrue(!template.isEmpty());
		assertEquals(template.contains("Katalon Studio"), true);
	}
	
	@Test
	public void getHTMLTemplateForTestSuiteCustomSignatureTest() throws Exception{
		String template = EmailTemplateUtil.getHTMLTemplateForTestSuite("Tester #1");
		
		assertTrue(!template.isEmpty());
		assertEquals(template.contains("Tester #1"), true);
	}
	
	@Test
	public void getEmailHTMLTemplateForTestSuiteCollectionTest() throws Exception{
		String template = EmailTemplateUtil.getEmailHTMLTemplateForTestSuiteCollection();
		
		assertTrue(!template.isEmpty());
	}
	
	@Test
	public void getTemplateFolderTest() throws Exception{
		File folder = EmailTemplateUtil.getTemplateFolder();
		
		assertEquals(folder.getPath().contains("template"), true);
	}
}
