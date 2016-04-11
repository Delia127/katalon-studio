package com.kms.katalon.composer.explorer.handlers;

import com.kms.katalon.composer.components.transfer.TransferMoveFlag;

public class CutHandler extends CopyHandler {

    @Override
    public void execute() {
        super.execute();
        TransferMoveFlag.setMove(true);
    }

}
