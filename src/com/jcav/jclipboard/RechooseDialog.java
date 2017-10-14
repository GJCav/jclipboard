package com.jcav.jclipboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class RechooseDialog extends JFrame {
    private JList<LogElement> chooseList;
    private DefaultListModel<LogElement> listModel;
    private JScrollPane listPane;
    private MutiListCellRenderer cellRenderer;
    private int dayOffset = 0;
    private Calendar curCalendar = Calendar.getInstance();
    private final Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();

    public RechooseDialog() {

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        initUI();
        this.addWindowListener(windowAdapter);
        this.addWindowFocusListener(windowAdapter);
        this.addWindowStateListener(windowAdapter);
    }

    private void initUI() {
        this.setTitle("Clipboard log");
        this.setSize(Const.CHOOSE_WIN_SIZE);
        this.setUndecorated(true);

        chooseList = new JList<LogElement>();
        chooseList.addKeyListener(keyAdapter);

        listModel = new DefaultListModel<>();
        chooseList.setModel(listModel);

        cellRenderer = new MutiListCellRenderer();
        chooseList.setCellRenderer(cellRenderer);

        listPane = new JScrollPane(chooseList);
        listPane.addKeyListener(keyAdapter);
        this.add(listPane);
    }

    public void setShownLog(DailyLog log) {
        listModel.removeAllElements();
        log.getElements()
                .stream()
                .sorted((a, b) -> {
                    long cmp = b.getTimestamp() - a.getTimestamp();
                    if (cmp > 0) {
                        return 1;
                    } else if (cmp < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .forEach(listModel::addElement);
    }

    private WindowAdapter windowAdapter = new WindowAdapter() {

        @Override
        public void windowLostFocus(WindowEvent e) {
            setVisible(false);
            cellRenderer.hide();
        }
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char chr = e.getKeyChar();
            if (chr == KeyEvent.VK_ESCAPE) {
                cellRenderer.hide();
                RechooseDialog.this.setVisible(false);
            } else if (chr == '\r' || chr == '\n') {
                cellRenderer.hide();
                RechooseDialog.this.setVisible(false);

                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

                LogElement element = chooseList.getSelectedValue();
                switch (element.getType()) {
                    case TEXT:
                        cb.setContents(new StringSelection(element.getText()), null);
                        break;

                    case IMAGE:
                        cb.setContents(new Transferable() {
                            @Override
                            public DataFlavor[] getTransferDataFlavors() {
                                return new DataFlavor[]{DataFlavor.imageFlavor};
                            }

                            @Override
                            public boolean isDataFlavorSupported(DataFlavor flavor) {
                                return DataFlavor.imageFlavor.equals(flavor);
                            }

                            @Override
                            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                                if (isDataFlavorSupported(flavor)) {
                                    return element.getImage();
                                } else {
                                    throw new UnsupportedFlavorException(flavor);
                                }
                            }
                        }, null);

                        break;

                    case FILES:
                        /*cb.setContents(new Transferable() {
                            @Override
                            public DataFlavor[] getTransferDataFlavors() {
                                return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                            }

                            @Override
                            public boolean isDataFlavorSupported(DataFlavor flavor) {
                                return DataFlavor.javaFileListFlavor.equals(flavor);
                            }

                            @Override
                            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                                if (isDataFlavorSupported(flavor)) {
                                    return element.getFiles();
                                } else {
                                    throw new UnsupportedFlavorException(flavor);
                                }
                            }
                        }, null);*/

                        break;
                }

            } else {
                int index = chooseList.getSelectedIndex();
                if (chr == 'a' || chr == KeyEvent.VK_LEFT) {
                    File logfile = findPreviousLog(curCalendar);
                    if (logfile != null) {
                        try {
                            DailyLog log = DailyLog.loadFromFile(logfile, gson);
                            setShownLog(log);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if (chr == 'd' || chr == KeyEvent.VK_R) {
                    File logfile = findNextLog(curCalendar);
                    if (logfile != null) {
                        try {
                            DailyLog log = DailyLog.loadFromFile(logfile, gson);
                            setShownLog(log);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                chooseList.setSelectedIndex(index);

            }
        }
    };

    private File findPreviousLog(Calendar curCalendar) {
        curCalendar.setTime(new Date());
        curCalendar.set(Calendar.DAY_OF_YEAR, curCalendar.get(Calendar.DAY_OF_YEAR) + dayOffset);
        File file = null;
        do {
            if (dayOffset - 1 <= -30)
                return null;

            dayOffset--;
            curCalendar.set(Calendar.DAY_OF_YEAR, curCalendar.get(Calendar.DAY_OF_YEAR) - 1);
            Date prevDate = curCalendar.getTime();
            String name = Const.DATE_FORMAT.format(prevDate) + ".json";
            file = new File("log", name);

        } while (!file.exists());

        return file;
    }

    private File findNextLog(Calendar curCalendar) {
        curCalendar.setTime(new Date());
        curCalendar.set(Calendar.DAY_OF_YEAR, curCalendar.get(Calendar.DAY_OF_YEAR) + dayOffset);

        File file = null;
        do {
            if (dayOffset + 1 > 0) {
                return null;
            }

            dayOffset++;

            curCalendar.set(Calendar.DAY_OF_YEAR, curCalendar.get(Calendar.DAY_OF_YEAR) + 1);
            Date nextDate = curCalendar.getTime();
            String name = Const.DATE_FORMAT.format(nextDate) + ".json";

            file = new File("log", name);
        } while (!file.exists());

        return file;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            dayOffset = 0;
        }

    }

}











