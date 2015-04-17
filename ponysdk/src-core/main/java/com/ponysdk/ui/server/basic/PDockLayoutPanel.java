/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
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

import com.ponysdk.core.instruction.Add;
import com.ponysdk.core.instruction.EntryInstruction;
import com.ponysdk.core.instruction.Update;
import com.ponysdk.core.stm.Txn;
import com.ponysdk.ui.terminal.Dictionnary.PROPERTY;
import com.ponysdk.ui.terminal.PUnit;
import com.ponysdk.ui.terminal.WidgetType;

/**
 * A panel that lays its child widgets out "docked" at its outer edges, and allows its last widget to take up
 * the remaining space in its center.
 * <p>
 * This widget will <em>only</em> work in standards mode, which requires that the HTML page in which it is run
 * have an explicit &lt;!DOCTYPE&gt; declaration.
 * </p>
 * DockLayoutPanel contains children tagged with the cardinal directions, and center:
 * <p>
 * <dl>
 * <dt>center</dt>
 * <dt>north</dt>
 * <dt>south</dt>
 * <dt>west</dt>
 * <dt>east</dt>
 * </dl>
 * <p>
 * Each child can hold only widget, and there can be only one &lt;g:center>. However, there can be any number
 * of the directional children.
 * </p>
 */
public class PDockLayoutPanel extends PComplexPanel implements PAnimatedLayout {

    public enum Direction {
        NORTH, EAST, SOUTH, WEST, CENTER, LINE_START, LINE_END
    }

    public PDockLayoutPanel(final PUnit unit) {
        super(new EntryInstruction(PROPERTY.UNIT, unit.ordinal()));
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.DOCK_LAYOUT_PANEL;
    }

    @Override
    public void add(final PWidget child) {
        add(child, Direction.CENTER, 0);
    }

    public void addNorth(final PWidget widget, final double size) {
        add(widget, Direction.NORTH, size);
    }

    public void addSouth(final PWidget widget, final double size) {
        add(widget, Direction.SOUTH, size);
    }

    public void addEast(final PWidget widget, final double size) {
        add(widget, Direction.EAST, size);
    }

    public void addWest(final PWidget widget, final double size) {
        add(widget, Direction.WEST, size);
    }

    public void addLineEnd(final PWidget widget, final double size) {
        add(widget, Direction.LINE_END, size);
    }

    public void addLineStart(final PWidget widget, final double size) {
        add(widget, Direction.LINE_START, size);
    }

    public void setWidgetSize(final PWidget widget, final double size) {
        final Update update = new Update(getID());
        update.put(PROPERTY.WIDGET_SIZE, size);
        update.put(PROPERTY.WIDGET, widget.getID());
        Txn.get().getTxnContext().save(update);
    }

    public void setWidgetHidden(final PWidget widget, final boolean hidden) {
        final Update update = new Update(getID());
        update.put(PROPERTY.WIDGET_HIDDEN, hidden);
        update.put(PROPERTY.WIDGET, widget.getID());
        Txn.get().getTxnContext().save(update);
    }

    public void add(final PWidget child, final Direction direction, final double size) {
        // Detach new child.
        child.removeFromParent();
        // Logical attach.
        getChildren().add(child);
        // Adopt.
        adopt(child);

        final Add add = new Add(child.getID(), getID());
        add.put(PROPERTY.DIRECTION, direction.ordinal());
        add.put(PROPERTY.SIZE, size);

        Txn.get().getTxnContext().save(add);
    }

    @Override
    public void animate(final int duration) {
        saveUpdate(PROPERTY.ANIMATE, duration);
    }
}
