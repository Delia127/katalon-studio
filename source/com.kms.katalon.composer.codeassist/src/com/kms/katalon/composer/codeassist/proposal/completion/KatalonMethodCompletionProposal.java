package com.kms.katalon.composer.codeassist.proposal.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.template.java.SignatureUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.ParameterGuesser;
import org.eclipse.jdt.internal.ui.text.java.ParameterGuessingProposal;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.InclusivePositionUpdater;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.execution.setting.TestCaseSettingStore;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings({ "restriction" })
public class KatalonMethodCompletionProposal extends ParameterGuessingProposal {

    private MethodNode methodNode;
    private JavaContentAssistInvocationContext javaContext;
    private ContentAssistContext assistContext;

    private ICompletionProposal[][] fChoices; // initialized by
                                              // guessParameters()
    private Position[] fPositions; // initialized by guessParameters()

    private IRegion fSelectedRegion; // initialized by apply()
    private IPositionUpdater fUpdater;

    public ContentAssistContext getAssistContext() {
        return assistContext;
    }

    public void setAssistContext(ContentAssistContext assistContext) {
        this.assistContext = assistContext;
    }

    public MethodNode getMethodNode() {
        return methodNode;
    }

    public void setMethodNode(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public JavaContentAssistInvocationContext getJavaContext() {
        return javaContext;
    }

    public void setJavaContext(JavaContentAssistInvocationContext javaContext) {
        this.javaContext = javaContext;
    }

    public KatalonMethodCompletionProposal(GroovyCompletionProposal proposal,
            JavaContentAssistInvocationContext context, ContentAssistContext assistContext, boolean fillBestGuess,
            MethodNode methodNode) {
        super(proposal, context, context.getCoreContext(), false);
        setMethodNode(methodNode);
        setJavaContext(context);
        setAssistContext(assistContext);
    }

    public static KatalonMethodCompletionProposal createProposal(GroovyCompletionProposal proposal,
            JavaContentAssistInvocationContext javaContext, ContentAssistContext assistContext, boolean fillBestGuess,
            MethodNode methodNode) {
        CompletionContext coreContext = javaContext.getCoreContext();
        if (coreContext != null && coreContext.isExtended()) {
            return new KatalonMethodCompletionProposal(proposal, javaContext, assistContext, fillBestGuess, methodNode);
        }
        return null;
    }

    @Override
    public Point getSelection(IDocument document) {
        if (fSelectedRegion == null) {
            return new Point(getReplacementOffset(), 0);
        }

        return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
    }

    @Override
    public void apply(final IDocument document, char trigger, int offset) {
        try {
            super.apply(document, trigger, offset);

            int baseOffset = getReplacementOffset();
            String replacement = getReplacementString();

            if (ArrayUtils.isNotEmpty(fPositions) && getTextViewer() != null) {

                LinkedModeModel model = new LinkedModeModel();

                for (int i = 0; i < fPositions.length; i++) {
                    LinkedPositionGroup group = new LinkedPositionGroup();
                    Position fPosition = fPositions[i];
                    int positionOffset = fPosition.getOffset();
                    int positionLength = fPosition.getLength();

                    if (fChoices[i].length < 2) {
                        group.addPosition(new LinkedPosition(document, positionOffset, positionLength,
                                LinkedPositionGroup.NO_STOP));
                    } else {
                        ensurePositionCategoryInstalled(document, model);
                        document.addPosition(getCategory(), fPosition);
                        group.addPosition(new ProposalPosition(document, positionOffset, positionLength,
                                LinkedPositionGroup.NO_STOP, fChoices[i]));
                    }
                    model.addGroup(group);
                }

                model.forceInstall();
                JavaEditor editor = getJavaEditor();
                if (editor != null) {
                    model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
                }

                LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
                ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
                // exit character can be either ')' or ';'
                final char exitChar = replacement.charAt(replacement.length() - 1);
                ui.setExitPolicy(new ExitPolicy(exitChar, document) {
                    @Override
                    public ExitFlags doExit(LinkedModeModel model2, VerifyEvent event, int offset2, int length) {
                        if (event.character == ',') {
                            for (int i = 0; i < fPositions.length - 1; i++) { // not for the last one
                                Position position = fPositions[i];
                                if (position.offset <= offset2 && offset2 + length <= position.offset + position.length) {
                                    try {
                                        ITypedRegion partition = TextUtilities.getPartition(document,
                                                IJavaPartitions.JAVA_PARTITIONING, offset2 + length, false);
                                        if (IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())
                                                || offset2 + length == partition.getOffset() + partition.getLength()) {
                                            event.character = '\t';
                                            event.keyCode = SWT.TAB;
                                            return null;
                                        }
                                    } catch (BadLocationException e) {
                                        // continue; not serious enough to log
                                    }
                                }
                            }
                        } else if (event.character == ')' && exitChar != ')') {
                            // exit from link mode when user is in the last ')'
                            // position.
                            Position position = fPositions[fPositions.length - 1];
                            if (position.offset <= offset2 && offset2 + length <= position.offset + position.length) {
                                return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
                            }
                        }
                        return super.doExit(model2, event, offset2, length);
                    }
                });
                ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
                ui.setDoContextInfo(true);
                ui.enter();
                fSelectedRegion = ui.getSelectedRegion();

            } else {
                fSelectedRegion = new Region(baseOffset + replacement.length(), 0);
            }

        } catch (BadLocationException | BadPositionCategoryException e) {
            LoggerSingleton.logError(e);
        }
    }

