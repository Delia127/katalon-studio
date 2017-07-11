package com.kms.katalon.composer.testcase.parts.decoration;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.preferences.StoredKeyword;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;

public class KeywordDecorationService {
    public static DecoratedKeyword getDecoratedKeyword(StoredKeyword storedKeyword) {
        if (storedKeyword.isCustom()) {
            return new DecoratedCustomKeyword(storedKeyword);
        }
        return new DecoratedBuiltinKeyword(storedKeyword);
    }

    public static class DecoratedBuiltinKeyword implements DecoratedKeyword {

        private StoredKeyword storedKeyword;

        public DecoratedBuiltinKeyword(StoredKeyword storedKeyword) {
            this.storedKeyword = storedKeyword;
        }

        @Override
        public Image getImage() {
            switch (getKeywordContributor().getAliasName()) {
                case "WebUI":
                    return ImageConstants.IMG_16_WEB_UI;
                case "Mobile":
                    return ImageConstants.IMG_16_MOBILE;
                case "WS":
                    return ImageConstants.IMG_16_WS;
            }
            return null;
        }

        @Override
        public String getLabel() {
            return TreeEntityUtil.getReadableKeywordName(storedKeyword.getKeywordName());
        }

        @Override
        public String getTooltip() {
            return String.format("%s (%s)", getLabel(), getKeywordContributor().getLabelName());
        }

        private IKeywordContributor getKeywordContributor() {
            return KeywordContributorCollection.getContributor(storedKeyword.getKeywordClass());
        }

        @Override
        public ExpressionStatementWrapper newStep(ASTNodeWrapper parentNode) {
            return AstKeywordsInputUtil.createBuiltInKeywordStatement(
                    getKeywordContributor().getKeywordClass().getSimpleName(),
                    storedKeyword.getKeywordName(), parentNode);
        }
    }

    public static class DecoratedCustomKeyword implements DecoratedKeyword {
        private StoredKeyword storedKeyword;

        public DecoratedCustomKeyword(StoredKeyword storedKeyword) {
            this.storedKeyword = storedKeyword;
        }

        @Override
        public Image getImage() {
            return ImageConstants.IMG_16_CUSTOM;
        }

        @Override
        public String getLabel() {
            return storedKeyword.getKeywordName();
        }

        @Override
        public String getTooltip() {
            return String.format("%s (%s)", getLabel(), "Custom Keyword");
        }

        @Override
        public ExpressionStatementWrapper newStep(ASTNodeWrapper parentNode) {
            return AstKeywordsInputUtil.createNewCustomKeywordStatement(storedKeyword.getKeywordClass(),
                    storedKeyword.getKeywordName(), parentNode);
        }
    }
}
