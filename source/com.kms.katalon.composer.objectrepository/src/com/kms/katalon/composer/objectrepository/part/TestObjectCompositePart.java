package com.kms.katalon.composer.objectrepository.part;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectCompositePart implements EventHandler, IComposerPartEvent, SavableCompositePart {

	private static final int CHILD_TEST_OBJECT_PART_INDEX = 0;

	private static final int CHILD_TEST_OBJECT_INTEGRATION_PART_INDEX = 1;

	private static final String TEST_OBJECT_TITLE = StringConstants.PA_TEST_OBJECT_PART_TITLE;

	private static final String TEST_OBJECT_INTEGRATION_TITLE = StringConstants.PA_TEST_OBJECT_INTEGRATION_PART_TITLE;

	@Inject
	protected EModelService modelService;

	@Inject
	protected MApplication application;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private EPartService partService;

	@Inject
	private MDirtyable dirtyable;

	public MDirtyable getDirty() {
		return dirtyable;
	}

	private CTabFolder tabFolder;

	private MPartStack subPartStack;

	private WebElementEntity originalTestObject;

	private MCompositePart compositePart;

	private TestObjectPart testObjectPart;

	private TestObjectIntegrationPart testObjectIntegrationPart;

	private boolean disposed = false;

	@PostConstruct
	public void init(Composite parent, MCompositePart compositePart) {
		this.compositePart = compositePart;
		dirtyable.setDirty(false);
		initListeners();
	}

	private void initListeners() {
		eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
		eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
	}

	public WebElementEntity getTestObject() {
		return originalTestObject;
	}

	public void initComponent() {
		List<MPartSashContainerElement> compositePartChildren = compositePart.getChildren();
		if (compositePartChildren.size() == 1 && compositePartChildren.get(0) instanceof MPartStack) {
			subPartStack = (MPartStack) compositePartChildren.get(0);
			if (subPartStack.getChildren().size() == 2) {
				for (MStackElement stackElement : subPartStack.getChildren()) {
					if (!(stackElement instanceof MPart)) {
						continue;
					}

					Object partObject = ((MPart) stackElement).getObject();

					if (partObject instanceof TestObjectPart) {
						testObjectPart = (TestObjectPart) partObject;
						continue;
					}

					if (partObject instanceof TestObjectIntegrationPart) {
						testObjectIntegrationPart = (TestObjectIntegrationPart) partObject;
						continue;
					}
				}
			}

			if (subPartStack.getWidget() instanceof CTabFolder) {
				tabFolder = (CTabFolder) subPartStack.getWidget();

				tabFolder.setTabPosition(SWT.BOTTOM);
				tabFolder.setBorderVisible(false);
				tabFolder.setMaximizeVisible(false);
				tabFolder.setMinimizeVisible(false);

				if (tabFolder.getItemCount() == 2) {
					CTabItem testCasePartTab = tabFolder.getItem(CHILD_TEST_OBJECT_PART_INDEX);
					testCasePartTab.setText(TEST_OBJECT_TITLE);
					testCasePartTab.setImage(ImageConstants.IMG_16_MANUAL);
					testCasePartTab.setShowClose(false);

					CTabItem groovyEditorPartTab = tabFolder.getItem(CHILD_TEST_OBJECT_INTEGRATION_PART_INDEX);
					groovyEditorPartTab.setText(TEST_OBJECT_INTEGRATION_TITLE);
					groovyEditorPartTab.setImage(ImageConstants.IMG_16_SCRIPT);
					groovyEditorPartTab.setShowClose(false);
				}

				tabFolder.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						if (tabFolder == null || testObjectPart == null) {
							return;
						}
					}

				});
				tabFolder.layout();
			}
		}
	}

	private MPart getTestObjectPart() {
		return testObjectPart.getPart();
	}

	private MPart getTestObjectIntegrationPart() {
		return testObjectIntegrationPart.getPart();
	}

	@Override
	public String getEntityId() {
		return getTestObject().getIdForDisplay();
	}

	@Override
	@Inject
	@Optional
	public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
		MPart part = EventUtil.getPart(event);
		if (part == null || !StringUtils.startsWith(part.getElementId(),
				EntityPartUtil.getTestCaseCompositePartId(originalTestObject.getId()))) {
			return;
		}

		EventUtil.post(EventConstants.PROPERTIES_ENTITY, originalTestObject);
	}

	@Override
	@Inject
	@Optional
	public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
		testObjectPart.onChangeEntityProperties(event);
	}

	@Override
	@PreDestroy
	public void onClose() {
		EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
			try {
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object instanceof WebElementTreeEntity) {
					handleTestObjectRefreshed(((WebElementTreeEntity) object).getObject());
				} else if (object instanceof FolderTreeEntity) {
					handleFolderRefreshed(((FolderTreeEntity) object).getObject());
				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		} else {
			testObjectPart.handleEvent(event);
		}
	}

	private void handleFolderRefreshed(FolderEntity folder) throws Exception {
		WebElementEntity currentTestObject = getTestObject();
		if (folder != null && FolderController.getInstance().isFolderAncestorOfEntity(folder, currentTestObject)
				&& isTestObjectDisposed(currentTestObject)) {
			dispose();
		}
	}

	private void handleTestObjectRefreshed(Object obj) throws Exception {
		WebElementEntity testObject = (WebElementEntity) obj;
		WebElementEntity currentTestCase = getTestObject();
		if (testObject == null || !testObject.getId().equals(currentTestCase.getId())) {
			return;
		}
		if (isTestObjectDisposed(testObject)) {
			dispose();
			return;
		}
		refresh(testObject);
	}

	private boolean isTestObjectDisposed(WebElementEntity testObject) throws Exception {
		return TestCaseController.getInstance().getTestCase(testObject.getId()) == null;
	}

	private void refresh(WebElementEntity testObject) throws Exception {
		if (dirtyable.isDirty()) {
			// do not refresh the modifying test case(s)
			return;
		}
	}

	private void dispose() {
		MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
		mStackPart.getChildren().remove(compositePart);
		eventBroker.unsubscribe(this);
		disposed = true;
	}

	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public List<MPart> getChildParts() {
		List<MPart> childrenParts = new ArrayList<MPart>();
		childrenParts.add(getTestObjectPart());
		childrenParts.add(getTestObjectIntegrationPart());
		return childrenParts;
	}

	@Override
	public void save() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDirty(boolean dirty) {
		dirtyable.setDirty(dirty);
	}

	@Override
	public boolean isDirty() {
		return compositePart.isDirty();
	}

}
