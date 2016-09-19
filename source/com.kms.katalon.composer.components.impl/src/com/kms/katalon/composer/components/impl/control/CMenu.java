package com.kms.katalon.composer.components.impl.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.components.log.LoggerSingleton;

@SuppressWarnings("restriction")
public class CMenu extends Menu {

    /**
     * Used to get MenuItem when users press hotkey
     * Key is keystroke format value as lowercase, value is id of the action
     */
    private Map<String, String> keyStrokeActionMap;

    private HotkeyActiveListener listener;

    public CMenu(Control parent, HotkeyActiveListener listener) {
        super(parent);
        keyStrokeActionMap = new HashMap<>();
        this.listener = listener;
        addControlListeners(parent);
    }

    private void addControlListeners(Control parent) {
        final EBindingService bindingService = new WorkbenchUtilizer().getService(EBindingService.class);
        parent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String hotkey = KeyStroke.getInstance(e.stateMask, e.keyCode).format().toLowerCase();
                if (keyStrokeActionMap.containsKey(hotkey)) {
                    listener.executeAction(keyStrokeActionMap.get(hotkey));
                }
            }
        });

        parent.addFocusListener(new FocusListener() {
            private Collection<Binding> oldContext;

            @Override
            public void focusLost(FocusEvent e) {
                for (Binding oldBinding : oldContext) {
                    bindingService.activateBinding(oldBinding);
                }
                oldContext.clear();
            }

            @Override
            public void focusGained(FocusEvent e) {
                oldContext = new ArrayList<>();
                for (Binding oldBinding : bindingService.getActiveBindings()) {
                    if (keyStrokeActionMap.containsKey(oldBinding.getTriggerSequence().format().toLowerCase())) {
                        oldContext.add(oldBinding);
                        bindingService.deactivateBinding(oldBinding);
                    }
                }
            }
        });
    }

    /**
     * Create a {@link MenuItem} by the given name and will be activated by the given <code>hotkey</code> and always be
     * enabled.
     * @param name Name of the {@link MenuItem}.
     * @param hotkey the hotkey, used to active the created {@link MenuItem}.
     */
    public MenuItem createMenuItem(final String name, String hotkey) {
        return createMenuItem(name, hotkey, null);
    }

    /**
     * Create a {@link MenuItem} by the given name and will be activated by the given <code>hotkey</code>.
     * 
     * @param name Name of the {@link MenuItem}.
     * @param hotkey the hotkey, used to active the created {@link MenuItem}.
     * @param visibleWhen used to set the create {@link MenuItem} enable when the menu is shown, null is equivalent as
     * always be enabled.
     * @return an instance of {@link MenuItem}, null if <code>hotkey</code> is invalid.
     */
    public MenuItem createMenuItem(final String name, final String hotkey, final Callable<Boolean> visibleWhen) {
        try {
            final MenuItem menuItem = new MenuItem(this, SWT.PUSH);
            String formattedHotkey = KeyStroke.getInstance(hotkey).format();
            menuItem.setText(createMenuItemText(name, formattedHotkey));
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    listener.executeAction(name);
                }
            });

            if (visibleWhen != null) {
                addMenuListener(new MenuAdapter() {
                    @Override
                    public void menuShown(MenuEvent e) {
                        try {
                            menuItem.setEnabled(visibleWhen.call());
                        } catch (Exception ex) {
                            LoggerSingleton.logError(ex);
                        }
                    }
                });
            }

            final String hotkeyAsId = formattedHotkey.toLowerCase();
            keyStrokeActionMap.put(hotkeyAsId, name);
            menuItem.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    keyStrokeActionMap.remove(hotkeyAsId);
                }
            });
            return menuItem;
        } catch (ParseException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    private String createMenuItemText(String name, String hotkey) {
        if (name == null || hotkey == null) {
            return null;
        }
        return name + "\t" + hotkey;
    }

    @Override
    protected void checkSubclass() {
        // Remove error policy
    }
}
