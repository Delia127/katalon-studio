package com.kms.katalon.objectspy.util;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;
import com.kms.katalon.entity.repository.WebElementXpathEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.util.collections.Pair;

public class WebElementUtils {
    private static final String PAGE_ELEMENT_NAME_PREFIX = "Page_";

    private static final String FRAME_ELEMENT_NAME_PREFIX = "Frame_";

    private static final String WEB_ELEMENT_NAME_PREFIX = "Element_";

    private static final String ELEMENT_ATTRIBUTES_STYLE_KEY = "style";

    private static final int NAME_LENGTH_LIMIT = 150;

    public static final String PAGE_TITLE_KEY = "title";

    // private static final String ELEMENT_CHILDREN_KEY = "children";

    private static final String ELEMENT_PARENT_KEY = "parent";

    private static final String ELEMENT_PAGE_KEY = "page";
    
    private static final String ELEMENT_XPATHS_KEY = "xpaths";
    
    private static final String ELEMENT_USEFUL_NEIGHBOR_TEXT = "neighbor_text";

    private static final String ELEMENT_ATTRIBUTES_KEY = "attributes";

    private static final String ELEMENT_CONTENT_KEY = "content";

    private static final String ELEMENT_TEXT_KEY = "text";

    private static final String ELEMENT_ID_KEY = "id";

    private static final String ELEMENT_NAME_KEY = "name";

    private static final String ELEMENT_CLASS_KEY = "class";

    private static final String ELEMENT_TYPE_KEY = "type";

    private static final String ELEMENT_TAG_KEY = "tag";

    private static final String XPATH_KEY = "xpath";
    
    private static final String PAGE_URL_KEY = "url";

    private static final List<String> PRIORITY_PROPERTIES;

    static {
        PRIORITY_PROPERTIES = Arrays.asList(new String[] { "id", "name", "alt", "checked", "form", "href",
                "placeholder", "selected", "src", "title", "type", "text", "linked_text" });
    }

	public static String generateWebElementName(String elementType, List<WebElementPropertyEntity> properties,
			String usefulNeighborText) {
		Map<String, String> propsMap = properties.stream()
				.filter(p -> ELEMENT_TEXT_KEY.equals(p.getName()) || ELEMENT_NAME_KEY.equals(p.getName())
						|| ELEMENT_ID_KEY.equals(p.getName()) || ELEMENT_CLASS_KEY.equals(p.getName()))
				.collect(Collectors.toMap(WebElementPropertyEntity::getName, WebElementPropertyEntity::getValue));
		String content = propsMap.get(ELEMENT_TEXT_KEY);
		if (content != null) {
			return elementType + "_" + toValidFileName(content);
		}
		String name = propsMap.get(ELEMENT_NAME_KEY);
		if (name != null) {
			if (StringUtils.EMPTY.equals(usefulNeighborText)) {
				return elementType + "_" + toValidFileName(name);
			} else {
				return elementType + "_" + toValidFileName(usefulNeighborText) + "_" + toValidFileName(name);
			}
		}
		String id = propsMap.get(ELEMENT_ID_KEY);
		if (id != null) {
			if (StringUtils.EMPTY.equals(usefulNeighborText)) {
				return elementType + toValidFileName(id);
			} else {
				return elementType + "_" + toValidFileName(usefulNeighborText) + "_" + toValidFileName(id);
			}
		}
		String cssClass = propsMap.get(ELEMENT_CLASS_KEY);
		if (cssClass != null) {
			if (StringUtils.EMPTY.equals(usefulNeighborText)) {
				return elementType + "_" + toValidFileName(cssClass);
			} else {
				return elementType + "_" + toValidFileName(usefulNeighborText) + "_" + toValidFileName(cssClass);
			}
		}
		return elementType;
	}

    public static String decodeURIComponent(String encodedString) throws UnsupportedEncodingException {
        return URLDecoder.decode(encodedString.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
    }

    public static WebElement buildWebElement(String jsonString) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(decodeURIComponent(jsonString).trim());
        if (jsonElement instanceof JsonObject) {
            return buildWebElement((JsonObject) jsonElement, false);
        }
        return null;
    }

