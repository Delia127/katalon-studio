package com.kms.katalon.composer.components.impl.tree;

import static com.kms.katalon.entity.repository.WebServiceRequestEntity.DELETE_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.GET_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.POST_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.PUT_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.PATCH_METHOD;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.SOAP;
import static com.kms.katalon.entity.repository.WebServiceRequestEntity.SOAP12;
import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.webui.constants.HTMLTags;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebElementTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -736426078298872979L;

    private static final String OBJECT_TYPE_NAME = StringConstants.TREE_OBJECT_TYPE_NAME;

    public static final String KEY_WORD = StringConstants.TREE_OBJECT_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "folder" };

    private WebElementEntity webElement;

    public WebElementTreeEntity(WebElementEntity webElement, ITreeEntity parentTreeEntity) {
        super(webElement, parentTreeEntity);
        this.webElement = webElement;
    }

    @Override
    public Object getObject() throws Exception {
        ObjectRepositoryController.getInstance().reloadTestObject(webElement, entity);
        loadAllDescentdantEntities();
        return webElement;
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public boolean hasChildren() throws Exception {
        return false;
    }

    @Override
    public Image getImage() throws Exception {

        if (webElement instanceof WebServiceRequestEntity) {
            WebServiceRequestEntity wsEntity = (WebServiceRequestEntity) webElement;
            if (wsEntity.getServiceType().equals(WebServiceRequestEntity.SERVICE_TYPES[1])) {
                switch (((WebServiceRequestEntity) webElement).getRestRequestMethod()) {
                    case GET_METHOD:
                        return ImageConstants.IMG_16_WS_GET_METHOD;
                    case POST_METHOD:
                        return ImageConstants.IMG_16_WS_POST_METHOD;
                    case PUT_METHOD:
                        return ImageConstants.IMG_16_WS_PUT_METHOD;
                    case DELETE_METHOD:
                        return ImageConstants.IMG_16_WS_DELETE_METHOD;
                    case PATCH_METHOD:
                        return ImageConstants.IMG_16_WS_PATCH_METHOD;
                    default:
                        return ImageConstants.IMG_16_WS_GET_METHOD;
                }
            }
            // SOAP
            if (wsEntity.getServiceType().equals(WebServiceRequestEntity.SERVICE_TYPES[0])) {
                switch (((WebServiceRequestEntity) webElement).getSoapRequestMethod()) {
                    case GET_METHOD:
                        return ImageConstants.IMG_16_WS_SOAP_GET_METHOD;
                    case POST_METHOD:
                        return ImageConstants.IMG_16_WS_SOAP_POST_METHOD;
                    case SOAP:
                        return ImageConstants.IMG_16_WS_SOAP_METHOD;
                    case SOAP12:
                        return ImageConstants.IMG_16_WS_SOAP12_METHOD;
                    default:
                        return ImageConstants.IMG_16_WS_GET_METHOD;
                }
            } else {
                return ImageConstants.IMG_16_WS_TEST_OBJECT;
            }
        }
        WebElementEntity webElement = (WebElementEntity) getObject();
        switch (HTMLTags.getElementType(webElement.getPropertyValue(WebElementPropertyEntity.TAG_PROPERTY),
                webElement.getPropertyValue(WebElementPropertyEntity.TYPE_PROPERTY))) {
            case HTMLTags.TAG_A:
                return ImageConstants.IMG_16_LNK_TEST_OBJECT;
            case HTMLTags.TAG_RESET:
            case HTMLTags.TAG_SUBMIT:
            case HTMLTags.TAG_BUTTON:
                return ImageConstants.IMG_16_BTN_TEST_OBJECT;
            case HTMLTags.TAG_CHECKBOX:
                return ImageConstants.IMG_16_CHK_TEST_OBJECT;
            case HTMLTags.TAG_FILE:
                return ImageConstants.IMG_16_FILE_TEST_OBJECT;
            case HTMLTags.TAG_IMG:
            case HTMLTags.TAG_IMAGE:
                return ImageConstants.IMG_16_IMG_TEST_OBJECT;
            case HTMLTags.TAG_SELECT:
                return ImageConstants.IMG_16_CBX_TEST_OBJECT;
            case HTMLTags.TAG_LABEL:
                return ImageConstants.IMG_16_LBL_TEST_OBJECT;
            case HTMLTags.TAG_TEXTAREA:
            case HTMLTags.TAG_TEXT:
                return ImageConstants.IMG_16_TXT_TEST_OBJECT;
            case HTMLTags.TAG_RADIO:
                return ImageConstants.IMG_16_RBT_TEST_OBJECT;
            default:
                return ImageConstants.IMG_16_TEST_OBJECT;
        }
    }

    @Override
    public String getTypeName() throws Exception {
        return OBJECT_TYPE_NAME;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        return true;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return true;
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return TreeEntityTransfer.getInstance();
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.WEBELEMENT.toString();
    }

    @Override
    public void setObject(Object object) throws Exception {
        if (object instanceof WebElementEntity) {
            entity = (FileEntity) object;
            webElement = (WebElementEntity) object;
        }
    }

    @Override
    public String getKeyWord() throws Exception {
        return KEY_WORD;
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if (key.equals("name")) {
            return webElement.getName();
        } else if (key.equals("id")) {
            return webElement.getRelativePathForUI().replace(File.separator, "/");
        } else if (key.equals("folder")) {
            return webElement.getParentFolder().getIdForDisplay();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
    }

    public boolean canAddToObjectSpy() {
        if (webElement instanceof WebServiceRequestEntity) {
            return false;
        }
        return true;
    }
}
