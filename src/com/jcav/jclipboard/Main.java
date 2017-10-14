package com.jcav.jclipboard;

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
