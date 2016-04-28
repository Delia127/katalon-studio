package com.kms.katalon.composer.execution.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.hcr.JavaHotCodeReplaceManager;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.launcher.model.LaunchMode;

@SuppressWarnings("restriction")
public class LaunchDelegate extends JavaLaunchDelegate {

    public LaunchDelegate() {
        DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(JavaHotCodeReplaceManager.getDefault());
        JDIDebugModel.addJavaBreakpointListener(new BreakpointListener());
        setDefaultSwitchedPerspectiveWhenBreakpointHit();
    }

    private void setDefaultSwitchedPerspectiveWhenBreakpointHit() {
        DebugUIPlugin.getDefault()
                .getPerspectiveManager()
                .setLaunchPerspective(getLaunchConfigurationType(), LaunchMode.DEBUG.toString(),
                        IdConstants.DEBUG_PERSPECTIVE_ID);
    }

    private ILaunchConfigurationType getLaunchConfigurationType() {
        return getLaunchManager().getLaunchConfigurationType(StringConstants.LAUNCH_CONFIGURATION_TYPE_ID);
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        launch.setSourceLocator(new SourceLocator());
        super.launch(configuration, mode, launch, monitor);
    }
}
