package com.kms.katalon.composer.webui.setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ClasspathExtension;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.webui.driver.RemoteDebugDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteFirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public class RemoteFirefoxPreferencePage extends RemoteDebugPreferencePage {
	
    @Override
    public WebUiDriverConnector createDriverConnector(String configurationFolderPath) {
        try {
        	if(remoteDebugDriverConnector == null){
        		remoteDebugDriverConnector = new RemoteFirefoxDriverConnector(configurationFolderPath);
        	}
            return remoteDebugDriverConnector;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

	public void doInstallDebuger(){	
		if(!System.getProperty("os.name").toLowerCase().contains("windows")){
			MessageDialog.openError(getShell(), "Error", "This platform has not been supported");
			return;
		}
		String profileLocation = getProfileLocation();
		
		ConfigurationDialog dlg = new ConfigurationDialog(getShell(), profileLocation);
		dlg.open();
	}
	
	private String getProfileLocation(){
		File iniFile = new File(System.getenv("APPDATA") + "/Mozilla/Firefox/profiles.ini");
		if(iniFile.exists()){
			for(Map<String, String> map : readProfilesIni(iniFile)){
				if("1".equals(map.get("Default")) && map.get("Path") != null){
					return iniFile.getParentFile().getAbsolutePath() + File.separator + map.get("Path").replace("/", File.separator);
				}
			}
		}
		return "";
	}
	
	private List<Map<String, String>> readProfilesIni(File iniFile){
		List<Map<String, String>> profilesIni = new ArrayList<>();
		if(!iniFile.isFile()){
			return profilesIni;
		}
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(iniFile));
			String line;
			while((line = br.readLine()) != null){
				if(line.matches("\\[Profile\\d+\\]")){
					profilesIni.add(new LinkedHashMap<String, String>());
					continue;
				}
				if(profilesIni.size() > 0 && line.indexOf("=") > 0){
					profilesIni.get(profilesIni.size()-1).put(line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=")+1));
				}
			}
			br.close();
		}
		catch(IOException ioe){}
		finally{
			IOUtils.closeQuietly(br);
		}
		return profilesIni;
	}
	
	private void updatePreference(String profileFolder, String prefKey, String newVal) throws IOException{
		File oldPrefsFile = new File(profileFolder, "prefs.js"); 
        if(oldPrefsFile.isFile()){
        	List<String> lines = FileUtils.readLines(oldPrefsFile);
        	boolean found = false;
        	for(int i=0; i<lines.size(); i++){
        		if(lines.get(i) != null && lines.get(i).startsWith("user_pref(\""+ prefKey +"\"")){
        			lines.set(i, "user_pref(\"webdriver_firefox_port\", "+ newVal +");");
        			found = true;
        			break;
        		}
        	}
        	if(!found){
        		lines.add("user_pref(\"webdriver_firefox_port\", "+ newVal +");");
        	}
        	FileUtils.writeLines(oldPrefsFile, lines, false);
        }
	}
	
	private class ConfigurationDialog extends TitleAreaDialog {

	    private Text txtProfileLocation;

	    private String profileLocation;

	    private Button btnFolderChooser;

	    public ConfigurationDialog(Shell parentShell, String profileLocation) {
	        super(parentShell);
	        this.profileLocation = profileLocation;
	    }
	    
	    @Override
	    protected Control createDialogArea(Composite parent) {
	        Composite area = (Composite) super.createDialogArea(parent);

	        getShell().setText("Configuration");
	        setTitle("Configure Firefox Remote Accessibilty");
	        setMessage("Katalon will install an add-on into your Firefox profile");

	        Composite container = new Composite(area, SWT.NONE);
	        container.setLayoutData(new GridData(GridData.FILL_BOTH));
	        container.setLayout(new GridLayout(2, false));

	        // Add
	        Label lbl = new Label(container, SWT.NONE);
	        lbl.setText("Profile location");
	        createFileChooserComposite(container);

	        // Build the separator line
	        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
	        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	        return area;
	    }

	    private Composite createFileChooserComposite(Composite parent) {
	        Composite container = new Composite(parent, SWT.NONE);
	        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        GridLayout theLayout = new GridLayout(2, false);
	        theLayout.marginWidth = 0;
	        container.setLayout(theLayout);

	        txtProfileLocation = new Text(container, SWT.BORDER);
	        txtProfileLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        txtProfileLocation.setText(profileLocation);
	        
	        btnFolderChooser = new Button(container, SWT.PUSH);
            btnFolderChooser.setText("Change folder");
            btnFolderChooser.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    DirectoryDialog dialog = new DirectoryDialog(btnFolderChooser.getShell());
                    String path = dialog.open();
                    if (path != null){
                    	txtProfileLocation.setText(path);	
                    }
                }
            });
            
	        return container;
	    }

	    @Override
	    protected void createButtonsForButtonBar(Composite parent) {
    		createButton(parent, IDialogConstants.OK_ID, "Install add-on", true);
    		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	    }

	    @Override
	    protected void okPressed() {
	        profileLocation = txtProfileLocation == null ? "" : txtProfileLocation.getText();
	        String debugPort = ((RemoteDebugDriverConnector)remoteDebugDriverConnector).getDebugPort();
	        File profileFolder = new File(profileLocation);
	        //Update old preferences if any
	        try {
				updatePreference(profileLocation, "webdriver_firefox_port", debugPort);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
	        
	        FirefoxProfile ffProfile = new FirefoxProfile();
	        ffProfile.addExtension("webdriver", new ClasspathExtension(FirefoxProfile.class, 
	        		"/" + FirefoxProfile.class.getPackage().getName().replace(".", "/") + "/webdriver.xpi"));
	        ffProfile.setPreference(FirefoxProfile.PORT_PREFERENCE, Integer.parseInt(debugPort));
	        File tmpProfileFolder = ffProfile.layoutOnDisk();
        	try{
        		for(File file : tmpProfileFolder.listFiles()){
	        		if(file.isFile()){
	        			FileUtils.copyFileToDirectory(file, profileFolder);
	        		}	
	        		else if(file.isDirectory()){
	        			FileUtils.copyDirectoryToDirectory(file, profileFolder);
	        		}
        		}
        	}
        	catch(Exception ex){
        		MessageDialog.openError(getShell(), "Error", ex.getMessage());
        	}
	        
	        try {
				FileUtils.forceDelete(tmpProfileFolder);
			} 
	        catch (IOException e) {
			}
	        
	        super.okPressed();
	    }
    }
}
