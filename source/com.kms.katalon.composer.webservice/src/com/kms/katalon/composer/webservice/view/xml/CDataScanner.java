package com.kms.katalon.composer.webservice.view.xml;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class CDataScanner extends RuleBasedScanner {

    private IToken ESCAPED_CHAR;

    private IToken CDATA;

    public CDataScanner(ColorManager colorManager) {

        ESCAPED_CHAR = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.ESCAPED_CHAR)));

        CDATA = new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.CDATA)));

        IRule[] rules = new IRule[3];

        // Add rule to pick up start of c section
        rules[0] = new CDataRule(CDATA, true);

        // Add a rule to pick up end of CDATA sections
        rules[1] = new CDataRule(CDATA, false);

        // Add rule to pick up escaped chars
        rules[2] = new EscapedCharRule(ESCAPED_CHAR);

        setRules(rules);
    }

    @Override
    public IToken nextToken() {
        return super.nextToken();
    }
}
