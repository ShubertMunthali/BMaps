package com.infbyte.bmap.ui;

import com.infbyte.bmap.color.BMapColor;
import com.infbyte.bmap.ui.listeners.ViewportMouseListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class BScrollPane extends JScrollPane{

    private final ViewportMouseListener viewportMouseListener = new ViewportMouseListener();

    private void setViewportMouseListener(){
        JViewport viewport = this.getViewport();
        viewport.addMouseListener(viewportMouseListener);
        viewport.addMouseMotionListener(viewportMouseListener);
        viewport.addMouseWheelListener(viewportMouseListener);
    }

    public BScrollPane(Component view){
        super(view);
        setViewportMouseListener();
        this.setBackground(BMapColor.getWaterBody());
        this.setViewportBorder(new LineBorder(Color.BLUE));
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
    }
}
