package com.kms.katalon.composer.explorer.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.Transfer;

public class TransferTypeCollection {
	private static TransferTypeCollection _instance;
	private List<Transfer> treeEntityTransferTypes;
	
	private TransferTypeCollection() {
		treeEntityTransferTypes = new ArrayList<Transfer>();
	};
	
	public static TransferTypeCollection getInstance() {
		if (_instance == null) {
			_instance = new TransferTypeCollection();
		}
		return _instance;
	}
	
	public void addTreeEntityTransferType(Transfer transfer) {
		if (!(treeEntityTransferTypes.contains(transfer))) {
			treeEntityTransferTypes.add(transfer);
		}
	}
	
	public List<Transfer> getTreeEntityTransfer() {
		return treeEntityTransferTypes;
	}
}
