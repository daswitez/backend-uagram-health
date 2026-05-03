package bo.edu.uagrm.ugram.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads key-value pairs from a {@code .env} file in the working directory
 * and registers them as a Spring {@link MapPropertySource} with low priority
 * (real environment variables and application.yml always win).
 *
 * <p>Supports blank lines, {@code #} comments, and values with or without quotes.
 * Trailing whitespace in values is trimmed to avoid authentication issues.</p>
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotenvPath = Path.of(".env");
        if (!Files.exists(dotenvPath)) {
            return; // No .env file — skip silently (production behavior)
        }

        try {
            Map<String, Object> dotenvProperties = new HashMap<>();
            for (String line : Files.readAllLines(dotenvPath)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eqIndex = trimmed.indexOf('=');
                if (eqIndex <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eqIndex).trim();
                String value = trimmed.substring(eqIndex + 1).trim();
                // Strip surrounding quotes if present
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                dotenvProperties.put(key, value);
            }

            // Add with low priority (last) so real env vars and application.yml win
            environment.getPropertySources()
                    .addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, dotenvProperties));

        } catch (IOException e) {
            throw new RuntimeException("Failed to read .env file", e);
        }
    }
}
