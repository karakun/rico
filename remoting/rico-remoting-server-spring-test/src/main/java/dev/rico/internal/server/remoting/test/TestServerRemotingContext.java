/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.server.remoting.test;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.commands.StartLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.remoting.server.config.RemotingConfiguration;
import dev.rico.internal.remoting.server.context.ServerRemotingContext;
import dev.rico.internal.remoting.server.controller.ControllerRepository;
import dev.rico.internal.remoting.server.legacy.communication.CommandHandler;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spi.components.ManagedBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Created by hendrikebbers on 05.12.17.
 */
public class TestServerRemotingContext extends ServerRemotingContext {

    private final BlockingQueue<Runnable> callLaterTasks = new LinkedBlockingQueue<>();

    public TestServerRemotingContext(final RemotingConfiguration configuration, final ClientSession clientSession, final ClientSessionProvider clientSessionProvider, final ManagedBeanFactory beanFactory, final ControllerRepository controllerRepository, final Consumer<ServerRemotingContext> onDestroyCallback) {
        super(configuration, clientSession, clientSessionProvider, beanFactory, controllerRepository, onDestroyCallback);

        getServerConnector().getRegistry().register(PingCommand.class, new CommandHandler() {
            @Override
            public void handleCommand(Command command, List response) {

            }
        });
    }

    @Override
    public List<Command> handle(final List<Command> commands) {
        final List<Command> commandsWithFackedLongPool = new ArrayList<>(commands);
        commandsWithFackedLongPool.add(new StartLongPollCommand());

        return super.handle(commandsWithFackedLongPool);
    }

    @Override
    protected void onLongPoll() {
        while (!callLaterTasks.isEmpty()) {
            callLaterTasks.poll().run();
        }
    }

    @Override
    public <T> Future<T> callLater(final Callable<T> callable) {
        Assert.requireNonNull(callable, "callable");
        final CompletableFuture<T> result = new CompletableFuture<>();
        callLaterTasks.offer(() -> {
            try {
                T taskResult = callable.call();
                result.complete(taskResult);
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });
        return result;
    }
}
