package com.kms.katalon.composer.integration.git.components.utils;

import java.io.File;
import java.util.TreeMap;

import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

public class Protocol {
    /** Ordered list of all protocols **/
    private static final TreeMap<String, Protocol> protocols = new TreeMap<>();

    /** Git native transfer */
    public static final Protocol GIT = new Protocol("git", //$NON-NLS-1$
            true, true, false);

    /** Git over SSH */
    public static final Protocol SSH = new Protocol("ssh", //$NON-NLS-1$
            true, true, true) {
        @Override
        public boolean handles(URIish uri) {
            if (!uri.isRemote()) {
                return false;
            }
            final String scheme = uri.getScheme();
            if (getDefaultScheme().equals(scheme)) {
                return true;
            }
            if ("ssh+git".equals(scheme)) { //$NON-NLS-1$
                return true;
            }
            if ("git+ssh".equals(scheme)) {//$NON-NLS-1$
                return true;
            }
            if (scheme == null && uri.getHost() != null && uri.getPath() != null) {
                return true;
            }
            return false;
        }
    };

    /** Secure FTP */
    public static final Protocol SFTP = new Protocol("sftp", //$NON-NLS-1$
            true, true, true);

    /** HTTP */
    public static final Protocol HTTP = new Protocol("http", //$NON-NLS-1$
            true, true, true);

    /** Secure HTTP */
    public static final Protocol HTTPS = new Protocol("https", //$NON-NLS-1$
            true, true, true);

    /** FTP */
    public static final Protocol FTP = new Protocol("ftp", //$NON-NLS-1$
            true, true, true);

    /** Local repository */
    public static final Protocol FILE = new Protocol("file", //$NON-NLS-1$
            false, false, false) {
        @Override
        public boolean handles(URIish uri) {
            if (getDefaultScheme().equals(uri.getScheme())) {
                return true;
            }
            if (uri.getHost() != null || uri.getPort() > 0 || uri.getUser() != null || uri.getPass() != null
                    || uri.getPath() == null) {
                return false;
            }
            if (uri.getScheme() == null) {
                return FS.DETECTED.resolve(new File("."), uri.getPath()).isDirectory(); //$NON-NLS-1$
            }
            return false;
        }
    };

    private final String defaultScheme;

    private final boolean hasHost;

    private final boolean hasPort;

    private final boolean canAuthenticate;

    private Protocol(String defaultScheme, boolean hasHost, boolean hasPort, boolean canAuthenticate) {
        this.defaultScheme = defaultScheme;
        this.hasHost = hasHost;
        this.hasPort = hasPort;
        this.canAuthenticate = canAuthenticate;
        protocols.put(defaultScheme, this);
    }

    /**
     * @param uri
     *            URI to match against this protocol
     * @return {@code true} if the uri is handled by this protocol
     */
    public boolean handles(URIish uri) {
        return getDefaultScheme().equals(uri.getScheme());
    }

    /**
     * @return the default protocol scheme
     */
    public String getDefaultScheme() {
        return defaultScheme;
    }

    /**
     * @return true if protocol has host segment
     */
    public boolean hasHost() {
        return hasHost;
    }

    /**
     * @return true if protocol has port
     */
    public boolean hasPort() {
        return hasPort;
    }

    /**
     * @return true if protocol can authenticate
     */
    public boolean canAuthenticate() {
        return canAuthenticate;
    }

    /**
     * @return all protocols
     */
    public static Protocol[] values() {
        return protocols.values().toArray(new Protocol[protocols.size()]);
    }

    /**
     * Lookup protocol supporting given default URL scheme
     *
     * @param scheme
     *            default scheme to lookup protocol for
     * @return protocol matching scheme or null
     */
    public static Protocol fromDefaultScheme(String scheme) {
        return protocols.get(scheme);
    }

    /**
     * Lookup protocol handling given URI
     *
     * @param uri
     *            URI to lookup protocol for
     * @return protocol handling this URI
     */
    public static Protocol fromUri(URIish uri) {
        for (Protocol p : protocols.values()) {
            if (p.handles(uri)) {
                return p;
            }
        }
        return null;
    }
}
