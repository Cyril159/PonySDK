/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.ui.server.basic;

import com.ponysdk.core.Parser;
import com.ponysdk.core.stm.Txn;
import com.ponysdk.ui.terminal.WidgetType;
import com.ponysdk.ui.terminal.model.ServerToClientModel;

/**
 * A flexible table that creates cells on demand. It can be jagged (that is,
 * each row can contain a different number of cells) and individual cells can be
 * set to span multiple rows or columns.
 */
public class PFlexTable extends PHTMLTable {

    public PFlexTable() {
        super();
        init();
        setCellFormatter(new PFlexCellFormatter());
    }

    public PFlexCellFormatter getFlexCellFormatter() {
        return (PFlexCellFormatter) getCellFormatter();
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.FLEX_TABLE;
    }

    public class PFlexCellFormatter extends PCellFormatter {

        public void setColSpan(final int row, final int column, final int colSpan) {
            final Parser parser = Txn.get().getParser();
            parser.beginObject();
            parser.parse(ServerToClientModel.TYPE_UPDATE, ID);
            if (window != null) {
                parser.parse(ServerToClientModel.WINDOW_ID, window.getID());
            }
            parser.parse(ServerToClientModel.SET_COL_SPAN, colSpan);
            parser.parse(ServerToClientModel.ROW, row);
            parser.parse(ServerToClientModel.COLUMN, column);
            parser.endObject();
        }

        public void setRowSpan(final int row, final int column, final int rowSpan) {
            final Parser parser = Txn.get().getParser();
            parser.beginObject();
            parser.parse(ServerToClientModel.TYPE_UPDATE, ID);
            if (window != null) {
                parser.parse(ServerToClientModel.WINDOW_ID, window.getID());
            }
            parser.parse(ServerToClientModel.SET_ROW_SPAN, rowSpan);
            parser.parse(ServerToClientModel.ROW, row);
            parser.parse(ServerToClientModel.COLUMN, column);
            parser.endObject();
        }
    }
}