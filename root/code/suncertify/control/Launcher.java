/*
 * Launcher.java Sun Certified Developer for the Java 2 Platform Submission.
 * 2010 Bodgitt and Scarper, LLC
 */
package suncertify.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import javax.swing.JOptionPane;

import suncertify.props.PropConfigLauncher;
import suncertify.view.CRLView;
import suncertify.view.LocalFileListener;

/**
 * The initial starting point used to bootstrap the programs. Serves as a single
 * point of reference for starting the network client, network server, and
 * stand-alone modes. In all modes, except network server mode, the start up
 * sequence is to build the graphical components first, initialize the data
 * services, and lastly display the UI to the user. All 3 of these tasks are
 * done synchronously to ensure that the customer view (UI) has a fully
 * operational handle to the data services and is initialized to the proper run
 * mode before being rendered to the user.
 * @author Starkie, Michael C.
 * @since Nov 3, 2010:7:49:24 AM
 */
public class Launcher {
    /** The property name of the database file location */
    public static final String DB_FILE_PROP = "db.file";
    /** The property name of the RMI registry port */
    public static final String RMI_REGISTRY_PROP = "rmi.registry.port";
    /** The property name of the RMI registry port */
    public static final String RMI_REGISTRY_HOST = "rmi.registry.host";
    /** The property name of the file holding the properties */
    public static final String PROP_FILE = "suncertify.properties";
    /** Object containing the property names and values as read from a file */
    private static Properties props = null;
    /** The default port the RMI server should run on */
    private int port = 1099;
    /** The RMI name used to bind the remote object */
    private String lookupString = null;
    /** A string representation of the rmi port */
    private String rmiPort = null;
    /** A string representation of the rmi host */
    private String rmiHost = null;
    /** The client UI */
    private CRLView view = null;
    /** The singleton instance of this bootstrap */
    private static final Launcher bootStrap = new Launcher();

    /**
     * Creates an instance of the bootstrap for public use.
     * @return An instance of Launcher
     */
    public static final Launcher getInstance() {
        return Launcher.bootStrap;
    }

