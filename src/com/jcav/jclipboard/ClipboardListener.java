package com.jcav.jclipboard;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ClipboardListener {
    private final Runnable listener = () -> {
        while (true) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable context = clipboard.getContents(null);
            // System.out.println("get context");
            if (context != null) {
                try {
                    // System.out.println("process context");
                    processContext(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    };
    private String lastText = "";
    private BufferedImage lastImage = null;
    private int lastFileListHash = 0;

    private void processContext(Transferable context) throws IOException, UnsupportedFlavorException {
        if (context.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String newText = getClipboardAsText(context);
            if (lastText == null) {
                lastText = newText;
            } else if (!lastText.equals(newText)) {
                lastText = newText;
                //System.out.println(newText);
                //store.addLog(newText);
            }

        } else if (context.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage newImage = toBufferedImage(getClipboardAsImage(context));

            if (lastImage == null) {
                lastImage = newImage;
                //store.addLog(lastImage);
            } else if (!isSameImage(lastImage, newImage)) {
                lastImage = newImage;
                //store.addLog(lastImage);
            }

        } else if (context.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            List<File> list = getClipboardAsFileList(context);
            int preHashCode = list.hashCode();

            if (lastFileListHash != list.hashCode()) {
                lastFileListHash = preHashCode;
                //store.addLog(list);
            }
        }
    }

    private boolean isSameImage(BufferedImage a, BufferedImage b) {
        if (a.getWidth(null) != b.getWidth(null) || a.getHeight(null) != b.getHeight(null))
            return false;
        for (int x = 0; x < a.getWidth(null); x++) {
            for (int y = 0; y < a.getHeight(null); y++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private BufferedImage toBufferedImage(Image image) {
        BufferedImage bufImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_3BYTE_BGR
        );

        Graphics2D g = bufImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bufImage;
    }

    private Image getClipboardAsImage(Transferable context) throws IOException, UnsupportedFlavorException {
        return (Image) context.getTransferData(DataFlavor.imageFlavor);
    }

    private List<File> getClipboardAsFileList(Transferable context) throws IOException, UnsupportedFlavorException {
        return (List<File>) context.getTransferData(DataFlavor.javaFileListFlavor);
    }

    private String getClipboardAsText(Transferable context) throws IOException, UnsupportedFlavorException {
        return (String) context.getTransferData(DataFlavor.stringFlavor);
    }

    public void start(){
        new Thread(listener, "ClipboardListener").start();
    }
}
