/*
 * Copyright 2012-2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.phantom.http.impl;

import org.apache.http.HttpResponse;

import com.flipkart.phantom.http.impl.registry.HttpProxyRegistry;
import com.flipkart.phantom.task.spi.Executor;
import com.flipkart.phantom.task.spi.RequestWrapper;
import com.flipkart.phantom.task.spi.TaskContext;
import com.flipkart.phantom.task.spi.registry.AbstractHandlerRegistry;
import com.flipkart.phantom.task.spi.repository.ExecutorRepository;

/**
 * Provides a repository of HttpProxyExecutor classes which execute HTTP requests using Hystrix commands
 *
 * @author kartikbu
 * @created 16/7/13 1:54 AM
 * @version 1.0
 */
public class HttpProxyExecutorRepository implements ExecutorRepository<HttpResponse, HttpProxy>{

    /** repository */
    private HttpProxyRegistry registry;

    /** The TaskContext instance */
    private TaskContext taskContext;

    /**
     * Returns {@link Executor} for the specified requestWrapper
     * @param commandName command Name as specified by Executor. Not used in this case.
     * @param proxyName   proxyName the HttpProxy name
     * @param requestWrapper requestWrapper Object containing requestWrapper Data
     * @return  an {@link HttpProxyExecutor} instance
     */
    public Executor<HttpResponse> getExecutor (String commandName, String proxyName, RequestWrapper requestWrapper)  {
        HttpProxy proxy = (HttpProxy) registry.getHandler(proxyName);
        if (proxy.isActive()) {
            return new HttpProxyExecutor(proxy, this.taskContext, requestWrapper);
        }
        throw new RuntimeException("The HttpProxy is not active.");
    }

    /** Getter/Setter methods */

    @Override
    public AbstractHandlerRegistry<HttpProxy> getRegistry() {
        return registry;
    }

    @Override
    public void setRegistry(AbstractHandlerRegistry<HttpProxy> registry) {
        this.registry = (HttpProxyRegistry)registry;
    }

    @Override
    public TaskContext getTaskContext() {
        return this.taskContext;
    }

    @Override
    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }
    /** End Getter/Setter methods */
}
