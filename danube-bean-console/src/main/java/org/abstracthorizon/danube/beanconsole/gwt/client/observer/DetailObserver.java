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
package org.abstracthorizon.danube.beanconsole.gwt.client.observer;

import org.abstracthorizon.danube.beanconsole.gwt.client.ClientServerTransaction;
import org.abstracthorizon.danube.beanconsole.gwt.client.DetailContainer;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModelListener;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;

import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;

/**
 * Deprecated class. No longer used.
 * Replaced by DetailTabObserver class.
 * 
 * @author Mile Lukic
 * @author Daniel Sendula
 */
public class DetailObserver implements DataModelListener {
    private DetailContainer details;

    public DetailObserver(DetailContainer detailContainer, final DataModel model) {
        this.details = detailContainer;

        detailContainer.getDetails().addCellDoubleClickHandler(new CellDoubleClickHandler() {
            public void onCellDoubleClick(CellDoubleClickEvent event) {
                ListGridRecord record = event.getRecord();
                String url = record.getAttribute("link");

                ClientServerTransaction transaction = new ClientServerTransaction(url, model);

                transaction.sendGetRequest();
            }

        });
    }

    public void modelUpdated(String url, Resource resource) {
        details.clearGrid();
        // TODO removed!
//        details.addDetailRecord(resource.getListGridRecords());
    }

}
