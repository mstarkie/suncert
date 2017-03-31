/*
 * CRLView.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import suncertify.control.DataAccess;
import suncertify.control.DataAccessImpl;
import suncertify.control.RunMode;

/**
 * The user view and graphical components.
 * @author Starkie, Michael C.
 * @since Nov 14, 2010:5:41:12 PM
 */
public final class CRLView {
    /** the width of the main frame */
    public static final int VIEW_WIDTH = 1000;
    /** the height of the main frame */
    public static final int VIEW_HEIGHT = 650;
    /** menu option to select remote file */
    private JMenuItem mntmRemoteFile;
    /** menu option to select local file */
    private JMenuItem mntmLocalFile;
    /** the main outer frame */
    private JFrame mainFrame;
    /** the search criteria table */
    private JTable searchTable;
    /** display results after a db operation */
    private JTable displayTable;
    /** option to work-offline */
    private JCheckBox workOffline;
    /** the database operation controller */
    private DataAccess dataAccess;
    /** the run mode */
    private RunMode runMode = RunMode.NONE;

    public CRLView() {
        createGUI();
    }

    /**
     * Stops all user action on the GUI and it's child components.
     */
    public void disableGUI() {
        this.mainFrame.setEnabled(false);
    }

    /**
     * Allows user action on the GUI and it's child components.
     */
    public void enableGUI() {
        this.mainFrame.setEnabled(true);
    }

    /**
     * Display a confirmation pop-up dialogue.
     * @see javax.swing.JOptionPane#showConfirmDialog(java.awt.Component,
     *      Object, String, int, int)
     */
    public int showConfirmDialog(String message, String title, int optionType,
        int messageType) {
        return JOptionPane.showConfirmDialog(mainFrame, message, title,
            optionType, messageType);
    }

    /**
     * Display an error pop-up
     * @see javax.swing.JOptionPane#showMessageDialog(java.awt.Component,
     *      Object, String, int)
     */
    public void showMessageDialog(String message, String title, int optionType,
        int messageType) {
        JOptionPane.showMessageDialog(mainFrame, message, title, optionType);
    }

    /**
     * @return The location of the main frame on the users screen.
     */
    public Point getLocation() {
        return mainFrame.getLocation();
    }

    /**
     * Sets the look and feel to the cross platform look and feel.
     * @see javax.swing.UIManager#setLookAndFeel(javax.swing.LookAndFeel)
     * @see javax.swing.UIManager#getCrossPlatformLookAndFeelClassName()
     */
    public void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager
                .getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a reference to the search criteria entry table
     */
    final JTable getSearchTable() {
        return this.searchTable;
    }

    /**
     * @return a reference to the search display results table
     */
    final JTable getDisplayTable() {
        return this.displayTable;
    }

    /**
     * @return a reference to the database operation controller
     */
    final DataAccess getDataAccess() {
        return this.dataAccess;
    }

