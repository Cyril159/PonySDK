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

package com.ponysdk.ui.terminal.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.ponysdk.ui.terminal.UIBuilder;
import com.ponysdk.ui.terminal.UIService;
import com.ponysdk.ui.terminal.event.CommunicationErrorEvent;
import com.ponysdk.ui.terminal.instruction.PTInstruction;
import com.ponysdk.ui.terminal.model.Model;

public class PTScheduler extends AbstractPTObject implements CommunicationErrorEvent.Handler {

    private final Map<Long, SchedulerCommand> commandByIDs = new HashMap<>();
    private boolean hasCommunicationError = false;

    public PTScheduler() {
        UIBuilder.getRootEventBus().addHandler(CommunicationErrorEvent.TYPE, this);
    }

    @Override
    public void create(final PTInstruction create, final UIService uiService) {}

    @Override
    public void update(final PTInstruction update, final UIService uiService) {
        final long commandID = update.getLong(Model.COMMAND_ID);
        if (update.containsKey(Model.STOP)) {
            // Stop the command
            commandByIDs.remove(commandID).cancel();
        } else if (update.containsKey(Model.FIXDELAY)) {
            // Fix-delay
            // Wait for execution terminated before scheduling again
            final SchedulerCommand previousCmd = commandByIDs.remove(commandID);
            if (previousCmd != null) previousCmd.cancel();
            final int delay = update.getInt(Model.FIXDELAY);
            final FixDelayCommand command = new FixDelayCommand(uiService, update.getObjectID(), commandID, delay);
            Scheduler.get().scheduleFixedDelay(command, delay);
            commandByIDs.put(commandID, command);
        } else if (update.containsKey(Model.FIXRATE)) {
            // Fix-rate
            final SchedulerCommand previousCmd = commandByIDs.remove(commandID);
            if (previousCmd != null) previousCmd.cancel();
            final int delay = update.getInt(Model.FIXRATE);
            final FixRateCommand command = new FixRateCommand(uiService, update.getObjectID(), commandID, delay);
            Scheduler.get().scheduleFixedDelay(command, delay);
            commandByIDs.put(commandID, command);
        }
    }

    @Override
    public void gc(final UIService uiService) {
        for (final SchedulerCommand command : commandByIDs.values()) {
            command.cancel();
        }
        commandByIDs.clear();
    }

    protected abstract class SchedulerCommand implements RepeatingCommand {

        protected final UIService uiService;
        protected final long schedulerID;
        protected final long commandID;
        protected final int delay;
        protected boolean cancelled = false;

        public SchedulerCommand(final UIService uiService, final long schedulerID, final long commandID, final int delay) {
            this.uiService = uiService;
            this.schedulerID = schedulerID;
            this.commandID = commandID;
            this.delay = delay;
        }

        public void cancel() {
            cancelled = true;
        }
    }

    protected class FixRateCommand extends SchedulerCommand {

        public FixRateCommand(final UIService uiService, final long schedulerID, final long commandID, final int delay) {
            super(uiService, schedulerID, commandID, delay);
        }

        @Override
        public boolean execute() {

            if (cancelled) return false;

            final PTInstruction instruction = new PTInstruction();
            instruction.setObjectID(schedulerID);
            // instruction.put(Model.TYPE_EVENT);
            instruction.put(Model.HANDLER_SCHEDULER);
            instruction.put(Model.ID, commandID);
            instruction.put(Model.FIXRATE, delay);

            uiService.sendDataToServer(instruction);

            return !hasCommunicationError;
        }

    }

    protected class FixDelayCommand extends SchedulerCommand {

        public FixDelayCommand(final UIService uiService, final long schedulerID, final long commandID, final int delay) {
            super(uiService, schedulerID, commandID, delay);
        }

        @Override
        public boolean execute() {

            if (cancelled) return false;

            final PTInstruction instruction = new PTInstruction();
            instruction.setObjectID(schedulerID);
            // instruction.put(Model.TYPE_EVENT);
            instruction.put(Model.HANDLER_SCHEDULER);
            instruction.put(Model.ID, commandID);
            instruction.put(Model.FIXDELAY, delay);

            uiService.sendDataToServer(instruction);

            return false;
        }

    }

    @Override
    public void onCommunicationError(final CommunicationErrorEvent event) {
        hasCommunicationError = true;
    }
}
