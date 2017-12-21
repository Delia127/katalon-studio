package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class AbstractApplicationWindow extends ApplicationWindow {

    /**
     * The dialog settings key name for stored dialog x location.
     *
     * @since 3.2
     */
    private static final String DIALOG_ORIGIN_X = "DIALOG_X_ORIGIN"; //$NON-NLS-1$

    /**
     * The dialog settings key name for stored dialog y location.
     *
     * @since 3.2
     */
    private static final String DIALOG_ORIGIN_Y = "DIALOG_Y_ORIGIN"; //$NON-NLS-1$

    /**
     * The dialog settings key name for stored dialog width.
     *
     * @since 3.2
     */
    private static final String DIALOG_WIDTH = "DIALOG_WIDTH"; //$NON-NLS-1$

    /**
     * The dialog settings key name for stored dialog height.
     *
     * @since 3.2
     */
    private static final String DIALOG_HEIGHT = "DIALOG_HEIGHT"; //$NON-NLS-1$

    /**
     * The dialog settings key name for the font used when the dialog
     * height and width was stored.
     *
     * @since 3.2
     */
    private static final String DIALOG_FONT_DATA = "DIALOG_FONT_NAME"; //$NON-NLS-1$

    /**
     * A value that can be used for stored dialog width or height that
     * indicates that the default bounds should be used.
     *
     * @since 3.2
     */
    public static final int DIALOG_DEFAULT_BOUNDS = -1;

    /**
     * Constants that can be used for specifying the strategy for persisting
     * dialog bounds. These constants represent bit masks that can be used
     * together.
     *
     * @since 3.2
     */

    /**
     * Persist the last location of the dialog.
     * 
     * @since 3.2
     */
    public static final int DIALOG_PERSISTLOCATION = 0x0001;

    /**
     * Persist the last known size of the dialog.
     * 
     * @since 3.2
     */
    public static final int DIALOG_PERSISTSIZE = 0x0002;

    public AbstractApplicationWindow(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public boolean close() {
        if (getShell() != null && !getShell().isDisposed()) {
            saveDialogBounds(getShell());
        }
        return super.close();
    }

    /**
     * Gets the dialog settings that should be used for remembering the bounds of
     * of the dialog, according to the dialog bounds strategy.
     *
     * @return settings the dialog settings used to store the dialog's location
     * and/or size, or <code>null</code> if the dialog's bounds should
     * never be stored.
     *
     * @since 3.2
     * @see Dialog#getDialogBoundsStrategy()
     */
    protected IDialogSettings getDialogBoundsSettings() {
        return null;
    }

    /**
     * Get the integer constant that describes the strategy for persisting the
     * dialog bounds. This strategy is ignored if the implementer does not also
     * specify the dialog settings for storing the bounds in
     * Dialog.getDialogBoundsSettings().
     *
     * @return the constant describing the strategy for persisting the dialog
     * bounds.
     *
     * @since 3.2
     * @see Dialog#DIALOG_PERSISTLOCATION
     * @see Dialog#DIALOG_PERSISTSIZE
     * @see Dialog#getDialogBoundsSettings()
     */
    protected int getDialogBoundsStrategy() {
        return DIALOG_PERSISTLOCATION | DIALOG_PERSISTSIZE;
    }

    /**
     * Saves the bounds of the shell in the appropriate dialog settings. The
     * bounds are recorded relative to the parent shell, if there is one, or
     * display coordinates if there is no parent shell.
     *
     * @param shell
     * The shell whose bounds are to be stored
     *
     * @since 3.2
     */
    private void saveDialogBounds(Shell shell) {
        IDialogSettings settings = getDialogBoundsSettings();
        if (settings != null) {
            Point shellLocation = shell.getLocation();
            Point shellSize = shell.getSize();
            Shell parent = getParentShell();
            if (parent != null) {
                Point parentLocation = parent.getLocation();
                shellLocation.x -= parentLocation.x;
                shellLocation.y -= parentLocation.y;
            }
            int strategy = getDialogBoundsStrategy();
            if ((strategy & DIALOG_PERSISTLOCATION) != 0) {
                settings.put(DIALOG_ORIGIN_X, shellLocation.x);
                settings.put(DIALOG_ORIGIN_Y, shellLocation.y);
            }
            if ((strategy & DIALOG_PERSISTSIZE) != 0) {
                settings.put(DIALOG_WIDTH, shellSize.x);
                settings.put(DIALOG_HEIGHT, shellSize.y);
                FontData[] fontDatas = JFaceResources.getDialogFont().getFontData();
                if (fontDatas.length > 0) {
                    settings.put(DIALOG_FONT_DATA, fontDatas[0].toString());
                }
            }
        }
    }

    /**
     * Returns the initial size to use for the shell. Overridden
     * to check whether a size has been stored in dialog settings.
     * If a size has been stored, it is returned.
     *
     * @return the initial size of the shell
     *
     * @since 3.2
     * @see #getDialogBoundsSettings()
     * @see #getDialogBoundsStrategy()
     */
    @Override
    protected Point getInitialSize() {
        Point result = super.getInitialSize();

        // Check the dialog settings for a stored size.
        if ((getDialogBoundsStrategy() & DIALOG_PERSISTSIZE) != 0) {
            IDialogSettings settings = getDialogBoundsSettings();
            if (settings != null) {
                // Check that the dialog font matches the font used
                // when the bounds was stored. If the font has changed,
                // we do not honor the stored settings.
                // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=132821
                boolean useStoredBounds = true;
                String previousDialogFontData = settings.get(DIALOG_FONT_DATA);
                // There is a previously stored font, so we will check it.
                // Note that if we haven't stored the font before, then we will
                // use the stored bounds. This allows restoring of dialog bounds
                // that were stored before we started storing the fontdata.
                if (previousDialogFontData != null && previousDialogFontData.length() > 0) {
                    FontData[] fontDatas = JFaceResources.getDialogFont().getFontData();
                    if (fontDatas.length > 0) {
                        String currentDialogFontData = fontDatas[0].toString();
                        useStoredBounds = currentDialogFontData.equalsIgnoreCase(previousDialogFontData);
                    }
                }
                if (useStoredBounds) {
                    try {
                        // Get the stored width and height.
                        int width = settings.getInt(DIALOG_WIDTH);
                        if (width != DIALOG_DEFAULT_BOUNDS) {
                            result.x = width;
                        }
                        int height = settings.getInt(DIALOG_HEIGHT);
                        if (height != DIALOG_DEFAULT_BOUNDS) {
                            result.y = height;
                        }

                    } catch (NumberFormatException e) {}
                }
            }
        }
        // No attempt is made to constrain the bounds. The default
        // constraining behavior in Window will be used.
        return result;
    }

    /**
     * Returns the initial location to use for the shell. Overridden
     * to check whether the bounds of the dialog have been stored in
     * dialog settings. If a location has been stored, it is returned.
     *
     * @param initialSize
     * the initial size of the shell, as returned by
     * <code>getInitialSize</code>.
     * @return the initial location of the shell
     *
     * @since 3.2
     * @see #getDialogBoundsSettings()
     * @see #getDialogBoundsStrategy()
     */
    @Override
    protected Point getInitialLocation(Point initialSize) {
        Point result = super.getInitialLocation(initialSize);
        if ((getDialogBoundsStrategy() & DIALOG_PERSISTLOCATION) != 0) {
            IDialogSettings settings = getDialogBoundsSettings();
            if (settings != null) {
                try {
                    int x = settings.getInt(DIALOG_ORIGIN_X);
                    int y = settings.getInt(DIALOG_ORIGIN_Y);
                    result = new Point(x, y);
                    // The coordinates were stored relative to the parent shell.
                    // Convert to display coordinates.
                    Shell parent = getParentShell();
                    if (parent != null) {
                        Point parentLocation = parent.getLocation();
                        result.x += parentLocation.x;
                        result.y += parentLocation.y;
                    }
                } catch (NumberFormatException e) {}
            }
        }
        // No attempt is made to constrain the bounds. The default
        // constraining behavior in Window will be used.
        return result;
    }
}
