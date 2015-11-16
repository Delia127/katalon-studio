
package com.kms.katalon.composer.webui.addon;


import javax.annotation.PostConstruct;

import com.kms.katalon.composer.testcase.model.CustomInputValueTypeCollector;
import com.kms.katalon.composer.webui.model.KeyInputValueType;
import com.kms.katalon.composer.webui.model.KeysInputValueType;

public class WebUIInjectionManagerAddon {
    @PostConstruct
    public void initHandlers() {
        CustomInputValueTypeCollector.getInstance().addCustomInputValueType(new KeysInputValueType());
        CustomInputValueTypeCollector.getInstance().addCustomInputValueType(new KeyInputValueType());
    }
}
