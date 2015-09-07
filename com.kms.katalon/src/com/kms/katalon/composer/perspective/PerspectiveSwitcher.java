package com.kms.katalon.composer.perspective;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.e4.compatibility.ModeledPageLayout;
import org.eclipse.ui.internal.menus.MenuHelper;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;

@SuppressWarnings("restriction")
public class PerspectiveSwitcher {

	@Inject
	IEventBroker eventBroker;

	@Inject
	EModelService modelService;

	@Inject
	private MWindow window;

	@Inject
	private EPartService partService;

	@Inject
	private MApplication application;

	private MToolControl psME;
	private ToolBar toolbar;

	@PostConstruct
	void init() {
		eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT, selectionHandler);
	}

	private EventHandler selectionHandler = new EventHandler() {
		@Override
		public void handleEvent(Event event) {
			if (toolbar.isDisposed()) {
				return;
			}

			MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);

			if (psME == null || !(changedElement instanceof MPerspectiveStack)) return;

			MWindow perspWin = modelService.getTopLevelWindowFor(changedElement);
			MWindow switcherWin = modelService.getTopLevelWindowFor(psME);
			if (perspWin != switcherWin) return;

			MPerspectiveStack perspStack = (MPerspectiveStack) changedElement;
			if (!perspStack.isToBeRendered()) return;

			MPerspective selElement = perspStack.getSelectedElement();
			for (ToolItem ti : toolbar.getItems()) {
				ti.setSelection(ti.getData() == selElement);
			}
		}
	};

	private void addPerspectiveItem(MPerspective persp) {
		ToolItem tltmNewItem = new ToolItem(toolbar, SWT.CHECK);

		tltmNewItem.setToolTipText(persp.getTooltip());

		if (persp.getIconURI() != null || !persp.getIconURI().isEmpty()) {
			try {
				ImageDescriptor image = ImageDescriptor.createFromURL(new URL(persp.getIconURI()));
				if (image != null) {
					tltmNewItem.setImage(image.createImage());
				} else {
					tltmNewItem.setText(persp.getLabel());
				}

			} catch (MalformedURLException e) {
				tltmNewItem.setText(persp.getLabel());
			}
		} else {
			tltmNewItem.setText(persp.getLabel());
		}

		tltmNewItem.setData(persp);

		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ToolItem selectedItem = (ToolItem) e.getSource();
				if (!selectedItem.getSelection()) {
					selectedItem.setSelection(true);
					return;
				}
				activePerspective(selectedItem);
			}

		});
	}

	/**
	 * Active a perspective that is data of the given toolItem
	 * 
	 * @param toolItem
	 */
	private void activePerspective(ToolItem toolItem) {

		for (ToolItem childItem : toolbar.getItems()) {
			if (!childItem.equals(toolItem)) {
				childItem.setSelection(false);
			}
		}

		MPerspective perspective = (MPerspective) toolItem.getData();

		MPerspectiveStack stack = getPerspectiveStack();	
		stack.setSelectedElement(perspective);

		// remove redundancy tool items
		MTrimBar toolControl = (MTrimBar) ModelServiceSingleton.getInstance().getModelService()
				.find("org.eclipse.ui.main.toolbar", ApplicationSingleton.getInstance().getApplication());
	
		toolControl.getChildren().removeAll(toolControl.getPendingCleanup());
	}

	private MPerspectiveStack getPerspectiveStack() {
		List<MPerspectiveStack> psList = modelService.findElements(window, null, MPerspectiveStack.class, null);
		if (psList.size() > 0) return psList.get(0);
		return null;
	}

	@PostConstruct
	void createWidget(Composite parent, MToolControl toolControl) {
		psME = toolControl;
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.heightHint = 24;
		label.setLayoutData(gd_label);
		toolbar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);

		MPerspectiveStack stack = getPerspectiveStack();

		// add debug perspective into the current perspectiveStack
		// IPerspectiveRegistry perspectiveRegistry =
		// PlatformUI.getWorkbench().getPerspectiveRegistry();
		// IPerspectiveDescriptor personalPerspectiveDescriptor =
		// perspectiveRegistry
		// .findPerspectiveWithId("org.eclipse.debug.ui.DebugPerspective");
		//
		// addPerspective(personalPerspectiveDescriptor, stack);
		if (stack != null) {
			// Create an item for each perspective that should show up
			for (MPerspective persp : stack.getChildren()) {
				if (persp.isToBeRendered()) {
					addPerspectiveItem(persp);
				}
			}
		}
		toolbar.getItems()[0].setSelection(true);
	}

	@SuppressWarnings("unused")
	private void addPerspective(IPerspectiveDescriptor perspective, MPerspectiveStack perspectives) {
		MPerspective modelPerspective = (MPerspective) modelService.cloneSnippet(application, perspective.getId(),
				window);

		if (modelPerspective == null) {

			// couldn't find the perspective, create a new one
			modelPerspective = modelService.createModelElement(MPerspective.class);

			// tag it with the same id
			modelPerspective.setElementId(perspective.getId());

			// instantiate the perspective
			IPerspectiveFactory factory = ((PerspectiveDescriptor) perspective).createFactory();
			ModeledPageLayout modelLayout = new ModeledPageLayout(window, modelService, partService, modelPerspective,
					perspective, (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
					true);
			factory.createInitialLayout(modelLayout);
		}

		modelPerspective.setLabel(perspective.getLabel());
		modelPerspective.setTooltip(perspective.getLabel());

		ImageDescriptor imageDescriptor = perspective.getImageDescriptor();
		if (imageDescriptor != null) {
			String imageURL = MenuHelper.getImageUrl(imageDescriptor);
			modelPerspective.setIconURI(imageURL);
		}

		// Hide place-holders for parts that exist in the 'global' areas
		modelService.hideLocalPlaceholders(window, modelPerspective);

		// add it to the stack
		perspectives.getChildren().add(modelPerspective);

	}
}
