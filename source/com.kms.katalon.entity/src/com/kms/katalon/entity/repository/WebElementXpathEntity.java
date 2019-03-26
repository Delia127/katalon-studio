package com.kms.katalon.entity.repository;

import org.apache.commons.lang.builder.EqualsBuilder;


public class WebElementXpathEntity extends WebElementPropertyEntity {

    public static final String ref_element = "ref_element";
    
    private static final long serialVersionUID = 1L;

    public static final String defaultMatchCondition = "equals";
    
    private String name;

    private String value;

    private String matchCondition = defaultMatchCondition;
    
    private boolean isSelected = false;

    public WebElementXpathEntity() {
    }

    public WebElementXpathEntity(String name, String value) {
        this.name = name;
        this.value = getCorrectForm(value);
        this.isSelected = true;
    }
    
    public WebElementXpathEntity(String name, String value, boolean isSelected) {
        this.name = name;
        this.value =  getCorrectForm(value);
        this.isSelected = isSelected;
    }
    
    // Process any ill-formed value in the future, for now it's just one case
    private String getCorrectForm(String value){
    	return value.replace("xpath=", "");
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMatchCondition() {
        return this.matchCondition;
    }

    public void setMatchCondition(String matchCondition) {
        if (matchCondition.equals("is exactly")) {
            this.matchCondition = MATCH_CONDITION.EQUAL.getText();
        } else {
            this.matchCondition = matchCondition;
        }
    }
    

    public Boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public WebElementXpathEntity clone() {
        return new WebElementXpathEntity(name, value, isSelected);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WebElementXpathEntity)) {
            return false;
        }
        WebElementXpathEntity other = (WebElementXpathEntity) obj;
        
        // Compare BOTH names and values, otherwise it will causes very obscure behaviors
        return new EqualsBuilder().append(this.getName(), other.getName())
        		.append(this.getValue(), other.getValue()).isEquals();
    }
}
