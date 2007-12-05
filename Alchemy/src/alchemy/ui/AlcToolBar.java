/**
 * AlcToolBar.java
 *
 * Created on November 24, 2007, 3:08 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.*;

public class AlcToolBar extends JPanel implements ActionListener, ItemListener, AlcConstants { // Extend JPanel rather than JComponent so the background can be set
    
    /** Reference to the root */
    private final AlcMain root;
    
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** Height of the ToolBar */
    private final static int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = toolBarHeight;
    
    /** Keep track of the windowSize */
    public Dimension windowSize;
    
    /** ToolBar Background Colour */
    public final static Color toolBarBgColour = new Color(225, 225, 225);
    public final static Color toolBarBgStartColour = new Color(235, 235, 235);
    public final static Color toolBarBgEndColour = new Color(215, 215, 215);
    public final static Color toolBarLineColour = new Color(140, 140, 140);
    public final static Color toolBarHighlightColour = new Color(231, 231, 231);
    
    
    /** ToolBar Text Size */
    private final static int toolBarTextSize = 10;
    /** ToolBar Popup Menu Y Location */
    public final static int uiPopupMenuY = toolBarHeight - 10;
    
    /** Popup buttons for the create and affect button in the toolbar - these are declared global so we can hide the popup when hiding the toolbar */
    private AlcPopupButton createButton, affectButton;
    
    /** The main tool bar inside the toolbar */
    private AlcMainToolBar mainToolBar;
    /** The sub toolbars inside the toolbar */
    private AlcSubToolBar[] subToolBars;
    /** The number of subtoolbars modules currently loaded */
    private int numberOfCurrentSubToolBars = 0;
    
    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(AlcMain root) {
        
        // General Toolbar settings
        this.root = root;
        // Make this a Box Layout so all submenus are stacked below
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Turn off the visibility untill the mouse enters the top of the screen
        setToolBarVisible(false);
        
        // Intialise the array of subToolBars - maximum size is that off all the affect modules plus one (create module)
        subToolBars = new AlcSubToolBar[root.getNumberOfAffectModules() + 1];
        
        loadToolBar();
    }
    
    /** Load the tool bar */
    private void loadToolBar(){
        // Create the main toolbar
        mainToolBar = new AlcMainToolBar(root);
        
        // Buttons in the main toolbar
        // Align LEFT
        JPanel toolBarLeft = new JPanel();
        toolBarLeft.setOpaque(false);   // Turn off the background
        
        // TODO - Change the Line/Solid to a single toggle button?
        // Line Button
        AlcButton lineButton = new AlcButton(this, "Line", "Draw as a Line", getUrlPath("data/icon.png"));
        lineButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                root.canvas.setStyle(LINE);
            }
        }
        );
        toolBarLeft.add(lineButton);
        
        AlcButton solidButton = new AlcButton(this, "Solid", "Draw as a Solid Shape", getUrlPath("data/icon.png"));
        solidButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                root.canvas.setStyle(SOLID);
            }
        }
        );
        toolBarLeft.add(solidButton);
        
        // TODO - Line Thickness button
        // TODO - Toggle Black/White Button
        // TODO - Transparency slider/numberbox
        
        //JToggleButton tbtn = new JToggleButton("Yes");
        //toolBarLeft.add(tbtn);
        mainToolBar.add(toolBarLeft, BorderLayout.LINE_START);
        
        
        // Align Right
        JPanel toolBarRight = new JPanel();
        toolBarRight.setOpaque(false);
        // Create
        createButton = new AlcPopupButton(this, "Create", "Create Shapes", getUrlPath("data/create.png"), root.creates);
        toolBarRight.add(createButton);
        // Affect
        affectButton = new AlcPopupButton(this, "Affect", "Affect Shapes", getUrlPath("data/create.png"), root.affects);
        toolBarRight.add(affectButton);
        
        mainToolBar.add(toolBarRight, BorderLayout.CENTER);
        this.add(mainToolBar);
        
        
        
        //JToggleButton tbtn = new JToggleButton("Yes");
        //this.add(tbtn);
    }
    
    public void resizeToolBar(){
        Dimension windowSize = new Dimension(this.windowSize.width, getTotalHeight());
        resizeToolBar(windowSize);
    }
    
    public void resizeToolBar(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, getTotalHeight());
        this.windowSize = windowSize;
    }
    
    
    /** Set the visibility of the UI Toolbar */
    public void setToolBarVisible(boolean b){
        this.setVisible(b);
        // Turn off the popup(s) when we leave the toolbar area
        if(!b){
            if(createButton != null) createButton.hidePopup();
            if(affectButton != null) affectButton.hidePopup();
        }
        toolBarVisible = b;
    }
    
    private void refreshToolBar(){
        // Recalculate the total height of the tool bar
        calculateTotalHeight();
        // Then resize it
        resizeToolBar();
        // And refresh it
        this.revalidate();
    }
    
    /** Add a Create Module sub-toolbar */
    public void addSubToolBar(AlcSubToolBar subToolBar){
        
        numberOfCurrentSubToolBars++;
        
        // Retrive the subtoolbars module index and add one to offset for the create toolbar
        int index = subToolBar.getIndex()+1;
        int type = subToolBar.getModuleType();
        
        switch(type){
            
            case CREATE:
                subToolBars[0] = subToolBar;
                subToolBars[0].setLocation(0, getToolBarHeight());
                this.add(subToolBars[0]);
                break;
                
            case AFFECT:
                if(subToolBars[index] != null){
                    this.remove(subToolBars[index]);
                    subToolBars[index] = null;
                }
                subToolBars[index] = subToolBar;
                subToolBars[index].setLocation(0, totalHeight);
                this.add(subToolBars[index]);
                break;
        }
        
        // Refresh the toolbar with the new contents
        refreshToolBar();
    }
    
    public void removeSubToolBar(int index){
        // If there there is a subtool bar loaded
        if(subToolBars[index] != null){
            this.remove(subToolBars[index]);
            subToolBars[index] = null;
            numberOfCurrentSubToolBars--;
            // Refresh the toolbar with the new contents
            refreshToolBar();
        }
    }
    
    // GETTERS
    /** Return the visibility of the UI Toolbar */
    public boolean getToolBarVisible(){
        return toolBarVisible;
    }
    
    /** Return the height of the UI Toolbar */
    public int getToolBarHeight(){
        return toolBarHeight;
    }
    
    /** Return the height of the UI Toolbar */
    public int getToolBarTextSize(){
        return toolBarTextSize;
    }
    
    
    
    // IMAGE LOADING FUNCTIONS
    /** Returns a URL from a String, or null if the path was invalid. */
    public URL getUrlPath(String path){
        // Path to the file from the main class
        URL imgUrl = AlcMain.class.getResource(path);
        if (imgUrl != null) {
            return imgUrl;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /** Function to append a string to the end of a given URL */
    public static URL appendStringToUrl(URL url, String append){
        String urlString = url.toString();
        URL newUrl = null;
        // Look for a file extension
        int dot = url.toString().lastIndexOf(".");
        if(dot == -1){
            try {
                // If no file extension return as is
                newUrl = new URL(urlString + append);
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        } else{
            try {
                // Append the string before the file extension
                newUrl = new URL(urlString.substring(0, dot) + append + urlString.substring(dot));
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        }
        return newUrl;
    }
    
    
    /** Returns an ImageIcon from a String, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path){
        
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return createImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + imgUrl.toString());
            return null;
        }
    }
    
    /** Returns an ImageIcon from a URL, or null if the path was invalid. */
    public ImageIcon createImageIcon(URL imgUrl) {
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Check the icon actually exists - bit of a hack!
            if(icon.getIconWidth() > 0) {
                return icon;
            }
        }
        //System.err.println("Couldn't find file: " + imgUrl.toString());
        return null;
    }
    
    // Returns just the class name -- no package info.
    public String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
    
    
    /** Calculate the total height of the toolbar and its subtoolbars */
    public void calculateTotalHeight(){
        // Start with the main toolbar height
        int totalHeight = mainToolBar.getHeight();
        
        // Only run the loop if there are subtoolbars loaded
        if(numberOfCurrentSubToolBars > 0){
            for (int i = 0; i < subToolBars.length; i++) {
                if(subToolBars[i] != null)
                    totalHeight += subToolBars[i].getHeight();
            }
        }
        
        this.totalHeight = totalHeight;
    }
    
    /** Return the total height of the toolbar and its subtoolbars */
    public int getTotalHeight(){
        return totalHeight;
    }
    
    
    public void loadCreate(int index){
        root.setCurrentCreate(index);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        
        AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem)e.getSource();
        
        if(source.getModuleType() == CREATE){
            // Check that the module is not already selected
            if(root.currentCreate != source.getIndex()){
                // Remove the subtoolbar of the create module
                removeSubToolBar(0);
                root.setCurrentCreate( source.getIndex() );
            }
            
        }
        
        
    }
    
    public void itemStateChanged(ItemEvent e) {
        
        AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem)e.getItemSelectable();
        
        
        if(source.getModuleType() == AFFECT){
            // SELECTED
            if (e.getStateChange() == ItemEvent.SELECTED) {
                
                //System.out.println( index );
                
                root.addAffect( source.getIndex() );
                
                // DESELECTED
            } else {
                
                root.removeAffect( source.getIndex() );
                // Index is offset to allow for the create module to always be first
                removeSubToolBar( source.getIndex() + 1 );
                
            }
        }
        
    }
    
    
}