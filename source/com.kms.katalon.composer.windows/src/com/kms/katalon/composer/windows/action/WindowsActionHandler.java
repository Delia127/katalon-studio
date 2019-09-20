package com.kms.katalon.composer.windows.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.testcase.ast.dialogs.KeysInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.AnnonatedNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.SnapshotWindowsElement;
import com.kms.katalon.composer.windows.exception.WindowsComposerException;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.testobject.WindowsTestObject;
import com.kms.katalon.core.testobject.WindowsTestObject.LocatorStrategy;
import com.kms.katalon.core.windows.driver.WindowsSession;
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper;

public class WindowsActionHandler {

    private WindowsSession windowsSession;

    private WindowsAction action;

    public WindowsActionHandler(WindowsSession driver, WindowsAction action) {
        this.windowsSession = driver;
        this.action = action;
    }

    private WindowsTestObject findElement(CapturedWindowsElement targetElement) {
        if (targetElement == null) {
            return null;
        }
        WindowsTestObject testObject = new WindowsTestObject(targetElement.getName());
        testObject.setLocator(targetElement.getLocator());
        testObject.setLocatorStrategy(LocatorStrategy.valueOf(targetElement.getLocatorStrategy().name()));
        return testObject;
    }

    public WindowsActionMapping perform(SnapshotWindowsElement element, Shell activeShell)
            throws WindowsComposerException {
        switch (action) {
            case ClearText: {
                ClearTextActionHandler handler = new ClearTextActionHandler();
                return handler.perform(element, activeShell);
            }
            case Click: {
                ClickActionHandler handler = new ClickActionHandler();
                return handler.perform(element, activeShell);
            }
            case RightClick: {
                RightClickActionHandler handler = new RightClickActionHandler();
                return handler.perform(element, activeShell);
            }
            case CloseApplication: {
                CloseAppHandler handler = new CloseAppHandler();
                handler.perform(element, activeShell);
                return handler.getActionMapping();
            }
            case DoubleClick: {
                DoubleClickActionHandler handler = new DoubleClickActionHandler();
                handler.perform(element, activeShell);
                return handler.getActionMapping();
            }
            case GetText: {
                GetTextActionHandler handler = new GetTextActionHandler();
                return handler.perform(element, activeShell);
            }
            case SetText: {
                SetTextActionHandler handler = new SetTextActionHandler();
                return handler.perform(element, activeShell);
            }
            case SendKeys: {
                SendKeysActionHandler handler = new SendKeysActionHandler();
                return handler.perform(element, activeShell);
            }
            case SwitchToApplication: {
                SwitchToApplicationHandler handler = new SwitchToApplicationHandler();
                return handler.perform(element, activeShell);
            }
            case SwitchToDesktop: {
                SwitchToDesktopHandler handler = new SwitchToDesktopHandler();
                return handler.perform(element, activeShell);
            }
            default:
                return null;
        }
    }

    public class BaseActionHandler {
        protected SnapshotWindowsElement element;

        protected Shell activeShell;

        protected void performActionBeforeProgress() throws InterruptedException {

        }

        protected void performActionAfterProgress() throws InterruptedException {

        }

        protected void performAction() throws InvocationTargetException {

        }

        public WindowsActionMapping getActionMapping() {
            return new WindowsActionMapping(action, null);
        }