    private JavaEditor getJavaEditor() {
        IEditorPart part = JavaPlugin.getActivePage().getActiveEditor();
        if (part instanceof JavaEditor) {
            return (JavaEditor) part;
        } else {
            return null;
        }
    }

    private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
        if (!document.containsPositionCategory(getCategory())) {
            document.addPositionCategory(getCategory());
            fUpdater = new InclusivePositionUpdater(getCategory());
            document.addPositionUpdater(fUpdater);

            model.addLinkingListener(new ILinkedModeListener() {

                /*
                 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.
                 * eclipse.jface.text.link.LinkedModeModel, int)
                 */
                public void left(LinkedModeModel environment, int flags) {
                    ensurePositionCategoryRemoved(document);
                }

                public void suspend(LinkedModeModel environment) {
                }

                public void resume(LinkedModeModel environment, int flags) {
                }
            });
        }
    }

    @Override
    protected String computeReplacementString() {
        try {
            return computeGuessingCompletion();
        } catch (JavaModelException e) {
            return "";
        }
    }

    private String computeGuessingCompletion() throws JavaModelException {
        StringBuffer buffer = new StringBuffer();
        appendMethodNameReplacement(buffer);

        FormatterPrefs prefs = getFormatterPrefs();

        setCursorPosition(buffer.length());

        if (prefs.afterOpeningParen) {
            buffer.append(SPACE);
        }

        char[][] parameterNames = fProposal.findParameterNames(null);

        fChoices = guessParameters(parameterNames);
        int count = fChoices.length;
        int replacementOffset = getReplacementOffset();

        for (int i = 0; i < count; i++) {
            if (i != 0) {
                if (prefs.beforeComma) {
                    buffer.append(SPACE);
                }
                
                buffer.append(COMMA);
                
                if (prefs.afterComma) {
                    buffer.append(SPACE);
                }
            }

            ICompletionProposal proposal = null;

            if (FailureHandling.class.getName().equals(getParameterTypes()[i])) {
                proposal = getDefaultFailureHandlingProposal(fChoices[i]);
            } else {
                proposal = fChoices[i][0];
            }

            String argument = proposal.getDisplayString();

            Position position = fPositions[i];
            position.setOffset(replacementOffset + buffer.length());
            position.setLength(argument.length());

            if (proposal instanceof JavaCompletionProposal)
                ((JavaCompletionProposal) proposal).setReplacementOffset(replacementOffset + buffer.length());
            buffer.append(argument);
        }

        if (prefs.beforeClosingParen) {
            buffer.append(SPACE);
        }

        buffer.append(RPAREN);

        return buffer.toString();
    }

    private ICompletionProposal[][] guessParameters(char[][] parameterNames) throws JavaModelException {
        int count = parameterNames.length;
        fPositions = new Position[count];
        fChoices = new ICompletionProposal[count][];

        String[] parameterTypes = getParameterTypes();
        ParameterGuesser guesser = new ParameterGuesser(getJavaContext().getCoreContext().getEnclosingElement());
        IJavaElement[][] assignableElements = getAssignableElements();

        for (int i = count - 1; i >= 0; i--) {
            String paramName = new String(parameterNames[i]);
            Position position = new Position(0, 0);
            boolean isLastParameter = i == count - 1;
            ICompletionProposal[] argumentProposals = null;
            ICompletionProposal[] parentGuessingProposal = guesser.parameterProposals(parameterTypes[i], paramName,
                    position, assignableElements[i], true, isLastParameter);
            if (FailureHandling.class.getName().equals(parameterTypes[i])) {
                argumentProposals = getFailureHandlingProposals(parentGuessingProposal, position);
            } else {
                argumentProposals = parentGuessingProposal;

                if (argumentProposals.length == 0) {
                    JavaCompletionProposal proposal = new JavaCompletionProposal(paramName, 0, paramName.length(),
                            null, paramName, 0);
                    if (isLastParameter) {
                        proposal.setTriggerCharacters(new char[] { ',' });
                    }
                    argumentProposals = new ICompletionProposal[] { proposal };
                }
            }

            fPositions[i] = position;
            fChoices[i] = argumentProposals;
        }

        return fChoices;
    }

    private ICompletionProposal[] getFailureHandlingProposals(ICompletionProposal[] parentGuessingProposal,
            Position position) {
        List<ICompletionProposal> argumentProposals = new ArrayList<ICompletionProposal>();
        List<String> stringContains = new ArrayList<String>();
        String[] falureHandlingValues = FailureHandling.valueStrings();
        Arrays.sort(falureHandlingValues);

        for (int index = 0; index < parentGuessingProposal.length; index++) {
            PositionBasedCompletionProposal guessingProposal = (PositionBasedCompletionProposal) parentGuessingProposal[index];
            String newReplecementString = "";

            if (Arrays.binarySearch(falureHandlingValues, guessingProposal.getDisplayString()) > -1) {
                newReplecementString = FailureHandling.class.getSimpleName() + "."
                        + guessingProposal.getDisplayString();
            } else {
                newReplecementString = guessingProposal.getDisplayString();
            }

            KatalonFailureHandlingCompletionProposal failureHandlingProposal = new KatalonFailureHandlingCompletionProposal(
                    newReplecementString, position, newReplecementString.length(), guessingProposal.getImage(),
                    newReplecementString, guessingProposal.getContextInformation(),
                    guessingProposal.getAdditionalProposalInfo(), guessingProposal.getTriggerCharacters());

            if (!stringContains.contains(newReplecementString)) {
                stringContains.add(newReplecementString);
                argumentProposals.add(failureHandlingProposal);
            }
        }

        return argumentProposals.toArray(new ICompletionProposal[0]);
    }

    private ICompletionProposal getDefaultFailureHandlingProposal(ICompletionProposal[] guessingCompletionProposals) {
        FailureHandling defaultFailureHandling = new TestCaseSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation()).getDefaultFailureHandling();

        if (defaultFailureHandling == null)  {
            return guessingCompletionProposals[0];
        }
        
        String defaultFailureHandlingName = defaultFailureHandling.getDeclaringClass().getSimpleName() + "."
                + defaultFailureHandling.name();

        for (ICompletionProposal suggesingProposal : guessingCompletionProposals) {
            if (defaultFailureHandlingName.equals(suggesingProposal.getDisplayString())) {
                return suggesingProposal;
            }
        }

        return guessingCompletionProposals[0];
    }

    /**
     * @see ParameterGuessingProposal#getParameterTypes
     * @return
     */
    private String[] getParameterTypes() {
        char[] signature = SignatureUtil.fix83600(fProposal.getSignature());
        char[][] types = Signature.getParameterTypes(signature);

        String[] ret = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            ret[i] = new String(Signature.toCharArray(types[i]));
        }
        return ret;
    }

    private IJavaElement[][] getAssignableElements() {
        char[] signature = SignatureUtil.fix83600(getProposal().getSignature());
        char[][] types = Signature.getParameterTypes(signature);

        IJavaElement[][] assignableElements = new IJavaElement[types.length][];
        for (int i = 0; i < types.length; i++) {
            assignableElements[i] = getJavaContext().getCoreContext().getVisibleElements(new String(types[i]));
        }
        return assignableElements;
    }

    /**
     * If <code>methodNode</code> is a method of <code>CustomKeywords</code> enclose its name by two single quotes.
     * Otherwise, use default.
     * 
     * @param buffer
     */
    @Override
    protected void appendMethodNameReplacement(StringBuffer buffer) {
        super.appendMethodNameReplacement(buffer);

        if (isCustomKeywordMethodNode()) {
            buffer.insert(0, "\'");
            buffer.insert(buffer.toString().lastIndexOf(LPAREN), "\'");
        }
    }

    private boolean isCustomKeywordMethodNode() {
        return GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME.equals(methodNode.getDeclaringClass().getName());
    }

    @Override
    protected StyledString computeDisplayString() {
        return super.computeDisplayString().append(getStyledGroovy());
    }

    /**
     * Add Katalon signature for the proposal
     * 
     * @return a {@link StyledString} contains Katalon signature
     */
    private StyledString getStyledGroovy() {
        try {
            String className = methodNode.getDeclaringClass().getName();
            String methodName = methodNode.getName();
            if (GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME.equals(className)
                    || (KeywordController.getInstance().getBuiltInKeywordByName(className, methodName)) != null) {
                return KatalonContextUtil.getKatalonSignature();
            }
        } catch (Exception e) {
            // Cannot find keyword, return empty string
        }
        return new StyledString("");
    }

    private String getCategory() {
        return "KatalonMethodCompletionProposal_" + toString();
    }

    private void ensurePositionCategoryRemoved(IDocument document) {
        if (document.containsPositionCategory(getCategory())) {
            try {
                document.removePositionCategory(getCategory());
            } catch (BadPositionCategoryException e) {
                // ignore
            }
            document.removePositionUpdater(fUpdater);
        }
    }

}
