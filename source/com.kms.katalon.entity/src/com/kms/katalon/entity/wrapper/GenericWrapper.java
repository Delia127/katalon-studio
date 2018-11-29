package com.kms.katalon.entity.wrapper;

public class GenericWrapper<T> {
    protected T t;
    
    public void setObject(T t){
        this.t = t;
    }
    
    public T getObject(){
        return t;
    }

}
