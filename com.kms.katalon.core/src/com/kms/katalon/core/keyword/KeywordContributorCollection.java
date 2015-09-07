package com.kms.katalon.core.keyword;

import java.util.ArrayList;
import java.util.List;

public class KeywordContributorCollection {
	private static KeywordContributorCollection _instance;

    private List<IKeywordContributor> keywordContributors;
    
    private KeywordContributorCollection() {
        keywordContributors = new ArrayList<IKeywordContributor>();
    }
    
    public void addKeywordContributor(IKeywordContributor contributor) {
        _instance.keywordContributors.add(contributor);
    }
    
    public List<IKeywordContributor> getKeywordContributors() {
        return keywordContributors;
    }

    public static KeywordContributorCollection getInstance() {
        if (_instance == null) {
            _instance = new KeywordContributorCollection();
        }
        return _instance;
    }
}
