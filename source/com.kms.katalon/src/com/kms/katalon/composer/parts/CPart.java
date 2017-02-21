package com.kms.katalon.composer.parts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.handlers.IActionCommandMappingService;
import org.eclipse.ui.operations.OperationHistoryActionHandler;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.WorkbenchPart;

import com.kms.katalon.composer.components.operation.OperationExecutor;

/**
 * Custom class for a Part to utilise the global undo/redo action handler of Eclipse. Sub-classes of this class must
 * call {@link #initialize(MPart, EPartService) initialize} method in their @PostConstruct method and {@link #dispose()
 * dispose} in their @PreDestroy method
 *
 */
@SuppressWarnings("restriction")
public abstract class CPart extends WorkbenchPart {
    public class ActivePartExpression extends Expression {
        private EPartService partService;

        private MPart mPart;

        public ActivePartExpression(MPart mPart, EPartService partService) {
            this.partService = partService;
            this.mPart = mPart;
        }

        @Override
        public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
            if (mPart.equals(partService.getActivePart()) && !isModalDialogPresent()) {
                return EvaluationResult.TRUE;
            }
            return EvaluationResult.FALSE;
        }
        
        private boolean isModalDialogPresent() {
            IWorkbench workbench = PlatformUI.getWorkbench();
            return workbench.getDisplay().getActiveShell() != workbench.getActiveWorkbenchWindow().getShell();
        }
    }

    public class CPartSite extends PartSite {
        public CPartSite(CPart part, MPart mPart) {
            super(mPart, part, null, null);
        }
    }

    protected OperationExecutor operationExecutor;

    private Map<String, String> actionIdByCommandId = new HashMap<>();

    private Map<String, IHandlerActivation> activationsByActionId = new HashMap<>();

    private Expression expression;

    /**
     * Initialise this part with the associated MPart and the part service
     * 
     * @param mPart the MPart instance
     * @param partService a EPartService instance obtained from injection
     */
    public void initialize(MPart mPart, EPartService partService) {
        setSite(new CPartSite(this, mPart));
        operationExecutor = new OperationExecutor(this);
        expression = new ActivePartExpression(mPart, partService);
        createUndoRedoActions();
    }

    @Override
    public void createPartControl(Composite parent) {
        // Leave this method empty for legacy purpose
    }

    @Override
    public void setFocus() {
        // Leave this method empty for legacy purpose
    }

    private void createUndoRedoActions() {
        // Create the undo action
        OperationHistoryActionHandler undoAction = new UndoActionHandler(getSite(), operationExecutor.getUndoContext());
        undoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);
        setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);

        // Create the redo action.
        OperationHistoryActionHandler redoAction = new RedoActionHandler(getSite(), operationExecutor.getUndoContext());
        redoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);
        setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
    }

    /**
     * This method is copied from internal codes of org.eclipse.ui.SubActionBars and should not be re-factored
     */
    private void setGlobalActionHandler(String actionID, OperationHistoryActionHandler handler) {
        final IHandlerService service = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
        if (handler != null) {
            // Add a mapping from this action id to the command id.
            String commandId = null;
            final IActionCommandMappingService mappingService = (IActionCommandMappingService) PlatformUI.getWorkbench()
                    .getService(IActionCommandMappingService.class);
            if (mappingService != null) {
                commandId = mappingService.getCommandId(actionID);
            }
            if (commandId == null) {
                commandId = handler.getActionDefinitionId();
            }
            // Update the handler activations.

            if (activationsByActionId.containsKey(actionID)) {
                final Object value = activationsByActionId.remove(actionID);
                if (value instanceof IHandlerActivation) {
                    final IHandlerActivation activation = (IHandlerActivation) value;
                    actionIdByCommandId.remove(activation.getCommandId());
                    if (service != null) {
                        service.deactivateHandler(activation);
                    }
                    activation.getHandler().dispose();
                }
            } else if (commandId != null && actionIdByCommandId.containsKey(commandId)) {
                final Object value = activationsByActionId.remove(actionIdByCommandId.remove(commandId));
                if (value instanceof IHandlerActivation) {
                    final IHandlerActivation activation = (IHandlerActivation) value;
                    if (service != null) {
                        service.deactivateHandler(activation);
                    }
                    activation.getHandler().dispose();
                }
            }

            if (commandId != null) {
                actionIdByCommandId.put(commandId, actionID);
                // Register this as a handler with the given definition id.
                // the expression gives the setGlobalActionHandler() a
                // priority.
                final IHandler actionHandler = new ActionHandler(handler);
                Expression handlerExpression = expression;
                if (service != null) {
                    final IHandlerActivation activation = service.activateHandler(commandId, actionHandler,
                            handlerExpression);
                    activationsByActionId.put(actionID, activation);
                }
            }
        } else if ((activationsByActionId != null) && (activationsByActionId.containsKey(actionID))) {
            final Object value = activationsByActionId.remove(actionID);
            if (value instanceof IHandlerActivation) {
                final IHandlerActivation activation = (IHandlerActivation) value;
                actionIdByCommandId.remove(activation.getCommandId());
                service.deactivateHandler(activation);
                activation.getHandler().dispose();
            }
        }
    }

    public IStatus executeOperation(IUndoableOperation operation, IProgressMonitor progressMonitor,
            IAdaptable adaptable) {
        return operationExecutor.executeOperation(operation, progressMonitor, adaptable);
    }

    public IStatus executeOperation(IUndoableOperation operation, IAdaptable adaptable) {
        return executeOperation(operation, new NullProgressMonitor(), adaptable);
    }

    public IStatus executeOperation(IUndoableOperation operation) {
        return executeOperation(operation, null);
    }

    public IStatus undoOperation(IAdaptable adaptable, IProgressMonitor progressMonitor) {
        return operationExecutor.undoOperation(adaptable, progressMonitor);
    }

    public IStatus undoOperation(IAdaptable adaptable) {
        return undoOperation(adaptable, new NullProgressMonitor());
    }

    public IStatus undoOperation() {
        return undoOperation(null);
    }

    public IStatus redoOperation(IAdaptable adaptable, IProgressMonitor progressMonitor) {
        return operationExecutor.undoOperation(adaptable, progressMonitor);
    }

    public IStatus redoOperation(IAdaptable adaptable) {
        return redoOperation(adaptable, new NullProgressMonitor());
    }

    public IStatus redoOperation() {
        return redoOperation(null);
    }
    
    public void clearHistory() {
        operationExecutor.refresh();
        createUndoRedoActions();
    }
}
