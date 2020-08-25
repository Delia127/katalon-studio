package com.kms.katalon.composer.quickstart;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.tracking.service.Trackings;

public class QuickCreateFirstWebUITestCase extends BaseQuickStartDialog {

    private static final String DEFAULT_URL = "https://www.amazon.com";

    private WebUIDriverType preferredBrowser = WebUIDriverType.CHROME_DRIVER;

    private String preferredSite = DEFAULT_URL;

    private BrowserSelect browserSelect;

    private String scenario = Math.random() < 0.5f
            ? "A"
            : "B";

    private boolean isScenarioA = scenario.equals("A");

    public QuickCreateFirstWebUITestCase(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createContent(Composite container) {
        Composite body = ComponentBuilder.gridContainer(container).gridMargin(50).gridVerticalSpacing(5).build();

        createTitle(body);
        createStartRecordComposite(body);

        Trackings.trackQuickStartRecordOpen();
    }

    private void createTitle(Composite parent) {
        ComponentBuilder.label(parent)
                .text("Create your first automated test in a minute with Record")
                .font(FontUtil.size(FontUtil.BOLD, FontUtil.SIZE_H3))
                // .center()
                .build();
    }

    private void createStartRecordComposite(Composite parent) {
        Composite startRecordComposite = ComponentBuilder.gridContainer(parent)
                .gridMarginTop(30)
                .gridMarginBottom(20)
                .gridVerticalSpacing(10)
                .build();

        String urlLabelA = isScenarioA
                ? "Enter your web application URL:"
                : "Enter your web application URL or try out amazon.com:";
        ComponentBuilder.label(startRecordComposite).text(urlLabelA).build();

        Composite configComposite = ComponentBuilder.gridContainer(startRecordComposite, 3)
                .gridHorizontalSpacing(10)
                .build();

        String urlTextA = isScenarioA
                ? StringUtils.EMPTY
                : DEFAULT_URL;
        Composite textWrapper = ComponentBuilder.gridContainer(configComposite, 1, SWT.BORDER).build();
        ComponentBuilder.text(textWrapper, SWT.SINGLE)
                .gridMarginTop(5)
                .gridMarginLeft(5)
                .size(350, 22)
                .text(urlTextA)
                .placeholder("E.g: https://www.amazon.com")
                .fontSize(FontUtil.SIZE_H5)
                .onChange((event) -> {
                    Text txtUrl = (Text) event.widget;
                    preferredSite = txtUrl.getText();
                })
                .build();

        browserSelect = new BrowserSelect(configComposite, SWT.NONE);

        ComponentBuilder.label(configComposite).text("Start").size(100, 30).primaryButton().onClick((event) -> {
            okPressed();
        }).build();
    }

    @Override
    protected String getMainButtonText() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String getTipContent() {
        return "You can easily maintain your test scripts after Recording via Object Repository and Dual-script interface";
    }

    @Override
    protected void createMoreTips(Composite tipsComposite) {
        addTip(ComponentBuilder.label(tipsComposite).size(5, 24).build());
        addTip(ComponentBuilder.image(tipsComposite, IImageKeys.TIP_SPY_BUTTON, 30).size(30, 24).build());
        addTip(ComponentBuilder.image(tipsComposite, IImageKeys.TIP_RECORD_BUTTON, 35).size(30, 24).build());
    }

    @Override
    protected void setInput() {
        super.setInput();
        browserSelect.setInput(preferredBrowser);
    }

    public WebUIDriverType getPreferredBrowser() {
        return browserSelect.getInput();
    }

    public String getPreferredSite() {
        return preferredSite;
    }

    @Override
    protected void okPressed() {
        Trackings.trackQuickStartStartRecord(browserSelect.getInput().name(), scenario);
        super.okPressed();
    }
}