    public static WebElement buildWebElement(JsonObject elementJsonObject, boolean isFrame)
            throws UnsupportedEncodingException {
        if (elementJsonObject == null || !elementJsonObject.get(ELEMENT_TYPE_KEY).isJsonPrimitive()) {
            return null;
        }
        String elementType = elementJsonObject.getAsJsonPrimitive(ELEMENT_TYPE_KEY).getAsString();

        List<WebElementPropertyEntity> properties = new ArrayList<>();
        properties.add(new WebElementPropertyEntity(ELEMENT_TAG_KEY, elementType));
        collectElementContents(elementJsonObject, properties);
        collectElementAttributes(elementJsonObject, properties);
        
        List<WebElementXpathEntity> xpaths = new ArrayList<>();
        collectElementXpaths(elementJsonObject, xpaths);
        
        String xpathString = getElementXpath(elementJsonObject);
        if (xpathString != null) {
            boolean hasPriorityProperty = properties.stream()
                    .filter(p -> PRIORITY_PROPERTIES.contains(p.getName()))
                    .findAny()
                    .isPresent();
            properties.add(new WebElementPropertyEntity(XPATH_KEY, xpathString, !hasPriorityProperty));
        }
        
        // Change default selected properties by user settings
        Map<String, Boolean> customSettings = getCapturedTestObjectAttributeLocatorSettings().stream()
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        properties.stream().filter(i -> customSettings.get(i.getName()) != null).forEach(i -> {
            i.setIsSelected(customSettings.get(i.getName()));
        });

        // Change default selected properties by user settings
        SelectorMethod selectorMethod = getCapturedTestObjectSelectorMethod();
        
        List<String> capturedTestObjectXpaths = getCapturedTestObjectXpathLocatorSettings().stream()
        		.map(o -> o.getLeft()).collect(Collectors.toList());
        
        Comparator<WebElementXpathEntity> comparator = new Comparator<WebElementXpathEntity>() {
            public int compare(WebElementXpathEntity o1, WebElementXpathEntity o2) {
                int p1 = capturedTestObjectXpaths.indexOf(o1.getName());
                int p2 = capturedTestObjectXpaths.indexOf(o2.getName());
                if (p1 == -1 && p2 != -1) {
                    return 1;
                }
                if (p1 != -1 && p2 == -1) {
                    return -1;
                }
                if (p1 != p2) {
                    return p1 - p2;
                }
                return o1.getName().compareTo(o2.getName());
            }
        };
        
        Collections.sort(xpaths, comparator);
        
        for (Iterator<WebElementXpathEntity> it = xpaths.iterator(); it.hasNext(); ) {
        	WebElementXpathEntity xpath = it.next();
            if (capturedTestObjectXpaths.indexOf(xpath.getName()) == -1) {
                it.remove();
            }
        }
        
		if (!xpaths.isEmpty()) {
			Optional.ofNullable(xpaths.get(0)).ifPresent(a -> a.setIsSelected(true));
		}
        
        
        String usefulNeighborText = getElementUsefulNeighborText(elementJsonObject);
        WebFrame parentElement = getParentElement(elementJsonObject);

        String newName = generateWebElementName(elementType, properties, usefulNeighborText);
        
        if (newName.length() > NAME_LENGTH_LIMIT) {
            newName = newName.substring(0, NAME_LENGTH_LIMIT);
        }
     
        WebElement el = isFrame ? new WebFrame(newName) : new WebElement(newName);
        el.setParent(parentElement);
        el.setProperties(properties);
        el.setXpaths(xpaths);
        el.setUsefulNeighborText(usefulNeighborText);
        el.setSelectorMethod(selectorMethod);
        
		// New TestObject will always have a NoneEmpty SelectorCollection
		switch (selectorMethod) {
		case XPATH:
			if (!xpaths.isEmpty()) {
				String value = Optional.ofNullable(xpaths.get(0)).orElse(new WebElementXpathEntity("", "")).getValue();
				el.setSelectorValue(selectorMethod, value);
			}
			break;
		default:
			break;
		}
        
        return el;
    }

