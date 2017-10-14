package com.jcav.jclipboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

public class Const {
    public static long CLIPBOARD_LISTENING_STIME = 700;
    //                                    min  sec   mills
    public static long AUTO_SAVE_STIME = 1 * 10 * 1000;

    public static enum LogType {
        TEXT,
        IMAGE,
        FILES;
    }

    public static final Type LOG_LIST_TYPE = new TypeToken<List<LogElement>>() {
    }.getType();
    public static final Type DAILYLOG_TYPE = new TypeToken<DailyLog>() {
    }.getType();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String LOG_PATH = "./log/";
    public static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();
}



