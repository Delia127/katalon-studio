package com.kms.katalon.composer.explorer.handlers.deletion;

import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;

public abstract class AbstractDeleteReferredEntityHandler {
    
    private YesNoAllOptions[] availableDeletionOptions;

    private YesNoAllOptions deletionOption = YesNoAllOptions.NO;

    public void setDeletePreferenceOption(YesNoAllOptions option) {
        deletionOption = option;
    }

    public YesNoAllOptions getDeletePreferenceOption() {
        return deletionOption;
    }

    protected boolean needToShowPreferenceDialog() {
        return deletionOption == YesNoAllOptions.NO || deletionOption == YesNoAllOptions.YES;
    }
    
    protected boolean canDelete() {
        return deletionOption == YesNoAllOptions.YES || deletionOption == YesNoAllOptions.YES_TO_ALL;
    }

    public YesNoAllOptions[] getAvailableDeletionOptions() {
        return availableDeletionOptions;
    }

    public void setAvailableDeletionOptions(YesNoAllOptions[] availableDeletionOptions) {
        this.availableDeletionOptions = availableDeletionOptions;
    }
}
