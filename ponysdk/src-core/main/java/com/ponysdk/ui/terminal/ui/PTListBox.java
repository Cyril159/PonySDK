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

package com.ponysdk.ui.terminal.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.ponysdk.ui.terminal.UIService;
import com.ponysdk.ui.terminal.instruction.PTInstruction;

public class PTListBox extends PTFocusWidget<ListBox> {

    @Override
    public void create(final PTInstruction create, final UIService uiService) {
        init(create, uiService, new ListBox());
    }

    @Override
    public void addHandler(final PTInstruction addHandler, final UIService uiService) {
        if (addHandler.getString(HANDLER.KEY).equals(HANDLER.KEY_.CHANGE_HANDLER)) {
            uiObject.addChangeHandler(new ChangeHandler() {

                @Override
                public void onChange(final ChangeEvent event) {
                    final int selectedIndex = uiObject.getSelectedIndex();
                    if (selectedIndex == -1) {
                        final PTInstruction eventInstruction = new PTInstruction();
                        eventInstruction.setObjectID(addHandler.getObjectID());
                        eventInstruction.put(TYPE.KEY, TYPE.KEY_.EVENT);
                        eventInstruction.put(HANDLER.KEY, HANDLER.KEY_.CHANGE_HANDLER);
                        eventInstruction.put(PROPERTY.VALUE, "-1");
                        uiService.sendDataToServer(uiObject, eventInstruction);
                    } else {
                        String selectedIndexes = selectedIndex + "";
                        for (int i = 0; i < uiObject.getItemCount(); i++) {
                            if (uiObject.isItemSelected(i)) {
                                if (i != selectedIndex) {
                                    selectedIndexes += "," + i;
                                }
                            }
                        }
                        final PTInstruction eventInstruction = new PTInstruction();
                        eventInstruction.setObjectID(addHandler.getObjectID());
                        eventInstruction.put(TYPE.KEY, TYPE.KEY_.EVENT);
                        eventInstruction.put(HANDLER.KEY, HANDLER.KEY_.CHANGE_HANDLER);
                        eventInstruction.put(PROPERTY.VALUE, selectedIndexes);
                        uiService.sendDataToServer(uiObject, eventInstruction);
                    }
                }
            });
            return;
        }

        super.addHandler(addHandler, uiService);
    }

    @Override
    public void update(final PTInstruction update, final UIService uiService) {
        if (update.containsKey(PROPERTY.CLEAR)) {
            uiObject.clear();
        } else if (update.containsKey(PROPERTY.ITEM_INSERTED)) {
            final int index = update.getInt(PROPERTY.INDEX);
            final String item = update.getString(PROPERTY.ITEM_TEXT);
            uiObject.insertItem(item, index);
        } else if (update.containsKey(PROPERTY.ITEM_ADD)) {
            final String items = update.getString(PROPERTY.ITEM_TEXT);
            final String groupName = update.getString(PROPERTY.ITEM_GROUP);
            final SelectElement select = uiObject.getElement().cast();

            final OptGroupElement groupElement = Document.get().createOptGroupElement();
            groupElement.setLabel(groupName);

            final String[] tokens = items.split(";");

            for (final String token : tokens) {
                final OptionElement optElement = Document.get().createOptionElement();
                optElement.setInnerText(token);
                groupElement.appendChild(optElement);
            }
            select.appendChild(groupElement);
        } else if (update.containsKey(PROPERTY.ITEM_UPDATED)) {
            final int index = update.getInt(PROPERTY.INDEX);
            final String item = update.getString(PROPERTY.ITEM_TEXT);
            uiObject.setItemText(index, item);
        } else if (update.containsKey(PROPERTY.ITEM_REMOVED)) {
            uiObject.removeItem(update.getInt(PROPERTY.INDEX));
        } else if (update.containsKey(PROPERTY.SELECTED)) {
            final boolean selected = update.getBoolean(PROPERTY.SELECTED);
            final int index = update.getInt(PROPERTY.SELECTED_INDEX);
            if (index == -1) uiObject.setSelectedIndex(index);
            else uiObject.setItemSelected(index, selected);
        } else if (update.containsKey(PROPERTY.VISIBLE_ITEM_COUNT)) {
            uiObject.setVisibleItemCount(update.getInt(PROPERTY.VISIBLE_ITEM_COUNT));
        } else if (update.containsKey(PROPERTY.MULTISELECT)) {
            uiObject.setMultipleSelect(update.getBoolean(PROPERTY.MULTISELECT));
        } else {
            super.update(update, uiService);
        }

    }
}
