package com.kms.katalon.composer.execution.debug;

import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.jdt.internal.debug.ui.JavaWatchExpressionDelegate;

@SuppressWarnings("restriction")
public class WatchExpressionDelegate extends JavaWatchExpressionDelegate {

    @Override
    public void evaluateExpression(String expression, IDebugElement context, final IWatchExpressionListener listener) {
        super.evaluateExpression(expression, context, listener);
    }
}
