package com.kms.katalon.composer.keyword.dialogs;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public abstract class CommonAbstractKeywordDialog extends AbstractEntityDialog {

    protected static final String NAME_EXISTED = com.kms.katalon.composer.components.impl.constants.StringConstants.DIA_NAME_EXISTED;

    public CommonAbstractKeywordDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell, parentFolder);
    }

    @Override
    public void updateStatus() {
        super.getButton(OK).setEnabled(isValidEntityName());
    }

    private boolean isValidEntityName() {
        try {
            validateEntityName(getName());
            setErrorMessage(null);
            return true;
        } catch (InvalidNameException e) {
            setErrorMessage(e.getMessage());
            return false;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Override
    public abstract void validateEntityName(String entityName) throws Exception;

    protected void validatePackageName(String name, IPackageFragment pkg) throws Exception {
        IStatus status = JavaConventionsUtil.validatePackageName(name, pkg);
        if (status.isOK()) {
            validateUnsupportedCharacters(name, StringConstants.DIA_MSG_INVALID_PACKAGE_NAME);
            return;
        }

        String errorMsg = status.getMessage();
        if (StringUtils.endsWith(errorMsg, StringConstants.DIA_MSG_INVALID_JAVA_IDENTIFIER)) {
            throw new InvalidNameException(StringConstants.DIA_MSG_INVALID_PACKAGE_NAME);
        }

        throw new InvalidNameException(errorMsg);
    }

    protected void validateKeywordName(String name, IPackageFragment parentPackage) throws Exception {
        name = name + GroovyConstants.GROOVY_FILE_EXTENSION;

        if (parentPackage.exists()) {
            for (ICompilationUnit cu : parentPackage.getCompilationUnits()) {
                if (StringUtils.equalsIgnoreCase(name, cu.getElementName())) {
                    throw new InvalidNameException(NAME_EXISTED);
                }
            }
        }

        GroovyCompilationUnit cu = (GroovyCompilationUnit) parentPackage.getCompilationUnit(name);
        IStatus status = JavaConventionsUtil.validateCompilationUnitName(name, cu);
        if (status.isOK()) {
            validateUnsupportedCharacters(name, StringConstants.DIA_MSG_INVALID_KEYWORD_NAME);
            if (StringUtils.isAllLowerCase(name.substring(0, 1))) {
                setMessage(StringConstants.DIA_WARN_KEYWORD_START_WITH_LOWERCASE, IMessageProvider.WARNING);
            }
            return;
        }

        String errorMsg = status.getMessage();
        if (StringUtils.endsWith(errorMsg, StringConstants.DIA_MSG_INVALID_JAVA_IDENTIFIER)) {
            throw new InvalidNameException(StringConstants.DIA_MSG_INVALID_KEYWORD_NAME);
        }
        throw new InvalidNameException(errorMsg.replaceAll(" Java", "").replaceAll("(?i)type", StringConstants.KEYWORD));
    }

    private void validateUnsupportedCharacters(String entityName, String customMessage) throws Exception {
        try {
            // Checking with Katalon naming convention
            EntityNameController.getInstance().validateName(entityName);
        } catch (InvalidNameException e) {
            // only care about invalid characters check
            // if the message is
            // com.kms.katalon.dal.fileservice.constants.StringConstants.FS_INVALID_FILE_NAME_BY_SPECIAL_CHAR
            customMessage = customMessage != null ? customMessage : e.getMessage();
            if (StringUtils.startsWith(e.getMessage(), "Invalid name! A file name")) {
                throw new InvalidNameException(customMessage);
            }
        }
    }

}
