package com.kms.katalon.composer.components.impl.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.util.GroovyUtil;

public class PackageTreeEntity implements ITreeEntity {

    private static final long serialVersionUID = 2019661366633516087L;

    private static final String PACKAGE_TYPE_NAME = StringConstants.TREE_PACKAGE_TYPE_NAME;

    private static final String DEFAULT_PACKAGE_LABEL = StringConstants.TREE_PACKAGE_DEFAULT_LBL;
    private IPackageFragment packageFragment;
    private String packageName;
    private boolean isDefaultPackage;

    private ITreeEntity parentTreeEntity;

    public PackageTreeEntity(IPackageFragment packageFragment, ITreeEntity parentTreeEntity) {
        this.packageFragment = packageFragment;
        this.packageName = packageFragment.getElementName();
        this.isDefaultPackage = packageName.equals(IPackageFragment.DEFAULT_PACKAGE_NAME);
        this.parentTreeEntity = parentTreeEntity;
    }

    @Override
    public Object[] getChildren() throws Exception {
        List<Object> children = new ArrayList<Object>();
        for (ICompilationUnit childGroovyClass : GroovyUtil.getAllGroovyClasses(packageFragment)) {
            if (childGroovyClass.getResource().exists()) {
                children.add(new KeywordTreeEntity(childGroovyClass, this));
            }
        }

        Collections.sort(children, new Comparator<Object>() {
            @Override
            public int compare(Object arg0, Object arg1) {
                if (arg0 instanceof KeywordTreeEntity && arg1 instanceof KeywordTreeEntity) {
                    KeywordTreeEntity keyword0 = (KeywordTreeEntity) arg0;
                    KeywordTreeEntity keyword1 = (KeywordTreeEntity) arg1;
                    try {
                        return keyword0.getText().compareTo(keyword1.getText());
                    } catch (Exception e) {
                        return 0;
                    }
                }
                return 0;
            }

        });
        return children.toArray();
    }

    @Override
    public ITreeEntity getParent() throws Exception {
        return parentTreeEntity;
    }

    @Override
    public boolean hasChildren() throws Exception {
        return true;
    }

    @Override
    public String getText() throws Exception {
        return packageName.isEmpty() ? DEFAULT_PACKAGE_LABEL : packageName;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_PACKAGE;
    }

    @Override
    public String getTypeName() throws Exception {
        return PACKAGE_TYPE_NAME;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        if (packageFragment == null || packageFragment.isDefaultPackage()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object getObject() throws Exception {
        return packageFragment;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return isRemoveable();
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return FileTransfer.getInstance();
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.KEYWORD.toString();
    }

    @Override
    public boolean equals(Object object) {
        try {
            if (!(object instanceof PackageTreeEntity)) {
                return false;
            }
            PackageTreeEntity anotherPackageTreeEntity = (PackageTreeEntity) object;
            if (!(anotherPackageTreeEntity.getObject() instanceof IPackageFragment)) {
                return false;
            }
            IPackageFragment anotherPackageFragment = (IPackageFragment) anotherPackageTreeEntity.getObject();
            return StringUtils.equalsIgnoreCase(anotherPackageFragment.getPath().toString(), packageFragment.getPath()
                    .toString());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).append(
                packageFragment.getPath().toString() + (isDefaultPackage ? "\\defaultPackage" : "")).toHashCode();
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof IPackageFragment) {
            this.packageFragment = (IPackageFragment) object;
            this.packageName = ((IPackageFragment) object).getElementName();
            this.isDefaultPackage = packageName.equals(IPackageFragment.DEFAULT_PACKAGE_NAME);
        }
    }

    @Override
    public String getKeyWord() throws Exception {
        return KeywordTreeEntity.KEY_WORD;
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return KeywordTreeEntity.SEARCH_TAGS;
    }

    /**
     * key: search tag
     */
    @Override
    public String getPropertyValue(String key) {
        if (key.equals("name")) {
            if (isDefaultPackage) {
                return DEFAULT_PACKAGE_LABEL;
            }
            return packageName;
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return ImageConstants.IMG_16_KEYWORD;
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
    }

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        return Collections.emptyList();
    }
}
