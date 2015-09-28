package com.kms.katalon.composer.components.transfer;

public class TransferMoveFlag {
	private static boolean isMove = false;

	public static boolean isMove() {
		return isMove;
	}

	public static void setMove(boolean isMove) {
		TransferMoveFlag.isMove = isMove;
	}
}
