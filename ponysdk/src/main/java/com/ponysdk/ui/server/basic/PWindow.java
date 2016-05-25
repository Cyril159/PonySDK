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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.json.JsonObject;

import com.ponysdk.core.Parser;
import com.ponysdk.core.tools.ListenerCollection;
import com.ponysdk.ui.server.basic.event.PCloseEvent;
import com.ponysdk.ui.server.basic.event.PCloseHandler;
import com.ponysdk.ui.server.basic.event.PNativeEvent;
import com.ponysdk.ui.server.basic.event.PNativeHandler;
import com.ponysdk.ui.server.basic.event.POpenHandler;
import com.ponysdk.ui.terminal.WidgetType;
import com.ponysdk.ui.terminal.model.ClientToServerModel;
import com.ponysdk.ui.terminal.model.HandlerModel;
import com.ponysdk.ui.terminal.model.ServerToClientModel;

public class PWindow extends PObject implements PNativeHandler {

    public static final int EMPTY_WINDOW_ID = -2;
    public static final int MAIN_WINDOW_ID = -1;

    private final List<Runnable> postedCommands = new ArrayList<>();
    private final ListenerCollection<PCloseHandler> closeHandlers = new ListenerCollection<>();
    private final ListenerCollection<POpenHandler> openHandlers = new ListenerCollection<>();

    private final boolean loaded = false;

    private final String url;

    private final String name;

    private final String features;

    private boolean opened = false;

    private final Queue<Runnable> stackedInstructions = new LinkedList<>();

    public PWindow(final String url, final String name, final String features) {
        super(MAIN_WINDOW_ID);

        this.url = url;
        this.name = name;
        this.features = features;

        init();
    }

    @Override
    protected void enrichOnInit(final Parser parser) {
        super.enrichOnInit(parser);
        parser.parse(ServerToClientModel.URL, url);
        parser.parse(ServerToClientModel.NAME, name);
        parser.parse(ServerToClientModel.FEATURES, features);
    }

    public boolean open() {
        if (opened) return false;
        addNativeHandler(this);
        saveUpdate(ServerToClientModel.OPEN, true);
        opened = true;

        // TODO Force send ?!
        // Txn.get().getParser().reset();
        return opened;
    }

    public void close() {
        saveUpdate(ServerToClientModel.CLOSE, true);
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.WINDOW;
    }

    @Override
    public void onNativeEvent(final PNativeEvent event) {
        final int nativeValue = event.getJsonObject().getInt(ClientToServerModel.NATIVE.toStringValue());
        if (HandlerModel.HANDLER_OPEN_HANDLER.getValue() == nativeValue) {
            WindowManager.registerWindow(this);
            while (!stackedInstructions.isEmpty()) {
                stackedInstructions.poll().run();
            }
        }
    }

    @Override
    public void onClientData(final JsonObject instruction) {
        if (instruction.containsKey(ClientToServerModel.HANDLER_CLOSE_HANDLER.toStringValue())) {
            WindowManager.unregisterWindow(this);
            final PCloseEvent e = new PCloseEvent(this);
            for (final PCloseHandler h : closeHandlers) {
                h.onClose(e);
            }
            return;
        } else {
            super.onClientData(instruction);
        }
    }

    public void addCloseHandler(final PCloseHandler handler) {
        closeHandlers.add(handler);
    }

    public void addOpenHandler(final POpenHandler handler) {
        openHandlers.add(handler);
    }

    public void removeCloseHandler(final PCloseHandler handler) {
        closeHandlers.remove(handler);
    }

    protected void postOpenerCommand(final Runnable runnable) {
        postedCommands.add(runnable);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public PRootLayoutPanel getPRootLayoutPanel() {
        return PRootLayoutPanel.get(getID());
    }

    public PRootPanel getPRootPanel() {
        return PRootPanel.get(getID());
    }

    public void add(final IsPWidget widget) {
        if (WindowManager.get().getWindow(ID) == this) add0(widget);
        else stackedInstructions.add(() -> add0(widget));
    }

    private void add0(final IsPWidget widget) {
        getPRootPanel().add(widget);
    }

    public static class TargetAttribut {

        static final String BLANK = "_blank";
        static final String PARENT = "_parent";
        static final String SELF = "_self";
        static final String TOP = "_top";
    }

}