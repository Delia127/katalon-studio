package com.kms.katalon.composer.testcase.providers;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Caret;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectScriptDropListener implements DropTargetListener {

    private StyledText text;

    private int characterSize;

    private int beginLineX;

    public TestObjectScriptDropListener(StyledText text) {
        this.text = text;
    }

    @Override
    public void dragEnter(DropTargetEvent event) {
        characterSize = getCharacterSize();
        beginLineX = getBeginLineX();
        text.getParent().setFocus();

    }

    @Override
    public void dragOperationChanged(DropTargetEvent event) {

    }

    @Override
    public void dragOver(DropTargetEvent event) {
        Caret caret = text.getCaret();
        Point rawLocation = text.toControl(event.x, event.y);
        Point offsetLocation = convertMouseLocationToOffsetLocation(rawLocation, false);
        caret.setLocation(offsetLocation);
        text.redraw();
    }

    @Override
    public void drop(DropTargetEvent event) {
        if (!(event.data instanceof ITreeEntity[])) {
            return;
        }
        ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
        for (ITreeEntity treeEntity : treeEntities) {
            if (!(treeEntity instanceof WebElementTreeEntity)) {
                continue;
            }
            WebElementTreeEntity webElementTreeEntity = (WebElementTreeEntity) treeEntity;
            String objectPk = null;
            try {
                if (!(webElementTreeEntity.getObject() instanceof WebElementEntity)) {
                    continue;
                }
                Point rawLocation = text.toControl(event.x, event.y);
                Point offsetLocation = convertMouseLocationToOffsetLocation(rawLocation, true);
                int maxYOfText = text.getLineHeight() * text.getLineCount();
                // check whether the user drop below the last line of the text
                if (offsetLocation.y > maxYOfText) {
                    offsetLocation.y = maxYOfText;
                }
                String line = text.getLine(
                        text.getLineAtOffset(text.getOffsetAtLocation(new Point(beginLineX, offsetLocation.y))));
                if (line.length() == 0) {
                    offsetLocation.x = beginLineX;
                }
                // check whether the user drop behind the last character of the line
                int maxXofCurrentLine = (line.length() * characterSize) + beginLineX;
                if (maxXofCurrentLine < offsetLocation.x) {
                    offsetLocation.x = maxXofCurrentLine;
                }
                objectPk = ((WebElementEntity) webElementTreeEntity.getObject()).getIdForDisplay();
                text.replaceTextRange(text.getOffsetAtLocation(offsetLocation), 0, createfindObjectStatement(objectPk));
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private int getCharacterSize() {
        Point currentCaret = text.getLocationAtOffset(0);
        Point nextCaret = text.getLocationAtOffset(1);
        return nextCaret.x - currentCaret.x;
    }

    private int getBeginLineX() {
        return text.getLocationAtOffset(0).x;
    }

    private Point convertMouseLocationToOffsetLocation(Point rawLocation, boolean isDrop) {
        Point newLocation = new Point(rawLocation.x, rawLocation.y);
        int lineHeigth = text.getLineHeight();
        newLocation.x = approximateLocationForOffset(newLocation.x, characterSize);
        int timeY = rawLocation.y / lineHeigth;
        if (isDrop && timeY == 0) {
            return newLocation;
        }
        newLocation.y = approximateLocationForOffset(newLocation.y, lineHeigth);
        return newLocation;
    }

    private String createfindObjectStatement(String objectPk) {
        StringBuilder builder = new StringBuilder();
        builder.append(MethodCallExpressionWrapper.FIND_TEST_OBJECT_METHOD_NAME);
        builder.append("('");
        builder.append(objectPk);
        builder.append("')");
        return builder.toString();
    }

    @Override
    public void dragLeave(DropTargetEvent event) {
    }

    @Override
    public void dropAccept(DropTargetEvent event) {

    }

    private int approximateLocationForOffset(int value, int size) {
        if (size == 0) {
            return value;
        }
        int remain = value % size;
        if (remain == 0) {
            return value;
        }
        if (remain < size / 2) {
            value -= remain;
        } else {
            int time = value / size;
            value = (time + 1) * size;
        }
        return value;
    }

}
