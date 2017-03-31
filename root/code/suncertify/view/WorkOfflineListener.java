/*
 * RemoteFileListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import suncertify.control.Launcher;

/**
 * Allows a user to switch the run mode between network client and stand-alone
 * run modes.
 * @author Starkie, Michael C.
 * @since Dec 26, 2010:4:50:29 PM
 */
public class WorkOfflineListener implements ActionListener {
    /** A reference to the UI */
    protected CRLView view = null;

    /**
     * @param v A non-null reference to the UI.
     */
    public WorkOfflineListener(CRLView v) {
        this.view = v;
    }

    /**
     * Switches run modes based on the value of a checkbox. Allows the user to
     * dynamically switch between networked client and stand-alone run modes.
     * @see LocalFileListener#selectForStandAlone(CRLView)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * @see suncertify.control.Launcher#startNetworkClient()
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JCheckBox workOffline = (JCheckBox) e.getSource();
            if (workOffline.isSelected()) {
                System.out.println("You chose to work OFFLINE: ");
                LocalFileListener.selectForStandAlone(view);
            } else {
                System.out.println("You chose to work ONLINE: ");
                Launcher.getInstance().startNetworkClient();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("Error while trying to start application.  Please note message "
                + "and contact technical support. Exception ("
                + ex.getMessage() + ")");
        }
    }

    /**
     * Display an error pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private void displayPopUpError(String errorMsg) {
        view.showMessageDialog(errorMsg, "Error", JOptionPane.OK_OPTION,
            JOptionPane.ERROR_MESSAGE);
    }
}
