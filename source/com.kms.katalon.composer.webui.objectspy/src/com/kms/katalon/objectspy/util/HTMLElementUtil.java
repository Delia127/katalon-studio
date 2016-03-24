package com.kms.katalon.objectspy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity.MATCH_CONDITION;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;

public class HTMLElementUtil {
    private static final String ELEMENT_ATTRIBUTES_STYLE_KEY = "style";

    private static final int NAME_LENGTH_LIMIT = 30;

    public static final String PAGE_TITLE_KEY = "title";

    private static final String PAGE_URL_KEY = "url";

    private static final String ELEMENT_CHILDREN_KEY = "children";

    private static final String ELEMENT_PARENT_KEY = "parent";

    private static final String ELEMENT_PAGE_KEY = "page";

    private static final String ELEMENT_ATTRIBUTES_KEY = "attributes";

    private static final String ELEMENT_CONTENT_KEY = "content";

    private static final String ELEMENT_TEXT_KEY = "text";

    private static final String ELEMENT_ID_KEY = "id";

    private static final String ELEMENT_CLASS_KEY = "class";

    private static final String ELEMENT_TYPE_KEY = "type";

    private static final String XPATH_KEY = "xpath";

    public static String generateHTMLElementName(String elementType, Map<String, String> attributes)
            throws UnsupportedEncodingException {
        String content = attributes.get(ELEMENT_TEXT_KEY);
        if (content != null) {
            return elementType + "_" + URLEncoder.encode(content, "UTF-8");
        }
        String id = attributes.get(ELEMENT_ID_KEY);
        if (id != null) {
            return elementType + "_" + id;
        }
        String cssClass = attributes.get(ELEMENT_CLASS_KEY);
        if (cssClass != null) {
            return elementType + "_" + cssClass;
        }
        return elementType;
    }

    public static String decodeURIComponent(String encodedString) throws UnsupportedEncodingException {
        return URLDecoder.decode(encodedString.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
    }

    public static HTMLElement buildHTMLElement(String jsonString) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(decodeURIComponent(jsonString).trim());
        if (jsonElement instanceof JsonObject) {
            return buildHTMLElement((JsonObject) jsonElement, false);
        }
        return null;
    }

    public static HTMLElement buildHTMLElement(JsonObject elementJsonObject, boolean isFrame)
            throws UnsupportedEncodingException {
        if (elementJsonObject == null || !elementJsonObject.get(ELEMENT_TYPE_KEY).isJsonPrimitive()) {
            return null;
        }
        String elementType = elementJsonObject.getAsJsonPrimitive(ELEMENT_TYPE_KEY).getAsString();

        Map<String, String> attributesMap = new HashMap<String, String>();
        collectElementContents(elementJsonObject, attributesMap);
        collectElementAttributes(elementJsonObject, attributesMap);

        String xpathString = getElementXpath(elementJsonObject);
        if (xpathString != null) {
            attributesMap.put(XPATH_KEY, xpathString);
        }

        HTMLFrameElement parentElement = getParentElement(elementJsonObject);

        String newName = generateHTMLElementName(elementType, attributesMap);
        if (newName.length() > NAME_LENGTH_LIMIT) {
            newName = newName.substring(0, NAME_LENGTH_LIMIT);
        }
        if (isFrame) {
            return new HTMLFrameElement(newName, elementType, attributesMap, parentElement,
                    new ArrayList<HTMLElement>());
        }
        return new HTMLElement(newName, elementType, attributesMap, parentElement);
    }

    private static HTMLFrameElement getParentElement(JsonObject elementJsonObject) throws UnsupportedEncodingException {
        HTMLFrameElement parentElement = null;
        if (elementJsonObject.has(ELEMENT_PARENT_KEY) && elementJsonObject.get(ELEMENT_PARENT_KEY).isJsonObject()) {
            HTMLElement tempParentElement = buildHTMLElement(elementJsonObject.getAsJsonObject(ELEMENT_PARENT_KEY),
                    true);
            if (tempParentElement instanceof HTMLFrameElement) {
                parentElement = (HTMLFrameElement) tempParentElement;
            }
        } else {
            parentElement = buildHTMLPageElement(elementJsonObject.getAsJsonObject(ELEMENT_PAGE_KEY));
        }
        return parentElement;
    }

    private static String getElementXpath(JsonObject elementJsonObject) {
        if (elementJsonObject.has(XPATH_KEY) && elementJsonObject.get(XPATH_KEY).isJsonPrimitive()) {
            return elementJsonObject.getAsJsonPrimitive(XPATH_KEY).getAsString();

        }
        return null;
    }

