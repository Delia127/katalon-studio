package com.kms.katalon.core.keyword;

public interface IKeyword {

    SupportLevel getSupportLevel(Object... params);

    Object execute(Object... params);

}
