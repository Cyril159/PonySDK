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

import com.ponysdk.ui.server.basic.PScheduler.RepeatingCommand;

/**
 * A simplified, browser-safe timer class. This class serves the same purpose as java.util.Timer, but is
 * simplified because of the single-threaded environment.
 * <p>
 * To schedule a timer, simply create a subclass of it (overriding {@link #run}) and call {@link #schedule} or
 * {@link #scheduleRepeating}.
 * </p>
 */
public abstract class PTimer {

    private boolean repeat = false;

    private final RepeatingCommand cmd = new RepeatingCommand() {

        @Override
        public boolean execute() {
            run();
            return repeat;
        }
    };

    public void scheduleRepeating(final int delayMillis) {
        repeat = true;
        PScheduler.scheduleFixedDelay(cmd, delayMillis);
    }

    public void schedule(final int delayMillis) {
        repeat = false;
        PScheduler.scheduleFixedDelay(cmd, delayMillis);
    }

    public void cancel() {
        repeat = false;
    }

    protected abstract void run();

}
