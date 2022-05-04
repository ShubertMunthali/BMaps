package com.infbyte.bmap.ui;

import com.infbyte.bmap.color.BMapColor;
import com.infbyte.bmap.ui.listeners.CanvasMouseListener;
import com.infbyte.bmap.ui.listeners.ViewportMouseListener;
import com.infbyte.kotmap.BMap;
import com.infbyte.kotmap.BMapCanvas;
import sun.util.resources.cldr.naq.CalendarData_naq_NA;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import static jdk.nashorn.internal.objects.NativeError.printStackTrace;
import static sun.misc.Version.println;

public class BMapUI {

    private final JFrame jFrame = new JFrame();
    private final BMapCanvas canvas;
    private final BScrollPane scrollPane;
    private final BMap bMap;
    private static final int WINDOW_H = 400;
    private static final int WINDOW_W = 600;
    private static final Dimension minSize = new Dimension(WINDOW_W, WINDOW_H);

    private JMenu[] getMenus() {
        JMenu[] menuArray;
        JMenu locationMenu, viewMenu, helpMenu;
        locationMenu = new JMenu("Locations");
        viewMenu = new JMenu("View");
        helpMenu = new JMenu("Help");
        About aboutW = new About(jFrame);
        JMenuItem connectCustomer, searchCustomer, zoomToExtents, zoomIn, zoomOut, pan, about;
        connectCustomer = new JMenuItem("Connect customer");
        searchCustomer = new JMenuItem("Search customer");
        zoomToExtents = new JMenuItem("Zoom Extents");
        zoomIn = new JMenuItem("Zoom In");
        zoomOut = new JMenuItem("Zoom Out");
        pan = new JMenuItem("Pan");
        about = new JMenuItem("About");

        connectCustomer.addActionListener(e ->
                System.out.println("Connecting to customer...")
        );

        searchCustomer.addActionListener(e->
                System.out.println("Searching for the customer in database...")
        );

        zoomToExtents.addActionListener (e->
            System.out.println("View selected...")
        );

        zoomIn.addActionListener(e-> {
            double oldScale = bMap.getProj().getScaleFactor();
            this.bMap.getProj().setScaleFactor(oldScale * 10);
            this.canvas.repaint();
            System.out.println("Zooming in...");
        });

        zoomOut.addActionListener(e->
                System.out.println("Zooming out..."));

        pan.addActionListener(e->
                System.out.println("Panning..."));

        about.addActionListener(e -> {
            if(!aboutW.isShowing()) {
                aboutW.showWindow(jFrame);
            }
        });

        locationMenu.add(connectCustomer);
        locationMenu.add(searchCustomer);

        viewMenu.add(zoomToExtents);
        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);
        viewMenu.add(pan);

        helpMenu.add(about);

        menuArray = new JMenu[]{locationMenu, viewMenu, helpMenu};
        return menuArray;
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getMenus()[0]);
        menuBar.add(getMenus()[1]);
        menuBar.add(getMenus()[2]);
        return menuBar;
    }

    private BScrollPane getScrollView(BMapCanvas canvas){
        BScrollPane scrollPane = new BScrollPane(canvas);
        JViewport viewport = scrollPane.getViewport();
        canvas.scrollRectToVisible(new Rectangle(new Point((637656/100), canvas.getHeight() - (8299197/100)), viewport.getSize()));
        return scrollPane;
    }

    private BMapCanvas getMapCanvas(BMap bMap){
        return new BMapCanvas(bMap);
    }

    private void setIcon(JFrame jFrame){
        File imgFile = new File("C:\\Users\\User\\IdeaProjects\\BMaps\\src\\main\\kotlin\\com\\infbyte\\data\\bmaps_icon.png");
        Image bMapIcon;
        try{
            bMapIcon = ImageIO.read(imgFile);
            jFrame.setIconImage(bMapIcon);
        }
        catch(IOException e){
            printStackTrace(e);
        }
    }

    public BMapUI(BMap bMap) {
        jFrame.setJMenuBar(getMenuBar());
        jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setMinimumSize(minSize);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIcon(jFrame);
        this.bMap = bMap;
        this.canvas = getMapCanvas(bMap);
        this.scrollPane = getScrollView(this.canvas);
        jFrame.setContentPane(this.scrollPane);
        jFrame.setVisible(true);
    }

}

