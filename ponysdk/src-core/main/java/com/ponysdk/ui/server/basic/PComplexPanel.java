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

import java.util.Iterator;

import com.ponysdk.core.instruction.EntryInstruction;
import com.ponysdk.ui.terminal.model.Model;

/**
 * Abstract base class for panels that can contain multiple child widgets.
 */
public abstract class PComplexPanel extends PPanel {

    private final PWidgetCollection children = new PWidgetCollection(this);

    public PComplexPanel(final EntryInstruction... entries) {
        super(entries);
    }

    public void add(final PWidget... widgets) {
        for (final PWidget w : widgets) {
            insert(w, getChildren().size());
        }
    }

    @Override
    public void add(final PWidget child) {
        insert(child, getChildren().size());
    }

    public void insert(final PWidget child, final int beforeIndex) {
        assertNotMe(child);

        child.removeFromParent();
        getChildren().insert(child, beforeIndex);
        adopt(child);

        saveAdd(child.getID(), ID, Model.INDEX, beforeIndex);

    }

    @Override
    public boolean remove(final PWidget w) {
        if (w.getParent() != this) return false;
        orphan(w);
        if (getChildren().remove(w)) {
            saveRemove(w.getID(), ID);
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(final int index) {
        return remove(getWidget(index));
    }

    protected PWidgetCollection getChildren() {
        return children;
    }

    public int getWidgetCount() {
        return getChildren().size();
    }

    public PWidget getWidget(final int index) {
        return getChildren().get(index);
    }

    public int getWidgetIndex(final PWidget child) {
        return getChildren().indexOf(child);
    }

    @Override
    public Iterator<PWidget> iterator() {
        return getChildren().iterator();
    }

    void assertIsChild(final PWidget widget) {
        if ((widget != null) && (widget.getParent() != this)) throw new IllegalStateException("The specified widget is not a child of this panel");
    }

    void assertNotMe(final PWidget widget) {
        if (widget == this) throw new IllegalStateException("Cannot insert widget to itself");
    }

}
