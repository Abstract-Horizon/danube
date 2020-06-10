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

import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.TreeGridField;

/**
 * 
 *
 * @author Mile Lukic
 * 
 * Deprecated class. No longer used.
 * 
 * Replaced by DetailTabContainer.
 */
public class DetailContainer {

    private HLayout detailLayout;
    private ListGrid detailGrid;
    private ListGridRecord[] detailRecs = {};

    public DetailContainer() {
        detailLayout = new HLayout();
        detailLayout.setBorder("1px solid #D0D0D0");
        detailLayout.setMargin(2);
        detailLayout.setHeight100();
        detailLayout.setWidth100();
        detailGrid = new ListGrid() {
            protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
                String followable = record.getAttribute("followable");
                if (record.getAttribute("group").equals("methods") || followable.equals(null) 
                        || followable.equals("false") || !(getFieldName(colNum).equals("name"))) {
                    return super.getCellCSSText(record, rowNum, colNum); 
                } else {  
                    return "color:blue;";
                }  
            }  
        }; 
        detailGrid.setWidth100();
        ListGridField beansField = new TreeGridField("group", "Group");
        ListGridField followableField = new TreeGridField("followable", "Followable");
        ListGridField iconField = new TreeGridField("icon", " ");
        iconField.setWidth(20);
        iconField.setType(ListGridFieldType.IMAGE);
        ListGridField nameField = new TreeGridField("name", "Name");
        nameField.setWidth("25%");
        nameField.setShowHover(true);
        nameField.setHoverCustomizer(new HoverCustomizer() {
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                return record.getAttribute("type");
            }
        });

        ListGridField typeField = new TreeGridField("type", "Type");
        ListGridField accessField = new TreeGridField("access", "Access");
        accessField.setWidth("7%");
        ListGridField valueField = new TreeGridField("value", "Value");
        valueField.setWidth("68%");
        valueField.setType(ListGridFieldType.TEXT);
        detailGrid.setFields(beansField, followableField, iconField, nameField, typeField, accessField, valueField);
        detailGrid.hideField("group");
        detailGrid.hideField("followable");
        detailGrid.hideField("type");
        detailGrid.groupBy("group");
        detailGrid.setAlternateRecordStyles(true);
      //  detailGrid.setStyleName("exampleStyleOnline");
        detailLayout.addMember(detailGrid);
    }

    public void addDetailRecord(ListGridRecord[] records) {
        int len = detailRecs.length;
        for (int i = 0; i < records.length; i++) {
            if (len == 0) {
                detailRecs[i] = records[i];
                detailGrid.getField("icon").setCellIcon(records[i].getAttribute("icon"));
            } else {
                detailRecs[detailRecs.length] = records[i];
            }
        }
        detailGrid.setData(detailRecs);
    }

    public ListGrid getDetails() {
        return detailGrid;
    }
    
    public void clearGrid() {
        ListGridRecord[] recs = {};
        detailRecs = recs;
        detailGrid.clear();
        detailGrid.draw();
    }

}
