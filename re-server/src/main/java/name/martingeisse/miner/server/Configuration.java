package name.martingeisse.miner.server;

import io.github.grumpystuff.grumpyrest.request.querystring.QuerystringParserRegistry;
import io.github.grumpystuff.grumpyrest.request.querystring.QuerystringParsingException;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserRegistry;
import io.github.grumpystuff.grumpyrest.request.stringparser.standard.IntegerFromStringParser;
import io.github.grumpystuff.grumpyrest.request.stringparser.standard.OptionalFieldParser;
import io.github.grumpystuff.grumpyrest.request.stringparser.standard.StringFromStringParser;
import name.martingeisse.miner.common.util.UnexpectedExceptionException;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides access to miner-server.properties through static getter methods.
 * <p>
 * This class must be initialized before use.
 */
public record Configuration(
        String databaseUrl,
        String databaseUsername,
        String databasePassword
) {

    public static Configuration get() {
        try {
            return initializer.get();
        } catch (Exception e) {
            throw new RuntimeException("could not initialize shared Configuration instance", e);
        }
    }

    private static final AtomicSafeInitializer<Configuration> initializer = new AtomicSafeInitializer<>() {
        @Override
        protected Configuration initialize() {

            // load properties
            Properties properties = new Properties();
            try (FileInputStream in = new FileInputStream("resource/config.properties")) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException("could not load configuration file", e);
            }

            // convert to Map
            Map<String, String> configurationMap = new HashMap<>();
            for (Object key : properties.keySet()) {
                Object value = properties.get(key);
                if (configurationMap.put(key.toString(), value.toString()) != null) {
                    throw new RuntimeException("duplicate configuration field: " + key);
                }
            }

            // define field parsers
            var fromStringParserRegistry = new FromStringParserRegistry();
            fromStringParserRegistry.register(new StringFromStringParser());
            fromStringParserRegistry.register(new IntegerFromStringParser());
            fromStringParserRegistry.register(new OptionalFieldParser(fromStringParserRegistry));
            fromStringParserRegistry.seal();

            // parse to high-level type
            var configurationParserRegistry = new QuerystringParserRegistry(fromStringParserRegistry);
            configurationParserRegistry.seal();
            try {
                var parser = configurationParserRegistry.get(Configuration.class);
                var result = (Configuration) parser.parse(configurationMap, Configuration.class);
                if (result == null) {
                    throw new RuntimeException("configuration parser returned null");
                }
                return result;
            } catch (QuerystringParsingException e) {
                // duplicate-parameter errors take precedence here
                for (Map.Entry<String, String> entry : e.getFieldErrors().entrySet()) {
                    throw new RuntimeException("error in configuration field " + entry.getKey() + ": " + entry.getValue());
                }
                throw new RuntimeException("exception while parsing the configuration", e);
            } catch (Exception e) {
                throw new UnexpectedExceptionException("exception while parsing the configuration", e);
            }

        }
    };
}
