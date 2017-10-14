package com.jcav.jclipboard;

/*
 * Copyright (c) 2017.  The JClipboard Author (JCav)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DETAILED_DATE_FORMAT = new SimpleDateFormat("yy-MM-dd  HH:mm:ss");
    public static final String LOG_PATH = "./log/";
    public static final Type LOG_LIST_TYPE = new TypeToken<List<LogElement>>() {
    }.getType();
    public static final Type DAILYLOG_TYPE = new TypeToken<DailyLog>() {
    }.getType();
    //                                    min  sec   mills
    public static long AUTO_SAVE_STIME = 1 * 10 * 1000;
    public static long CLIPBOARD_LISTENING_STIME = 700;

    public static enum LogType {
        TEXT,
        IMAGE,
        FILES;
    }

    public static final KeyStroke HOTKEY = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.VK_WINDOWS);
    public static final int ITEM_HIGHT = 16;
    public static final int MAX_CHR_LENGTH = 30;

    public static final Icon WORD_ICON = new ImageIcon("word_type.png");
    public static final Icon IMAGE_ICON = new ImageIcon("img_type.png");
    public static final Icon FILE_ICON = new ImageIcon("file_type.png");
    public static final Dimension DETAILED_INFO_WIN_SIZE = new Dimension(400, 300);
    public static final Dimension CHOOSE_WIN_SIZE = new Dimension(400, 200);
    public static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();
}










