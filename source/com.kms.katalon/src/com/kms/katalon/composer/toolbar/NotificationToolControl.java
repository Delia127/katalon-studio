package com.kms.katalon.composer.toolbar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.toolbar.notification.NotificationContent;
import com.kms.katalon.composer.toolbar.notification.PopupNotification;
import com.kms.katalon.composer.toolbar.notification.TrackedNotification;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.CryptoUtil;

public class NotificationToolControl {

    @Inject
    private IEventBroker eventBroker;

    private List<PopupNotification> popupNotifications = new ArrayList<>();

    private ToolItem communityToolItem;

    @Inject
    public void registerListeners() {
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Executors.newFixedThreadPool(1).submit(() -> {
                    popupNotifications = getPopupNotificationsForTrialUser();
                  
                    UISynchronizeService.asyncExec(() -> {

                        long unreadMessageCount = popupNotifications.stream().filter(p -> {
                            return p.getTracked() == null;
                        }).count();
                        if (unreadMessageCount == 1) {                            
                            communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_1_24);
                        } else if (unreadMessageCount == 2) {
                            communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_2_24);
                        } else if (unreadMessageCount == 3) {
                            communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_3_24);
                        }
                    });
                });
            }
        });
        
        eventBroker.subscribe(EventConstants.ACTIVATION_DEACTIVATED, new EventHandler() {
            
            @Override
            public void handleEvent(Event event) {
                popupNotifications = new ArrayList<>();
                
                UISynchronizeService.asyncExec(() -> {
                    communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_24);
                });
            }
        });
    }

    @PostConstruct
    public void createWidget(Composite parent, MToolControl toolControl) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        communityToolItem = new ToolItem(toolbar, SWT.CHECK);
        communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_24);

        communityToolItem.addSelectionListener(new SelectionAdapter() {
            private Shell popup;
            
            private void uncheckNotificationItem() {
                communityToolItem.setSelection(false);
                communityToolItem.notifyListeners(SWT.Selection, new org.eclipse.swt.widgets.Event());
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                communityToolItem.setImage(ImageConstants.IMG_KATALON_NOTIFICATION_24);
                if (communityToolItem.getSelection()) {
                    Trackings.trackClickOnTrialNotificationButton();
                    popup = new Shell(toolbar.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP);
                    popup.setLayout(new FillLayout());
                    Composite mainComposite = new Composite(popup, SWT.BORDER);
                    mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
                    mainComposite.setLayout(new GridLayout());

                    if (!popupNotifications.isEmpty()) {
                        for (PopupNotification noti : popupNotifications) {
                            Composite composite = new Composite(mainComposite, SWT.NONE);
                            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                            composite.setLayout(new GridLayout(2, false));

                            Link link = new Link(composite, SWT.NONE);
                            link.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.CENTER, true, false));
                            link.setText(noti.getContent().getMessage());
                            link.addSelectionListener(new SelectionAdapter() {
                                @Override
                                public void widgetSelected(SelectionEvent e) {
                                    link.removeSelectionListener(this);
                                    uncheckNotificationItem();
                                    Executors.newFixedThreadPool(1).submit(() -> {
                                        try {
                                            Thread.sleep(200L);
                                        } catch (InterruptedException ignored) {}
                                        Program.launch(e.text);
                                        Trackings.trackClickOnTrialNotification(noti.getContent().getMessage());
                                    });
                                    
                                }
                            });

                            if (noti.getTracked() == null) {
                                TrackedNotification tracked = new TrackedNotification();
                                tracked.setId(noti.getContent().getId());
                                tracked.setTrackedDate(new Date());
                                noti.setTracked(tracked);
                            }
                            Label lbl = new Label(composite, SWT.NONE);
                            GridData gdLbl = new GridData(SWT.LEFT, SWT.CENTER, false, false);
                            lbl.setLayoutData(gdLbl);
                            lbl.setText(getReadDate(noti.getTracked().getTrackedDate(), new Date()));
                        }

                        List<TrackedNotification> newTracked = popupNotifications.stream()
                                .map(noti -> noti.getTracked())
                                .collect(Collectors.toList());
                        saveTrackedNotifications(newTracked);

                        popup.setSize(mainComposite.computeSize(400, SWT.DEFAULT));
                    } else {
                        Composite composite = new Composite(mainComposite, SWT.NONE);
                        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
                        GridLayout gridLayout = new GridLayout(1, false);
                        gridLayout.marginWidth = 10;
                        gridLayout.marginHeight = 10;
                        composite.setLayout(gridLayout);

                        Label lbl = new Label(composite, SWT.NONE);
                        lbl.setText("There are no new messages");

                        popup.setSize(mainComposite.computeSize(300, SWT.DEFAULT));
                    }
                    
                    popup.getDisplay().addFilter(SWT.Activate, new Listener() {
                        
                        @Override
                        public void handleEvent(org.eclipse.swt.widgets.Event event) {
                            if (popup == null || popup.isDisposed()) {
                                return;
                            }
                            if (event.widget == null) {
                                event.display.removeFilter(SWT.Activate, this);
                                uncheckNotificationItem();
                                return;
                            }
                            
                            if (event.widget instanceof Shell) {
                                if (event.widget == popup) {
                                    return;
                                } else {
                                    event.display.removeFilter(SWT.Activate, this);
                                    uncheckNotificationItem();
                                    return;
                                }
                            }
                            
                            Control control = (Control) event.widget;
                            if (control.getShell() != popup) {
                                event.display.removeFilter(SWT.Activate, this);
                                uncheckNotificationItem();
                                return;
                            }
                        }
                    });

                    popup.setLocation(toolbar.getBounds().x - popup.getSize().x + 36, toolbar.getBounds().y + 78);
                    popup.setVisible(true);
                } else {
                    popup.setVisible(false);
                    popup.dispose();
                }
            }
        });
    }

    public List<NotificationContent> getNotificationContents() {
        try {
            Bundle bundle = FrameworkUtil.getBundle(NotificationToolControl.class);
            URL contentUrl = FileLocator
                    .toFileURL(bundle.getResource("resources/notification/notification_content.json"));
            Type listType = new TypeToken<List<NotificationContent>>() {}.getType();
            return JsonUtil.fromJson(FileUtils.readFileToString(new File(contentUrl.getFile())), listType);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private List<TrackedNotification> getTrackedNotifications() {
        File trackedNotificationFile = new File(GlobalStringConstants.APP_USER_DIR_LOCATION,
                "notification/tracked_notifications.json");
        if (!trackedNotificationFile.exists()) {
            return Collections.emptyList();
        }

        try {
            Type mapType = new TypeToken<Map<String, List<TrackedNotification>>>() {}.getType();
            Map<String, List<TrackedNotification>> userNotifications = JsonUtil
                    .fromJson(FileUtils.readFileToString(trackedNotificationFile), mapType);
            String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            return userNotifications.getOrDefault(email, Collections.emptyList());
        } catch (IllegalArgumentException | IOException e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }

    private void saveTrackedNotifications(List<TrackedNotification> trackedNotifications) {
        File trackedNotificationFile = new File(GlobalStringConstants.APP_USER_DIR_LOCATION,
                "notification/tracked_notifications.json");
        try {

            Type mapType = new TypeToken<Map<String, List<TrackedNotification>>>() {}.getType();
            Map<String, List<TrackedNotification>> trackedNotiByUsers = new HashMap<>();
            if (trackedNotificationFile.exists()) {
                trackedNotiByUsers = JsonUtil.fromJson(FileUtils.readFileToString(trackedNotificationFile), mapType);
            }
            String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            trackedNotiByUsers.put(email, trackedNotifications);

            FileUtils.writeStringToFile(trackedNotificationFile, JsonUtil.toJson(trackedNotiByUsers), false);
        } catch (IllegalArgumentException | IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private List<PopupNotification> getPopupNotifications(List<NotificationContent> contentList,
            List<TrackedNotification> trackedNotifications) {
        Map<String, TrackedNotification> trackedById = trackedById(trackedNotifications);

        List<PopupNotification> popupNotifications = new ArrayList<>();

        for (NotificationContent content : contentList) {
            PopupNotification popup = new PopupNotification();
            popup.setContent(content);

            String notificationId = content.getId();
            popup.setTracked(trackedById.getOrDefault(notificationId, null));

            popupNotifications.add(popup);
        }
        
        popupNotifications.sort(new Comparator<PopupNotification>() {
            @Override
            public int compare(PopupNotification notiA, PopupNotification notiB) {
                return notiB.getContent().getStartDate() - notiA.getContent().getStartDate();
            }
        });

        return popupNotifications;
    }

    private Map<String, TrackedNotification> trackedById(List<TrackedNotification> trackedNotifications) {
        Map<String, TrackedNotification> trackedById = new HashMap<>();
        for (TrackedNotification tracked : trackedNotifications) {
            trackedById.put(tracked.getId(), tracked);
        }
        return trackedById;
    }

    private String getReadDate(Date oldDate, Date newDate) {
        long diffDays = diffDays(oldDate, newDate);
        if (diffDays <= 0) {
            return "Today";
        }
        if (diffDays == 1) {
            return "Yesterday";
        }
        return String.format("%d days ago", diffDays);
    }

    private long diffDays(Date oldDate, Date newDate) {
        long diff = DateUtils.truncate(newDate, Calendar.DATE).getTime() - 
                DateUtils.truncate(oldDate, Calendar.DATE).getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays;
    }

    private List<PopupNotification> getPopupNotificationsForTrialUser() {
        if (ActivationInfoCollector.getLicenseType() == LicenseType.ENTERPRISE) {
            return Collections.emptyList();
        }
        try {
            Date expiredDate = getExpiredDate();
            if (getExpiredDate() == null) {
                return Collections.emptyList();
            }
            Date activationDate = DateUtils.addDays(expiredDate, -30);
            long diffDays = diffDays(activationDate, new Date());
            List<TrackedNotification> trackedNotifications = getTrackedNotifications();
            Map<String, TrackedNotification> trackedById = trackedById(trackedNotifications);
            List<NotificationContent> notifications = getNotificationContents().stream().filter(content -> {
                if (trackedById.containsKey(content.getId())) {
                    return true;
                }
                if (content.getStartDate() > diffDays) {
                    return false;
                }
                if (content.getEndDate() > 0 && content.getEndDate() < diffDays) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            return getPopupNotifications(notifications, trackedNotifications);
        } catch (GeneralSecurityException | IOException | AnalyticsApiExeception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }

    private Date getExpiredDate() throws GeneralSecurityException, IOException, AnalyticsApiExeception {
        String serverUrl = ApplicationInfo.getTestOpsServer();
        String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(encryptedPassword)) {
            String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
            AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            if (token != null) {
                return AnalyticsApiProvider.getExpirationTrial(serverUrl, token.getAccess_token());
            }
        }
        return null;
    }
}
