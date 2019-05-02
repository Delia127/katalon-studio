/*******************************************************************************
 * Copyright (c) 2014 OPCoach. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: OPCoach - initial API and implementation
 *******************************************************************************/
package com.kms.katalon.preferences.internal;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.preferences.PreferenceNodeDescription;
import com.kms.katalon.preferences.constants.StringConstants;

@SuppressWarnings("restriction")
@Creatable
public class PreferencesRegistry {

    public static final String PREFS_PAGE_XP = "com.kms.katalon.preferences.PreferencePages"; // $NON-NLS-1$

    public static final String PREFS_PROJECT_XP = "com.kms.katalon.preferences.ProjectSettingPages"; // $NON-NLS-1$

    public static final String PREF_STORE_PROVIDER = "com.kms.katalon.preferences.PreferenceStoreProvider"; // $NON-NLS-1$

    protected static final String ELMT_PAGE = "page"; // $NON-NLS-1$

    protected static final String ATTR_ID = "id"; // $NON-NLS-1$

    protected static final String ATTR_CATEGORY = "category"; // $NON-NLS-1$

    protected static final String ATTR_CLASS = "class"; // $NON-NLS-1$

    protected static final String ATTR_NAME = "name"; // $NON-NLS-1$

    protected static final String ATTR_PLUGIN_ID = "pluginId"; // $NON-NLS-1$

    protected static final String ATTR_ID_IN_WBCONTEXT = "idInWorkbenchContext"; // $NON-NLS-1$

    @Inject
    protected Logger logger;

    @Inject
    protected IEclipseContext context;

    @Inject
    protected IExtensionRegistry registry;

    private PreferenceManager pm = null;

    // A map of (pluginId, { IPreferenceStoreProvider, or key in wbcontext }
    private Map<String, Object> psProviders;

    public PreferenceManager getPreferenceManager(String perferencePage) {
        return getPreferenceManager(perferencePage, Collections.emptyMap());
    }

    public PreferenceManager getPreferenceManager(String perferencePage,
            Map<String, List<PreferenceNodeDescription>> additional) {
        if (pm != null) {
            return pm;
        }
        Map<String, List<PreferenceNodeDescription>> nodeDescriptionLookup = new HashMap<>();
        nodeDescriptionLookup.putAll(additional);

        pm = new PreferenceManager();
        IContributionFactory factory = context.get(IContributionFactory.class);

        for (IConfigurationElement elmt : registry.getConfigurationElementsFor(perferencePage)) {
            String bundleId = elmt.getNamespaceIdentifier();
            String prefPageClassname = elmt.getAttribute(ATTR_CLASS);
            String category = elmt.getAttribute(ATTR_CATEGORY) != null ? elmt.getAttribute(ATTR_CATEGORY) : "";
            String nodeId = elmt.getAttribute(ATTR_ID);
            String nodeName = elmt.getAttribute(ATTR_NAME);
            if (!elmt.getName().equals(ELMT_PAGE)) {
                logger.warn(StringConstants.INL_LOG_WARN_UNEXPECTED_ELEMENT_X, elmt.getName());
                continue;
            } else if (isEmpty(nodeId) || isEmpty(nodeName)) {
                logger.warn(StringConstants.INL_LOG_WARN_MISSING_ID_AND_OR_NAME, bundleId);
                continue;
            }

            PreferenceNodeDescription nodeDesc = new PreferenceNodeDescriptionImpl(bundleId, nodeId, nodeName, category,
                    prefPageClassname);
            if (nodeDescriptionLookup.containsKey(category)) {
                List<PreferenceNodeDescription> children = new ArrayList<>(nodeDescriptionLookup.get(category));
                children.add(nodeDesc);
                nodeDescriptionLookup.put(category, children);
            } else {
                nodeDescriptionLookup.put(category, Arrays.asList(nodeDesc));
            }
        }
        

        Collection<String> categoriesDone = new ArrayList<String>();
        while (!nodeDescriptionLookup.isEmpty()) {
            for (String cat : Collections.unmodifiableSet(nodeDescriptionLookup.keySet())) {
                IPreferenceNode parent = findNode(pm, cat);
                if (parent == null && !cat.isEmpty()) {
                    continue;
                }
                for (PreferenceNodeDescription pnDesc : nodeDescriptionLookup.get(cat)) {
                    IPreferenceNode pn = null;
                    if (!pnDesc.hasPage()) {
                        pn = new PreferenceNode(pnDesc.getNodeId(), new EmptyPreferencePage(pnDesc.getNodeName()));
                    } else {
                        PreferencePage page = pnDesc.getBuilder().build(factory, context);
                        if (page == null) {
                            continue;
                        }
                        if (page.getPreferenceStore() == null) {
                            setPreferenceStore(pnDesc.getBundleId(), page);
                        }
                        ContextInjectionFactory.inject(page, context);
                        if ((page.getTitle() == null || page.getTitle().isEmpty()) && pnDesc.getNodeName() != null) {
                            page.setTitle(pnDesc.getNodeName());
                        }

                        pn = new PreferenceNode(pnDesc.getNodeId(), page);
                    }
                    if (parent != null) {
                        parent.add(pn);
                    } else {
                        pm.addToRoot(pn);
                    }
                }
                categoriesDone.add(cat);
            }
            
            for (String keyToRemove : categoriesDone) {
                nodeDescriptionLookup.remove(keyToRemove);
            }
            categoriesDone.clear();
        }

        return pm;
    }

