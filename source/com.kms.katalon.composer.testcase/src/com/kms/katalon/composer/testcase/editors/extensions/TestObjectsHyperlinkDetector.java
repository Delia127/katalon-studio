package com.kms.katalon.composer.testcase.editors.extensions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

public class TestObjectsHyperlinkDetector extends AbstractHyperlinkDetector implements IHyperlinkDetector {
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		 IDocument document = textViewer.getDocument();
		  int offset = region.getOffset();
		  // extract relevant characters
		  IRegion lineRegion;
		  String candidate;
		  try {			  
			  lineRegion = document.getLineInformationOfOffset(offset);
			  candidate = document.get(lineRegion.getOffset(), lineRegion.getLength());
		  } catch (BadLocationException ex) {
			  return null;
		  }
		  // WebUI.click(findTestObject('Object Repository/objects/Page_Demo AUT/label_Gender'))
		  // -> findTestObject('Object Repository/objects/Page_Demo AUT/label_Gender'
		  Pattern findFunctionPattern = Pattern.compile("find([^\']+)\\('([^\']+)'", Pattern.MULTILINE);
		  // look for keyword
		  Matcher findFunctionPatternMatcher = findFunctionPattern.matcher(candidate);
		  ArrayList<IHyperlink> result = new ArrayList<>();

		  
		  while (findFunctionPatternMatcher.find()) {
			  String findFunctiontMatch = findFunctionPatternMatcher.group();
			  Pattern argumentPattern = Pattern.compile("'([^\']+)'");
			  Matcher argumentPatternMatcher = argumentPattern.matcher(findFunctiontMatch);
			  // System.out.println(findFunctiontMatch);
			  int findFunctionIndex = candidate.indexOf(findFunctiontMatch);
			  
			  if(argumentPatternMatcher.find()){
				  String argumentMatch = argumentPatternMatcher.group();
				  int argumentIndex = findFunctiontMatch.indexOf(argumentMatch);
				  // System.out.println(argumentMatch);
				  int regionOffset =  lineRegion.getOffset() + findFunctionIndex + argumentIndex;
				  IRegion targetRegion = new Region(regionOffset, argumentMatch.length());
				  Pattern functionNamePattern = Pattern.compile("find([^\\(]+)");
				  Matcher functionNamePatternMatcher = functionNamePattern.matcher(findFunctiontMatch);
				  if(functionNamePatternMatcher.find()){
					  String functionNameMatch = functionNamePatternMatcher.group();
					  // System.out.println(functionNameMatch);
					  if ((targetRegion.getOffset() <= offset) && ((targetRegion.getOffset() + targetRegion.getLength()) > offset))
						  result.add(new TestObjectsHyperlink(targetRegion, functionNameMatch, argumentMatch));
				  }
			  }
		  }
		  
		  if(result != null && result.size() > 0 ){
			  return result.toArray(new IHyperlink[]{});
		  }  
		  
		return null;
	}
	
}
