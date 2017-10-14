package com.jcav.jclipboard;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LogElement {

    private Const.LogType type = null;
    private String text = null;
    private String imagehex = null;
    private List<File> files = null;
    private long timestamp = System.currentTimeMillis();

    public LogElement(String text) {
        type = Const.LogType.TEXT;
        this.text = text;
    }

    public LogElement(BufferedImage image){
        type = Const.LogType.IMAGE;
        this.setImage(image);
    }

    public LogElement(List<File> files){
        type = Const.LogType.FILES;
        this.files = files;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if(type != Const.LogType.TEXT){
            throw new UnsupportedOperationException("This is not a text log.");
        }
        this.text = text;
    }

    public BufferedImage getImage() {
        if(type != Const.LogType.IMAGE){
            throw new UnsupportedOperationException("This is a not image log.");
        }

        ByteArrayInputStream bytein = new ByteArrayInputStream(parseHexBinary(imagehex));
        BufferedImage img = null;
        try {
            GZIPInputStream in = new GZIPInputStream(bytein);
            img = ImageIO.read(in);
            in.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Decode image failed. At LogElement.getImage()");
        }
        return img;
    }

    public void setImage(BufferedImage image) {
        if(type != Const.LogType.IMAGE){
            throw new UnsupportedOperationException("It's not a image log.");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            GZIPOutputStream out = new GZIPOutputStream(buffer);
            ImageIO.write(image, "jpg", out);
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Encode image failed. At LogElement.setImage(BufferedImage)");
        }
        byte[] data = buffer.toByteArray();
        imagehex = printHexBinary(data);
    }

    public List<File> getFiles() {
        if(type != Const.LogType.FILES){
            throw new UnsupportedOperationException("This is not a files log.");
        }
        return files;
    }

    public void setFiles(List<File> files) {
        if(type != Const.LogType.FILES){
            throw new UnsupportedOperationException("This is not a files log.");
        }
        this.files = files;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Const.LogType getType() {
        return type;
    }

    private static byte[] parseHexBinary(String s){
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    private static int hexToBin(char hex){
        if ('0' <= hex && hex <= '9') {
            return hex - '0';
        }
        if ('A' <= hex && hex <= 'F') {
            return hex - 'A' + 10;
        }
        if ('a' <= hex && hex <= 'f') {
            return hex - 'a' + 10;
        }
        return -1;
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    private static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
}
