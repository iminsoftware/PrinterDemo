package com.imin.newprinterdemo;

public class FunctionBean {
    private String name;

    public FunctionBean(int id,String name,int resource) {
        this.id = id;
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int resource;
    private int id;
}
