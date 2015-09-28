package com.kms.katalon.composer.keyword.dialogs;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class RenameKeywordDialog extends AbstractEntityDialog {

    private KeywordTreeEntity keywordTreeEntity;

    private IPackageFragment parentPackage = null;

    public RenameKeywordDialog(Shell parentShell, KeywordTreeEntity keywordTreeEntity) {
        super(parentShell, null);
        setWindowTitle(StringConstants.DIA_TITLE_RENAME);
        setDialogTitle(StringConstants.DIA_TITLE_KEYWORD);
        setDialogMsg(StringConstants.DIA_MSG_RENAME_KEYWORD);
        this.keywordTreeEntity = keywordTreeEntity;
    }

    @Override
    public void validateEntityName(String entityName) throws Exception {
        entityName = entityName + GroovyConstants.GROOVY_FILE_EXTENSION;
        if (parentPackage == null) {
            parentPackage = (IPackageFragment) ((PackageTreeEntity) keywordTreeEntity.getParent()).getObject();
        }

        ICompilationUnit[] cuList = parentPackage.getCompilationUnits();
        for (ICompilationUnit cu : cuList) {
            if (StringUtils.equalsIgnoreCase(entityName, cu.getElementName())) {
                throw new InvalidNameException(
                        com.kms.katalon.composer.components.impl.constants.StringConstants.DIA_NAME_EXISTED);
            }
        }

        GroovyCompilationUnit cu = (GroovyCompilationUnit) parentPackage.getCompilationUnit(entityName);
        IStatus status = JavaConventionsUtil.validateCompilationUnitName(entityName, cu);
        if (status.isOK()) {
            setMessage(getDialogMsg(), getMsgType());
            if (StringUtils.isAllLowerCase(entityName.substring(0, 1))) {
                setMessage(StringConstants.DIA_WARN_KEYWORD_START_WITH_LOWERCASE, IMessageProvider.WARNING);
            }
        } else {
            throw new InvalidNameException(status.getMessage().replaceAll(" Java", "")
                    .replaceAll("(?i)type", StringConstants.KEYWORD));
        }
    }

}
