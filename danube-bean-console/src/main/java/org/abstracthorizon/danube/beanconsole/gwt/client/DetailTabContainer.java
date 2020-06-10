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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.TreeGridField;

/**
 * 
 *
 * @author Mile Lukic
 * 
 * The component displayed in the Danube Bean Console which shows details of one particular bean.
 * This component contains a summary section describing the bean, and a more details section 
 * comprising of a number of tabs. The tabs list the components making up the bean.
 */
public class DetailTabContainer {

    private VLayout detailLayout;
    private TabSet tabSet;
    private DataModel dataModel;
    private Resource resource;
    private ListGrid [] listGrids = new ListGrid[5];
    private Canvas[] tabs = new Canvas[5];
    private String[] tabTitles = new String[5];
    private boolean[] displayedTabs = new boolean[5];
    private HTMLPane text;
    private ListGridRecord[] records;

    private static final int BEANS_PROPERTIES = 0;
    private static final int COLLECTION = 1;
    private static final int MAP = 2;
    private static final int VALUE = 3;
    private static final int METHODS = 4;
    
    /**
     * Constructor.
     * Defines and sizes the main layout for the component and the TabSet component.
     */
    public DetailTabContainer(DataModel dataModel) {
        this.dataModel = dataModel;
        detailLayout = new VLayout();
        detailLayout.setBorder("1px solid #D0D0D0");
        detailLayout.setHeight100();
        detailLayout.setWidth100();
        tabSet = new TabSet();
        detailLayout.addMember(tabSet);
        tabSet.setSize("100%", "100%");
        
        createTab(BEANS_PROPERTIES, "beans & properties");
        createTab(COLLECTION, "collection");
        createTab(MAP, "map");
        createTab(VALUE, "value");
        createTab(METHODS, "methods");
    }

    
    
    /**
     * 
     */
    protected void createTab(int index, String tabTitle) {
        
        tabTitles[index] = tabTitle;
        if (index == METHODS) {

            VLayout methodsLayout = new VLayout();
            methodsLayout.setSize("100%", "100%");
            
            ListGrid methodListGrid = new ListGrid();
            listGrids[index] = methodListGrid;
            
            ListGridField nameField = new ListGridField("name");
            nameField.setTitle("Method Name & Signature");
            ListGridField paramField = new ListGridField("parameterTypes");
            methodListGrid.setTitle("Methods");
            methodListGrid.setFields(nameField, paramField);
            methodListGrid.hideField("parameterTypes");
            
            methodListGrid.addCellClickHandler(new CellClickHandler() {
                public void onCellClick(CellClickEvent event) {
                    new MethodWindow(event.getRecord().getAttribute("link"), event.getRecord().getAttribute("name"), event.getRecord().getAttribute("parameterTypes"));
                }
            });
            
            methodsLayout.addMember(methodListGrid);

            Canvas sectionCanvas = new Canvas();
            sectionCanvas.addChild(methodsLayout);
            methodsLayout.setSize("100%", "100%");
            tabs[index] = sectionCanvas;
        } else if (index == VALUE) {
            
            text = new HTMLPane();
            text.setSize("100%", "100%");
            text.setContents("&nbsp;");
            Canvas valueCanvas = new Canvas();
            
            valueCanvas.addChild(text);
            tabs[index] = valueCanvas;

        } else {
            ListGrid listGrid = createGrid(tabTitle);
            listGrid.setSize("100%", "100%");
            listGrid.setCanEdit(true);
            listGrid.setEditByCell(true);
            listGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
            listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
                public void onCellDoubleClick(CellDoubleClickEvent event) {
                    ListGridRecord record = event.getRecord();
                    //We only want to process a click event if the grid row contains an item that is "followable".
                    if (record.getAttribute("followable").equals("true")) {
                        String url = record.getAttribute("link");

                        ClientServerTransaction transaction = new ClientServerTransaction(url, dataModel);
                        transaction.sendGetRequest();
                    }
                }
            });
            
