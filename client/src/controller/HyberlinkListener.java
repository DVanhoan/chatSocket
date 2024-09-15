package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Arrays;

class HyberlinkListener extends AbstractAction {
    String filename;
    byte[] file;
    JFrame frame;

    public HyberlinkListener(String filename, byte[] file, JFrame frame) {
        this.filename = filename;
        this.file = Arrays.copyOf(file, file.length);
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        execute();
    }

    public void execute() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(filename));
        int rVal = fileChooser.showSaveDialog(frame.getParent());
        if (rVal == JFileChooser.APPROVE_OPTION) {

            // Mở file đã chọn sau đó lưu thông tin xuống file đó
            File saveFile = fileChooser.getSelectedFile();
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(saveFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Hiển thị JOptionPane cho người dùng có muốn mở file vừa tải về không
            int nextAction = JOptionPane.showConfirmDialog(null,
                    "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?",
                    "Successful", JOptionPane.YES_NO_OPTION);
            if (nextAction == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(saveFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bos != null) {
                try {
                    bos.write(this.file);
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}