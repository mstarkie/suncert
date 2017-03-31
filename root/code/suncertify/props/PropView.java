/*
 * PropView.java Sun Certified Developer for the Java 2 Platform Submission.
 * 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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

import suncertify.props.PropTableFactory.PropertiesTable;
import suncertify.props.PropTableFactory.PropertyInsertTable;

/**
 * The user view and graphical components.
 * @author Starkie, Michael C.
 * @since Nov 14, 2010:5:41:12 PM
 */
public final class PropView {
    /** the width of the main frame */
    public static final int VIEW_WIDTH = 800;
    /** the height of the main frame */
    public static final int VIEW_HEIGHT = 450;
    /** the main outer frame */
    private JFrame mainFrame;
    /** display results after loading properties */
    private PropertiesTable propsTable;
    private PropertyInsertTable propInsertTable;

    public PropView() {
        createGUI();
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
    final JTable getPropsTable() {
        return this.propsTable;
    }

    /**
     * @return a reference to the search criteria entry table
     */
    final JTable getPropInsertTable() {
        return this.propInsertTable;
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
     * Builds the display components but does not render them.
     */
    public void createGUI() {
        setSystemLookAndFeel();
        mainFrame = new JFrame();
        mainFrame.setBackground(new Color(176, 224, 230));
        mainFrame.getContentPane().setBackground(new Color(153, 153, 255));
        mainFrame.setTitle("System Properties Editor");
        mainFrame.setBounds(100, 100, 1100, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
        propInsertTable = PropTableFactory.getPropertyInsertTable();
        propsTable = PropTableFactory.getPropertyDisplayTable();
        JScrollPane displayScroll = new JScrollPane(propsTable);
        JPanel displayPanel = new JPanel();
        displayPanel.add(displayScroll, BorderLayout.CENTER);
        displayPanel.setForeground(new Color(255, 255, 255));
        displayPanel.setBackground(new Color(153, 153, 255));
        displayPanel.setBorder(new MatteBorder(0, 2, 0, 2, new Color(153, 153,
            255)));
        displayPanel.setLayout(new BorderLayout(0, 0));
        JPanel displayButtonPanel = new JPanel();
        displayButtonPanel.setForeground(new Color(255, 255, 255));
        displayButtonPanel.setBackground(new Color(153, 153, 255));
        displayButtonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        JButton aBtnClear = new JButton("Clear");
        aBtnClear
            .setToolTipText("Clear the properties from the display table.");
        aBtnClear.addActionListener(new PropertiesClearButtonListener(this));
        JButton aBtnLoad = new JButton("Load");
        aBtnLoad.setToolTipText("Load the properties into the display table.");
        aBtnLoad.addActionListener(new PropertiesLoadButtonListener(this));
        displayButtonPanel.add(aBtnLoad);
        displayButtonPanel.add(aBtnClear);
        JButton aBtnCommit = new JButton("Commit");
        aBtnCommit
            .setToolTipText("Save all changes in the display to the properties file.");
        aBtnCommit.addActionListener(new PropertiesCommitButtonListener(this));
        displayButtonPanel.add(aBtnCommit);
        JPanel actionPanel = new JPanel();
        actionPanel.setForeground(new Color(255, 255, 255));
        actionPanel.setBackground(new Color(153, 153, 255));
        actionPanel.setBorder(new TitledBorder(new CompoundBorder(
            new EmptyBorder(0, 5, 10, 5), new LineBorder(new Color(255, 255,
                255), 5)), "   Enter New Property   ", TitledBorder.CENTER,
            TitledBorder.TOP, null, new Color(0, 0, 0)));
        actionPanel.setLayout(new BorderLayout(0, 0));
        mainFrame.getContentPane().add(actionPanel, BorderLayout.NORTH);
        // //
        actionPanel.setLayout(new BorderLayout(0, 0));
        JPanel insertPanel = new JPanel();
        insertPanel.setBackground(new Color(153, 153, 255));
        insertPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),
            new MatteBorder(1, 1, 0, 0, new Color(102, 102, 153))));
        insertPanel.setLayout(new BorderLayout(0, 0));
        insertPanel.add(propInsertTable.getTableHeader(),
            BorderLayout.PAGE_START);
        insertPanel.add(propInsertTable, BorderLayout.CENTER);
        actionPanel.add(insertPanel, BorderLayout.CENTER);
        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setBackground(new Color(153, 153, 255));
        actionButtonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JButton btnClear = new JButton("Clear");
        btnClear.setToolTipText("Clears all new properties");
        btnClear.addActionListener(new ClearInsertPropertyButtonListener(this));
        btnClear.setVerticalAlignment(SwingConstants.BOTTOM);
        actionButtonPanel.add(btnClear);
        JButton btnSave = new JButton("Insert");
        btnSave.setToolTipText("Insert new property");
        btnSave.addActionListener(new InsertPropertyButtonListener(this));
        btnSave.setVerticalAlignment(SwingConstants.BOTTOM);
        actionButtonPanel.add(btnSave);
        actionPanel.add(actionButtonPanel, BorderLayout.SOUTH);
        mainFrame.setPreferredSize(new Dimension(PropView.VIEW_WIDTH,
            PropView.VIEW_HEIGHT));
        displayPanel.add(displayButtonPanel, BorderLayout.SOUTH);
        mainFrame.getContentPane().add(displayPanel, BorderLayout.CENTER);
        displayScroll.setBorder(new CompoundBorder(new MatteBorder(0, 5, 0, 5,
            new Color(153, 153, 255)), new LineBorder(new Color(0, 0, 0), 5)));
        displayPanel.add(displayScroll, BorderLayout.CENTER);
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem_3 = new JMenuItem("Exit");
        menuItem_3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        });
        menu.add(menuItem_3);
        JMenu mnHelp = new JMenu("Help");
        mnHelp.setEnabled(false);
        menuBar.add(mnHelp);
        JMenuItem mntmHelpContents = new JMenuItem("Help Contents");
        mnHelp.add(mntmHelpContents);
    }
}
