package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
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
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;

public class MobileDeviceDialog extends Dialog {

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_DEVICE_VIEW;

    private Image currentScreenShot;

    private Composite mainContainer;

    private static final int DIALOG_WIDTH = 400;

    private static final int DIALOG_HEIGHT = 600;

    public static final Point DIALOG_SIZE = new Point(DIALOG_WIDTH + 9, DIALOG_HEIGHT + 57);

    private double currentX = 0, currentY = 0, currentWidth = 0, currentHeight = 0;

    double wRatio, hRatio;

    private JScrollPane scrImage;

    private ImageIcon icon;

    private JPanel contextPanel;

    private java.awt.Frame frame;

    private boolean isDisposed;

    private Point initialLocation;
    
    private MobileObjectSpyDialog objectSpyDialog;

    public MobileDeviceDialog(Shell parentShell, MobileObjectSpyDialog objectSpyDialog, Point location) {
        super(parentShell);
        this.objectSpyDialog = objectSpyDialog;
        this.initialLocation = location;
        setShellStyle(SWT.SHELL_TRIM | SWT.FILL | SWT.RESIZE);
        this.isDisposed = false;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        mainContainer = new Composite(parent, SWT.EMBEDDED | SWT.INHERIT_NONE);
        mainContainer.setBackground(ColorUtil.getBlackBackgroundColor());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        mainContainer.setLayout(layout);
        mainContainer.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        GridData gdata = new GridData(GridData.FILL_BOTH);
        gdata.widthHint = DIALOG_WIDTH;
        gdata.heightHint = DIALOG_HEIGHT;
        mainContainer.setLayoutData(gdata);

        frame = SWT_AWT.new_Frame(mainContainer);

        contextPanel = new JPanel();
        JLabel defaultInfo = new JLabel(StringConstants.DIA_LBL_SCREEN);
        contextPanel.add(defaultInfo, BorderLayout.CENTER);
        contextPanel.setBackground(Color.decode("#ecf8fc"));
        scrImage = new JScrollPane(contextPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                inspectElementAt(e.getX(), e.getY());
            }
        });
        frame.add(new AlphaContainer(scrImage));
        frame.pack();

        return mainContainer;
    }

    private void inspectElementAt(int x, int y) {
        Double realX = x / wRatio;
        Double realY = y / hRatio;
        objectSpyDialog.setSelectedElementByLocation(safeRoundDouble(realX), safeRoundDouble(realY));
    }

    public void highlight(final double x, final double y, final double width, final double height) {
        // Scale the coordinator depend on the ratio between scaled image / source image
        this.currentX = x * wRatio;
        this.currentY = y * hRatio;
        this.currentWidth = width * wRatio;
        this.currentHeight = height * hRatio;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JLabel c = (JLabel) scrImage.getViewport().getView();
                JLabel label = new JLabel();
                label.setOpaque(false);
                label.setBorder(BorderFactory.createLineBorder(Color.green, 2));
                label.setBounds(safeRoundDouble(currentX), safeRoundDouble(currentY), safeRoundDouble(currentWidth),
                        safeRoundDouble(currentHeight));
                // flash
                for (int i = 0; i < 6; i++) {
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
        return DIALOG_SIZE;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DIALOG_TITLE);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        setBlockOnOpen(false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        // No need bottom Button bar
        return parent;
    }

    /* package */void closeApp() {
        handleShellCloseEvent();
    }

    /* package */void highlightElement(MobileElement selectedElement) {
        Map<String, String> attributes = selectedElement.getAttributes();
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

    public void refreshDialog(File imageFile, MobileElement root) {
        try {
            String userTempDir = System.getProperty("java.io.tmpdir");
            if (root != null && root.getAttributes().containsKey(GUIObject.WIDTH)
                    && root.getAttributes().containsKey(GUIObject.HEIGHT)) {
                double appWidth = Double.parseDouble(root.getAttributes().get(GUIObject.WIDTH));
                double appHeight = Double.parseDouble(root.getAttributes().get(GUIObject.HEIGHT));

                ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
                Image img = imgDesc.createImage();
                currentScreenShot = scaleImage(img, appWidth, appHeight);

                String scaledImageFilePath = userTempDir + File.separator + UUID.randomUUID() + "_scaled1.png";
                ImageLoader imgLoader = new ImageLoader();
                imgLoader.data = new ImageData[] { this.currentScreenShot.getImageData() };
                imgLoader.save(scaledImageFilePath, SWT.IMAGE_PNG);

                imageFile = new File(scaledImageFilePath);
            }

            ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
            Image img = imgDesc.createImage();

            // Calculate scaled ratio
            wRatio = DIALOG_WIDTH / (double) img.getBounds().width;
            hRatio = contextPanel.getHeight() / (double) img.getBounds().height;
            // wRatio = (wRatio==0 ? theWidthRatio : theWidthRatio * wRatio);
            // hRatio = (hRatio==0 ? theHeightRatio : theHeightRatio * hRatio);

            currentScreenShot = scaleImage(img, contextPanel.getWidth(), contextPanel.getHeight());

            // Save scaled version
            String scaledImageFile = userTempDir + File.separator + UUID.randomUUID() + "_scaled2.png";
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { this.currentScreenShot.getImageData() };
            loader.save(scaledImageFile, SWT.IMAGE_PNG);

            icon = new ImageIcon(scaledImageFile);
            scrImage.setViewportView(new JLabel(icon));
            scrImage.revalidate();
            scrImage.repaint();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
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

    @SuppressWarnings("unused")
    private class AlphaContainer extends JComponent {
        private static final long serialVersionUID = -244003111111860211L;

        private JComponent component;

        public JComponent getComponent() {
            return component;
        }

        public void setComponent(JComponent component) {
            this.component = component;
        }

        public AlphaContainer(JComponent component) {
            this.component = component;
            setLayout(new BorderLayout());
            Color whiteColor = new Color(255, 255, 255, 0);
            component.setBackground(whiteColor);
            setBackground(whiteColor);
            setOpaque(false);
            component.setOpaque(false);
            add(component);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(component.getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
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
