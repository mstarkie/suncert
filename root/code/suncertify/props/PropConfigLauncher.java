/*
 * Launcher.java Sun Certified Developer for the Java 2 Platform Submission.
 * 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import suncertify.control.Launcher;

/**
 * The properties editor bootstrap. Contains the main method to launch the
 * properties editor.
 * @author Starkie, Michael C.
 * @since Nov 3, 2010:7:49:24 AM
 */
public class PropConfigLauncher {
    /** The singleton instance of this bootstrap */
    private static final PropConfigLauncher bootStrap = new PropConfigLauncher();
    /** The client UI */
    private PropView view = null;

    private PropConfigLauncher() {
    }

    /**
     * Creates an instance of the bootstrap for public use.
     * @return An instance of Launcher
     */
    public static final PropConfigLauncher getInstance() {
        return PropConfigLauncher.bootStrap;
    }

    /**
     * The application entry point.
     */
    public static void main(String[] args) {
        PropConfigLauncher.bootStrap.systemInit();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the system.
     * @see #main(String[])
     */
    private void systemInit() {
        PropConfigLauncher.readProperties();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view = new PropView();
            }
        });
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.displayGUI();
            }
        });
    }

    /**
     * Reads the system properties from the properties file.
     * @return A properties object containing the system properties.
     */
    public static Properties readProperties() {
        ClassLoader classLoader = Launcher.class.getClassLoader();
        URL propFileLocator = classLoader.getResource(Launcher.PROP_FILE);
        String workingDir = System.getProperty("user.dir");
        System.out.println("current working directory: " + workingDir);
        String classPath = System.getProperty("java.class.path");
        System.out.println("java.class.path=" + classPath);
        if (propFileLocator == null) {
            System.out.println("suncertify.properties not found...exiting");
            System.exit(-1);
        }
        Properties props = null;
        try {
            URI resourceURI = propFileLocator.toURI();
            InputStream propStream = new FileInputStream(new File(resourceURI));
            props = new Properties();
            props.load(propStream);
            Iterator<Entry<Object, Object>> i = props.entrySet().iterator();
            while (i.hasNext()) {
                Entry<Object, Object> e = i.next();
                System.out.println("loading property from file: key="
                    + e.getKey() + ", value=" + e.getValue());
            }
        } catch (Exception e) {
            System.err
                .println("An attempt was made to read the start up properties file: "
                    + Launcher.PROP_FILE
                    + ". Ensure that the CLASSPATH contains this file.  ");
            System.exit(-1);
        }
        return props;
    }

    /**
     * Overwrites the properties file with properties from the properties
     * object.
     * @param props The properties object containing the new properties.
     * @throws Exception When there a problem writing to the file.
     */
    public static void writeProperties(Properties props) throws Exception {
        ClassLoader classLoader = Launcher.class.getClassLoader();
        URL propFileLocator = classLoader.getResource(Launcher.PROP_FILE);
        String workingDir = System.getProperty("user.dir");
        System.out.println("current working directory: " + workingDir);
        if (propFileLocator == null) {
            System.out
                .println("suncertify.properties not found...aborting writeProperties().");
            return;
        }
        URI resourceURI = propFileLocator.toURI();
        OutputStream propStream = new FileOutputStream(new File(resourceURI));
        props.store(propStream, "Saved from ConfigLauncher");
    }
}
