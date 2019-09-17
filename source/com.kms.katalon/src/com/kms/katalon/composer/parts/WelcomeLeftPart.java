package com.kms.katalon.composer.parts;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class WelcomeLeftPart extends Composite {

    private static final String RELEASE_NOTES_URL = MessageConstants.RELEASE_NOTE_URL;

    private static final Cursor CURSOR_HAND = Display.getDefault().getSystemCursor(SWT.CURSOR_HAND);

    private static final int MIN_WIDTH = 300;
    
    private static final Color BACKGROUND_COLOR = ColorUtil.getColor("#70746F");
    
    private static final Color PLUGIN_STORE_ITEM_BACKGROUND_COLOR = ColorUtil.getColor("#FFD966");

    private Composite headerComposite;

    private Label lblVersion;

    private Composite tutComposite;

    private Composite faqComposite;

    private Composite pluginComposite;

    private Composite supportComposite;
    
    public WelcomeLeftPart(Composite parent, int style) {
        super(parent, style);
        setBackground(BACKGROUND_COLOR);
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
        upperComposite.setLayout(new GridLayout());
        GridData gd_upperComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_upperComposite.minimumWidth = MIN_WIDTH;
        upperComposite.setLayoutData(gd_upperComposite);

        headerComposite = new Composite(upperComposite, SWT.NONE);
        headerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        RowLayout rlHeaderComposite = new RowLayout(SWT.VERTICAL);
        rlHeaderComposite.spacing = 20;
        rlHeaderComposite.marginHeight = 40;
        rlHeaderComposite.center = true;
        headerComposite.setLayout(rlHeaderComposite);
        headerComposite.setBackground(BACKGROUND_COLOR);

        Label lblLogo = new Label(headerComposite, SWT.NONE);
        lblLogo.setAlignment(SWT.CENTER);
        lblLogo.setImage(ImageConstants.IMG_BRANDING);

        lblVersion = new Label(headerComposite, SWT.NONE);
        ControlUtils.setFontSize(lblVersion, 14);
        lblVersion.setText(MessageFormat.format(MessageConstants.PA_LBL_KATALON_VERSION, ApplicationInfo.versionNo()));
        lblVersion.setForeground(ColorUtil.getTextWhiteColor());

        Link lblReleaseNote = new Link(headerComposite, SWT.NONE);
        ControlUtils.setFontStyle(lblReleaseNote, SWT.ITALIC, 12);
        lblReleaseNote.setText(RELEASE_NOTES_URL);
        lblReleaseNote.setLinkForeground(ColorUtil.getColor("#4DE1FF"));
        lblReleaseNote.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    private void createQuickHelpMenu() {
        Composite lowerComposite = new Composite(this, SWT.NONE);
        GridLayout glLowerComposite = new GridLayout();
        glLowerComposite.marginHeight = 0;
        glLowerComposite.marginWidth = 0;
        lowerComposite.setLayout(glLowerComposite);
        GridData gdLowerComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdLowerComposite.minimumWidth = MIN_WIDTH;
        lowerComposite.setLayoutData(gdLowerComposite);

        tutComposite = addMenuItem(lowerComposite, ImageConstants.IMG_TUTORIAL, MessageConstants.PA_LBL_TUTORIALS,
                MessageConstants.PA_LBL_TUTORIALS_URL, null, ColorUtil.getTextWhiteColor());
        addMenuSeparator(lowerComposite);

        faqComposite = addMenuItem(lowerComposite, ImageConstants.IMG_FAQ, StringConstants.PA_LBL_FAQ, StringConstants.PA_LBL_FAQ_URL, null, ColorUtil.getTextWhiteColor());

        addMenuSeparator(lowerComposite);

        pluginComposite = addMenuItem(lowerComposite, ImageConstants.IMG_KATALON_STORE, StringConstants.PA_LBL_PLUGIN_STORE, StringConstants.PA_LBL_PLUGIN_STORE_URL,
                PLUGIN_STORE_ITEM_BACKGROUND_COLOR, ColorUtil.getTextColor());

        addMenuSeparator(lowerComposite);
        supportComposite =addMenuItem(lowerComposite, ImageConstants.IMG_BUSSINESS_SUPPORT, StringConstants.PA_LBL_BUSINESS_SUPPORT,
                StringConstants.URL_KATALON_SUPPORT_SERVICE, null, ColorUtil.getTextWhiteColor());
    }

    private Composite addMenuItem(Composite parent, Image icon, String label, String url, Color backgroundColor, Color foregroundColor) {
        Composite holder = new Composite(parent, SWT.NONE);
        GridData gdHolder = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdHolder.minimumWidth = 170;
        holder.setLayoutData(gdHolder);
        if (backgroundColor != null) {
            holder.setBackground(backgroundColor);
        }
        GridLayout gl = new GridLayout(2, false);
        gl.verticalSpacing = 20;
        gl.marginHeight = 10;
        holder.setLayout(gl);
        holder.setCursor(CURSOR_HAND);

        Label menuImage = new Label(holder, SWT.NONE);
        GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, true);
        layoutData.horizontalIndent = 50;
        menuImage.setLayoutData(layoutData);
        menuImage.setImage(icon);

        Label menuItem = new Label(holder, SWT.NONE);
        menuItem.setAlignment(SWT.CENTER);
        menuItem.setText(label);
        menuItem.setCursor(CURSOR_HAND);
        if (menuItem != null) {
            menuItem.setForeground(foregroundColor);
        }
        
        ControlUtils.setFontSize(menuItem, 12);

        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                openURL(url);
            }
        };
        menuItem.addMouseListener(mouseAdapter);
        menuImage.addMouseListener(mouseAdapter);
        holder.addMouseListener(mouseAdapter);
        
        return holder;
    }

    private void addMenuSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.NONE);
        separator.setAlignment(SWT.RIGHT);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        separator.setImage(ImageConstants.IMG_GRADIENT_LINE_SEPARATOR);
    }

    private void openURL(String url) {
       Program.launch(url);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    
    public void updateColor() {
        setBackground(BACKGROUND_COLOR);
        headerComposite.setBackground(BACKGROUND_COLOR);
        lblVersion.setForeground(ColorUtil.getTextWhiteColor());
        ControlUtils.recursivelySetColor(faqComposite, ColorUtil.getTextWhiteColor(), null);
        ControlUtils.recursivelySetColor(tutComposite, ColorUtil.getTextWhiteColor(), null);
        ControlUtils.recursivelySetColor(supportComposite, ColorUtil.getTextWhiteColor(), null);
        ControlUtils.recursivelySetColor(pluginComposite, ColorUtil.getTextBlackColor(), PLUGIN_STORE_ITEM_BACKGROUND_COLOR);
    }
}
