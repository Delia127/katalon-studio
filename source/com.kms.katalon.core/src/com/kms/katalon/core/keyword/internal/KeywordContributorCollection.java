package com.kms.katalon.core.keyword.internal;

import java.util.ArrayList;
import java.util.List;

public class KeywordContributorCollection {

    private static List<IKeywordContributor> keywordContributors = new ArrayList<IKeywordContributor>();

    public static void addKeywordContributor(IKeywordContributor contributor) {
        keywordContributors.add(contributor);
    }

    public static List<IKeywordContributor> getKeywordContributors() {
        return keywordContributors;
    }
}