        protected IRunnableWithProgress getActionMappingProgress() {
            return new IRunnableWithProgress() {

                private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
                    if (monitor.isCanceled()) {
                        throw new InterruptedException("Operation has been canceled");
                    }
                }

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Running commands...", 1);
                        performAction();
                        checkMonitorCanceled(monitor);
                        monitor.worked(1);
                    } finally {
                        monitor.done();
                    }
                }
            };
        }

        public WindowsActionMapping perform(SnapshotWindowsElement element, Shell activeShell)
                throws WindowsComposerException {
            try {
                this.element = element;
                this.activeShell = activeShell;

                performActionBeforeProgress();
                final ProgressMonitorDialogWithThread progressDlg = new ProgressMonitorDialogWithThread(activeShell) {
                    @Override
                    public void cancelPressed() {
                        super.cancelPressed();
                        finishedRun();
                        getProgressMonitor().done();
                    }
                };
                IRunnableWithProgress runnable = getActionMappingProgress();
                progressDlg.run(true, false, runnable);
                performActionAfterProgress();

                return getActionMapping();
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ExecutionException) {
                    ExecutionException executionException = (ExecutionException) e.getTargetException();
                    Throwable cause = executionException.getCause();
                    if (cause instanceof StepFailedException) {
                        throw (StepFailedException) cause;
                    }
                    if (cause instanceof CancellationException) {
                        return null;
                    }
                    throw new WindowsComposerException(cause);
                }
                throw new WindowsComposerException(e.getTargetException());
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    public class BaseActionObjectHandler {
        protected SnapshotWindowsElement element;

        protected Shell activeShell;

        protected CapturedWindowsElement capturedElement;

        protected void performActionBeforeProgress() throws InterruptedException, InvocationTargetException {

        }

        protected void performActionAfterProgress() throws InterruptedException, InvocationTargetException {

        }

        protected void performAction(WindowsTestObject testObject)
                throws InterruptedException, InvocationTargetException {

        }

        public WindowsActionMapping getActionMapping() {
            return new WindowsActionMapping(action, capturedElement);
        }

        protected IRunnableWithProgress getActionMappingProgress() {
            return new IRunnableWithProgress() {

                private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
                    if (monitor.isCanceled()) {
                        throw new InterruptedException("Operation has been canceled");
                    }
                }

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Running commands...", 2);
                        capturedElement = element.newCapturedElement(windowsSession.getRunningDriver());
                        WindowsTestObject testObject = findElement(capturedElement);
                        monitor.worked(1);

                        checkMonitorCanceled(monitor);

                        performAction(testObject);
                        checkMonitorCanceled(monitor);
                        monitor.worked(1);
                    } finally {
                        monitor.done();
                    }
                }
            };
        }

        public WindowsActionMapping perform(SnapshotWindowsElement element, Shell activeShell)
                throws WindowsComposerException {
            try {
                this.element = element;
                this.activeShell = activeShell;

                performActionBeforeProgress();
                final ProgressMonitorDialogWithThread progressDlg = new ProgressMonitorDialogWithThread(activeShell) {
                    @Override
                    public void cancelPressed() {
                        super.cancelPressed();
                        finishedRun();
                        getProgressMonitor().done();
                    }
                };
                IRunnableWithProgress runnable = getActionMappingProgress();
                progressDlg.run(true, false, runnable);
                performActionAfterProgress();

                return getActionMapping();
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ExecutionException) {
                    ExecutionException executionException = (ExecutionException) e.getTargetException();
                    Throwable cause = executionException.getCause();
                    if (cause instanceof StepFailedException) {
                        throw (StepFailedException) cause;
                    }
                    if (cause instanceof CancellationException) {
                        return null;
                    }
                    throw new WindowsComposerException(cause);
                }
                throw new WindowsComposerException(e.getTargetException());
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    public class SetTextActionHandler extends BaseActionObjectHandler {
        private String textInput;

        @Override
        protected void performActionBeforeProgress() throws InterruptedException {
            SetTextDialog inputDialog = new SetTextDialog(activeShell);
            if (inputDialog.open() != InputDialog.OK) {
                throw new InterruptedException();
            }
            textInput = StringUtils.defaultString(inputDialog.text);
        }

        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).setText(testObject, textInput);
        }

        @Override
        public WindowsActionMapping getActionMapping() {
            WindowsActionMapping actionMapping = super.getActionMapping();
            actionMapping.getData()[0].setValue(new ConstantExpressionWrapper(textInput));
            return actionMapping;
        }
    }

    public class SendKeysActionHandler extends BaseActionObjectHandler {
        private MethodCallExpressionWrapper keysExpression = (MethodCallExpressionWrapper) GroovyWrapperParser
                .parseGroovyScriptAndGetFirstExpression("Keys.chord()");

        @Override
        protected void performActionBeforeProgress() throws InterruptedException {
            KeysInputBuilderDialog inputDialog = new KeysInputBuilderDialog(activeShell,
                    (MethodCallExpressionWrapper) getValue());
            if (inputDialog.open() != InputDialog.OK) {
                throw new InterruptedException();
            }
            keysExpression = inputDialog.getReturnValue();
        }

        private MethodCallExpressionWrapper getValue() {
            return keysExpression;
        }

        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).sendKeys(testObject, getKeys());
        }

        @SuppressWarnings("unchecked")
        private String getKeys() {
            String keys = new String();
            ArgumentListExpressionWrapper arguments = keysExpression.getArguments();

            List<AnnonatedNodeWrapper> children;
            boolean isSpecialKey = false;
            for (ExpressionWrapper expr : arguments.getExpressions()) {
                children = (List<AnnonatedNodeWrapper>) expr.getAstChildren();
                isSpecialKey = children.size() > 0;
                if (isSpecialKey) {
                    keys += children.stream().map(child -> {
                        if (child instanceof ConstantExpressionWrapper) {
                            String keyName = ((ConstantExpressionWrapper) child).getValue().toString();
                            return Keys.valueOf(keyName).toString();
                        }
                        return "";
                    }).collect(Collectors.joining());
                } else {
                    children = (List<AnnonatedNodeWrapper>) expr.getParent().getAstChildren();
                    keys += children.stream().map(child -> {
                        if (child instanceof ConstantExpressionWrapper) {
                            return ((ConstantExpressionWrapper) child).getValue().toString();
                        }
                        return "";
                    }).collect(Collectors.joining());
                }
            }

            return Keys.chord(keys);
        }

        @Override
        public WindowsActionMapping getActionMapping() {
            WindowsActionMapping actionMapping = super.getActionMapping();
            actionMapping.getData()[0].setValue(keysExpression);
            return actionMapping;
        }

    }

    public class ClearTextActionHandler extends BaseActionObjectHandler {
        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).clearText(testObject);
        }
    }

    public class ClickActionHandler extends BaseActionObjectHandler {
        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).click(testObject);
        }
    }

    public class DoubleClickActionHandler extends BaseActionObjectHandler {
        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).doubleClick(testObject);
        }
    }

    public class RightClickActionHandler extends BaseActionObjectHandler {
        @Override
        protected void performAction(WindowsTestObject testObject) {
            WindowsActionHelper.create(windowsSession).rightClick(testObject);
        }
    }

    public class CloseAppHandler extends BaseActionHandler {
        @Override
        protected void performAction() {
            WindowsActionHelper.create(windowsSession).closeApp();
        }
    }

    public class GetTextActionHandler extends BaseActionObjectHandler {
        private String text;

        @Override
        protected void performAction(WindowsTestObject testObject) {
            text = WindowsActionHelper.create(windowsSession).getText(testObject);
        }

        @Override
        protected void performActionAfterProgress() throws InterruptedException {
            GetTextDialog getTextActionDialog = new GetTextDialog(activeShell, text);
            if (getTextActionDialog.open() != GetTextDialog.OK) {
                throw new InterruptedException();
            }
        }
    }

    public class SwitchToDesktopHandler extends BaseActionHandler {
        @Override
        protected void performAction() throws InvocationTargetException {
            try {
                WindowsActionHelper.create(windowsSession).switchToDesktop();
            } catch (IOException | URISyntaxException e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    public class SwitchToApplicationHandler extends BaseActionHandler {
        @Override
        protected void performAction() {
            WindowsActionHelper.create(windowsSession).switchToApplication();
        }
    }

    private class GetTextDialog extends AbstractDialog {

        private Text txtText;

        private String text;

        protected GetTextDialog(Shell parentShell, String text) {
            super(parentShell, false);
            this.text = text;
        }

        @Override
        protected void registerControlModifyListeners() {
        }

        @Override
        protected void setInput() {
            txtText.setText(StringUtils.defaultIfEmpty(text, "<empty>"));
        }

        @Override
        protected Control createDialogContainer(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());

            Label lblText = new Label(composite, SWT.NONE);
            lblText.setText("Text is:");

            txtText = new Text(composite, SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
            txtText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            return composite;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, "Apply action", true);
            createButton(parent, IDialogConstants.CANCEL_ID, "Cancel action", false);
        }

        @Override
        protected Point getInitialSize() {
            return new Point(400, 250);
        }

        @Override
        public String getDialogTitle() {
            return "Get Text action";
        }
    }

    private class SetTextDialog extends AbstractDialog {

        private Text txtText;

        private String text;

        protected SetTextDialog(Shell parentShell) {
            super(parentShell, false);
        }

        @Override
        protected void registerControlModifyListeners() {
        }

        @Override
        protected void setInput() {
            txtText.forceFocus();
        }

        @Override
        protected Control createDialogContainer(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());

            Label lblText = new Label(composite, SWT.NONE);
            lblText.setText("Please input text value to set to element:");

            txtText = new Text(composite, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
            txtText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            return composite;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, "Apply action", true);
            createButton(parent, IDialogConstants.CANCEL_ID, "Cancel action", false);
        }

        @Override
        protected Point getInitialSize() {
            return new Point(400, 250);
        }

        @Override
        public String getDialogTitle() {
            return "Set Text action";
        }

        @Override
        protected void okPressed() {
            this.text = txtText.getText();
            super.okPressed();
        }
    }
}
