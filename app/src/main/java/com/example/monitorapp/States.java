package com.example.monitorapp;

public class States {
    private static States mInstance= null;

    public Boolean isServerOnline;

    protected States(){
        isServerOnline = true;
    }

    public static synchronized States getInstance() {
        if(null == mInstance){
            mInstance = new States();
        }
        return mInstance;
    }
}
