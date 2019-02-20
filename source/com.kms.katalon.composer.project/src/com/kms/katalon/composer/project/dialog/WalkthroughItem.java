package com.kms.katalon.composer.project.dialog;

import java.util.List;

public class WalkthroughItem {
	private String primaryInstruction = "";
	private List<SecondaryLinkItem> secondaryLinkItems;
	private String eventConstant;
	
	/**
	 * 
	 * @param primaryInstruction Text of this instruction
	 * @param eventConstant Which EventConstant corresponds to this instruction
	 */
	public WalkthroughItem(String primaryInstruction, String eventConstant){
		this.primaryInstruction = primaryInstruction;
		this.eventConstant = eventConstant;
	}
	
	public List<SecondaryLinkItem> getSecondaryLinkItems(){
		return this.secondaryLinkItems;
	}
	
	public void setSecondaryLinkItems(List<SecondaryLinkItem> items){
		this.secondaryLinkItems = items;
	}
	
	public String getPrimaryInstruction(){
		return this.primaryInstruction;
	}
	
	public void setPrimaryInstruction(String instr){
		this.primaryInstruction = instr;
	}
	
	public void registerEvent(String eventConstant){
		this.eventConstant = eventConstant;
	}
	
	public String getRegisteredEvent(){
		return this.eventConstant;
	}
	
	public static class SecondaryLinkItem {
		private String secondaryLinkText = "";
		private String secondaryUrl = "";
		
		public SecondaryLinkItem(String text, String url){
			this.secondaryLinkText = text;
			this.secondaryUrl = url;
		}
		
		public String getText(){
			return this.secondaryLinkText;
		}
		
		public String getLink(){
			return this.secondaryUrl;
		}
	}
}
