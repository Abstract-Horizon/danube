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
package org.abstracthorizon.danube.beanconsole.gwt.client;

import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Consrtucts the status bar component of the Danube Bean Console.
 *
 * @author Mile Lukic
 */
public class StatusBar {

    private static Label statusLabel;
    private HLayout statusLayout;

    /**
     * Constructor. Defines the Layout component and adds the label and status field to it.
     */
    public StatusBar() {
        statusLayout = new HLayout();
        statusLayout.setHeight(20);
        statusLabel = new Label("Status: ");
        statusLabel.setWidth("*");
        statusLabel.setOverflow(Overflow.HIDDEN);
        statusLabel.setWrap(false);
        statusLabel.setCursor(Cursor.HAND);

        statusLayout.addMember(statusLabel);
        
        statusLayout.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                /*open a dialog with more space to show status bar contents - useful is status 
                 *bar contents are clipped due to length */
                final Window statusWindow = new Window();
                statusWindow.setTitle("Status");
                statusWindow.setHeaderStyle(null);
                statusWindow.setIsModal(true);
                statusWindow.setSize("60%", "50%");
                statusWindow.setAutoCenter(true);
                statusWindow.setShowCloseButton(false);
                statusWindow.setShowMaximizeButton(false);
                statusWindow.setShowMinimizeButton(false);
                statusWindow.setShowHeader(true);
                statusWindow.setShowModalMask(true);
                statusWindow.setEdgeSize(1);
                statusWindow.setAlign(VerticalAlignment.TOP);
                Label statusContent = new Label(statusLabel.getContents());
                statusWindow.addChild(statusContent);
                statusContent.setWidth100();
                statusContent.setHeight100();
                statusContent.setTop(25);
                statusContent.setOverflow(Overflow.AUTO);
                statusContent.setValign(VerticalAlignment.TOP);
                statusWindow.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        statusWindow.destroy();
                    }
                });
                statusWindow.show();
            }
        });

        Button consoleButton = new Button("Console");
        consoleButton.setHeight(18);
        consoleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                SC.showConsole();
            }
        });
        statusLayout.addMember(consoleButton);
    }

    /**
     * Get the status bar component.
     * 
     * @return statusLayout - the Layout component containing the status fields.
     */
    public HLayout getStatusBar() {
        return statusLayout;
    }

    /**
     * Sets the text contents of the status field.
     * 
     * @param status - a string representing a status to be communicated by the status bar.
     */
    public static void setStatus(String status) {
        statusLabel.setContents(status);
    }

}
