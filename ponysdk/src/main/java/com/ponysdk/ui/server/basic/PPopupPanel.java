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

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;

import com.ponysdk.core.Parser;
import com.ponysdk.ui.server.basic.event.HasPAnimation;
import com.ponysdk.ui.server.basic.event.PCloseEvent;
import com.ponysdk.ui.server.basic.event.PCloseHandler;
import com.ponysdk.ui.terminal.WidgetType;
import com.ponysdk.ui.terminal.model.ClientToServerModel;
import com.ponysdk.ui.terminal.model.HandlerModel;
import com.ponysdk.ui.terminal.model.ServerToClientModel;

/**
 * A panel that can "pop up" over other widgets. It overlays the browser's client area (and any
 * previously-created popups).
 * <p>
 * A PPopupPanel should not generally be added to other panels; rather, it should be shown and
 * hidden using the {@link #show()} and {@link #hide()} methods.
 * </p>
 * <p>
 * The width and height of the PPopupPanel cannot be explicitly set; they are determined by the
 * PPopupPanel's widget. Calls to {@link #setWidth(String)} and {@link #setHeight(String)} will call
 * these methods on the PPopupPanel's widget.
 * </p>
 * <p>
 * The PopupPanel can be optionally displayed with a "glass" element behind it, which is commonly
 * used to gray out the widgets behind it. It can be enabled using {@link #setGlassEnabled(boolean)}
 * . It has a default style name of "gwt-PopupPanelGlass", which can be changed using
 * {@link #setGlassStyleName(String)}.
 * </p>
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.gwt-PopupPanel</dt>
 * <dd>the outside of the popup</dd>
 * <dt>.gwt-PopupPanel .popupContent</dt>
 * <dd>the wrapper around the content</dd>
 * <dt>.gwt-PopupPanelGlass</dt>
 * <dd>the glass background behind the popup</dd>
 * </dl>
 */
public class PPopupPanel extends PSimplePanel implements HasPAnimation {

    /**
     * A callback that is used to set the position of a {@link PPopupPanel} right before it is
     * shown.
     */
    public interface PPositionCallback {

        void setPosition(int offsetWidth, int offsetHeight, int windowWidth, int windowHeight);
    }

    private final boolean autoHide;

    private boolean glassEnabled;

    private boolean animationEnabled;

    private boolean center;

    private boolean showing;

    private int leftPosition;

    private int topPosition;

    private String glassStyleName;

    private PPositionCallback positionCallback;

    private final List<PCloseHandler> listeners = new ArrayList<>();

    public PPopupPanel(final boolean autoHide, final int windowID) {
        this.visible = false;
        this.autoHide = autoHide;
    }

    public PPopupPanel() {
        this(false, PWindow.EMPTY_WINDOW_ID);
    }

    public PPopupPanel(final boolean autoHide) {
        this(autoHide, PWindow.EMPTY_WINDOW_ID);
    }

    @Override
    protected boolean attach(final int windowID) {
        final boolean result = super.attach(windowID);

        if (windowID != PWindow.EMPTY_WINDOW_ID) {
            final PRootPanel root = windowID != PWindow.MAIN_WINDOW_ID ? PRootPanel.get(windowID) : PRootPanel.get();

            final PWidget child = root.getChild(ID);
            if (child == null) root.add(this);
        }

        return result;
    }

    @Override
    protected void enrichOnInit(final Parser parser) {
        super.enrichOnInit(parser);
        if (autoHide) parser.parse(ServerToClientModel.POPUP_AUTO_HIDE, autoHide);
    }

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.POPUP_PANEL;
    }

    public void setModal(final boolean modal) {
        saveUpdate(ServerToClientModel.POPUP_MODAL, modal);
    }

    public void setGlassEnabled(final boolean glassEnabled) {
        this.glassEnabled = glassEnabled;
        saveUpdate(ServerToClientModel.POPUP_GLASS_ENABLED, glassEnabled);
    }

    public void setDraggable(final boolean draggable) {
        if (draggable) {
            saveUpdate(ServerToClientModel.POPUP_DRAGGABLE);
        }
    }

    @Override
    public void setAnimationEnabled(final boolean animationEnabled) {
        this.animationEnabled = animationEnabled;
        saveUpdate(ServerToClientModel.ANIMATION, animationEnabled);
    }

    public void center() {
        this.center = true;
        this.showing = true;
        saveUpdate(ServerToClientModel.POPUP_CENTER);
    }

    public void show() {
        if (!showing) {
            this.showing = true;
            saveUpdate(ServerToClientModel.POPUP_SHOW);
        }
    }

    public void hide() {
        if (showing) {
            this.showing = false;
            saveUpdate(ServerToClientModel.POPUP_HIDE);
        }
    }

    public void setGlassStyleName(final String glassStyleName) {
        this.glassStyleName = glassStyleName;
        saveUpdate(ServerToClientModel.POPUP_GLASS_STYLE_NAME, glassStyleName);
    }

    @Override
    public boolean isAnimationEnabled() {
        return animationEnabled;
    }

    public boolean isGlassEnabled() {
        return glassEnabled;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setPopupPosition(final int left, final int top) {
        leftPosition = left;
        topPosition = top;

        saveUpdate(ServerToClientModel.POPUP_POSITION_LEFT, leftPosition, ServerToClientModel.POPUP_POSITION_TOP, topPosition);
    }

    public void setPopupPositionAndShow(final PPositionCallback callback) {
        this.positionCallback = callback;
        this.showing = true;
        saveAddHandler(HandlerModel.HANDLER_POPUP_POSITION_CALLBACK);
    }

    public void addCloseHandler(final PCloseHandler handler) {
        listeners.add(handler);
    }

    @Override
    public void onClientData(final JsonObject instruction) {
        if (instruction.containsKey(ClientToServerModel.WIDGET_POSITION.toStringValue())) {
            final JsonArray widgetInfo = instruction.getJsonArray(ClientToServerModel.WIDGET_POSITION.toStringValue());

            final Integer windowWidth = ((JsonNumber) widgetInfo.get(0)).intValue();
            final Integer windowHeight = ((JsonNumber) widgetInfo.get(1)).intValue();
            final Integer clientWith = ((JsonNumber) widgetInfo.get(2)).intValue();
            final Integer clientHeight = ((JsonNumber) widgetInfo.get(3)).intValue();

            setPosition(windowWidth, windowHeight, clientWith, clientHeight);

            saveUpdate(ServerToClientModel.POPUP_POSITION_AND_SHOW);
        } else if (instruction.containsKey(ClientToServerModel.HANDLER_CLOSE_HANDLER.toStringValue())) {
            this.showing = false;
            fireOnClose();
        } else {
            super.onClientData(instruction);
        }
    }

    private void fireOnClose() {
        for (final PCloseHandler handler : listeners) {
            handler.onClose(new PCloseEvent(this));
        }
    }

    public void setPosition(final int offsetWidth, final int offsetHeight, final int windowWidth,
            final int windowHeight) {
        this.positionCallback.setPosition(offsetWidth, offsetHeight, windowWidth, windowHeight);
        this.visible = false;
        setVisible(true);
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public String getGlassStyleName() {
        return glassStyleName;
    }

    public boolean isCenter() {
        return center;
    }

    public PPositionCallback getPositionCallback() {
        return positionCallback;
    }

    public int getLeftPosition() {
        return leftPosition;
    }
}