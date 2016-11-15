package com.kms.katalon.core.webui.driver.safari;

import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;

/**
 * This class copy code from CSafariOptions class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSafariOptions {

    /**
     * Key used to store SafariOptions in a {@link DesiredCapabilities} object.
     */
    public static final String CAPABILITY = "safari.options";

    public static class Option {
        private Option() {
        }  // Utility class.

        public static final String CLEAN_SESSION = "cleanSession";

        public static final String PORT = "port";
    }

    /**
     * @see #setPort(int)
     */
    private int port = 0;

    /**
     * @see #setUseCleanSession(boolean)
     */
    private boolean useCleanSession = false;

    /**
     * Construct a {@link SafariOptions} instance from given capabilites.
     * When the {@link #CAPABILITY} capability is set, all other capabilities will be ignored!
     *
     * @param capabilities Desired capabilities from which the options are derived.
     * @return SafariOptions
     * @throws WebDriverException If an error occurred during the reconstruction of the options
     */
    public static CSafariOptions fromCapabilities(Capabilities capabilities) throws WebDriverException {
        Object cap = capabilities.getCapability(CSafariOptions.CAPABILITY);
        if (cap instanceof CSafariOptions) {
            return (CSafariOptions) cap;
        } else if (cap instanceof Map) {
            try {
                return CSafariOptions.fromJsonMap((Map<?, ?>) cap);
            } catch (IOException e) {
                throw new WebDriverException(e);
            }
        } else {
            return new CSafariOptions();
        }
    }

    // Setters

    /**
     * Set the port the {@link SafariDriverServer} should be started on. Defaults to 0, in which case
     * the server selects a free port.
     *
     * @param port The port the {@link SafariDriverServer} should be started on,
     * or 0 if the server should select a free port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Instruct the SafariDriver to delete all existing session data when starting a new session.
     * This includes browser history, cache, cookies, HTML5 local storage, and HTML5 databases.
     *
     * <p>
     * <strong>Warning:</strong> Since Safari uses a single profile for the
     * current user, enabling this capability will permanently erase any existing
     * session data.
     *
     * @param useCleanSession If true, the SafariDriver will erase all existing session data.
     */
    public void setUseCleanSession(boolean useCleanSession) {
        this.useCleanSession = useCleanSession;
    }

    // Getters

    /**
     * @return The port the {@link SafariDriverServer} should be started on.
     * If 0, the server should select a free port.
     * @see #setPort(int)
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Whether the SafariDriver should erase all session data before launching Safari.
     * @see #setUseCleanSession(boolean)
     */
    public boolean getUseCleanSession() {
        return useCleanSession;
    }

    // (De)serialization of the options

    /**
     * Converts this instance to its JSON representation.
     *
     * @return The JSON representation of the options.
     * @throws IOException If an error occurred while reading the Safari extension files.
     */
    public JsonObject toJson() throws IOException {
        JsonObject options = new JsonObject();
        options.addProperty(Option.PORT, port);
        options.addProperty(Option.CLEAN_SESSION, useCleanSession);
        return options;
    }

    /**
     * Parse a Map and reconstruct the {@link SafariOptions}.
     * A temporary directory is created to hold all Safari extension files.
     *
     * @param options A Map derived from the output of {@link #toJson()}.
     * @return A {@link SafariOptions} instance associated with these extensions.
     * @throws IOException If an error occurred while writing the safari extensions to a
     * temporary directory.
     */
    private static CSafariOptions fromJsonMap(Map<?, ?> options) throws IOException {
        CSafariOptions safariOptions = new CSafariOptions();

        Number port = (Number) options.get(Option.PORT);
        if (port != null) {
            safariOptions.setPort(port.intValue());
        }

        Boolean useCleanSession = (Boolean) options.get(Option.CLEAN_SESSION);
        if (useCleanSession != null) {
            safariOptions.setUseCleanSession(useCleanSession);
        }
        return safariOptions;
    }

    /**
     * Returns DesiredCapabilities for Safari with these options included as
     * capabilities. This does not copy the object. Further changes will be
     * reflected in the returned capabilities.
     *
     * @return DesiredCapabilities for Safari with these extensions.
     */
    public DesiredCapabilities toCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.safari();
        capabilities.setCapability(CAPABILITY, this);
        return capabilities;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CSafariOptions)) {
            return false;
        }
        CSafariOptions that = (CSafariOptions) other;
        return this.port == that.port && this.useCleanSession == that.useCleanSession;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.port, this.useCleanSession);
    }
}
