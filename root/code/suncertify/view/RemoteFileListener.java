/*
 * RemoteFileListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * Presents a text entry dialogue to the user when user wishes to select remote
 * file. Ability for the user to browse a remote directory in the same way she
 * could when working offline is currently not supported. The URI of the
 * configured remote file is presented offering the user a hint as to the format
 * she should use when specifying the remote file. The user may edit the text of
 * the URI but the user must already know the location of the file she wishes to
 * specify on the remote file system and ensure that the URI syntax of the text
 * field is valid.
 * @author Starkie, Michael C.
 * @since Dec 26, 2010:4:50:29 PM
 */
public class RemoteFileListener implements ActionListener {
    /** The instance of the main GUI */
    protected CRLView view = null;
    /** A text field to display and enter the URI of a remote file */
    protected JTextField textField = null;
    /** The main frame of the pop-up the text field will appear in */
    protected JFrame frame = null;

    /**
     * @param v a non-null reference to the GUI
     */
    public RemoteFileListener(CRLView v) {
        this.view = v;
    }

    /**
     * Renders the URI of the current remote database file in a text field.
     * Allows the user to modify the URI. When the user submits the modified
     * entry a remote operation will occur to re-initialize the data access
     * module on the remote server and attempt to use the specified file as the
     * underlying database.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * @see SetRemoteFile
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            frame = new JFrame("Set Remote Database File");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout(0, 0));
            panel.add(new JLabel("Remote File:"), BorderLayout.NORTH);
            textField = new JTextField();
            Font f = new Font(Font.DIALOG, Font.PLAIN, 18);
            textField.setFont(f);
            panel.add(textField, BorderLayout.CENTER);
            String remoteFile = view.getDataAccess().getLocatorURI();
            textField.setText(remoteFile);
            JPanel buttonPanel = new JPanel();
            JButton button = new JButton("Use File");
            button.addActionListener(new SetRemoteFile());
            buttonPanel.add(button);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            panel.setBorder(new EmptyBorder(8, 8, 8, 8));
            Point p = new Point(view.getLocation().x + 100,
                view.getLocation().y + 100);
            frame.setLocation(p);
            frame.getContentPane().add(panel);
            frame.setSize(700, 135);
            frame.setVisible(true);
        } catch (RemoteException re) {
            re.printStackTrace();
            displayPopUpError("Unable to retrieve remote file URL.  "
                + "Please contact technical support. " + "(" + re.getMessage()
                + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("Unable to offer remote file specification.  "
                + "Please contact technical support. " + "(" + ex.getMessage()
                + ")");
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

    /**
     * Responsible for invoking a remote operation to re-initialize the data
     * access module on the remote server to use a new database file specified
     * as a URI in a parameter passed during this operation.
     * @author Starkie, Michael C.
     * @since Feb 1, 2011:6:54:09 PM
     */
    private class SetRemoteFile implements ActionListener {
        /**
         * Invokes the operation described above.
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                boolean result = view.getDataAccess().setLocatorURI(
                    textField.getText().trim());
                if (result) {
                    frame.dispose();
                } else {
                    displayPopUpError("Unable to set remote file URL.  "
                        + "Please contact technical support.");
                }
            } catch (RemoteException e1) {
                displayPopUpError("Unable to set remote file URL.  "
                    + "Please contact technical support. " + "("
                    + e1.getMessage() + ")");
                e1.printStackTrace();
            } catch (Exception e2) {
                displayPopUpError("Unable to set remote file URL.  "
                    + "Please contact technical support. " + "("
                    + e2.getMessage() + ")");
                e2.printStackTrace();
            }
        }
    }
}
