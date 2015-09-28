package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.keywords.BuiltinKeywordFolderBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.CustomKeywordFolderBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntityTransfer;
import com.kms.katalon.composer.testcase.keywords.KeywordFolderBrowserTreeEntity;
import com.kms.katalon.composer.testcase.providers.KeywordBrowserEntityViewerFilter;
import com.kms.katalon.composer.testcase.providers.KeywordTreeContentProvider;
import com.kms.katalon.composer.testcase.providers.KeywordTreeLabelProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.IKeywordContributor;

public class KeywordBrowserPart implements EventHandler {
	private static final String SEARCH_TEXT_DEFAULT_VALUE = "Enter text to search...";

	private TreeViewer treeViewer;

	private Text txtSearchInput;
	private KeywordTreeLabelProvider labelProvider;
	private KeywordBrowserEntityViewerFilter viewerFilter;

	@Inject
	private IEventBroker eventBroker;

	@PostConstruct
	public void init(Composite parent, MPart mpart) {
		createControls(parent);
		registerListerners();
		hookDragEvent();
		loadTreeData();
	}

	private void registerListerners() {
		eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
	}

	private void hookDragEvent() {
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		DragSource dragSource = new DragSource(treeViewer.getTree(), operations);
		dragSource.setTransfer(new Transfer[] { new KeywordBrowserTreeEntityTransfer() });
		dragSource.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				List<KeywordBrowserTreeEntity> treeEntities = getKeywordTreeEntityFromTree();
				if (treeEntities.size() > 0) {
					event.doit = true;
				} else {
					event.doit = false;
				}
			}

			public void dragSetData(DragSourceEvent event) {
				List<KeywordBrowserTreeEntity> treeEntities = getKeywordTreeEntityFromTree();
				if (treeEntities.size() > 0) {
					event.data = treeEntities.toArray(new KeywordBrowserTreeEntity[treeEntities.size()]);
				}
			}

			public void dragFinished(DragSourceEvent event) {
			}
		});
	}

	private List<KeywordBrowserTreeEntity> getKeywordTreeEntityFromTree() {
		TreeItem[] selection = treeViewer.getTree().getSelection();
		List<KeywordBrowserTreeEntity> treeEntities = new ArrayList<KeywordBrowserTreeEntity>();
		for (TreeItem item : selection) {
			if (item.getData() instanceof KeywordBrowserTreeEntity) {
				treeEntities.add((KeywordBrowserTreeEntity) item.getData());
			}
		}
		return treeEntities;
	};

	private void createControls(Composite parent) {
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(1, false));

		Composite searchComposite = new Composite(parent, SWT.BORDER);
		searchComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
		GridLayout glSearchComposite = new GridLayout(1, false);
		glSearchComposite.verticalSpacing = 0;
		glSearchComposite.horizontalSpacing = 0;
		glSearchComposite.marginWidth = 0;
		glSearchComposite.marginHeight = 0;
		searchComposite.setLayout(glSearchComposite);

		GridData grSearchComposite = new GridData(GridData.FILL_HORIZONTAL);
		grSearchComposite.heightHint = 24;
		searchComposite.setLayoutData(grSearchComposite);

		txtSearchInput = new Text(searchComposite, SWT.NONE);
		txtSearchInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		txtSearchInput.setMessage(SEARCH_TEXT_DEFAULT_VALUE);

		GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
		gdTxtInput.grabExcessVerticalSpace = true;
		gdTxtInput.verticalAlignment = SWT.CENTER;
		txtSearchInput.setLayoutData(gdTxtInput);
		txtSearchInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filterKeywordTreeEntitiesBySearchedText();
			}
		});

		txtSearchInput.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					filterKeywordTreeEntitiesBySearchedText();
				}
			}
		});

		treeViewer = new TreeViewer(parent, SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		treeViewer.setContentProvider(new KeywordTreeContentProvider());
		treeViewer.setLabelProvider(labelProvider = new KeywordTreeLabelProvider());
		treeViewer.addFilter(viewerFilter = new KeywordBrowserEntityViewerFilter());

	}

	protected void filterKeywordTreeEntitiesBySearchedText() {
		if (treeViewer.getTree().isDisposed()) {
			return;
		}
		final String searchString = txtSearchInput.getText();
		while (treeViewer.isBusy()) {
			// wait for tree is not busy
		}
		BusyIndicator.showWhile(treeViewer.getTree().getDisplay(), new Runnable() {

			@Override
			public void run() {
				try {
					if (searchString.equals(txtSearchInput.getText()) && treeViewer.getInput() != null) {
						treeViewer.getTree().setRedraw(false);
						labelProvider.setSearchString(searchString);
						viewerFilter.setSearchString(searchString);
						treeViewer.refresh(true);
						if (searchString != null && !searchString.isEmpty()) {
							treeViewer.expandAll();
						} else {
							treeViewer.collapseAll();
						}
					}
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				} finally {
					treeViewer.getTree().setRedraw(true);
				}
			}
		});
	}

	protected void loadTreeData() {
		List<IKeywordContributor> builtInKeywordContributors = KeywordController.getInstance()
				.getBuiltInKeywordContributors();

		KeywordFolderBrowserTreeEntity builtinKeywordRootFolder = new KeywordFolderBrowserTreeEntity(
				StringConstants.KEYWORD_BROWSER_BUILTIN_KEYWORD_ROOT_TREE_ITEM_LABEL, null);
		List<IKeywordBrowserTreeEntity> keywordTreeEntities = new ArrayList<IKeywordBrowserTreeEntity>();
		for (IKeywordContributor builtInKeywordContributor : builtInKeywordContributors) {
			keywordTreeEntities.add(new BuiltinKeywordFolderBrowserTreeEntity(builtInKeywordContributor
					.getKeywordClass().getName(), builtInKeywordContributor.getKeywordClass().getSimpleName(),
					builtInKeywordContributor.getLabelName(), builtinKeywordRootFolder));
		}
		builtinKeywordRootFolder.setChildren(keywordTreeEntities);
		CustomKeywordFolderBrowserTreeEntity customKeywordRootFolder = new CustomKeywordFolderBrowserTreeEntity(null);

		keywordTreeEntities = new ArrayList<IKeywordBrowserTreeEntity>();
		keywordTreeEntities.add(builtinKeywordRootFolder);
		keywordTreeEntities.add(customKeywordRootFolder);
		treeViewer.setInput(keywordTreeEntities);
		treeViewer.refresh();
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(EventConstants.PROJECT_OPENED)) {
			loadTreeData();
		}
	}
}
