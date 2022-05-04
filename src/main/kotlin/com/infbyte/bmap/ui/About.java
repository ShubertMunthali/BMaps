package com.infbyte.bmap.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.security.Key;

public class About extends JDialog {

    public static final String version = "Sigerege 1.0.0.0";

    public static final String copyRight  = "Copyright " + (char) 169 + " 2018-2022 Infbyte Technologies";

    private Image getImage(){
        File imgFile = new File("C:\\Users\\User\\IdeaProjects\\BMaps\\src\\main\\kotlin\\com\\infbyte\\data\\bmaps_logo.png");
        Image image = null;
        try {
             image = ImageIO.read(imgFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    private void addComponents(Dialog parent){
        Dimension size = new Dimension(this.getWidth(), 25);
        final JPanel jPanel = new JPanel();
        LayoutManager boxLayout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);

        JLabel bMapsLogo = new JLabel( new Icon(){
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D graphics = (Graphics2D) g;
                graphics.drawImage(getImage(),(parent.getWidth() - 300)/2, y, null);
            }

            @Override
            public int getIconHeight() {
                return 100;
            }

            @Override
            public int getIconWidth() {
                return parent.getWidth();
            }
        },JLabel.CENTER);
        bMapsLogo.setPreferredSize(new Dimension(parent.getWidth(), 200));

        JTextField versionText = new JTextField(version);
        versionText.setPreferredSize(size);
        versionText.setHorizontalAlignment(JTextField.CENTER);
        versionText.setEditable(false);
        versionText.setBorder(null);

        JTextField copyRightText = new JTextField(copyRight);
        copyRightText.setPreferredSize(size);
        copyRightText.setHorizontalAlignment(JTextField.CENTER);
        copyRightText.setEditable(false);
        copyRightText.setBorder(null);

        jPanel.add(bMapsLogo);
        jPanel.add(versionText);
        jPanel.add(copyRightText);
        this.add(jPanel);
    }

    private void configureWindow(){
        String about = "About";
        Dimension maxSize = new Dimension(500, 300);
        Dimension minSize = new Dimension(500, 300);
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setMaximumSize(maxSize);
        this.setMinimumSize(minSize);
        this.setResizable(false);
        this.setTitle(about);
        this.addKeyListener(new AboutKeyListener(this));
    }

    public void showWindow(JFrame parent){
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    private static class AboutKeyListener extends KeyAdapter {
        private final JDialog window;
        @Override
        public void keyPressed(KeyEvent e) {
            char key = e.getKeyChar();
            System.out.println(key);
            if (key == (char) 27) {
                this.window.dispose();
            }
        }
        public AboutKeyListener(JDialog window){
            this.window = window;
        }
    }

    public About(JFrame parent){
        super(parent);
        addComponents(this);
        configureWindow();
    }
}
