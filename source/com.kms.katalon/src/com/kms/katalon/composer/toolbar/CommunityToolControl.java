package com.kms.katalon.composer.toolbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.control.DropdownToolItemSelectionListener;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.toolbar.ForumSearchToolControl;
import com.kms.katalon.composer.handlers.ChatSupportHandler;
import com.kms.katalon.composer.intro.FeedbackUser;
import com.kms.katalon.composer.intro.Tweetaboutus;
import com.kms.katalon.constants.ImageConstants;

public class CommunityToolControl {

    @Inject
    IEventBroker eventBroker;

    @PostConstruct
    void createWidget(Composite parent, MToolControl toolControl) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        ToolItem communityToolItem = new ToolItem(toolbar, SWT.DROP_DOWN);
        communityToolItem.setImage(ImageConstants.IMG_KATALON_COMMUNITY_24);
        communityToolItem.addSelectionListener(new DropdownToolItemSelectionListener() {

            @Override
            protected Menu getMenu() {
                Menu menu = new Menu(toolbar);
                MenuItem forumMenuItem = new MenuItem(menu, SWT.PUSH);
                forumMenuItem.setText("Forum");
                forumMenuItem.setImage(ImageConstants.KATALON_FORUM_24);
                forumMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new ForumSearchToolControl().execute();
                    }
                });

                MenuItem chatSupportMenuItem = new MenuItem(menu, SWT.PUSH);
                chatSupportMenuItem.setText("Chat");
                chatSupportMenuItem.setImage(ImageConstants.KATALON_CHAT_24);
                chatSupportMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new ChatSupportHandler().execute();
                    }
                });

                MenuItem tweetAboutUsMenuItem = new MenuItem(menu, SWT.PUSH);
                tweetAboutUsMenuItem.setText("Tweet about us");
                tweetAboutUsMenuItem.setImage(ImageConstants.KATALON_TWEETABOUTUS_24);
                tweetAboutUsMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new Tweetaboutus().execute();
                    }
                });

                MenuItem feedBackMenuItem = new MenuItem(menu, SWT.PUSH);
                feedBackMenuItem.setText("Feedback");
                feedBackMenuItem.setImage(ImageConstants.KATALON_FEEDBACK_24);
                feedBackMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        new FeedbackUser().execute();
                    }
                });
                return menu;
            }
        });

    }
}