    private static void collectElementAttributes(JsonObject elementJsonObject, Map<String, String> attributesMap) {
        if (!isElementAttributesSet(elementJsonObject)) {
            return;
        }
        for (Entry<String, JsonElement> entry : elementJsonObject.getAsJsonObject(ELEMENT_ATTRIBUTES_KEY).entrySet()) {
            if (!isValidElementAttribute(entry)) {
                continue;
            }
            attributesMap.put(entry.getKey(), entry.getValue().getAsString().trim());
        }
    }

    private static boolean isElementAttributesSet(JsonObject elementJsonObject) {
        return elementJsonObject.has(ELEMENT_ATTRIBUTES_KEY)
                && elementJsonObject.get(ELEMENT_ATTRIBUTES_KEY).isJsonObject();
    }

    private static boolean isValidElementAttribute(Entry<String, JsonElement> attributeEntry) {
        return attributeEntry.getValue() != null && !StringUtils.isBlank(attributeEntry.getValue().getAsString())
                && !attributeEntry.getKey().equals(ELEMENT_ATTRIBUTES_STYLE_KEY);
    }

    private static void collectElementContents(JsonObject elementJsonObject, Map<String, String> attributesMap) {
        if (!isElementContent(elementJsonObject)) {
            return;
        }
        JsonArray contentArray = elementJsonObject.getAsJsonArray(ELEMENT_CONTENT_KEY);
        if (!isValidElementContent(contentArray)) {
            return;
        }
        attributesMap.put(ELEMENT_TEXT_KEY, contentArray.get(0).getAsString());
    }

    private static boolean isElementContent(JsonObject elementJsonObject) {
        return elementJsonObject.has(ELEMENT_CONTENT_KEY) && elementJsonObject.get(ELEMENT_CONTENT_KEY).isJsonArray();
    }

    private static boolean isValidElementContent(JsonArray contentArray) {
        return contentArray != null && contentArray.size() == 1 && contentArray.get(0).isJsonPrimitive()
                && !StringUtils.isBlank(contentArray.get(0).getAsString());
    }

    private static HTMLPageElement buildHTMLPageElement(JsonObject parentPageJsonObject)
            throws UnsupportedEncodingException {
        if (parentPageJsonObject == null) {
            return null;
        }

        String pageUrlString = parentPageJsonObject.getAsJsonPrimitive(PAGE_URL_KEY).getAsString();
        String pageTitleString = parentPageJsonObject.getAsJsonPrimitive(PAGE_TITLE_KEY).getAsString();

        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put(PAGE_URL_KEY, pageUrlString);
        attributeMap.put(PAGE_TITLE_KEY, pageTitleString);
        return new HTMLPageElement(generateHTMLPageElementName(pageTitleString), attributeMap,
                new ArrayList<HTMLElement>(), pageUrlString);
    }

    private static String generateHTMLPageElementName(String pageTitleString) throws UnsupportedEncodingException {
        return "Page_"
                + URLEncoder.encode(
                        (pageTitleString.length() > NAME_LENGTH_LIMIT) ? pageTitleString
                                .substring(0, NAME_LENGTH_LIMIT) : pageTitleString, "UTF-8");
    }

    public static WebElementEntity convertElementToWebElementEntity(HTMLElement element, WebElementEntity refElement,
            FolderEntity parentFolder) throws Exception {
        WebElementEntity newWebElement = new WebElementEntity();
        newWebElement.setName(ObjectRepositoryController.getInstance().getAvailableWebElementName(parentFolder,
                element.getName()));
        newWebElement.setParentFolder(parentFolder);
        newWebElement.setElementGuidId(Util.generateGuid());
        newWebElement.setProject(parentFolder.getProject());
        newWebElement.setWebElementProperties(new ArrayList<WebElementPropertyEntity>());

        for (Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
            WebElementPropertyEntity webElementPropertyEntity = new WebElementPropertyEntity();
            webElementPropertyEntity.setName(entry.getKey());
            webElementPropertyEntity.setValue(entry.getValue());
            if (entry.getKey().equals(XPATH_KEY)) {
                webElementPropertyEntity.setIsSelected(true);
            }
            newWebElement.getWebElementProperties().add(webElementPropertyEntity);
        }

        if (refElement != null) {
            WebElementPropertyEntity webElementPropertyEntity = new WebElementPropertyEntity();
            webElementPropertyEntity.setName(WebElementEntity.ref_element);
            webElementPropertyEntity.setValue(refElement.getIdForDisplay());
            webElementPropertyEntity.setIsSelected(true);
            newWebElement.getWebElementProperties().add(webElementPropertyEntity);
        }
        return newWebElement;
    }

