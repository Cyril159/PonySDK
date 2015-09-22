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

package com.ponysdk.sample.trading.client.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.activity.AbstractActivity;
import com.ponysdk.core.place.Place;
import com.ponysdk.impl.webapplication.login.DefaultLoginPageView;
import com.ponysdk.impl.webapplication.page.place.PagePlace;
import com.ponysdk.sample.client.datamodel.User;
import com.ponysdk.sample.client.event.UserLoggedInEvent;
import com.ponysdk.ui.server.basic.IsPWidget;
import com.ponysdk.ui.server.basic.PKeyCodes;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;
import com.ponysdk.ui.server.basic.event.PKeyPressEvent;
import com.ponysdk.ui.server.basic.event.PKeyPressFilterHandler;

public class LoginActivity extends AbstractActivity {

    private static Logger log = LoggerFactory.getLogger(LoginActivity.class);

    private DefaultLoginPageView loginPageView;

    @Override
    protected IsPWidget buildView() {
        log.info("Showing login page");

        loginPageView = new DefaultLoginPageView("PonySDK trading showcase");

        loginPageView.getLoginTextBox().setText("Trader");
        loginPageView.getPasswordTextBox().setText("Trader");

        loginPageView.getLoginTextBox().ensureDebugId("login");
        loginPageView.getPasswordTextBox().ensureDebugId("password");
        loginPageView.getLoginButton().ensureDebugId("signin");

        loginPageView.addLoginClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent clickEvent) {
                doLogin();
            }

        });

        loginPageView.asWidget().addDomHandler(new PKeyPressFilterHandler(PKeyCodes.ENTER) {

            @Override
            public void onKeyPress(final PKeyPressEvent keyPressEvent) {
                doLogin();
            }
        }, PKeyPressEvent.TYPE);

        return loginPageView;
    }

    @Override
    public void updateView(final Place place) {}

    private void doLogin() {
        final User user = new User();
        user.setID(0);
        user.setLogin(loginPageView.getLogin());
        user.setName(loginPageView.getLogin());
        user.setPassword(loginPageView.getPassword());

        // UIContext.get().setApplicationAttribute(UISampleEntryPoint.USER, user);

        final UserLoggedInEvent loggedInEvent = new UserLoggedInEvent(LoginActivity.this, user);
        loggedInEvent.setBusinessMessage(loginPageView.getLogin() + " is now connected");
        fireEvent(loggedInEvent);

        log.info("Logged with #" + user.getLogin() + "/" + user.getPassword());

        goTo(new PagePlace("CheckBox"));
    }
}
