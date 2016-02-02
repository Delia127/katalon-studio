package com.kms.katalon.composer.webui.recorder.action;

public interface IHTMLAction {
    public class HTMLActionParam {
        private String name;
        private Class<?> clazz;
        public HTMLActionParam(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
        public String getName() {
            return name;
        }
        public Class<?> getClazz() {
            return clazz;
        }
    }
    public String getName();
    public String getMappedKeywordClassSimpleName();
    public String getMappedKeywordClassName();
    public String getMappedKeywordMethod();
    public boolean hasElement();
    public boolean hasInput();
    public HTMLActionParam[] getParams();
}