    /**
     * @return The location of the database file in URI form.
     * @see java.net.URI#toString()
     */
    public String getDataAccessLocator() {
        if (dataAccess == null) {
            return null;
        }
        try {
            return dataAccess.getLocatorURI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the database access controller.
     * @param d the db access controller.
     */
    public final void setDataAccess(DataAccess d) {
        this.dataAccess = d;
    }

    /**
     * Renders the display GUI
     */
    public void displayGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainFrame.pack();
                mainFrame.setVisible(true);
            }
        });
    }

    /**
     * enables/disables components based on client run mode. For example, the
     * option to chose a local file is disabled when running in networked mode.
     */
    public void setClientRunMode() {
        mntmRemoteFile.setEnabled(true);
        mntmLocalFile.setEnabled(false);
        workOffline.setSelected(false);
        runMode = RunMode.CLIENT;
    }

    /**
     * enables/disables components based on stand alone run mode. For example,
     * the option to specify a remote file location is disabled when running in
     * non-networked mode.
     */
    public void setStandAloneRunMode() {
        mntmRemoteFile.setEnabled(false);
        mntmLocalFile.setEnabled(true);
        workOffline.setSelected(true);
        runMode = RunMode.ALONE;
    }

    /**
     * @return the current running mode as understood by the GUI.
     */
    public final RunMode getRunMode() {
        return runMode;
    }

    /**
     * Builds the display components but does not render them.
     */
    public void createGUI() {
        setSystemLookAndFeel();
        mainFrame = new JFrame();
        mainFrame.setBackground(new Color(176, 224, 230));
        mainFrame.getContentPane().setBackground(new Color(153, 153, 255));
        mainFrame.setTitle("Customer Record Locator");
        mainFrame.setBounds(100, 100, 1100, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
        searchTable = JTableFactory.getSearchCriteriaTable();
        displayTable = JTableFactory.getDisplayTable();
        JScrollPane displayScroll = new JScrollPane(displayTable);
        JPanel displayPanel = new JPanel();
        displayPanel.setForeground(new Color(255, 255, 255));
        displayPanel.setBackground(new Color(153, 153, 255));
        displayPanel.setBorder(new MatteBorder(0, 7, 0, 7, new Color(153, 153,
            255)));
        displayPanel.setLayout(new BorderLayout(0, 0));
        JPanel displayButtonPanel = new JPanel();
        displayButtonPanel.setForeground(new Color(255, 255, 255));
        displayButtonPanel.setBackground(new Color(153, 153, 255));
        displayButtonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        JButton aBtnClear = new JButton("Clear");
        aBtnClear
            .setToolTipText("Clear the search results from the display table.");
        aBtnClear.addActionListener(new DisplayClearButtonListener(this));
        displayButtonPanel.add(aBtnClear);
        JButton aBtnCommit = new JButton("Commit");
        aBtnCommit
            .setToolTipText("Commit all changes in the display table to the database.");
        displayButtonPanel.add(aBtnCommit);
        aBtnCommit.addActionListener(new CommitButtonListener(this));
        JPanel actionPanel = new JPanel();
        actionPanel.setForeground(new Color(255, 255, 255));
        actionPanel.setBackground(new Color(153, 153, 255));
        actionPanel.setBorder(new TitledBorder(new CompoundBorder(
            new EmptyBorder(0, 5, 10, 5), new LineBorder(new Color(255, 255,
                255), 5)), "   Enter Search Criteria / Insert New Record   ",
            TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainFrame.getContentPane().add(actionPanel, BorderLayout.NORTH);
        mainFrame.setPreferredSize(new Dimension(CRLView.VIEW_WIDTH,
            CRLView.VIEW_HEIGHT));
        actionPanel.setLayout(new BorderLayout(0, 0));
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(153, 153, 255));
        searchPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 0, 5),
            new MatteBorder(1, 1, 0, 0, new Color(102, 102, 153))));
        searchPanel.setLayout(new BorderLayout(0, 0));
        searchPanel.add(searchTable.getTableHeader(), BorderLayout.PAGE_START);
        searchPanel.add(searchTable, BorderLayout.CENTER);
        actionPanel.add(searchPanel, BorderLayout.CENTER);
        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setBackground(new Color(153, 153, 255));
        actionButtonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JButton btnSearch = new JButton("Search");
        btnSearch
            .setToolTipText("Enter search criteria in any above field and click.  Record # takes precedence.  Partial text allowed.");
        btnSearch.addActionListener(new SearchButtonListener(this));
        btnSearch.setVerticalAlignment(SwingConstants.BOTTOM);
        actionButtonPanel.add(btnSearch);
        JButton btnClear = new JButton("Clear");
        btnClear.setToolTipText("Clears all search criterial fields.");
        btnClear.addActionListener(new SearchClearButtonListener(this));
        btnClear.setVerticalAlignment(SwingConstants.BOTTOM);
        actionButtonPanel.add(btnClear);
        JButton btnSave = new JButton("Insert");
        btnSave.setToolTipText("Insert new record.  Record# field ignored.  ");
        btnSave.addActionListener(new InsertButtonListener(this));
        btnSave.setVerticalAlignment(SwingConstants.BOTTOM);
        actionButtonPanel.add(btnSave);
        actionPanel.add(actionButtonPanel, BorderLayout.SOUTH);
        displayPanel.add(displayButtonPanel, BorderLayout.SOUTH);
        mainFrame.getContentPane().add(displayPanel, BorderLayout.CENTER);
        displayScroll.setBorder(new LineBorder(new Color(0, 0, 0), 5));
        displayPanel.add(displayScroll, BorderLayout.CENTER);
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        workOffline = new JCheckBox("Work Offline");
        workOffline
            .setToolTipText("Switch between stand-alone and network client run modes.");
        workOffline.addActionListener(new WorkOfflineListener(this));
        fileMenu.add(workOffline);
        mntmRemoteFile = new JMenuItem("Select Remote File...");
        mntmRemoteFile.addActionListener(new RemoteFileListener(this));
        fileMenu.add(mntmRemoteFile);
        mntmLocalFile = new JMenuItem("Select Local File...");
        mntmLocalFile.addActionListener(new LocalFileListener(this));
        fileMenu.add(mntmLocalFile);
        switch (runMode) {
            case CLIENT:
                setClientRunMode();
                break;
            default:
                setStandAloneRunMode();
        }
        JMenuItem mntmSaveAs = new JMenuItem("Save Search Results to File...");
        mntmSaveAs.setEnabled(false);
        fileMenu.add(mntmSaveAs);
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Goodbye!");
                if ((dataAccess != null)
                    && (dataAccess instanceof DataAccessImpl)) {
                    ((DataAccessImpl) dataAccess).close();
                }
                System.exit(0);
            }
        });
        fileMenu.add(mntmExit);
        JMenu mnHelp = new JMenu("Help");
        mnHelp.setEnabled(false);
        menuBar.add(mnHelp);
        JMenuItem mntmHelpContents = new JMenuItem("Help Contents");
        mnHelp.add(mntmHelpContents);
    }
}