    public static FolderEntity convertPageElementToFolderEntity(HTMLPageElement pageElement, FolderEntity parentFolder)
            throws Exception {
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(pageElement.getName());
        newFolder.setParentFolder(parentFolder);
        newFolder.setDescription("folder");
        newFolder.setFolderType(parentFolder.getFolderType());
        newFolder.setProject(parentFolder.getProject());

        return newFolder;
    }

    public static HTMLPageElement generateNewPageElement() {
        long currentTime = System.currentTimeMillis();
        String title = String.valueOf(currentTime);
        String url = PAGE_URL_KEY + currentTime + ".com";
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(PAGE_TITLE_KEY, title);
        attributes.put(PAGE_URL_KEY, url);
        return new HTMLPageElement("Page_" + currentTime, attributes, new ArrayList<HTMLElement>(), url);
    }

    public static HTMLFrameElement generateNewFrameElement(HTMLFrameElement parentElement) {
        long currentTime = System.currentTimeMillis();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(XPATH_KEY, XPATH_KEY + currentTime);
        return new HTMLFrameElement("Frame_" + currentTime, "IFRAME", attributes, parentElement,
                new ArrayList<HTMLElement>());
    }

    public static HTMLElement generateNewElement(HTMLFrameElement parentElement) {
        long currentTime = System.currentTimeMillis();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(XPATH_KEY, XPATH_KEY + currentTime);
        return new HTMLElement("Element_" + currentTime, "DIV", attributes, parentElement);
    }

