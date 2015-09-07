package com.kms.katalon.composer.components.transfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;

public abstract class EntityTransfer extends ByteArrayTransfer {

	protected static final Class<?> getTypeClass() {
		return ITreeEntity.class;
	}

	protected static final String getTypeName() {
		return getTypeClass().getName();
	}

	protected static final int getTypeID() {
		return registerType(getTypeName());
	}

	@SuppressWarnings("restriction")
	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (!checkType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		ITreeEntity[] treeEntites = (ITreeEntity[]) object;
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
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}

	@SuppressWarnings("restriction")
	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			ITreeEntity[] myData = new ITreeEntity[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while (readIn.available() > 20) {
					int size = readIn.readInt();
					byte[] payload = new byte[size];
					readIn.read(payload);
					Object obj = bytesToObject(payload);
					ITreeEntity datum = (ITreeEntity) obj;

					ITreeEntity[] newMyData = new ITreeEntity[myData.length + 1];
					System.arraycopy(myData, 0, newMyData, 0, myData.length);
					newMyData[myData.length] = datum;
					myData = newMyData;
				}
				readIn.close();
			} catch (Exception ex) {
				LoggerSingleton.getInstance().getLogger().error(ex);
				return null;
			}
			return myData;
		}
		return null;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { getTypeName() };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { getTypeID() };
	}

	protected boolean checkType(Object object) {
		if (object == null || !(object instanceof ITreeEntity[]) || ((ITreeEntity[]) object).length == 0) {
			return false;
		}
		ITreeEntity[] elementArray = (ITreeEntity[]) object;

		for (int i = 0; i < elementArray.length; i++) {
			if (elementArray[i] == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean validate(Object object) {
		return checkType(object);
	}

	protected abstract byte[] objectToBytes(Object theObject) throws Exception;

	// Sub-classes must override this function to read Object from the correct
	// plug-in hierarchy.
	protected abstract Object bytesToObject(byte[] bytes) throws Exception;
}
