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

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Main {

    public static void main(String[] argv){
        DataStore ds = new DataStore();
        ClipboardListener cl = new ClipboardListener(ds);
        cl.start();
        RechooseDialog rd = new RechooseDialog();
        rd.setVisible(true);
        rd.setVisible(false);

        Provider provider = Provider.getCurrentProvider(true);
        provider.register(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), listener -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            int x = mouse.x, y = mouse.y;
            int width = rd.getWidth(), height = rd.getHeight();

            if(x + width > screenSize.width){
                x -= width;
            }

            if(y + height > screenSize.height){
                y -= height;
            }

            DailyLog log = ds.getPrelog();
            rd.setShownLog(log);
            rd.setLocation(x, y);
            rd.setVisible(true);
        });
    }

}
