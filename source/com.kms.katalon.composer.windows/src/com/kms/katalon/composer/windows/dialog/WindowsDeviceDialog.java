package com.kms.katalon.composer.windows.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.windows.element.BasicWindowsElement;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;

public class WindowsDeviceDialog extends Dialog {

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_DEVICE_VIEW;

    private Image currentScreenShot;

    private Composite swtAwtContainter;

    public static final int DIALOG_WIDTH = 400;

    public static final int DIALOG_HEIGHT = 600;

    private double currentX = 0, currentY = 0, currentWidth = 0, currentHeight = 0;

    private double hRatio;

    private JLabel scrImage;

    private ImageIcon icon;

    private java.awt.Frame frame;

    private boolean isDisposed;

    private Point initialLocation;

    private SpyWindowsObjectDialog mobileInspetorDialog;

    private ScrolledComposite scrolledComposite;

    public WindowsDeviceDialog(Shell parentShell, SpyWindowsObjectDialog mobileInspectorDialog, Point location) {
        super(parentShell);
        this.mobileInspetorDialog = mobileInspectorDialog;
        this.initialLocation = location;
        setShellStyle(SWT.SHELL_TRIM | SWT.FILL | SWT.RESIZE);
        this.isDisposed = false;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        final GridLayout dialogAreaGridLayout = (GridLayout) dialogArea.getLayout();
        dialogAreaGridLayout.marginWidth = 0;
        dialogAreaGridLayout.marginHeight = 0;

        scrolledComposite = new ScrolledComposite(dialogArea, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite container = new Composite(scrolledComposite, SWT.NULL);
        final GridLayout compositeGridLayout = new GridLayout();
        compositeGridLayout.marginHeight = 0;
        compositeGridLayout.marginWidth = 0;
        container.setLayout(compositeGridLayout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setContent(container);

        swtAwtContainter = new Composite(container, SWT.EMBEDDED | SWT.INHERIT_NONE);
        swtAwtContainter.setBackground(ColorUtil.getBlackBackgroundColor());

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        swtAwtContainter.setLayout(new GridLayout());
        swtAwtContainter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        frame = SWT_AWT.new_Frame(swtAwtContainter);

        scrImage = new JLabel();
        scrImage.setHorizontalAlignment(JLabel.LEFT);
        scrImage.setVerticalAlignment(JLabel.TOP);
        scrImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                inspectElementAt(e.getX(), e.getY());
            }
        });
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            panel.add(scrImage, BorderLayout.CENTER);
        } else {
            frame.add(scrImage);
        }
        frame.pack();
        swtAwtContainter.pack();
        return dialogArea;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void inspectElementAt(int x, int y) {
        Double realX = x / hRatio;
        Double realY = y / hRatio;
        mobileInspetorDialog.setSelectedElementByLocation(safeRoundDouble(realX), safeRoundDouble(realY));
    }

    private boolean isElementOnScreen(final Double x, final Double y, final Double width, final Double height) {
        Rectangle elementRect = new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
        return elementRect.intersects(getCurrentViewportRect());
    }

    private void scrollToElement(final Double x, final Double y) {
        Rectangle elementRect = new Rectangle(x.intValue(), y.intValue());
        scrolledComposite.setOrigin(elementRect.x, elementRect.y);
    }

    private Rectangle getCurrentViewportRect() {
        ScrollBar verticalBar = scrolledComposite.getVerticalBar();
        ScrollBar horizontalBar = scrolledComposite.getHorizontalBar();
        int viewPortY = (verticalBar.isVisible()) ? verticalBar.getSelection() : 0;
        int viewPortX = (horizontalBar.isVisible()) ? horizontalBar.getSelection() : 0;
        Point viewPortSize = scrolledComposite.getSize();
        Rectangle viewPortRect = new Rectangle(viewPortX, viewPortY, viewPortSize.x, viewPortSize.y);
        return viewPortRect;
    }

    public void highlight(final double x, final double y, final double width, final double height) {
        // Scale the coordinator depend on the ratio between scaled image / source image
        this.currentX = x * hRatio;
        this.currentY = y * hRatio;
        this.currentWidth = width * hRatio;
        this.currentHeight = height * hRatio;
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (!isElementOnScreen(currentX, currentY, currentWidth, currentHeight)) {
                    scrollToElement(currentX, currentY);
                }
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JLabel c = scrImage;
                JLabel label = new JLabel();
                label.setOpaque(false);
                label.setBorder(BorderFactory.createLineBorder(Color.green, 2));
                label.setBounds(safeRoundDouble(currentX), safeRoundDouble(currentY), safeRoundDouble(currentWidth),
                        safeRoundDouble(currentHeight));
                // flash
                for (int i = 0; i < 5; i++) {
                    c.add(label);
                    c.revalidate();
                    c.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {}
                    c.remove(label);
                    c.revalidate();
                    c.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {}
                }
            }
        });
        thread.start();
    }

    private Image scaleImage(Image image, double newWidth, double newHeight) {
        Image scaled = new Image(Display.getDefault(), safeRoundDouble(newWidth), safeRoundDouble(newHeight));
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, safeRoundDouble(newWidth),
                safeRoundDouble(newHeight));
        gc.dispose();
        image.dispose();
        return scaled;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(DIALOG_WIDTH, DIALOG_HEIGHT + 57);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DIALOG_TITLE);
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            shell.addListener(SWT.Resize, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    refreshViewForMac();
                }
            });
        }
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
        setBlockOnOpen(false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        // No need bottom Button bar
        return parent;
    }

    public void closeApp() {
        handleShellCloseEvent();
    }

    public void highlightElement(BasicWindowsElement selectedElement) {
        Map<String, String> attributes = selectedElement.getProperties();
        if (attributes == null || !attributes.containsKey(GUIObject.X) || !attributes.containsKey(GUIObject.Y)
                || !attributes.containsKey(GUIObject.WIDTH) || !attributes.containsKey(GUIObject.HEIGHT)) {
            return;
        }
        double x = Double.parseDouble(attributes.get(GUIObject.X));
        double y = Double.parseDouble(attributes.get(GUIObject.Y));
        double w = Double.parseDouble(attributes.get(GUIObject.WIDTH));
        double h = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
        highlight(x, y, w, h);
    }

    public void refreshDialog(File imageFile) {
        try {
            String userTempDir = System.getProperty("java.io.tmpdir");

            ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
            Image img = imgDesc.createImage();

            // Calculate scaled ratio
            hRatio = DIALOG_HEIGHT / (double) img.getBounds().height;

            currentScreenShot = scaleImage(img, ((double) img.getBounds().width) * hRatio, DIALOG_HEIGHT);

            // Save scaled version
            String scaledImageFile = userTempDir + File.separator + UUID.randomUUID() + "_scaled2.png";
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { this.currentScreenShot.getImageData() };
            loader.save(scaledImageFile, SWT.IMAGE_PNG);

            icon = new ImageIcon(scaledImageFile);
            scrImage.setIcon(icon);
            scrImage.revalidate();
            scrImage.repaint();

            if (Platform.getOS().equals(Platform.OS_MACOSX)) {
                refreshViewForMac();
                return;
            }
            refreshView();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void refreshView() {
        if (scrolledComposite == null || icon == null) {
            return;
        }
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scrolledComposite.setMinSize(icon.getIconWidth(), icon.getIconHeight());
            }
        });
    }

    private void refreshViewForMac() {
        if (scrolledComposite == null || icon == null || frame == null) {
            return;
        }
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                scrolledComposite.setMinSize(icon.getIconWidth(), icon.getIconHeight());
            }
        });
        frame.pack();
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void dispose() {
        this.isDisposed = true;
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        if ((getShell().getStyle() & SWT.RESIZE) == 0) {
            return new Point(initialLocation.x, initialLocation.y + 5);
        }
        return initialLocation;
    }

    public static int safeRoundDouble(double d) {
        long rounded = Math.round(d);
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, rounded));
    }
}
