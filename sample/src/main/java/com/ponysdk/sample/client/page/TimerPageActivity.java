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

package com.ponysdk.sample.client.page;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import com.ponysdk.core.concurrent.PScheduler;
import com.ponysdk.core.concurrent.PScheduler.UIRunnable;
import com.ponysdk.ui.server.basic.PButton;
import com.ponysdk.ui.server.basic.PHTML;
import com.ponysdk.ui.server.basic.PLabel;
import com.ponysdk.ui.server.basic.PTextBox;
import com.ponysdk.ui.server.basic.PVerticalPanel;
import com.ponysdk.ui.server.basic.event.PClickEvent;
import com.ponysdk.ui.server.basic.event.PClickHandler;

public class TimerPageActivity extends SamplePageActivity {

    protected long time1 = 0;
    protected long time2 = 0;
    protected AtomicLong time3 = new AtomicLong();

    private final PTextBox textBox = new PTextBox("1000");
    private final PVerticalPanel panel = new PVerticalPanel();
    private UIRunnable scheduleAtFixedRate;
    private PLabel labelScheduler;
    private PLabel label;
    private UIRunnable scheduleAtFixedDelay;

    public TimerPageActivity() {
        super("Timer", "Extra");
    }

    @Override
    protected void onFirstShowPage() {
        super.onFirstShowPage();

        label = new PLabel("0");

        final PButton scheduleRepeatingButton = new PButton("Start");
        scheduleRepeatingButton.addClickHandler((event) -> {
            scheduleAtFixedDelay = PScheduler.scheduleAtFixedRate(() -> {
                time1++;
                label.setText("" + time1);
            }, Duration.ofMillis(Integer.valueOf(textBox.getText())));
        });
        panel.add(new PLabel("Simple repeating timer"));
        panel.add(label);
        panel.add(textBox);
        panel.add(scheduleRepeatingButton);

        // Fixed delay
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
        final PLabel dateLabel = new PLabel(dateFormat.format(Calendar.getInstance().getTime()));
        panel.add(new PHTML("<br>"));
        panel.add(new PLabel("Fixed delay timer"));
        panel.add(dateLabel);
        final PButton fixedDelayButton = new PButton("Start");
        fixedDelayButton.addClickHandler(new PClickHandler() {

            @Override
            public void onClick(final PClickEvent clickEvent) {
                PScheduler.scheduleAtFixedRate(() -> dateLabel.setText(dateFormat.format(Calendar.getInstance().getTime())), Duration.ofMillis(1000));
            }
        });
        panel.add(fixedDelayButton);

        // Client side only
        panel.add(new PHTML("<br>"));
        panel.add(new PLabel("Timer (terminal side)"));
        final PButton changeColorsBUtton = new PButton("Start");

        changeColorsBUtton.addClickHandler((event) -> {
            PScheduler.schedule(() -> changeColorsBUtton.setStyleProperty("color", "blue"), Duration.ofMillis(2000));
            PScheduler.schedule(() -> changeColorsBUtton.setStyleProperty("color", "orange"), Duration.ofMillis(4000));
            PScheduler.schedule(() -> changeColorsBUtton.setStyleProperty("color", "black"), Duration.ofMillis(6000));
        });

        panel.add(changeColorsBUtton);

        labelScheduler = new PLabel("0");

        panel.add(new PLabel("UI Scheduler"));
        panel.add(labelScheduler);

        examplePanel.setWidget(panel);

    }

    @Override
    protected void onLeavingPage() {
        super.onLeavingPage();
        scheduleAtFixedRate.cancel();
        if (scheduleAtFixedDelay != null) {
            scheduleAtFixedDelay.cancel();
        }
    }

}
