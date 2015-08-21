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

import com.google.gwt.user.client.ui.MenuBar;
import com.ponysdk.ui.terminal.UIService;
import com.ponysdk.ui.terminal.instruction.PTInstruction;

public class PTMenuBar extends PTWidget<MenuBar> {

    @Override
    public void create(final PTInstruction create, final UIService uiService) {
        init(create, uiService, new MenuBar(create.getBoolean(PROPERTY.MENU_BAR_IS_VERTICAL)));
    }

    @Override
    public void add(final PTInstruction add, final UIService uiService) {

        final PTObject child = uiService.getPTObject(add.getObjectID());
        final MenuBar menuBar = cast();

        if (child instanceof PTMenuItem) {
            final PTMenuItem menuItem = (PTMenuItem) child;
            if (add.containsKey(PROPERTY.BEFORE_INDEX)) {
                menuBar.insertItem(menuItem.cast(), add.getInt(PROPERTY.BEFORE_INDEX));
            } else {
                menuBar.addItem(menuItem.cast());
            }
        } else {
            final PTMenuItemSeparator menuItem = (PTMenuItemSeparator) child;
            menuBar.addSeparator(menuItem.cast());
        }
    }

    @Override
    public void remove(final PTInstruction remove, final UIService uiService) {
        final PTObject child = uiService.getPTObject(remove.getObjectID());
        if (child instanceof PTMenuItem) {
            final PTMenuItem menuItem = (PTMenuItem) child;
            uiObject.removeItem(menuItem.cast());
        } else {
            final PTMenuItemSeparator menuItem = (PTMenuItemSeparator) child;
            uiObject.removeSeparator(menuItem.cast());
        }
    }

    @Override
    public void update(final PTInstruction update, final UIService uiService) {
        if (update.containsKey(PROPERTY.CLEAR)) {
            uiObject.clearItems();
        } else if (update.containsKey(PROPERTY.ANIMATION)) {
            uiObject.setAnimationEnabled(update.getBoolean(PROPERTY.ANIMATION));
        } else {
            super.update(update, uiService);
        }
    }
}
