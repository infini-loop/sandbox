#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright 2010 Proofpoint, Inc.
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
package ${package};

import com.google.inject.Injector;
import ${groupId}.bootstrap.Bootstrap;
import ${groupId}.discovery.client.Announcer;
import ${groupId}.discovery.client.DiscoveryModule;
import ${groupId}.event.client.HttpEventModule;
import ${groupId}.http.server.HttpServerModule;
import ${groupId}.jaxrs.JaxrsModule;
import ${groupId}.jmx.JmxHttpModule;
import ${groupId}.jmx.JmxModule;
import ${groupId}.jmx.http.rpc.JmxHttpRpcModule;
import ${groupId}.json.JsonModule;
import ${groupId}.log.LogJmxModule;
import ${groupId}.log.Logger;
import ${groupId}.node.NodeModule;
import ${groupId}.tracetoken.TraceTokenModule;
import org.weakref.jmx.guice.MBeanModule;

public class Main
{
    private final static Logger log = Logger.get(Main.class);

    public static void main(String[] args)
            throws Exception
    {
        Bootstrap app = new Bootstrap(
                new NodeModule(),
                new DiscoveryModule(),
                new HttpServerModule(),
                new JsonModule(),
                new JaxrsModule(true),
                new MBeanModule(),
                new JmxModule(),
                new JmxHttpModule(),
                new JmxHttpRpcModule(),
                new LogJmxModule(),
                new HttpEventModule(),
                new TraceTokenModule(),
                new MainModule());

        try {
            Injector injector = app.strictConfig().initialize();
            injector.getInstance(Announcer.class).start();
        }
        catch (Throwable e) {
            log.error(e);
            System.exit(1);
        }
    }
}
