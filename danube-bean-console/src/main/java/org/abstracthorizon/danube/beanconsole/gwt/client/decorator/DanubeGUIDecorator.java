/*
 * Copyright (c) 2009 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Criterion Design Concepts  - initial API and implementation
 *   
 */
package org.abstracthorizon.danube.beanconsole.gwt.client.decorator;

import org.abstracthorizon.danube.beanconsole.gwt.client.ClientServerTransaction;
import org.abstracthorizon.danube.beanconsole.gwt.client.MainConsole;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * A Decorator Layout which adds bespoke graphics to the Danube Bean Console.
 * This class extends the abstract DBCDecorator class whcih contains methods that need to be 
 * implemented by all decorator classes.
 *
 * @author Mile Lukic
 */
public class DanubeGUIDecorator extends DBCDecorator{
    private VLayout outerLayout = new VLayout();
    private HLayout ftrLayout = new HLayout();
    private HLayout ftrSpacerLayout = new HLayout();
    private DataModel model;
    
    /**
     * Constructor.
     */
    public DanubeGUIDecorator(DataModel dataModel) {
        model = dataModel;
        createDecorator();
    }

    /**
     * Creates the Layout containing the graphics elements of the Danube Bean Console. The header
     * and footer graphics are built using images added to child layouts. These are composited 
     * inside parent Layouts to generate the required look.
     */
    private void createDecorator() {
        //create the frame containing the GUI images
        outerLayout.setSize("100%", "100%");
        HLayout hdrLayout = new HLayout();
        hdrLayout.setSize("100%", "31px");
        hdrLayout.setMinWidth(600);

        VLayout title = getTitle(); 
        Img spacerImg1 = new Img("dbc_hdr_pxl.png");
        spacerImg1.setImageType(ImageStyle.TILE);
        spacerImg1.setMinWidth(2);
        spacerImg1.setSize("100%", "31px");
        
        VLayout buttons = getButtons();
 
        outerLayout.addMember(hdrLayout);

        title.setLeft(0);
        title.setTop(0);
        buttons.setTop(0);
        buttons.setLayoutAlign(Alignment.RIGHT);
        hdrLayout.addMember(title);
        hdrLayout.addMember(spacerImg1);
        hdrLayout.addMember(buttons);
        
        ftrLayout.setSize("100%", "20px");
        ftrLayout.setMinWidth(600);
        ftrLayout.setLayoutAlign(VerticalAlignment.BOTTOM);
        ftrSpacerLayout.setSize("100%", "20px");
        Img ahLogoImg = new Img("dbc_footer_ah_logo.png", 321, 20); 
        ahLogoImg.setImageType(ImageStyle.NORMAL);
        ahLogoImg.setMinWidth(321);
        ahLogoImg.setCursor(Cursor.HAND);
        ahLogoImg.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open("http://www.abstracthorizon.org/", "Abstract Horizon", null);
            } 
        });
        
        ftrSpacerLayout.setLeft(0);
        ftrSpacerLayout.setBackgroundImage("dbc_footer_pxl.png");
        ahLogoImg.setLayoutAlign(Alignment.RIGHT);
        ahLogoImg.setTop(0);
        ftrLayout.addMember(ftrSpacerLayout);
        ftrLayout.addMember(ahLogoImg);
    }

    /**
     * Creates and returns a component containing the danube bean console title and logo. The logo provides
     * a link to the danube web page.
     * 
     * @return title - a Layout component containing the GUI title image and link to the Danube website
     */
    private VLayout getTitle() {
        VLayout title = new VLayout();
        
        HLayout titleLayoutLine1 = new HLayout();
        titleLayoutLine1.setSize("287px", "8px");
        Img titleImg1 = new Img("dbc_title_1.png", 37, 8);
        Img titleImg2 = new Img("dbc_title_2.png", 30, 8);
        Img titleImg3 = new Img("dbc_title_3.png", 220, 8);
        titleLayoutLine1.addMember(titleImg1);
        titleLayoutLine1.addMember(titleImg2);
        titleLayoutLine1.addMember(titleImg3);
        
        HLayout titleLayoutLine2 = new HLayout();
        titleLayoutLine2.setSize("287px", "18px");
        Img titleImg4 = new Img("dbc_title_4.png", 37, 18);
        Img titleImg5 = new Img("dbc_title_5.png", 30, 18);
        titleImg5.setCursor(Cursor.HAND);
        titleImg5.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open("http://danube.abstracthorizon.org/", "Danube", null);
            } 
        });
        Img titleImg6 = new Img("dbc_title_6.png", 220, 18);
        titleLayoutLine2.addMember(titleImg4);
        titleLayoutLine2.addMember(titleImg5);
        titleLayoutLine2.addMember(titleImg6);
        
        HLayout titleLayoutLine3 = new HLayout();
        titleLayoutLine3.setSize("287px", "5px");
        Img titleImg7 = new Img("dbc_title_7.png", 37, 5);
        Img titleImg8 = new Img("dbc_title_8.png", 30, 5);
        Img titleImg9 = new Img("dbc_title_9.png", 220, 5);
        titleLayoutLine3.addMember(titleImg7);
        titleLayoutLine3.addMember(titleImg8);
        titleLayoutLine3.addMember(titleImg9);
        
        title.addMember(titleLayoutLine1);
        title.addMember(titleLayoutLine2);
        title.addMember(titleLayoutLine3);
        
        return title;
    }
    
    /**
     * Creates and returns a component containing graphical buttons to be displayed in the 
     * header GUI in the Danube Bean Console. Click handlers are added to the sections of 
     * the image representing the buttons.  
     * 
     * @return buttons - a Layout component containing the GUI header buttons
     */
    private VLayout getButtons() {
        VLayout buttons = new VLayout();
        
        HLayout btnLayoutLine1 = new HLayout();
        btnLayoutLine1.setSize("275px", "9px");
        Img btnImg1 = new Img("dbc_buttons_map_1.png", 21, 9);
        Img btnImg2 = new Img("dbc_buttons_map_2.png", 52, 9);
        Img btnImg3 = new Img("dbc_buttons_map_3.png", 27, 9);
        Img btnImg4 = new Img("dbc_buttons_map_4.png", 52, 9);
        Img btnImg5 = new Img("dbc_buttons_map_5.png", 24, 9);
        Img btnImg6 = new Img("dbc_buttons_map_6.png", 52, 9);
        Img btnImg7 = new Img("dbc_buttons_map_7.png", 47, 9);
        btnLayoutLine1.addMember(btnImg1);
        btnLayoutLine1.addMember(btnImg2);
        btnLayoutLine1.addMember(btnImg3);
        btnLayoutLine1.addMember(btnImg4);
        btnLayoutLine1.addMember(btnImg5);
        btnLayoutLine1.addMember(btnImg6);
        btnLayoutLine1.addMember(btnImg7);
        
        HLayout btnLayoutLine2 = new HLayout();
        btnLayoutLine2.setSize("275px", "11px");
        Img btnImg8 = new Img("dbc_buttons_map_8.png", 21, 11);
        Img btnImg9 = new Img("dbc_buttons_map_9.png", 52, 11);
        btnImg9.setCursor(Cursor.HAND);
        btnImg9.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ClientServerTransaction transaction = new ClientServerTransaction("/", model);
                transaction.sendGetRequest();
            } 
        });
        Img btnImg10 = new Img("dbc_buttons_map_10.png", 27, 11);
        Img btnImg11 = new Img("dbc_buttons_map_11.png", 52, 11);
        btnImg11.setCursor(Cursor.HAND);
        btnImg11.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Resource currentResource = model.getSelectedResource();
                String url = currentResource.getParent();
                int index = url.lastIndexOf("/");
                if (index < 0) {
                    url = "/";
                } else {
                    url = url.substring(0, index);
                }
                ClientServerTransaction transaction = new ClientServerTransaction(url, model);
                transaction.sendGetRequest();
            } 
        });
        Img btnImg12 = new Img("dbc_buttons_map_12.png", 24, 11);
        Img btnImg13 = new Img("dbc_buttons_map_13.png", 52, 11);
        btnImg13.setCursor(Cursor.HAND);
        btnImg13.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Resource currentResource = model.getSelectedResource();
                String url = currentResource.getParent();
                ClientServerTransaction transaction = new ClientServerTransaction(url, model);
                transaction.sendGetRequest();
            } 
        });
        Img btnImg14 = new Img("dbc_buttons_map_14.png", 47, 11);
        btnLayoutLine2.addMember(btnImg8);
        btnLayoutLine2.addMember(btnImg9);
        btnLayoutLine2.addMember(btnImg10);
        btnLayoutLine2.addMember(btnImg11);
        btnLayoutLine2.addMember(btnImg12);
        btnLayoutLine2.addMember(btnImg13);
        btnLayoutLine2.addMember(btnImg14);
        
        HLayout btnLayoutLine3 = new HLayout();
        btnLayoutLine3.setSize("275px", "11px");
        Img btnImg15 = new Img("dbc_buttons_map_15.png", 21, 11);
        Img btnImg16 = new Img("dbc_buttons_map_16.png", 52, 11);
        Img btnImg17 = new Img("dbc_buttons_map_17.png", 27, 11);
        Img btnImg18 = new Img("dbc_buttons_map_18.png", 52, 11);
        Img btnImg19 = new Img("dbc_buttons_map_19.png", 24, 11);
        Img btnImg20 = new Img("dbc_buttons_map_20.png", 52, 11);
        Img btnImg21 = new Img("dbc_buttons_map_21.png", 47, 11);
        btnLayoutLine3.addMember(btnImg15);
        btnLayoutLine3.addMember(btnImg16);
        btnLayoutLine3.addMember(btnImg17);
        btnLayoutLine3.addMember(btnImg18);
        btnLayoutLine3.addMember(btnImg19);
        btnLayoutLine3.addMember(btnImg20);
        btnLayoutLine3.addMember(btnImg21);
        
        buttons.addMember(btnLayoutLine1);
        buttons.addMember(btnLayoutLine2);
        buttons.addMember(btnLayoutLine3);
        
        return buttons;
    }
    
    /**
     *  Adds the main console component of the Danube Bean Console to the Layout component to 
     *  which the GUI elements have been added 
     */
    public void addConsoleComponent(MainConsole console) {
        outerLayout.addMember(console.getConsole());
        outerLayout.addMember(ftrLayout);
    }
    
    /**
     * Adds the status bar component to the footer Layout containing the footer GUI.
     */
    public void addStatusBar(HLayout status) {
        ftrSpacerLayout.addMember(status);
    }
    
    /**
     * Gets a reference to the outer layout.
     */
    public VLayout getContainer() {
        return outerLayout;
    }
}
