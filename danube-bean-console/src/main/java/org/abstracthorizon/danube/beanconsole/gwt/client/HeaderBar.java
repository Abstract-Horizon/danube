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

import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * The summary section displayed as a header bar in the Danube Bean Console. This contains a 
 * summary of details for the bean currently displayed.
 *
 * @author Mile Lukic
 */
public class HeaderBar {

    private HLayout hdrLayout;
    private StaticTextItem nameLabel = new StaticTextItem("name");
    private StaticTextItem typeLabel = new StaticTextItem("type");
    private StaticTextItem pathLabel = new StaticTextItem("path");
    private StaticTextItem valueLabel = new StaticTextItem("value");
    private Img iconImage = new Img();
    private DynamicForm iconForm = new DynamicForm();

    /**
     * Constructor. Constructs the header bar. Adds labels and fields to be displayed.
     */
    public HeaderBar() {
        hdrLayout = new HLayout();
        hdrLayout.setPadding(5);
        hdrLayout.setHeight(65);
        hdrLayout.setWidth100();
        iconForm = new DynamicForm();
        iconForm.setSize("30px", "65px");
        iconForm.setHeight100();
        DynamicForm headerForm = new DynamicForm();
        headerForm.setWidth100();
        hdrLayout.addMember(iconForm);
        hdrLayout.addMember(headerForm);
        
        nameLabel.setTitle("<b>Name</b>");
        // nameLabel.setOutputAsHTML(true);
        nameLabel.setWrap(false);
        nameLabel.setClipValue(true);
        
        typeLabel.setTitle("<b>Type</b>");
        // typeLabel.setOutputAsHTML(true);
        typeLabel.setClipValue(true);
        typeLabel.setWrap(false);
        
        pathLabel.setTitle("<b>Path</b>");
        // pathLabel.setOutputAsHTML(true);
        pathLabel.setClipValue(true);
        pathLabel.setWrap(false);

        valueLabel.setTitle("<b>Value</b>");
        // valueLabel.setOutputAsHTML(true);
        valueLabel.setClipValue(true);
        valueLabel.setWrap(false);

        headerForm.setFields(nameLabel, typeLabel, pathLabel, valueLabel);
        nameLabel.setWidth("*");
        typeLabel.setWidth("*");
        pathLabel.setWidth("*");
        valueLabel.setWidth("*");
    }

    /**
     * Gets the constructed header bar. 
     * @return hdrLayout - the Layout container in which the header summary details have been placed. 
     */
    public HLayout getHeaderBar() {
        return hdrLayout;
    }

    /**
     * Sets the name, type an path values shown in the header bar and also an icon appropriate 
     * to the type being viewed.
     * 
     * @param nameVal
     * @param typeVal
     * @param pathVal
     * @param icn
     */
    public void setHeaderValues(String nameVal, String typeVal, String pathVal, String valueVal, String icn) {
        if (!(icn.equals(null))) {
            iconForm.removeChild(iconImage);
            iconImage = new Img("./" + icn);
            iconImage.setSize("30px", "65px");
            iconImage.setImageType(ImageStyle.CENTER);
            iconForm.addChild(iconImage);
        }
        if (!(nameVal.equals(null))) {
            nameLabel.setValue(nameVal);
        }
        if (!(typeVal.equals(null))) {
            typeLabel.setValue(typeVal);
        }
        if (pathVal.equals(null) || (pathVal.length() < 1)) {
            pathLabel.setValue("/"); //to avoid "&nbsp;" being displayed 
        } else {
            pathLabel.setValue(pathVal);
        }

        if (valueVal != null) {
            if (valueVal.length() > 40) {
                valueVal = valueVal.substring(0, 40);
            } else if (valueVal.length() < 1) {
                valueVal = " ";
            }
            valueLabel.setValue(valueVal);
        } else {
            valueLabel.setValue(" ");
        }
    }

}