            listGrids[index] = listGrid;
            Canvas gridCanvas = new Canvas();
            gridCanvas.addChild(listGrid);
            tabs[index] = gridCanvas;
        }
    }
    
    /**
     * Creates a grid to display bean details. The grid created is customised depending upon the
     * type provided in the parameter provided. The grid returned will be displayed on the tab
     * in the TabSet corresponding to the type.
     * 
     * @param type - specified the type for which this grid will be constructed
     * @return grid - the customised grid created for the type specified.
     */
    public ListGrid createGrid(String type) {
       ListGrid grid = new ListGrid() {
           /* Overrides the getCellCSSText method to colour the text displayed in the grid
            * in blue for "followable" types*/
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
       //Define the ListGridField's
       grid.setWidth100();
       
       ListGridField beansField = new TreeGridField("group", "Group");
       
       ListGridField followableField = new TreeGridField("followable", "Followable");
       followableField.setCanEdit(false);
       
       ListGridField iconField = new TreeGridField("icon", " ");
       iconField.setWidth(20);
       iconField.setType(ListGridFieldType.IMAGE);
       iconField.setCanEdit(false);
       iconField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                String link = record.getAttribute("link");
                return "<a href=\"" + GWT.getHostPageBaseURL() + "#" + link + "\"><img src=images/" + value + " width=\"16\" height=\"16\" border=\"0\" /></a>";
            }
        });

       ListGridField nameField = new TreeGridField("name", "Name");
       nameField.setWidth("25%");
       nameField.setShowHover(true);
       nameField.setCanEdit(false);
       nameField.setHoverCustomizer(new HoverCustomizer() {
           public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
               return record.getAttribute("type");
           }
       });

       ListGridField typeField = new TreeGridField("type", "Type");
       typeField.setCanEdit(false);
       
       ListGridField accessField = new TreeGridField("access", "Access");
       accessField.setCanEdit(false);
       accessField.setWidth("7%");

       final SelectItem boolItem = new SelectItem();
       LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
       valueMap.put("true", "true");  
       valueMap.put("false", "false");
       boolItem.setValueMap(valueMap);       
       
       final ListGridField valueField = new TreeGridField("value", "Value");
       valueField.setWidth("68%");
       valueField.setType(ListGridFieldType.TEXT);