    /**
     * The application entry point for all run modes. The mode flag (args[0])
     * must be either "server", indicating the server program must run, "alone",
     * indicating standalone mode, or left out entirely, in which case the
     * network client and gui must run in a single JVM.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Launcher.bootStrap.systemInit(RunMode.CLIENT);
        } else {
            String mode = args[0].toLowerCase().trim();
            if (mode.equals("server")) {
                Launcher.bootStrap.systemInit(RunMode.SERVER);
            } else
                if (mode.equals("alone")) {
                    Launcher.bootStrap.systemInit(RunMode.ALONE);
                } else {
                    System.out
                        .println("usage: [server|alone(client & server)] || <empty>(client only)");
                    System.exit(-1);
                }
        }
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the system given a run mode.
     * @param mode The mode to run in.
     * @see suncertify.control.RunMode
     * @see #main(String[])
     */
    private void systemInit(RunMode mode) {
        Launcher.props = PropConfigLauncher.readProperties();
        rmiPort = Launcher.props
            .getProperty(Launcher.RMI_REGISTRY_PROP, "1099");
        rmiHost = Launcher.props.getProperty(Launcher.RMI_REGISTRY_HOST, "");
        port = Integer.valueOf(rmiPort);
        lookupString = "//" + rmiHost + ":" + port + "/DataAccessImpl";
        switch (mode) {
            case CLIENT:
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view = new CRLView();
                    }
                });
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        startNetworkClient();
                    }
                });
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view.displayGUI();
                    }
                });
                break;
            case ALONE:
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view = new CRLView();
                    }
                });
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        startStandAlone(Launcher.props
                            .getProperty(Launcher.DB_FILE_PROP));
                    }
                });
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        view.displayGUI();
                    }
                });
                break;
            default:
                startNetworkServer();
        }
    }

    /**
     * Starts the network client. Since this mode uses RMI the reference to the
     * client data services ({@linkplain suncertify.control.DataAccess
     * DataAccess}) will be via an RMI client stub.
     * @see suncertify.control.Launcher#getNetworkClientImpl()
     */
    public void startNetworkClient() {
        System.out.println("Starting network client...");
        DataAccess dataAccess = getNetworkClientImpl();
        if (dataAccess == null) {
            int option = view.showConfirmDialog(
                "Problem with remote connection.\nDo you wish to work OFFINE?"
                    + "\nProgram will exit if you select No.",
                "Work-Online Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                String locatorURI = null;
                if (view.getRunMode() == RunMode.ALONE) {
                    locatorURI = view.getDataAccessLocator();
                }
                if (locatorURI != null) {
                    dataAccess = getStandAloneImpl(locatorURI);
                } else {
                    dataAccess = getStandAloneImpl(Launcher.props
                        .getProperty(Launcher.DB_FILE_PROP));
                }
                if (dataAccess == null) {
                    LocalFileListener.selectForStandAlone(view);
                    return;
                }
            } else {
                System.out.println("Goodbye");
                System.exit(0);
            }
            view.setStandAloneRunMode();
            view.setDataAccess(dataAccess);
            return;
        }
        view.setClientRunMode();
        view.setDataAccess(dataAccess);
    }

    /**
     * Starts the network server. In this mode only the data services are
     * initialized and made available to a remote client via RMI. The client
     * data services ({@linkplain suncertify.control.DataAccess DataAccess}) are
     * bound to an RMI service.
     * @see suncertify.control.Launcher#getNetworkServerImpl()
     */
    private void startNetworkServer() {
        System.out.println("Starting network server...");
        getNetworkServerImpl();
    }

    /**
     * Starts the application in stand-alone mode. The client data services (
     * {@linkplain suncertify.control.DataAccess DataAccess}) is a pure java
     * object and not bound to any RMI services.
     * @param locatorURI The file location of the underlying database.
     * @see suncertify.control.Launcher#getStandAloneImpl(String)
     */
    public void startStandAlone(String locatorURI) {
        view.setStandAloneRunMode();
        System.out.println("Recursing?");
        DataAccess dataAccess = getStandAloneImpl(locatorURI);
        view.setDataAccess(dataAccess);
        if (dataAccess == null) {
            int option = view.showConfirmDialog(
                "Problem with configured file.\n"
                    + "Select a new file and continue to work OFFLINE?",
                "Stand-Alone Error", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                LocalFileListener.selectForStandAlone(view);
                return;
            } else {
                System.out.println("Goodbye");
                System.exit(0);
            }
        }
    }

    /**
     * Returns an instance of DataAccess that operates on a local database file.
     * @param locatorURI A string representing the location of a local database
     *            file.
     * @return an instance of DataAccess
     * @see suncertify.control.DataAccess
     * @see suncertify.control.Launcher#startStandAlone(String)
     */
    private DataAccess getStandAloneImpl(String locatorURI) {
        DataAccess dataAccess = null;
        try {
            dataAccess = DataAccessFactory.getDataAccess(locatorURI);
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        }
        System.out.println("Running in STAND-ALONE mode");
        return dataAccess;
    }

    /**
     * Returns an instance of DataAccess that operates on a remote database
     * file.
     * @return an instance of DataAccess
     * @see suncertify.control.DataAccess
     * @see suncertify.control.Launcher#startNetworkClient()
     */
    private DataAccess getNetworkClientImpl() {
        DataAccess dataAccess = null;
        try {
            dataAccess = (DataAccess) Naming.lookup(lookupString);
        } catch (MalformedURLException e) {
            System.out
                .println("There seems to be a problem with the URL of a remote resource: "
                    + lookupString);
            System.out.println("Please contact technical support.");
            e.printStackTrace();
            return null;
        } catch (RemoteException e) {
            System.out
                .println("There seems to be a problem obtaining a remote resource: "
                    + lookupString);
            System.out.println("Please contact technical support.");
            e.printStackTrace();
            return null;
        } catch (NotBoundException e) {
            System.out
                .println("There seems to be a problem with the port that the RMI registry is running on: "
                    + lookupString);
            System.out.println("Please contact technical support.");
            e.printStackTrace();
            return null;
        }
        System.out.println("RMI naming lookup successful.");
        System.out.println("Running in CLIENT mode.");
        return dataAccess;
    }

    /**
     * Returns an instance of DataAccess that operates on a local database file.
     * However the instance is operating in RMI mode and is bound to RMI.
     * @return an instance of DataAccess
     * @see suncertify.control.DataAccess
     * @see suncertify.control.Launcher#startNetworkServer()
     */
    private DataAccess getNetworkServerImpl() {
        DataAccess dataAccess = null;
        try {
            System.out
                .println("Attemping RMI Registry creation and name binding...please stand by before proceeding...");
            if (port != 1099) {
                System.out
                    .println("I see you're not using the default RMI port.  \nIn case of any issues you might try setting the rmi.registry.port in suncertify.properties to 1099");
            }
            LocateRegistry.createRegistry(port);
            dataAccess = DataAccessFactory.getDataAccess(Launcher.props
                .getProperty(Launcher.DB_FILE_PROP));
            Naming.rebind(lookupString, dataAccess);
            System.out.println("RMI Registry started: " + lookupString
                + ".  Server mode initialization complete.  Please proceed.");
        } catch (FileNotFoundException f) {
            System.out
                .println("There seems to be a problem with location the of the DB File "
                    + "specified in the suncerty.properties file: "
                    + Launcher.props.getProperty(Launcher.DB_FILE_PROP)
                    + ". \nPlease ensure the properties listed in this file are correct.");
            f.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out
                .println("There seems to be an IO problem that is shutting this server down"
                    + ". \nPerhaps another instance of the RMI Registry is already running on port: "
                    + port + "?");
            System.out.println("Please contact technical support.");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Running in SERVER mode");
        return dataAccess;
    }
}
