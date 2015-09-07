package com.kms.katalon.composer.components.impl.transfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.transfer.EntityTransfer;

public final class TreeEntityTransfer extends EntityTransfer {

    private static TreeEntityTransfer _instance;

    private TreeEntityTransfer() {

    }

    public static TreeEntityTransfer getInstance() {
        if (_instance == null) {
            _instance = new TreeEntityTransfer();
        }
        return _instance;
    }
    
    @SuppressWarnings("restriction")
    protected byte[] objectToBytes(Object theObject) throws Exception {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os;
            os = new ObjectOutputStream(bos);
            os.writeObject(theObject);
            return bos.toByteArray();
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return null;
    }

    // Sub-classes must override this function to read Object from the correct
    // plug-in hierarchy.
    protected Object bytesToObject(byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream is = new ObjectInputStream(bis);
        return is.readObject();
    }
}
