package com.kms.katalon.objectspy.websocket.messages;

import com.kms.katalon.application.utils.VersionInfo;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.objectspy.websocket.AddonCommand;

public class KatalonVersionAddOnMessage extends AddonMessage {

	public KatalonVersionAddOnMessage() {
        super(AddonCommand.REQUEST_BROWSER_INFO,
                new KatalonVersionAddonMessageData(VersionUtil.getCurrentVersion()));
	}
	
	public static class KatalonVersionAddonMessageData{
		private String currentVersionString;
		
		public KatalonVersionAddonMessageData(VersionInfo currentVersionInfo){
			currentVersionString = currentVersionInfo.getVersion();
		}
		
		public String getCurrentVersionString(){
			return currentVersionString;
		}
	}

}
