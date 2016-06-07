package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.mobile.keyword.GUIObject;

@SuppressWarnings("restriction")
public class MobileDeviceDialog extends Dialog implements EventHandler {

    private static final String DIALOG_TITLE = StringConstants.DIA_DIALOG_TITLE_DEVICE_VIEW;

    private Image currentScreenShot;

    private Composite mainContainer;

    private static final int DIALOG_WIDTH = 400;

    private static final int DIALOG_HEIGHT = 600;

    public static final Point DIALOG_SIZE = new Point(DIALOG_WIDTH + 9, DIALOG_HEIGHT + 57);

    private float currentX = 0, currentY = 0, currentWidth = 0, currentHeight = 0;

    float wRatio, hRatio;

    private JScrollPane scrImage;

    private ImageIcon icon;

    private JPanel contextPanel;

    private java.awt.Frame frame;

    private boolean isDisposed;

    private IEventBroker eventBroker;
    
    private Point initialLocation;

    public MobileDeviceDialog(Shell parentShell, Point location, Logger logger, IEventBroker eventBroker) {
        super(parentShell);
        this.initialLocation = location;
        setShellStyle(SWT.SHELL_TRIM | SWT.FILL | SWT.RESIZE);
        this.eventBroker = eventBroker;
        this.isDisposed = false;
        eventBroker.subscribe(EventConstants.OBJECT_SPY_CLOSE_MOBILE_APP, this);
        eventBroker.subscribe(EventConstants.OBJECT_SPY_MOBILE_HIGHLIGHT, this);
        eventBroker.subscribe(EventConstants.OBJECT_SPY_MOBILE_SCREEN_CAPTURE, this);
    }

	
    @Override
    protected Control createDialogArea(Composite parent) {
        mainContainer = new Composite(parent, SWT.EMBEDDED | SWT.INHERIT_NONE);
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
            public void mouseClicked(MouseEvent me) {
            }
        });
        frame.add(new AlphaContainer(scrImage));
        frame.pack();

        return mainContainer;
    }

    public void highlight(final float x, final float y, final float width, final float height) {
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
                label.setBounds(Math.round(currentX), Math.round(currentY), Math.round(currentWidth),
                        Math.round(currentHeight));
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

    private Image scaleImage(Image image, float newWidth, float newHeight) {
        Image scaled = new Image(Display.getDefault(), Math.round(newWidth), Math.round(newHeight));
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, Math.round(newWidth),
                Math.round(newHeight));
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

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.OBJECT_SPY_CLOSE_MOBILE_APP)) {
            handleShellCloseEvent();
        } else if (event.getTopic().equals(EventConstants.OBJECT_SPY_MOBILE_HIGHLIGHT)) {
            MobileElement selectedElement = (MobileElement) event.getProperty("selected_object");
            float x = Float.parseFloat(selectedElement.getAttributes().get(GUIObject.X));
            float y = Float.parseFloat(selectedElement.getAttributes().get(GUIObject.Y));
            float w = Float.parseFloat(selectedElement.getAttributes().get(GUIObject.WIDTH));
            float h = Float.parseFloat(selectedElement.getAttributes().get(GUIObject.HEIGHT));
            highlight(x, y, w, h);
        } else if (event.getTopic().equals(EventConstants.OBJECT_SPY_MOBILE_SCREEN_CAPTURE)) {
            try {
                File imageFile = (File) event.getProperty("real_image_file_path");
                MobileElement root = (MobileElement) event.getProperty("appium_screen_object");

                if (root != null && root.getAttributes().containsKey("width")
                        && root.getAttributes().containsKey("height")) {
                    float appWidth = Float.parseFloat(root.getAttributes().get("width"));
                    float appHeight = Float.parseFloat(root.getAttributes().get("height"));

                    ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
                    Image img = imgDesc.createImage();
                    currentScreenShot = scaleImage(img, appWidth, appHeight);

                    String scaledImageFilePath = System.getProperty("java.io.tmpdir") + File.separator
                            + UUID.randomUUID() + "_scaled1.png";
                    ImageLoader imgLoader = new ImageLoader();
                    imgLoader.data = new ImageData[] { this.currentScreenShot.getImageData() };
                    imgLoader.save(scaledImageFilePath, SWT.IMAGE_PNG);

                    imageFile = new File(scaledImageFilePath);
                }

                ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
                Image img = imgDesc.createImage();

                // Calculate scaled ratio
                wRatio = DIALOG_WIDTH / (float) img.getBounds().width;
                hRatio = DIALOG_HEIGHT / (float) img.getBounds().height;
                // wRatio = (wRatio==0 ? theWidthRatio : theWidthRatio * wRatio);
                // hRatio = (hRatio==0 ? theHeightRatio : theHeightRatio * hRatio);

                currentScreenShot = scaleImage(img, DIALOG_WIDTH, DIALOG_HEIGHT);

                // Save scaled version
                String scaledImageFile = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID()
                        + "_scaled2.png";
                ImageLoader loader = new ImageLoader();
                loader.data = new ImageData[] { this.currentScreenShot.getImageData() };
                loader.save(scaledImageFile, SWT.IMAGE_PNG);

                icon = new ImageIcon(scaledImageFile);
                scrImage.setViewportView(new JLabel(icon));
                scrImage.revalidate();
                scrImage.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void dispose() {
        this.eventBroker.unsubscribe(this);
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
            component.setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
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
        return initialLocation;
    }
}