    public static HTMLRawElement buildHTMLRawElement(Document document, String jsonString) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(decodeURIComponent(jsonString).trim());
        if (jsonElement instanceof JsonObject) {
            // root elements
            HTMLRawElement rootElement = buildHTMLRawElement(document, 1, null, (JsonObject) jsonElement);
            document.appendChild(rootElement.getDomElement());
            return rootElement;
        }
        return null;
    }

    public static HTMLRawElement buildHTMLRawElement(Document document, int index, HTMLRawElement parentElement,
            JsonObject elementJsonObject) throws Exception {
        if (elementJsonObject != null) {
            String elementType = null;
            List<HTMLRawElement> childrenElements = new ArrayList<HTMLRawElement>();

            JsonPrimitive elementTypeObject = elementJsonObject.getAsJsonPrimitive(ELEMENT_TYPE_KEY);
            if (elementTypeObject != null) {
                elementType = elementTypeObject.getAsString();
            } else {
                return null;
            }
            Element element = null;
            try {
                element = document.createElement(elementType);
            } catch (DOMException e) {
                return null;
            }

            JsonObject elementAttributesObject = elementJsonObject.getAsJsonObject(ELEMENT_ATTRIBUTES_KEY);
            if (elementAttributesObject != null) {
                Set<Entry<String, JsonElement>> entrySet = elementAttributesObject.entrySet();
                for (Entry<String, JsonElement> entry : entrySet) {
                    if (entry.getValue() != null) {
                        if (entry.getKey().equals(ELEMENT_ATTRIBUTES_STYLE_KEY)) {
                            continue;
                        }
                        element.setAttribute(entry.getKey(), entry.getValue().getAsString().trim());
                        if (entry.getKey().equals(ELEMENT_TEXT_KEY)) {
                            element.setTextContent(entry.getValue().getAsString().trim());
                        } else if (entry.getKey().equals(ELEMENT_ID_KEY)) {
                            element.setIdAttribute(ELEMENT_ID_KEY, true);
                        }
                    }
                }
            }

            String xpathString = elementJsonObject.getAsJsonPrimitive(XPATH_KEY).getAsString();

            HTMLRawElement htmlRawElement = new HTMLRawElement(element, index, parentElement, xpathString,
                    childrenElements);

            int count = 0;
            JsonElement childrenArrayObject = elementJsonObject.get(ELEMENT_CHILDREN_KEY);
            if (childrenArrayObject != null && childrenArrayObject.isJsonArray()) {
                JsonArray childrenArray = childrenArrayObject.getAsJsonArray();
                for (int i = 0; i < childrenArray.size(); i++) {
                    HTMLRawElement htmlChildElement = buildHTMLRawElement(document, count + 1, htmlRawElement,
                            childrenArray.get(i).getAsJsonObject());
                    if (htmlChildElement != null) {
                        childrenElements.add(htmlChildElement);
                        element.appendChild(htmlChildElement.getDomElement());
                        count++;
                    }
                }
            }
            return htmlRawElement;

        }
        return null;
    }

    public static String buildXpathForHTMLElement(HTMLElement element) {
        if (element != null && element.getParentElement() instanceof HTMLFrameElement) {
            if (element.getXpath().startsWith("/")) {
                return buildXpathForHTMLElement(element.getParentElement()) + element.getXpath();
            } else {
                return element.getXpath();
            }
        }
        return "";
    }

    public static List<HTMLPageElement> createHTMLElementFromFolder(FolderEntity folder,
            Map<String, HTMLElement> elementsMap) {
        List<HTMLPageElement> pageElements = new ArrayList<HTMLPageElement>();
        if (folder != null) {
            if (elementsMap.get(folder.getId()) != null) {
                pageElements.add((HTMLPageElement) elementsMap.get(folder.getId()));
                return pageElements;
            }
            HTMLPageElement pageElement = generateNewPageElement();
            pageElement.setName(folder.getName());
            elementsMap.put(folder.getId(), pageElement);
            pageElements.add(pageElement);
            try {
                for (FileEntity entity : FolderController.getInstance().getChildren(folder)) {
                    if (entity instanceof WebElementEntity) {
                        createHTMLElementFromWebElement((WebElementEntity) entity, false, pageElement, elementsMap);
                    } else if (entity instanceof FolderEntity) {
                        List<HTMLPageElement> childPageElements = createHTMLElementFromFolder((FolderEntity) entity,
                                elementsMap);
                        pageElements.addAll(childPageElements);
                    }
                }
            } catch (Exception e) {
                // error reading folder, continue
                LoggerSingleton.logError(e);
            }
        }
        return pageElements;
    }

    public static HTMLElement createHTMLElementFromWebElement(WebElementEntity webElement, boolean isFrame,
            HTMLPageElement pageElement, Map<String, HTMLElement> elementsMap) {
        if (webElement != null) {
            if (elementsMap.get(webElement.getId()) != null) {
                if (isFrame && !(elementsMap.get(webElement.getId()) instanceof HTMLFrameElement)) {
                    HTMLElement htmlElement = elementsMap.get(webElement.getId());
                    elementsMap.remove(webElement.getId());
                    HTMLFrameElement parentElementOfFrame = htmlElement.getParentElement();
                    parentElementOfFrame.getChildElements().remove(htmlElement);
                    HTMLFrameElement replacedElement = new HTMLFrameElement(htmlElement.getName(),
                            htmlElement.getType(), htmlElement.getAttributes(), parentElementOfFrame,
                            new ArrayList<HTMLElement>());
                    elementsMap.put(webElement.getId(), replacedElement);
                    return replacedElement;
                } else {
                    return elementsMap.get(webElement.getId());
                }
            }
            Map<String, String> attributes = new HashMap<String, String>();
            HTMLFrameElement parentFrameElement = null;
            String xpath = null;
            for (WebElementPropertyEntity property : webElement.getWebElementProperties()) {
                if (property.getMatchCondition().equals(MATCH_CONDITION.EQUAL.toString())) {
                    if (property.getName().equals(WebElementEntity.ref_element)) {
                        try {
                            parentFrameElement = (HTMLFrameElement) createHTMLElementFromWebElement(
                                    ObjectRepositoryController.getInstance().getWebElementByDisplayPk(
                                            property.getValue()), true, pageElement, elementsMap);
                        } catch (Exception e) {
                            // error reading web element, continue
                            LoggerSingleton.logError(e);
                        }
                    } else {
                        if (property.getName().equals(XPATH_KEY)) {
                            xpath = property.getValue();
                        }
                        attributes.put(property.getName(), property.getValue());
                    }
                }
            }
            if (xpath == null) {
                attributes.put(XPATH_KEY, XPATH_KEY + System.currentTimeMillis());
            }
            HTMLElement element = null;
            if (isFrame) {
                element = new HTMLFrameElement(webElement.getName(), "", attributes,
                        parentFrameElement != null ? parentFrameElement : pageElement, new ArrayList<HTMLElement>());
            } else {
                element = new HTMLElement(webElement.getName(), "", attributes,
                        parentFrameElement != null ? parentFrameElement : pageElement);
            }
            elementsMap.put(webElement.getId(), element);
            return element;
        }
        return null;
    }
}
