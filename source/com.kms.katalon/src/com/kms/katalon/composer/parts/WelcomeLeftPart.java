package com.kms.katalon.composer.parts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.DesktopUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class WelcomeLeftPart extends Composite {

    private static final String RELEASE_NOTES_URL = MessageConstants.RELEASE_NOTE_URL;

    private static final Color BACKGROUND_COLOR = ColorUtil.getColor("#70746F");

    private static final Color VERSION_TEXT_COLOR = ColorUtil.getColor("#BDBDBD");

    private static final Cursor CURSOR_HAND = Display.getDefault().getSystemCursor(SWT.CURSOR_HAND);

    private static final int MIN_WIDTH = 300;

    public WelcomeLeftPart(Composite parent, int style) {
        super(parent, style);
        setBackground(BACKGROUND_COLOR);
        setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        createBrandingControls();
        createQuickHelpMenu();
    }

    private void createBrandingControls() {
        Composite upperComposite = new Composite(this, SWT.NONE);
        upperComposite.setBackground(BACKGROUND_COLOR);
        upperComposite.setLayout(new GridLayout());
        GridData gd_upperComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_upperComposite.minimumWidth = MIN_WIDTH;
        upperComposite.setLayoutData(gd_upperComposite);

        Composite headerComposite = new Composite(upperComposite, SWT.NONE);
        headerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        RowLayout rlHeaderComposite = new RowLayout(SWT.VERTICAL);
        rlHeaderComposite.spacing = 20;
        rlHeaderComposite.marginHeight = 40;
        rlHeaderComposite.center = true;
        headerComposite.setLayout(rlHeaderComposite);

        CLabel lblLogo = new CLabel(headerComposite, SWT.NONE);
        lblLogo.setAlignment(SWT.CENTER);
        lblLogo.setImage(ImageConstants.IMG_BRANDING);

        CLabel lblVersion = new CLabel(headerComposite, SWT.NONE);
        ControlUtils.setFontSize(lblVersion, 14);
        lblVersion.setText(MessageFormat.format(MessageConstants.PA_LBL_KATALON_VERSION, ApplicationInfo.versionNo()));
        lblVersion.setForeground(VERSION_TEXT_COLOR);

        Link lblReleaseNote = new Link(headerComposite, SWT.NONE);
        ControlUtils.setFontStyle(lblReleaseNote, SWT.ITALIC, 12);
        lblReleaseNote.setText(RELEASE_NOTES_URL);
        lblReleaseNote.setLinkForeground(VERSION_TEXT_COLOR);
        lblReleaseNote.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    private void createQuickHelpMenu() {
        Composite lowerComposite = new Composite(this, SWT.NONE);
        lowerComposite.setBackground(BACKGROUND_COLOR);
        GridLayout glLowerComposite = new GridLayout();
        glLowerComposite.marginHeight = 0;
        glLowerComposite.marginWidth = 0;
        lowerComposite.setLayout(glLowerComposite);
        GridData gdLowerComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdLowerComposite.minimumWidth = MIN_WIDTH;
        lowerComposite.setLayoutData(gdLowerComposite);

        addMenuItem(lowerComposite, ImageConstants.IMG_TUTORIAL, MessageConstants.PA_LBL_TUTORIALS,
                MessageConstants.PA_LBL_TUTORIALS_URL);
        addMenuSeparator(lowerComposite);
        addMenuItem(lowerComposite, ImageConstants.IMG_FAQ, StringConstants.PA_LBL_FAQ, StringConstants.PA_LBL_FAQ_URL);
        addMenuSeparator(lowerComposite);
        addMenuItem(lowerComposite, ImageConstants.IMG_USER_GUIDE, StringConstants.PA_LBL_USER_GUIDE,
                StringConstants.PA_LBL_USER_GUIDE_URL);
    }

    private void addMenuItem(Composite parent, Image icon, String label, String url) {
        Composite holder = new Composite(parent, SWT.NONE);
        GridData gdHolder = new GridData(SWT.CENTER, SWT.FILL, true, false);
        gdHolder.minimumWidth = 150;
        holder.setLayoutData(gdHolder);
        holder.setBackground(BACKGROUND_COLOR);
        RowLayout rlMenuComposite = new RowLayout(SWT.VERTICAL);
        rlMenuComposite.marginHeight = 10;
        rlMenuComposite.spacing = 20;
        holder.setLayout(rlMenuComposite);
        holder.setCursor(CURSOR_HAND);

        CLabel menuItem = new CLabel(holder, SWT.NONE);
        menuItem.setAlignment(SWT.CENTER);
        menuItem.setImage(icon);
        menuItem.setText(label);
        menuItem.setForeground(ColorUtil.getTextWhiteColor());
        menuItem.setCursor(CURSOR_HAND);
        // menuItem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        ControlUtils.setFontSize(menuItem, 12);

        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                openURL(url);
            }
        };
        menuItem.addMouseListener(mouseAdapter);
        holder.addMouseListener(mouseAdapter);
    }

    private void addMenuSeparator(Composite parent) {
        CLabel separator = new CLabel(parent, SWT.NONE);
        separator.setMargins(0, 0, 0, 0);
        separator.setAlignment(SWT.RIGHT);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        separator.setImage(ImageConstants.IMG_GRADIENT_LINE_SEPARATOR);
    }

    private void openURL(String url) {
        try {
            DesktopUtils.openUri(new URL(url).toURI());
        } catch (IOException | URISyntaxException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
