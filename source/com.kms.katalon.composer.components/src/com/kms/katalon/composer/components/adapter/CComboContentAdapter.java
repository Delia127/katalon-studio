package com.kms.katalon.composer.components.adapter;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class CComboContentAdapter implements IControlContentAdapter,
		IControlContentAdapter2 {
	
	private static final boolean COMPUTE_TEXT_USING_CLIENTAREA = !Util.isCarbon();

	@Override
	public String getControlContents(Control control) {
		return ((CCombo) control).getText();
	}

	@Override
	public void setControlContents(Control control, String text,
			int cursorPosition) {
		((CCombo) control).setText(text);
		((CCombo) control)
				.setSelection(new Point(cursorPosition, cursorPosition));
	}

	@Override
	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		CCombo combo = (CCombo) control;
		String contents = combo.getText();
		Point selection = combo.getSelection();
		StringBuffer sb = new StringBuffer();
		sb.append(contents.substring(0, selection.x));
		sb.append(text);
		if (selection.y < contents.length()) {
			sb.append(contents.substring(selection.y, contents.length()));
		}
		combo.setText(sb.toString());
		selection.x = selection.x + cursorPosition;
		selection.y = selection.x;
		combo.setSelection(selection);
	}

	@Override
	public int getCursorPosition(Control control) {
		return ((CCombo) control).getSelection().x;
	}

	@Override
	public Rectangle getInsertionBounds(Control control) {
		CCombo combo = (CCombo) control;
		int position = combo.getSelection().y;
		String contents = combo.getText();
		GC gc = new GC(combo);
		gc.setFont(combo.getFont());
		Point extent = gc.textExtent(contents.substring(0, Math.min(position,
				contents.length())));
		gc.dispose();
		if (COMPUTE_TEXT_USING_CLIENTAREA) {
			return new Rectangle(combo.getClientArea().x + extent.x, combo
				.getClientArea().y, 1, combo.getClientArea().height);
		}
		return new Rectangle(extent.x, 0, 1, combo.getSize().y);
	}

	@Override
	public void setCursorPosition(Control control, int index) {
		((CCombo) control).setSelection(new Point(index, index));
	}

	/**
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter2#getSelection(org.eclipse.swt.widgets.Control)
	 * 
	 * @since 3.4
	 */
	@Override
	public Point getSelection(Control control) {
		return ((CCombo) control).getSelection();
	}

	/**
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter2#setSelection(org.eclipse.swt.widgets.Control,
	 *      org.eclipse.swt.graphics.Point)
	 * 
	 * @since 3.4
	 */
	@Override
	public void setSelection(Control control, Point range) {
		((CCombo) control).setSelection(range);
	}

}