    private void setPreferenceStore(String bundleId, PreferencePage page) {

        // Affect preference store to this page if this is a
        // PreferencePage, else, must manage it internally
        // Set the issue#1 on github :
        // https://github.com/opcoach/e4Preferences/issues/1
        // And manage the extensions of IP
        initialisePreferenceStoreProviders();

        IPreferenceStore store = null;

        // Get the preference store according to policy.
        Object data = psProviders.get(bundleId);
        if (data != null) {
            if (data instanceof IPreferenceStore)
                store = (IPreferenceStore) data;
            else if (data instanceof IPreferenceStoreProvider)
                store = ((IPreferenceStoreProvider) data).getPreferenceStore();
            else if (data instanceof String)
                store = (IPreferenceStore) context.get((String) data);

        } else {
            // Default behavior : create a preference store for this bundle and
            // remember of it
            store = getPreferenceStore(bundleId);
            psProviders.put(bundleId, store);
        }

        if (store != null)
            page.setPreferenceStore(store);
        else {
            logger.warn(MessageFormat.format(StringConstants.INL_LOG_WARN_CANNOT_SET_PREF_STORE_FOR_PAGE,
                    page.getTitle(), bundleId));
        }

    }

    /** Read the e4PreferenceStoreProvider extension point */
    private void initialisePreferenceStoreProviders() {
        if (psProviders == null) {
            IContributionFactory factory = context.get(IContributionFactory.class);

            psProviders = new HashMap<String, Object>();

            // Read extensions and fill the map...
            for (IConfigurationElement elmt : registry.getConfigurationElementsFor(PREF_STORE_PROVIDER)) {
                String declaringBundle = elmt.getNamespaceIdentifier();
                String pluginId = elmt.getAttribute(ATTR_PLUGIN_ID);
                if (isEmpty(pluginId)) {
                    logger.warn(MessageFormat.format(StringConstants.INL_LOG_WARN_MISSING_PLUGIN_ID_IN_EXT,
                            PREF_STORE_PROVIDER, declaringBundle));
                    continue;
                }

                String classname = elmt.getAttribute(ATTR_CLASS);
                String objectId = elmt.getAttribute(ATTR_ID_IN_WBCONTEXT);

                if ((isEmpty(classname) && isEmpty(objectId)) || (((classname != null) && classname.length() > 0)
                        && ((objectId != null) && objectId.length() > 0))) {
                    logger.warn(MessageFormat.format(StringConstants.INL_LOG_WARN_IN_EXT_ONLY_1_OR_2_ATTR_MUST_BE_SET,
                            PREF_STORE_PROVIDER, declaringBundle));
                    continue;
                }

                // Ok can now work with data...
                Object data = objectId;
                if (classname != null) {
                    try {
                        String prefStoreProviderURI = getClassURI(pluginId, elmt.getAttribute(ATTR_CLASS));

                        data = factory.create(prefStoreProviderURI, context);
                        if (!(data instanceof IPreferenceStoreProvider)) {
                            logger.warn(MessageFormat.format(
                                    StringConstants.INL_LOG_WARN_IN_EXT_CLASS_MUST_IMPL_IPREFERENCESTOREPROVIDER,
                                    PREF_STORE_PROVIDER, declaringBundle));
                            continue;
                        }

                    } catch (ClassNotFoundException e) {
                        logger.warn(e.getMessage());
                        continue;
                    } catch (InvalidRegistryObjectException e) {
                        logger.warn(e.getMessage());
                        continue;
                    }

                }

                psProviders.put(pluginId, data);

            }
        }
    }

    private IPreferenceNode findNode(PreferenceManager pm, String categoryId) {
        for (Object o : pm.getElements(PreferenceManager.POST_ORDER)) {
            if (o instanceof IPreferenceNode && ((IPreferenceNode) o).getId().equals(categoryId)) {
                return (IPreferenceNode) o;
            }
        }
        return null;
    }

    private String getClassURI(String definingBundleId, String spec) throws ClassNotFoundException {
        if (spec.startsWith("platform:")) {
            return spec;
        } // $NON-NLS-1$
        return "bundleclass://" + definingBundleId + '/' + spec;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class EmptyPreferencePage extends PreferencePage {

        public EmptyPreferencePage(String title) {
            setTitle(title);
            noDefaultAndApplyButton();
        }

        @Override
        protected Control createContents(Composite parent) {
            return new Label(parent, SWT.NONE);
        }

    }

}
