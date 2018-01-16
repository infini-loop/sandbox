#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
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

import com.google.common.net.HostAndPort;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.facebook.presto.client.ClientSession.stripTransactionId;
import static ${package}.OkHttpUtil.basicAuth;
import static ${package}.OkHttpUtil.setupHttpProxy;
import static ${package}.OkHttpUtil.setupKerberos;
import static ${package}.OkHttpUtil.setupSocksProxy;
import static ${package}.OkHttpUtil.setupSsl;
import static ${package}.OkHttpUtil.setupTimeouts;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CommandRunner
        implements Closeable
{
 	private final URI server;
	private final Set<String> clientTags;
	private final Map<String, String> headers;
    private final OkHttpClient httpClient;
    private final Consumer<OkHttpClient.Builder> sslSetup;

    public CommandRunner(
			URI server,
			Set<String> clientTags,
			Map<String, String> headers,
            Optional<HostAndPort> socksProxy,
            Optional<HostAndPort> httpProxy,
            Optional<String> keystorePath,
            Optional<String> keystorePassword,
            Optional<String> truststorePath,
            Optional<String> truststorePassword,
            Optional<String> user,
            Optional<String> password,
            Optional<String> kerberosPrincipal,
            Optional<String> kerberosRemoteServiceName,
            Optional<String> kerberosConfigPath,
            Optional<String> kerberosKeytabPath,
            Optional<String> kerberosCredentialCachePath,
            boolean kerberosUseCanonicalHostname,
            boolean kerberosEnabled)
    {
		this.server = server;
		this.clientTags = ImmutableSet.copyOf(clientTags);
		this.headers = ImmutableMap.copyOf(headers);
		this.sslSetup = builder -> setupSsl(builder, keystorePath, keystorePassword, truststorePath, truststorePassword);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        setupTimeouts(builder, 5, SECONDS);
        setupSocksProxy(builder, socksProxy);
        setupHttpProxy(builder, httpProxy);
        setupBasicAuth(builder, session, user, password);

        if (kerberosEnabled) {
            setupKerberos(
                    builder,
                    kerberosRemoteServiceName.orElseThrow(() -> new ClientException("Kerberos remote service name must be set")),
                    kerberosUseCanonicalHostname,
                    kerberosPrincipal,
                    kerberosConfigPath.map(File::new),
                    kerberosKeytabPath.map(File::new),
                    kerberosCredentialCachePath.map(File::new));
        }

        this.httpClient = builder.build();
    }
    
	public void executeCommand(String command)
	{
	}
}

