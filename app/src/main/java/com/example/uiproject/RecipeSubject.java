package com.example.uiproject;

public class RecipeSubject {
    private String name;
    private int value;
    private boolean status;

    RecipeSubject(String inName, int inValue, boolean inStatus){
        this.name = inName;
        this.value = inValue;
        this.status = inStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(boolean status) {
        this.status = status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public boolean getStatus(){
        return status;
    }

}
