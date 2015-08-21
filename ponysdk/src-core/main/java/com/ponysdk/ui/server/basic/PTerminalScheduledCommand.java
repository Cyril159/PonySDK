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

import java.util.concurrent.TimeUnit;

import com.ponysdk.ui.terminal.WidgetType;

/**
 * A command that will be deferred on the terminal.
 */
public abstract class PTerminalScheduledCommand extends PObject {

    public void schedule(final long delay, final TimeUnit unit) {
        schedule(unit.toMillis(delay));
    }

    public void schedule(final long delayMillis) {
        return;

        //
        // try {
        // // TODO nciaravola put delay instruction
        // run();
        // } finally {}
        // final Update update = new Update(ID);
        // update.put(PROPERTY.FIXDELAY, delayMillis);
        // Txn.get().getTxnContext().save(update);
    }

    protected abstract void run();

    @Override
    protected WidgetType getWidgetType() {
        return WidgetType.TERMINAL_SCHEDULED_COMMAND;
    }

}
