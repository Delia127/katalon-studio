package com.kms.katalon.composer.testcase.keywords;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.annotation.Keyword;

public class BuiltinKeywordFolderBrowserTreeEntity extends KeywordFolderBrowserTreeEntity {
	private static final long serialVersionUID = 1L;
	private String className;
	private String simpleName;
	private String label;

	public BuiltinKeywordFolderBrowserTreeEntity(String className, String simpleName, String label,
			IKeywordBrowserTreeEntity parent) {
		super(simpleName, parent);
		this.className = className;
		setSimpleName(simpleName);
		this.label = label;
	}

	@Override
	public boolean hasChildren() {
		try {
			if (KeywordController.getInstance().getBuiltInKeywords(className).size() > 0) {
				return true;
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return false;
	}

	@Override
	public Object[] getChildren() {
		try {
			return getKeywordByKeywordObject().toArray();
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return null;
	}

	private List<IKeywordBrowserTreeEntity> getKeywordByKeywordObject() throws Exception {
		List<Method> allKeywordMethod = KeywordController.getInstance().getBuiltInKeywords(className);
		Map<String, List<Method>> methodObjectMap = new HashMap<String, List<Method>>();
		for (Method method : allKeywordMethod) {
			Keyword keywordParameter = method.getAnnotation(Keyword.class);
			if (keywordParameter != null) {
				List<Method> methodList = methodObjectMap.get(keywordParameter.keywordObject());
				if (methodList == null) {
					methodList = new ArrayList<Method>();
					methodObjectMap.put(keywordParameter.keywordObject(), methodList);
				}
				methodList.add(method);
			}
		}
		List<IKeywordBrowserTreeEntity> childTreeEntityList = new ArrayList<IKeywordBrowserTreeEntity>();
		Iterator<Entry<String, List<Method>>> it = methodObjectMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<Method>> pair = (Entry<String, List<Method>>) it.next();
			KeywordFolderBrowserTreeEntity keywordFolder = new KeywordFolderBrowserTreeEntity(pair.getKey(), this);
			for (Method method : pair.getValue()) {
				keywordFolder.children.add(new KeywordBrowserTreeEntity(simpleName, method.getName(), false,
						keywordFolder));
			}
			childTreeEntityList.add(keywordFolder);
		}
		Collections.sort(childTreeEntityList, new Comparator<IKeywordBrowserTreeEntity>() {
			@Override
			public int compare(IKeywordBrowserTreeEntity o1, IKeywordBrowserTreeEntity o2) {
				if (o1 != null && o2 != null) {
					return o1.getName().compareTo(o2.getName());
				}
				return 0;
			}
		});
		return childTreeEntityList;
	}

	@Override
	public String getName() {
		return label;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

}
