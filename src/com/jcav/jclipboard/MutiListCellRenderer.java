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

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class MutiListCellRenderer implements ListCellRenderer<LogElement> {
    private JLabel renderLabel = new JLabel();
    private final JWindow infoWindow;
    private final JTextArea textArea;
    private final JLabel imageLabel;
    private final JLabel timeInfo;
    private final JScrollPane scrollPane;

    public MutiListCellRenderer(){

        infoWindow = new JWindow();
        infoWindow.setSize(Const.DETAILED_INFO_WIN_SIZE);

        JPanel main = new JPanel();
        main.setBorder(BorderFactory.createLineBorder(Color.black));
        main.setLayout(new BorderLayout());

        timeInfo = new JLabel();
        main.add(timeInfo, BorderLayout.SOUTH);

        scrollPane = new JScrollPane();
        main.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        imageLabel = new JLabel("", JLabel.CENTER);

        infoWindow.add(main);
        infoWindow.setAlwaysOnTop(true);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends LogElement> list,
            LogElement value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        JLabel label = renderLabel;
        label.setFont(list.getFont());
        label.setOpaque(list.isOpaque());
        label.setEnabled(list.isEnabled());

        if(isSelected){
            label.setForeground(list.getBackground());
            label.setBackground(list.getForeground());
            showDetailedInfomation(list, value, index);
        }else {
            label.setForeground(list.getForeground());
            label.setBackground(list.getBackground());
        }

        switch (value.getType()){
            case TEXT:
                String info = value.getText();
                if(info.length() > Const.MAX_CHR_LENGTH){
                    info = info.substring(0, Const.MAX_CHR_LENGTH - 2) + "..." + "  ";
                }
                StringBuilder sbuf = new StringBuilder();
                sbuf.append(info);

                if(info.length() < Const.MAX_CHR_LENGTH){
                    int count = Const.MAX_CHR_LENGTH - info.length();
                    while(count >= 0){
                        sbuf.append("  ");
                        count--;
                    }
                }
                sbuf.append("  ");

                sbuf.append(Const.DETAILED_DATE_FORMAT.format(value.getTimestamp()));
                label.setText(sbuf.toString());
                label.setIcon(Const.WORD_ICON);
                break;

            case IMAGE:
                String time = Const.DETAILED_DATE_FORMAT.format(value.getTimestamp());
                label.setText("Image : " + time);
                label.setIcon(Const.IMAGE_ICON);
                break;

            case FILES:
                time = Const.DETAILED_DATE_FORMAT.format(value.getTimestamp());
                label.setText("Files : " + time);
                label.setIcon(Const.FILE_ICON);
                break;
        }

        return label;
    }

    private void showDetailedInfomation(JList<? extends LogElement> list, LogElement value, int index) {
        Container containerForList = list.getParent();
        Point listPoint = containerForList.getLocationOnScreen();
        int x = listPoint.x + containerForList.getWidth();
        int y = listPoint.y;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Const.DETAILED_INFO_WIN_SIZE.width, height = Const.DETAILED_INFO_WIN_SIZE.height;
        if(x + width > screenSize.width){
            x -= containerForList.getWidth();
            y += containerForList.getHeight();
        }
        if(y + height > screenSize.height){
            y = containerForList.getLocationOnScreen().y - height;
        }

        infoWindow.setLocation(x, y);

        timeInfo.setText(Const.DETAILED_DATE_FORMAT.format(value.getTimestamp()));

        switch (value.getType()){
            case TEXT:
                textArea.setText(value.getText());
                scrollPane.setViewportView(textArea);
                break;

            case IMAGE:
                imageLabel.setIcon(new ImageIcon(value.getImage()));
                scrollPane.setViewportView(imageLabel);
                break;

            case FILES:
                JList<String>  fileList = new JList<String>(
                        value.getFiles().stream()
                        .map((file) -> file.getAbsolutePath())
                        .collect(
                                () -> new Vector<String>(),
                                (v, s) -> v.add(s),
                                (va, vb) -> va.addAll(vb)
                        )
                );
                scrollPane.setViewportView(fileList);
                break;
        }

        if(!infoWindow.isShowing()){
            infoWindow.setVisible(true);
        }
    }

    public void hide(){
        infoWindow.setAlwaysOnTop(false);
        infoWindow.setVisible(false);
    }
}
