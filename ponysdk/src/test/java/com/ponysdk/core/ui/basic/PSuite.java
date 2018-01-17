/*
 * Copyright (c) 2017 PonySDK
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

package com.ponysdk.core.ui.basic;

import com.ponysdk.core.server.application.Application;
import com.ponysdk.core.server.application.UIContext;
import com.ponysdk.core.server.servlet.WebSocket;
import com.ponysdk.core.writer.ModelWriter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

public class PSuite {

    @BeforeClass
    public static void beforeClass() {
        final Application application = Mockito.mock(Application.class, Mockito.RETURNS_MOCKS);
        final ServletUpgradeRequest request = Mockito.mock(ServletUpgradeRequest.class, Mockito.RETURNS_MOCKS);
        final UIContext uiContext = Mockito.spy(new UIContext(Mockito.mock(WebSocket.class), request, application));
        final ModelWriter mw = Mockito.mock(ModelWriter.class);
        Mockito.when(uiContext.getWriter()).thenReturn(mw);
        uiContext.acquire();
    }

    @AfterClass
    public static void afterClass() {
        UIContext.get().release();
    }

}
