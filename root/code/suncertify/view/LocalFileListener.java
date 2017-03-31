/*
 * RemoteFileListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import suncertify.control.DataAccess;
import suncertify.control.DataAccessImpl;
import suncertify.control.Launcher;

/**
 * Displays a file selector when user is working offline and wishes to select a
 * local database file.
 * @author Starkie, Michael C.
 * @since Dec 26, 2010:4:50:29 PM
 */
public class LocalFileListener implements ActionListener {
    /** a reference to the GUI */
    protected CRLView view = null;

    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public LocalFileListener(CRLView v) {
        this.view = v;
    }

    /**
     * Present a file chooser for the user to select a new local database file
     * to use and reinitializes the data access module to use the file in
     * stand-alone mode.
     * @param v The GUI view
     */
    public static void selectForStandAlone(CRLView v) {
        try {
            DataAccess dataAccess = v.getDataAccess();
            if ((dataAccess != null) && (dataAccess instanceof DataAccessImpl)) {
                try {
                    ((DataAccessImpl) dataAccess).close();
                } catch (Exception e) {
                }
            }
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Database Files", "db");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out
                    .println("You chose to open this file and work OFFLINE: "
                        + chooser.getSelectedFile().getName());
            } else {
                System.out
                    .println("User has selected no file for work offline.");
                LocalFileListener.displayPopUpWarning(v, "No file selected");
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                LocalFileListener.displayPopUpWarning(v, "No file selected");
                return;
            }
            String locatorURI = file.toURI().toString();
            System.out.println("User has selected local file: " + locatorURI);
            Launcher.getInstance().startStandAlone(locatorURI);
        } catch (Exception ex) {
            ex.printStackTrace();
            LocalFileListener
                .displayPopUpError(v,
                    "Unable to start application.  Please contact technical support.");
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        LocalFileListener.selectForStandAlone(view);
    }

    /**
     * Display an error pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private static void displayPopUpError(CRLView view, String errorMsg) {
        view.showMessageDialog(errorMsg, "Error", JOptionPane.OK_OPTION,
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Display a warning pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private static void displayPopUpWarning(CRLView view, String msg) {
        view.showMessageDialog(msg, "Warning", JOptionPane.OK_OPTION,
            JOptionPane.WARNING_MESSAGE);
    }
}
