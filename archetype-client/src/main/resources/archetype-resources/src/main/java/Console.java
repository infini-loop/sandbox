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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.log.Logging;
import io.airlift.log.LoggingConfiguration;
import io.airlift.units.Duration;
import org.fusesource.jansi.AnsiConsole;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static ${package}.Completion.commandCompleter;
import static ${package}.Completion.lowerCaseCommandCompleter;
import static ${package}.Help.getHelpText;
import static ${package}.QueryPreprocessor.preprocessQuery;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.SECONDS;

@Command(name = "client", description = "client command")
public class Console
        implements Runnable
{
    private static final Duration EXIT_DELAY = new Duration(3, SECONDS);

    @Inject
    public HelpOption helpOption;

    @Inject
    public VersionOption versionOption = new VersionOption();

    @Inject
    public ClientOptions clientOptions = new ClientOptions();

    @Override
    public void run()
    {
        if (clientOptions.configFil!= null) {
            // Read
            System.setProperty("config", clientOptions.configFil);
        }

        ImmutableList.Builder<Module> builder = ImmutableList.<Module>builder()
                .add(new ClientMainModule())
                .addAll(getAdditionalModules());

        Bootstrap app = new Bootstrap(builder.build());
        Injector injector;
        try {
            injector = app.strictConfig().initialize();
        }
        catch (Exception e) {
            throwIfUnchecked(e);
            throw new RuntimeException(e);
        }

        boolean hasCommand = !Strings.isNullOrEmpty(clientOptions.execute);
        boolean isFromFile = !Strings.isNullOrEmpty(clientOptions.file);

        if (!hasQuery && !isFromFile) {
            AnsiConsole.systemInstall();
        }

        initializeLogging(clientOptions.logLevelsFile);

        String command = clientOptions.execute;
        if (hasCommand) {
            command += ";";
        }

        if (isFromFile) {
            if (hasCommand) {
                throw new RuntimeException("both --execute and --file specified");
            }
            try {
                command = Files.toString(new File(clientOptions.file), UTF_8);
                hasCommand = true;
            }
            catch (IOException e) {
                throw new RuntimeException(format("Error reading from file %s: %s", clientOptions.file, e.getMessage()));
            }
        }

        AtomicBoolean exiting = new AtomicBoolean();
        interruptThreadOnExit(Thread.currentThread(), exiting);

		HttpClientBuilder clientBuilder = new HttpClientBuilder(
                Optional.ofNullable(clientOptions.socksProxy),
                Optional.ofNullable(clientOptions.httpProxy),
                Optional.ofNullable(clientOptions.keystorePath),
                Optional.ofNullable(clientOptions.keystorePassword),
                Optional.ofNullable(clientOptions.truststorePath),
                Optional.ofNullable(clientOptions.truststorePassword),
                Optional.ofNullable(clientOptions.user),
                clientOptions.password ? Optional.of(getPassword()) : Optional.empty(),
                Optional.ofNullable(clientOptions.krb5Principal),
                Optional.ofNullable(clientOptions.krb5RemoteServiceName),
                Optional.ofNullable(clientOptions.krb5ConfigPath),
                Optional.ofNullable(clientOptions.krb5KeytabPath),
                Optional.ofNullable(clientOptions.krb5CredentialCachePath),
                !clientOptions.krb5DisableRemoteServiceHostnameCanonicalization,
                clientOptions.authenticationEnabled);

		try (CommandRunner commandRunner = new CommandRunner(
                httpClientBuilder,
                clientOptions))
			commandRunner.executeCommand(command);
		}
		finally {
            try {
                injector.getInstance(LifeCycleManager.class).stop();
            }
            catch (Exception e) {
                throwIfUnchecked(e);
                throw new RuntimeException(e);
            }
        }
    }

    private String getPassword()
    {
        checkState(clientOptions.user != null, "Username must be specified along with password");
        String defaultPassword = System.getenv("PRESTO_PASSWORD");
        if (defaultPassword != null) {
            return defaultPassword;
        }

        java.io.Console console = System.console();
        if (console == null) {
            throw new RuntimeException("No console from which to read password");
        }
        char[] password = console.readPassword("Password: ");
        if (password != null) {
            return new String(password);
        }
        return "";
    }

    private static void initializeLogging(String logLevelsFile)
    {
        // unhook out and err while initializing logging or logger will print to them
        PrintStream out = System.out;
        PrintStream err = System.err;

        try {
            LoggingConfiguration config = new LoggingConfiguration();

            if (logLevelsFile == null) {
                System.setOut(new PrintStream(nullOutputStream()));
                System.setErr(new PrintStream(nullOutputStream()));

                config.setConsoleEnabled(false);
            }
            else {
                config.setLevelsFile(logLevelsFile);
            }

            Logging logging = Logging.initialize();
            logging.configure(config);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            System.setOut(out);
            System.setErr(err);
        }
    }

    private static void interruptThreadOnExit(Thread thread, AtomicBoolean exiting)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            exiting.set(true);
            thread.interrupt();
            try {
                thread.join(EXIT_DELAY.toMillis());
            }
            catch (InterruptedException ignored) {
            }
        }));
    }

    protected Iterable<Module> getAdditionalModules()
    {
        return ImmutableList.of();
    }
}
