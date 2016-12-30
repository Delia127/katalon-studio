package com.kms.katalon.composer.webservice.view.xml;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class XMLPartitionScanner extends RuleBasedPartitionScanner {

    public final static String XML_DEFAULT = "__xml_default";

    public final static String XML_COMMENT = "__xml_comment";

    public final static String XML_PI = "__xml_pi";

    public final static String XML_CDATA = "__xml_cdata";

    public final static String XML_START_TAG = "__xml_start_tag";

    public final static String XML_END_TAG = "__xml_end_tag";

    public final static String XML_TEXT = "__xml_text";

    public XMLPartitionScanner() {
        IToken xmlComment = new Token(XML_COMMENT);
        IToken xmlPI = new Token(XML_PI);
        IToken startTag = new Token(XML_START_TAG);
        IToken endTag = new Token(XML_END_TAG);
        IToken text = new Token(XML_TEXT);

        IPredicateRule[] rules = new IPredicateRule[6];
        rules[0] = new NonMatchingRule();
        rules[1] = new MultiLineRule("<!--", "-->", xmlComment);
        rules[2] = new MultiLineRule("<?", "?>", xmlPI);
        rules[3] = new MultiLineRule("</", ">", endTag);
        rules[4] = new XMLStartTagRule(startTag);
        rules[5] = new XMLTextPredicateRule(text);

        setPredicateRules(rules);
    }
}
