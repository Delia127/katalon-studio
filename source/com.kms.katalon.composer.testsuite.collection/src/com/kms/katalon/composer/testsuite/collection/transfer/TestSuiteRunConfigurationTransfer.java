package com.kms.katalon.composer.testsuite.collection.transfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class TestSuiteRunConfigurationTransfer extends ByteArrayTransfer {

    protected static final Class<?> getTypeClass() {
        return TestSuiteRunConfigurationTransfer.class;
    }

    protected static final String getTypeName() {
        return getTypeClass().getName();
    }

    protected static final int getTypeID() {
        return registerType(getTypeName());
    }

    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkType(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        TestSuiteRunConfigurationTransferData[] treeEntites = (TestSuiteRunConfigurationTransferData[]) object;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream writeOut = new DataOutputStream(out);
            for (int i = 0, length = treeEntites.length; i < length; i++) {
                byte[] buffer = objectToBytes(treeEntites[i]);
                writeOut.writeInt(buffer.length);
                writeOut.write(buffer);
            }
            byte[] buffer = out.toByteArray();
            writeOut.close();
            super.javaToNative(buffer, transferData);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Object nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null)
                return null;

            TestSuiteRunConfigurationTransferData[] myData = new TestSuiteRunConfigurationTransferData[0];
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                DataInputStream readIn = new DataInputStream(in);
                while (readIn.available() > 20) {
                    int size = readIn.readInt();
                    byte[] payload = new byte[size];
                    readIn.read(payload);
                    Object obj = bytesToObject(payload);
                    TestSuiteRunConfigurationTransferData datum = (TestSuiteRunConfigurationTransferData) obj;

                    TestSuiteRunConfigurationTransferData[] newMyData = new TestSuiteRunConfigurationTransferData[myData.length + 1];
                    System.arraycopy(myData, 0, newMyData, 0, myData.length); 
                    newMyData[myData.length] = datum;
                    myData = newMyData;
                }
                readIn.close();
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
                return null;
            }
            return myData;
        }
        return null;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { getTypeID() };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { getTypeName() };
    }

    protected boolean checkType(Object object) {
        if (object == null || !(object instanceof TestSuiteRunConfigurationTransferData[])
                || ((TestSuiteRunConfigurationTransferData[]) object).length == 0) {
            return false;
        }
        TestSuiteRunConfigurationTransferData[] elementArray = (TestSuiteRunConfigurationTransferData[]) object;

        for (int i = 0; i < elementArray.length; i++) {
            if (elementArray[i] == null) {
                return false;
            }
        }
        return true;
    }

    protected byte[] objectToBytes(Object theObject) throws Exception {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os;
            os = new ObjectOutputStream(bos);
            os.writeObject(theObject);
            return bos.toByteArray();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    protected Object bytesToObject(byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream is = new ObjectInputStream(bis);
        return is.readObject();
    }
}
