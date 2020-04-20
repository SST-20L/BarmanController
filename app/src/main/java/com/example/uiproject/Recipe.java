package com.example.uiproject;

public class Recipe {
    private String name;
    private boolean status;

    Recipe(String inName, boolean inStatus){
        this.name = inName;
        this.status = inStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public boolean getStatus(){
        return status;
    }
}