    private static List<Pair<String, Boolean>> getCapturedTestObjectAttributeLocatorSettings() {
        WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(
                ProjectController.getInstance().getCurrentProject());
        try {
            return store.getCapturedTestObjectAttributeLocators();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }
    
    private static List<Pair<String, Boolean>> getCapturedTestObjectXpathLocatorSettings() {
        WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(
                ProjectController.getInstance().getCurrentProject());
        try {
            return store.getCapturedTestObjectXpathLocators();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }
    
    private static SelectorMethod getCapturedTestObjectSelectorMethod() {
        WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(
                ProjectController.getInstance().getCurrentProject());
        try {
            return store.getCapturedTestObjectSelectorMethod();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return SelectorMethod.BASIC;
        }
    }

    public static String toValidFileName(String fileName) {
        return fileName.trim().replaceAll("[^A-Za-z0-9_()\\- ]", "");
    }

    private static WebFrame getParentElement(JsonObject elementJsonObject) throws UnsupportedEncodingException {
        WebFrame parentElement = null;
        if (elementJsonObject.has(ELEMENT_PARENT_KEY) && elementJsonObject.get(ELEMENT_PARENT_KEY).isJsonObject()) {
            WebElement tempParentElement = buildWebElement(elementJsonObject.getAsJsonObject(ELEMENT_PARENT_KEY), true);
            if (tempParentElement instanceof WebFrame) {
                parentElement = (WebFrame) tempParentElement;
            }
        } else {
            parentElement = buildWebPageElement(elementJsonObject.getAsJsonObject(ELEMENT_PAGE_KEY));
        }
        return parentElement;
    }

    private static String getElementXpath(JsonObject elementJsonObject) {
        if (elementJsonObject.has(XPATH_KEY) && elementJsonObject.get(XPATH_KEY).isJsonPrimitive()) {
            return elementJsonObject.getAsJsonPrimitive(XPATH_KEY).getAsString();

        }
        return null;
    }

    private static void collectElementAttributes(JsonObject elementJsonObject,
            List<WebElementPropertyEntity> properties) {
        if (!isElementAttributesSet(elementJsonObject)) {
            return;
        }
        for (Entry<String, JsonElement> entry : elementJsonObject.getAsJsonObject(ELEMENT_ATTRIBUTES_KEY).entrySet()) {
            if (!isValidElementAttribute(entry)) {
                continue;
            }
            String propertyName = entry.getKey();
            properties.add(new WebElementPropertyEntity(propertyName, entry.getValue().getAsString(),
                    PRIORITY_PROPERTIES.contains(propertyName)));
        }
    }
    
    private static String getElementUsefulNeighborText(JsonObject elementJsonObject){
    	 if (elementJsonObject.has(ELEMENT_USEFUL_NEIGHBOR_TEXT) && elementJsonObject.get(ELEMENT_USEFUL_NEIGHBOR_TEXT).isJsonPrimitive()) {
             return elementJsonObject.getAsJsonPrimitive(ELEMENT_USEFUL_NEIGHBOR_TEXT).getAsString();
         }
         return StringUtils.EMPTY;
    }
    
    private static void collectElementXpaths(JsonObject elementJsonObject,
            List<WebElementXpathEntity> xpaths) {
        if (!isElementXpathsSet(elementJsonObject)) {
            return;
        }
        for (Entry<String, JsonElement> entry : elementJsonObject.getAsJsonObject(ELEMENT_XPATHS_KEY).entrySet()) {
            String xpathFinder = entry.getKey();
            JsonElement xpath = entry.getValue();
            
            if (xpath instanceof JsonObject) {
                xpaths.add(new WebElementXpathEntity(xpathFinder, entry.getValue().getAsString(), false));
             } else if (xpath instanceof JsonArray) {
            	 for(JsonElement jsonElement : xpath.getAsJsonArray()){
                     xpaths.add(new WebElementXpathEntity(xpathFinder, jsonElement.getAsString(), false));
            	 }
             }
        }
    }

    private static boolean isElementAttributesSet(JsonObject elementJsonObject) {
        return elementJsonObject.has(ELEMENT_ATTRIBUTES_KEY)
                && elementJsonObject.get(ELEMENT_ATTRIBUTES_KEY).isJsonObject();
    }
    
    private static boolean isElementXpathsSet(JsonObject elementJsonObject) {
        return elementJsonObject.has(ELEMENT_XPATHS_KEY)
                && elementJsonObject.get(ELEMENT_XPATHS_KEY).isJsonObject();
    }

    private static boolean isValidElementAttribute(Entry<String, JsonElement> attributeEntry) {
        return attributeEntry.getValue() != null && isNotBlank(attributeEntry.getValue().getAsString())
                && !ELEMENT_ATTRIBUTES_STYLE_KEY.equals(attributeEntry.getKey());
    }
    


    private static void collectElementContents(JsonObject elementJsonObject,
            List<WebElementPropertyEntity> properties) {
        if (!isElementContent(elementJsonObject)) {
            return;
        }
        JsonArray contentArray = elementJsonObject.getAsJsonArray(ELEMENT_CONTENT_KEY);
        if (!isValidElementContent(contentArray)) {
            return;
        }
        properties.add(new WebElementPropertyEntity(ELEMENT_TEXT_KEY, contentArray.get(0).getAsString()));
    }

    private static boolean isElementContent(JsonObject elementJsonObject) {
        return elementJsonObject.has(ELEMENT_CONTENT_KEY) && elementJsonObject.get(ELEMENT_CONTENT_KEY).isJsonArray();
    }

    private static boolean isValidElementContent(JsonArray contentArray) {
        return contentArray != null && contentArray.size() == 1 && contentArray.get(0).isJsonPrimitive()
                && isNotBlank(contentArray.get(0).getAsString());
    }

    private static WebPage buildWebPageElement(JsonObject parentPageJsonObject) {
        if (parentPageJsonObject == null) {
            return null;
        }

        String pageUrlString = parentPageJsonObject.getAsJsonPrimitive(PAGE_URL_KEY).getAsString();
        String pageTitleString = parentPageJsonObject.getAsJsonPrimitive(PAGE_TITLE_KEY).getAsString();
        
        List<WebElementPropertyEntity> properties = new ArrayList<>();
        properties.add(new WebElementPropertyEntity(PAGE_URL_KEY, pageUrlString, PRIORITY_PROPERTIES.contains(PAGE_URL_KEY)));
        properties.add(new WebElementPropertyEntity(PAGE_TITLE_KEY, pageTitleString, PRIORITY_PROPERTIES.contains(PAGE_TITLE_KEY)));
        WebPage webPage = new WebPage(generateWebPageName(pageTitleString));
        webPage.setProperties(properties);
        return webPage;
    }

    private static String generateWebPageName(String pageTitleString) {
        return PAGE_ELEMENT_NAME_PREFIX + StringUtils.substring(toValidFileName(pageTitleString), 0, NAME_LENGTH_LIMIT);
    }

    public static WebElementEntity convertWebElementToTestObject(WebElement element, WebElementEntity refElement,
            FolderEntity parentFolder) throws Exception {
        WebElementEntity newWebElement = new WebElementEntity();
        newWebElement.setName(ObjectRepositoryController.getInstance().getAvailableWebElementName(parentFolder,
                toValidFileName(StringUtils.trim(element.getName()))));
        newWebElement.setParentFolder(parentFolder);
        newWebElement.setElementGuidId(Util.generateGuid());
        newWebElement.setProject(parentFolder.getProject());
        newWebElement.setWebElementProperties(new ArrayList<>(element.getProperties()));
        newWebElement.setWebElementXpaths(new ArrayList<>(element.getXpaths()));
        newWebElement.setSelectorMethod(WebElementSelectorMethod.valueOf(element.getSelectorMethod().name()));
        element.getSelectorCollection().entrySet().forEach(entry -> {
            SelectorMethod selectorMethod = entry.getKey();
            if (SelectorMethod.BASIC == selectorMethod) {
                return;
            }
            newWebElement.setSelectorValue(WebElementSelectorMethod.valueOf(selectorMethod.name()), entry.getValue());
        });

        if (refElement == null) {
            return newWebElement;
        }

        newWebElement.getWebElementProperties()
                .add(new WebElementPropertyEntity(WebElementEntity.ref_element, refElement.getIdForDisplay()));

        return newWebElement;
    }

    public static FolderEntity convertWebPageToFolder(WebPage page, FolderEntity parentFolder) throws Exception {
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(toValidFileName(StringUtils.trim(page.getName())));
        newFolder.setParentFolder(parentFolder);
        newFolder.setDescription("folder");
        newFolder.setFolderType(parentFolder.getFolderType());
        newFolder.setProject(parentFolder.getProject());
        return newFolder;
    }

    public static WebPage createWebPage() {
        return new WebPage(PAGE_ELEMENT_NAME_PREFIX + System.currentTimeMillis());
    }

    public static WebFrame createWebFrame(WebFrame parent) {
        long currentTime = System.currentTimeMillis();
        WebFrame el = new WebFrame(FRAME_ELEMENT_NAME_PREFIX + currentTime);
        el.setTag("IFRAME");
        el.setParent(parent);
        el.addProperty(XPATH_KEY, XPATH_KEY + currentTime);
        return el;
    }

    public static WebElement createWebElement(WebFrame parent) {
        long currentTime = System.currentTimeMillis();
        WebElement el = new WebElement(WEB_ELEMENT_NAME_PREFIX + currentTime);
        el.setTag("DIV");
        el.setParent(parent);
        el.addProperty(XPATH_KEY, XPATH_KEY + currentTime);
        return el;
    }

    public static String buildXpathForHTMLElement(WebElement element) {
        if (element != null && element.getParent() instanceof WebFrame) {
            if (element.getXpath().startsWith("/")) {
                return buildXpathForHTMLElement(element.getParent()) + element.getXpath();
            } else {
                return element.getXpath();
            }
        }
        return StringConstants.EMPTY;
    }

    public static List<WebPage> createWebElementFromFolder(FolderEntity folder, Map<String, WebElement> elementsMap) {
        if (folder == null) {
            return Collections.emptyList();
        }

        List<WebPage> pageElements = new ArrayList<>();
        WebElement mappedWebPage = elementsMap.get(folder.getId());
        if (mappedWebPage != null) {
            pageElements.add((WebPage) mappedWebPage);
            return pageElements;
        }

        WebPage pageElement = createWebPage();
        pageElement.setName(folder.getName());
        elementsMap.put(folder.getId(), pageElement);
        pageElements.add(pageElement);

        try {
            for (FileEntity entity : FolderController.getInstance().getChildren(folder)) {
                if (entity instanceof WebElementEntity) {
                    createWebElementFromTestObject((WebElementEntity) entity, false, pageElement, elementsMap);
                    continue;
                }

                if (entity instanceof FolderEntity) {
                    List<WebPage> childPageElements = createWebElementFromFolder((FolderEntity) entity, elementsMap);
                    pageElements.addAll(childPageElements);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return pageElements;
    }

    public static WebElement createWebElementFromTestObject(WebElementEntity webElementEntity, boolean isFrame,
            WebPage pageElement, Map<String, WebElement> elementsMap) {
        if (webElementEntity == null) {
            return null;
        }
        String entityId = webElementEntity.getId();
        WebElement mappedWebElement = elementsMap.get(entityId);
        if (mappedWebElement != null) {
            if (isFrame && !(mappedWebElement instanceof WebFrame)) {
                elementsMap.remove(entityId);
                WebFrame parentElementOfFrame = mappedWebElement.getParent();
                parentElementOfFrame.getChildren().remove(mappedWebElement);
                WebFrame replacedElement = new WebFrame(mappedWebElement.getName());
                replacedElement.setParent(parentElementOfFrame);
                if (mappedWebElement.hasProperty()) {
                    replacedElement.setProperties(mappedWebElement.getProperties());
                }
                if (mappedWebElement.hasXpath()) {
                    replacedElement.setXpaths(mappedWebElement.getXpaths());
                }
                elementsMap.put(entityId, replacedElement);
                return replacedElement;
            } else {
                return mappedWebElement;
            }
        }

        WebFrame parentFrameElement = null;
        Optional<WebElementPropertyEntity> refElement = webElementEntity.getWebElementProperties()
                .stream()
                .filter(p -> WebElementEntity.ref_element.equals(p.getName()))
                .findFirst();

        if (refElement.isPresent()) {
            try {
                parentFrameElement = (WebFrame) createWebElementFromTestObject(
                        ObjectRepositoryController.getInstance().getWebElementByDisplayPk(refElement.get().getValue()),
                        true, pageElement, elementsMap);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }

        String entityName = webElementEntity.getName();
        WebElement element = isFrame ? new WebFrame(entityName) : new WebElement(entityName);
        element.setParent(parentFrameElement != null ? parentFrameElement : pageElement);
        element.setProperties(webElementEntity.getWebElementProperties());
        element.setXpaths(webElementEntity.getWebElementXpaths());
        element.setSelectorMethod(SelectorMethod.valueOf(webElementEntity.getSelectorMethod().name()));
        webElementEntity.getSelectorCollection().entrySet().forEach(entry -> {
            WebElementSelectorMethod selectorMethod = entry.getKey();
            if (WebElementSelectorMethod.BASIC == selectorMethod) {
                return;
            }
            element.setSelectorValue(SelectorMethod.valueOf(selectorMethod.name()), entry.getValue());
        });

        element.setSelectorMethod(SelectorMethod.valueOf(webElementEntity.getSelectorMethod().name()));
        webElementEntity.getSelectorCollection().entrySet().forEach(entry -> {
            WebElementSelectorMethod selectorMethod = entry.getKey();
            if (WebElementSelectorMethod.BASIC == selectorMethod) {
                return;
            }
            element.setSelectorValue(SelectorMethod.valueOf(selectorMethod.name()), entry.getValue());
        });

        element.setSelectorMethod(SelectorMethod.valueOf(webElementEntity.getSelectorMethod().name()));
        webElementEntity.getSelectorCollection().entrySet().forEach(entry -> {
            WebElementSelectorMethod selectorMethod = entry.getKey();
            if (WebElementSelectorMethod.BASIC == selectorMethod) {
                return;
            }
            element.setSelectorValue(SelectorMethod.valueOf(selectorMethod.name()), entry.getValue());
        });

        element.setSelectorMethod(SelectorMethod.valueOf(webElementEntity.getSelectorMethod().name()));
        webElementEntity.getSelectorCollection().entrySet().forEach(entry -> {
            WebElementSelectorMethod selectorMethod = entry.getKey();
            if (WebElementSelectorMethod.BASIC == selectorMethod) {
                return;
            }
            element.setSelectorValue(SelectorMethod.valueOf(selectorMethod.name()), entry.getValue());
        });

        elementsMap.put(entityId, element);
        return element;
    }

    public static TestObject buildTestObject(WebElement webElement) {
        TestObject testObject = new TestObject(webElement.getScriptId());
        WebFrame parentFrame = webElement.getParent();
        if (parentFrame != null && !(parentFrame instanceof WebPage)) {
            TestObject parent = buildTestObject(parentFrame);
            testObject.setParentObject(parent);
        }
        
        webElement.getProperties().forEach(prop -> {
            testObject.addProperty(prop.getName(), ConditionType.fromValue(prop.getMatchCondition()), prop.getValue(),
                    prop.getIsSelected());
        });
        
        webElement.getXpaths().forEach(xpath -> {
            testObject.addXpath(xpath.getName(), ConditionType.fromValue(xpath.getMatchCondition()), xpath.getValue(),
            		xpath.getIsSelected());
        });
        
        testObject.setSelectorMethod(webElement.getSelectorMethod());
        webElement.getSelectorCollection().entrySet().forEach(entry -> {
            testObject.setSelectorValue(entry.getKey(), entry.getValue());
        });
        return testObject;
    }
}
