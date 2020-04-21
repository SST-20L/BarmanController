package com.example.objectclasses;

import java.io.Serializable;

public class RecipeItem implements Serializable {
    public String name;
    public int value;

    public RecipeItem(String inName, int inValue){
        name = inName;
        value = inValue;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
