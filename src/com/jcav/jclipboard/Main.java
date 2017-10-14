package com.jcav.jclipboard;

public class Main {

    public static void main(String[] argv){
        DataStore ds = new DataStore();
        new ClipboardListener(ds).start();
    }

}
