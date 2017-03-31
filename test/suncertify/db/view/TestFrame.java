package suncertify.db.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class TestFrame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -6402693055646556672L;
    private JButton testButton;
    private JTextField testField;
    private JTextArea testArea;

    public TestFrame() {
        init();
    }

    private void init() {
        testButton = new JButton("Get Record Number:");
        testArea = new JTextArea();
        Dimension textDims = new Dimension(300, 75);
        testArea.setPreferredSize(textDims);
        testField = new JTextField(10);
        Dimension d = testButton.getPreferredSize();
        testField.setPreferredSize(new Dimension(25, d.height));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("CSR");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup().addGroup(
            layout.createSequentialGroup().addComponent(testButton)
                .addComponent(testField, GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(testArea));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
            layout.createParallelGroup().addComponent(testButton).addComponent(
                testField, GroupLayout.PREFERRED_SIZE,
                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(testArea));
        pack();
    }

    private void testButtonActionPerformed(ActionEvent evt) {
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestFrame().setVisible(true);
            }
        });
    }
}