//       valueField.addRecordClickHandler(new RecordClickHandler() {
//
//           public void onRecordClick(RecordClickEvent event) {
//               Record record = event.getRecord();
//               
//               String access = record.getAttribute("access").toUpperCase();
//               String type = record.getAttribute("type");
//               if (access.indexOf('W') >= 0) {
//                   SC.logWarn("Click Need to allow editing for: " + event.getRecord().getAttribute("value"));
//                   if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
//                       valueField.setEditorType(boolItem);
//                   } else {
//                       valueField.setEditorType(new TextItem());
//                   }
//                   // Allow editing
//               } else {
//                   SC.logWarn("Click Need to prevent editing for: " + event.getRecord().getAttribute("value"));
//                   valueField.setEditorType(new StaticTextItem());
//               }
//           }
//           
//       });
       valueField.addCellSavedHandler(new CellSavedHandler() {
           public void onCellSaved(CellSavedEvent event) {
               // Save value to the server
               SC.logWarn("Value to be saved: " + event.getRecord().getAttribute("value"));
               ClientServerTransaction transaction = new ClientServerTransaction(event.getRecord().getAttribute("link"), dataModel);
               transaction.sendSimplePostRequest((String)event.getNewValue());
           }
       });
       
       ListGridField parametersField = new TreeGridField("parameterTypes", "Parameter Types");
       parametersField.setWidth("68%");
       parametersField.setCanEdit(false);
       parametersField.setType(ListGridFieldType.TEXT);
       /* Customise the grid by only adding the ListGridFields to the grid that are pertinent 
        * to the type. Hide any columns not to be displayed.*/
       if (type.equals("beans & properties")) {
           grid.setFields(beansField, followableField, iconField, nameField, typeField, accessField, valueField);
           grid.hideField("type");
       }

       if (type.equals("collection")) {
           grid.setFields(beansField, followableField, iconField, nameField, typeField, valueField);
           //collections don't use the "name" so hide this column which only contains a unique dummy ID
           grid.hideField("name");
       }
       if (type.equals("maps")) {
           grid.setFields(beansField, followableField, iconField, nameField, typeField, valueField);
       }

       if (type.equals("methods")) {
           grid.setFields(beansField, followableField, iconField, nameField, parametersField);
       }
       grid.hideField("group");
       grid.hideField("followable");
       grid.setAlternateRecordStyles(true);
       
       grid.addSelectionChangedHandler(new SelectionChangedHandler() {
           public void onSelectionChanged(SelectionEvent event) {
               Record record = event.getRecord();
               
               String access = record.getAttribute("access").toUpperCase();
               String type = record.getAttribute("type");
               if (access.indexOf('W') >= 0) {
                   SC.logWarn("Select Need to allow editing for: " + event.getRecord().getAttribute("value"));
                   if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
                       valueField.setEditorType(boolItem);
                   } else {
                       valueField.setEditorType(new TextItem());
                   }
                   // Allow editing
               } else {
                   SC.logWarn("Select Need to prevent editing for: " + event.getRecord().getAttribute("value"));
                   valueField.setEditorType(new StaticTextItem());
               }
                
           } 
       });
       
       return grid;
    }

    public void setResource(Resource resource, ListGridRecord[] records) {
        boolean updated = false;
        if (!resource.isFollowable()) {
            String thisPath = "";
            if (this.resource != null) {
                thisPath = this.resource.getPath();
            }
            String newPath = resource.getPath();
            if (newPath.startsWith(thisPath)) {
                String trail = newPath.substring(thisPath.length());
                if ((trail.indexOf('/', 1) < 0) && !trail.startsWith("/s_")) {
                    ListGridRecord record = null;
                    int i = 0;
                    while ((i < this.records.length) && (record == null)) {
                        String path = this.records[i].getAttribute("link");
                        if (newPath.equals(path)) {
                            record = this.records[i];
                        }
                        i++;
                    }
    
                    if (record != null) {
                        updated = true;
                        record.setAttribute("value", resource.getValue());
                        for (i = 0; i < listGrids.length; i++) {
                            if (displayedTabs[i]) {
                                listGrids[i].refreshFields();
                            }
                        }
                    } else {
                        SC.logWarn("Record is null?!?!?");
                    }
                }
            }
        }
        if (!updated) {
            this.resource = resource;
            this.records = records;
            clearTabs();
            
            populateTabs(records);
            
            String value = resource.getValue(); 
            if ((value != null) && (value.length() >= 40)) {
                SC.logWarn("Setting value ...");
                String contents = value.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
                contents = contents.replaceAll(" ", "&nbsp;");
                contents = contents.replaceAll("\n", "<br>");
                text.setContents(contents);
                Tab tab = new Tab();
                tab.setTitle(tabTitles[VALUE]);
                tab.setPane(tabs[VALUE]);
                tabSet.addTab(tab);
            }
        }
    }
    
    /**
     * Get the entire TabSet.
     * 
     * @return tabset - The TabSet component used to hold details for the bean being displayed.
     */
    public TabSet getDetails() {
        return tabSet;
    }
    
    /**
     * Removes each tab from the TabSet.
     */
    public void clearTabs() {
        Tab[] tabs = tabSet.getTabs();
        for(int i = tabs.length - 1; i >= 0; i--) {
            tabSet.setTabPane(i, new Canvas());
            tabSet.removeTab(i);
            listGrids[i].setData(new ListGridRecord[0]);
        }
    }
    
    protected void populateTabs(ListGridRecord [] detailRecs) {
        for (int i = 0; i < displayedTabs.length; i++) { // This is not needed, but just in case...
            displayedTabs[i] = false;
        }
        
        Map<String, List<ListGridRecord>> map = new HashMap<String, List<ListGridRecord>>();
        
        for (int i = 0; i < detailRecs.length; i++) {
            String group = detailRecs[i].getAttribute("group");
            List<ListGridRecord> records = map.get(group);
            if (records == null) {
                records = new ArrayList<ListGridRecord>();
                map.put(group, records);
            }
            
            if (group.equals("methods")) {
                String name = detailRecs[i].getAttribute("name");
                String params = detailRecs[i].getAttribute("parameterTypes");
                name = name + "(" + params + ")";
                detailRecs[i].setAttribute("name", name);
                
//                String url = detailRecs[i].getAttribute("link");
//                detailRecs[i].setAttribute("link", url);
            }            
            records.add(detailRecs[i]);
        }
        
        List<ListGridRecord> beansAndPropsRecords = map.get("beans");
        if (beansAndPropsRecords != null) {
            List<ListGridRecord> propsRecords = map.get("properties");
            if (propsRecords != null) {
                beansAndPropsRecords.addAll(propsRecords);
            }
        } else {
            beansAndPropsRecords = map.get("properties");
        }
        if (beansAndPropsRecords != null) {
            addTab(beansAndPropsRecords, BEANS_PROPERTIES);
        }

        List<ListGridRecord> collectionRecords = map.get("collection");
        if (collectionRecords != null) {
            addTab(collectionRecords, COLLECTION);
        }
        
        List<ListGridRecord> mapsRecords = map.get("map");
        if (mapsRecords != null) {
            addTab(mapsRecords, MAP);
        }
        
        List<ListGridRecord> methodsRecords = map.get("methods");
        if (methodsRecords != null) {
            addTab(methodsRecords, METHODS);
        }
        
    }
    
    protected void addTab(List<ListGridRecord> recs, int index) {
        ListGridRecord[] records = new ListGridRecord[recs.size()];
        records = recs.toArray(records);
        listGrids[index].setData(records);
        Tab tab = new Tab();
        tab.setTitle(tabTitles[index]);
        tab.setPane(tabs[index]);
        tabSet.addTab(tab);
        displayedTabs[index] = true;
    }

    private class MethodWindow {
        private Window methodWindow;
        
        public MethodWindow(final String url, String name, String params) {
            methodWindow = new Window();
            methodWindow.animateShow(AnimationEffect.FLY);
            methodWindow.setDragAppearance(DragAppearance.OUTLINE);
            methodWindow.setShowCloseButton(false);
            methodWindow.setShowMaximizeButton(false);
            methodWindow.setShowMinimizeButton(false);
            methodWindow.setDragOpacity(30);
            methodWindow.setAutoCenter(true);
            methodWindow.setTitle(name + " url: " + url);
            methodWindow.setIsModal(true);
            methodWindow.setShowModalMask(true);            
            String[] paramTypes = params.split(",");
            
            VLayout methodsLayout = new VLayout();
            methodsLayout.setMargin(2);
            methodsLayout.setWidth100();
            final DynamicForm method = new DynamicForm();
            method.setAction(url);
            method.setMethod(FormMethod.POST);
            method.setWidth100();
            method.setAutoFocus(true);
            final FormItem [] methodParams = {};
            //process all parameters for a method
            for (int j = 0; j < paramTypes.length; j++) {
                FormItem item = new FormItem();
                item.setWidth("100%");
                item.setName(paramTypes[j]);
                item.setTitle(paramTypes[j]);
                item.setType("text");
                item.setValue("");
                item.addKeyPressHandler(new com.smartgwt.client.widgets.form.fields.events.KeyPressHandler() {
                    public void onKeyPress(com.smartgwt.client.widgets.form.fields.events.KeyPressEvent event) {
                        //if the "enter" key is pressed inside a form item, submit the form
                        if(event.getKeyName().equals("Enter")) {
                            ClientServerTransaction transaction = new ClientServerTransaction(url, dataModel);
                            transaction.sendPostRequest(methodParams);
                            methodWindow.destroy(); 
                        }
                    }
                });
                methodParams[j] = item;
            }
            //Label to show if there are no parameters for a method
            Label noParams = new Label("No parameters required for this method.");
            noParams.setWidth("100%");
            noParams.setHeight(20);
            
            //Add the parameter input fields (if any) and buttons to the window. 
            Button execButton = new Button();
            execButton.setTitle("Execute");
            execButton.setAlign(Alignment.CENTER);
            execButton.setLayoutAlign(Alignment.CENTER);
            execButton.setMargin(1);
            
            execButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    ClientServerTransaction transaction = new ClientServerTransaction(url, dataModel);
                    transaction.sendPostRequest(methodParams);
                    methodWindow.destroy(); 
                }
            });
            
            
            Button cancelButton = new Button();
            cancelButton.setTitle("Cancel");
            cancelButton.setAlign(Alignment.CENTER);
            cancelButton.setLayoutAlign(Alignment.CENTER);
            cancelButton.setMargin(1);
            cancelButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    methodWindow.destroy(); 
                }
            });
            
            HLayout btnLayout = new HLayout();
            btnLayout.setWidth100();
            btnLayout.setHeight(30);
            btnLayout.setPadding(5);
            btnLayout.setAlign(Alignment.CENTER);
            btnLayout.addMember(execButton);
            btnLayout.addMember(cancelButton);
            int windowHeight = 0;
            int layoutHeight = 0;
            if (paramTypes.length == 0) {
                windowHeight = 100;
                layoutHeight = 30;
            } else {
                windowHeight = 70 + (paramTypes.length * 30);
                layoutHeight = paramTypes.length * 30;
            }
            methodsLayout.setHeight(layoutHeight);
            method.setHeight(30 * paramTypes.length);
            method.setFields(methodParams);
            if (paramTypes.length == 0) {
                methodsLayout.addChild(noParams);
            } else {
                methodsLayout.addMember(method);
            }
                
            methodWindow.setWidth("60%");
            methodWindow.setHeight(windowHeight);
            methodWindow.addMember(methodsLayout);
            methodWindow.addMember(btnLayout);
            methodWindow.addKeyPressHandler(new com.smartgwt.client.widgets.events.KeyPressHandler() {
                public void onKeyPress(com.smartgwt.client.widgets.events.KeyPressEvent event) {
                    // if "esc" key is pressed close the window
                    if(event.getKeyName().equals("Escape")) {
                        methodWindow.destroy();
                    }
                } 
            });
            methodWindow.show();
        }
        
    }

}
