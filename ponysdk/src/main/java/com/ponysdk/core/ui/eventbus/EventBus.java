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

package com.ponysdk.core.ui.eventbus;

import java.util.Collection;

import com.ponysdk.core.ui.eventbus.Event.Type;

public interface EventBus {

    <H extends EventHandler> HandlerRegistration addHandler(Type type, H handler);

    <H extends EventHandler> void removeHandler(Type type, H handler);

    <H extends EventHandler> HandlerRegistration addHandlerToSource(Type type, Object source, H handler);

    <H extends EventHandler> void removeHandlerFromSource(Type type, Object source, H handler);

    void addHandler(BroadcastEventHandler handler);

    void removeHandler(BroadcastEventHandler handler);

    void fireEvent(Event<?> event);

    void fireEventFromSource(Event<?> event, Object source);

    <H extends EventHandler> Collection<H> getHandlers(final Type type, final Object source);
}
