package MyApp.kiosk;

import MyApp.building.Building;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;

import MyApp.building.Floor;
import MyApp.panel.Panel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class KioskPanel implements Panel {
    private String keypadText = "";
    private String infoText = "";
    private JFrame frame;
    private JTextField keypadDispaly;
    private JTextField infoDisplay;
    private Building building;
    private String[] floorList;

    @Override
    public void showInfo() {
        EventQueue.invokeLater(() -> {
            try {
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void dismissInfo() {
        EventQueue.invokeLater(() -> {
            try {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public KioskPanel(Building building) {
        this.building = building;
        floorList = building.getFloorNames();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 507, 323);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{83, 83, 91, 0, 0};
        gbl_panel.rowHeights = new int[]{23, 39, 19, 0, 0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        //Label Floor
        JLabel lblNewLabel = new JLabel("Floor");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        panel.add(lblNewLabel, gbc_lblNewLabel);

//		System.out.println(Arrays.toString(floorList));
        //floor combobox
        JComboBox FloorCbx = new JComboBox(floorList);
        GridBagConstraints gbc_FloorCbx = new GridBagConstraints();
        gbc_FloorCbx.fill = GridBagConstraints.HORIZONTAL;
        gbc_FloorCbx.gridwidth = 2;
        gbc_FloorCbx.insets = new Insets(0, 0, 5, 5);
        gbc_FloorCbx.gridx = 1;
        gbc_FloorCbx.gridy = 0;
        panel.add(FloorCbx, gbc_FloorCbx);

        //Text field for display keypad result
        keypadDispaly = new JTextField();
        keypadDispaly.setHorizontalAlignment(SwingConstants.CENTER);
        keypadDispaly.setEditable(false);
        GridBagConstraints gbc_keypadDispaly = new GridBagConstraints();
        gbc_keypadDispaly.fill = GridBagConstraints.BOTH;
        gbc_keypadDispaly.gridwidth = 4;
        gbc_keypadDispaly.insets = new Insets(0, 0, 5, 0);
        gbc_keypadDispaly.gridx = 0;
        gbc_keypadDispaly.gridy = 1;
        panel.add(keypadDispaly, gbc_keypadDispaly);
        keypadDispaly.setColumns(1);

        //Text field for display Update form building
        infoDisplay = new JTextField();
        infoDisplay.setEditable(false);
        GridBagConstraints gbc_infoDisplay = new GridBagConstraints();
        gbc_infoDisplay.fill = GridBagConstraints.BOTH;
        gbc_infoDisplay.gridwidth = 4;
        gbc_infoDisplay.insets = new Insets(0, 0, 5, 0);
        gbc_infoDisplay.gridx = 0;
        gbc_infoDisplay.gridy = 2;
        panel.add(infoDisplay, gbc_infoDisplay);
        infoDisplay.setColumns(1);

        //Keypad button
        //========================================================
        JButton btn1 = new JButton("1");
        GridBagConstraints gbc_btn1 = new GridBagConstraints();
        gbc_btn1.fill = GridBagConstraints.BOTH;
        gbc_btn1.insets = new Insets(0, 0, 5, 5);
        gbc_btn1.gridx = 0;
        gbc_btn1.gridy = 5;
        btn1.addActionListener(e -> {
            keypadText += 1;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn1, gbc_btn1);

        JButton btn2 = new JButton("2");
        GridBagConstraints gbc_btn2 = new GridBagConstraints();
        gbc_btn2.fill = GridBagConstraints.BOTH;
        gbc_btn2.insets = new Insets(0, 0, 5, 5);
        gbc_btn2.gridx = 1;
        gbc_btn2.gridy = 5;
        btn2.addActionListener(e -> {
            keypadText += 2;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn2, gbc_btn2);

        JButton btn3 = new JButton("3");
        GridBagConstraints gbc_btn3 = new GridBagConstraints();
        gbc_btn3.fill = GridBagConstraints.BOTH;
        gbc_btn3.insets = new Insets(0, 0, 5, 5);
        gbc_btn3.gridx = 2;
        gbc_btn3.gridy = 5;
        btn3.addActionListener(e -> {
            keypadText += 3;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn3, gbc_btn3);

        JButton btn4 = new JButton("4");
        GridBagConstraints gbc_btn4 = new GridBagConstraints();
        gbc_btn4.fill = GridBagConstraints.BOTH;
        gbc_btn4.insets = new Insets(0, 0, 5, 5);
        gbc_btn4.gridx = 0;
        gbc_btn4.gridy = 4;
        btn4.addActionListener(e -> {
            keypadText += 4;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn4, gbc_btn4);

        JButton btn5 = new JButton("5");
        GridBagConstraints gbc_btn5 = new GridBagConstraints();
        gbc_btn5.fill = GridBagConstraints.BOTH;
        gbc_btn5.insets = new Insets(0, 0, 5, 5);
        gbc_btn5.gridx = 1;
        gbc_btn5.gridy = 4;
        btn5.addActionListener(e -> {
            keypadText += 5;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn5, gbc_btn5);

        JButton btn6 = new JButton("6");
        GridBagConstraints gbc_btn6 = new GridBagConstraints();
        gbc_btn6.fill = GridBagConstraints.BOTH;
        gbc_btn6.insets = new Insets(0, 0, 5, 5);
        gbc_btn6.gridx = 2;
        gbc_btn6.gridy = 4;
        btn6.addActionListener(e -> {
            keypadText += 6;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn6, gbc_btn6);

        JButton btn7 = new JButton("7");
        GridBagConstraints gbc_btn7 = new GridBagConstraints();
        gbc_btn7.fill = GridBagConstraints.BOTH;
        gbc_btn7.insets = new Insets(0, 0, 5, 5);
        gbc_btn7.gridx = 0;
        gbc_btn7.gridy = 3;
        btn7.addActionListener(e -> {
            keypadText += 7;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn7, gbc_btn7);


        JButton btn8 = new JButton("8");
        GridBagConstraints gbc_btn8 = new GridBagConstraints();
        gbc_btn8.fill = GridBagConstraints.BOTH;
        gbc_btn8.insets = new Insets(0, 0, 5, 5);
        gbc_btn8.gridx = 1;
        gbc_btn8.gridy = 3;
        btn8.addActionListener(e -> {
            keypadText += 8;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn8, gbc_btn8);

        JButton btn9 = new JButton("9");
        GridBagConstraints gbc_btn9 = new GridBagConstraints();
        gbc_btn9.fill = GridBagConstraints.BOTH;
        gbc_btn9.insets = new Insets(0, 0, 5, 5);
        gbc_btn9.gridx = 2;
        gbc_btn9.gridy = 3;
        btn9.addActionListener(e -> {
            keypadText += 9;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn9, gbc_btn9);

        JLabel lblRfidReader = new JLabel("RFID READER");
        GridBagConstraints gbc_lblRfidReader = new GridBagConstraints();
        gbc_lblRfidReader.insets = new Insets(0, 0, 5, 0);
        gbc_lblRfidReader.gridx = 3;
        gbc_lblRfidReader.gridy = 3;
        panel.add(lblRfidReader, gbc_lblRfidReader);

        JButton btnPF = new JButton("PF");
        GridBagConstraints gbc_btnPF = new GridBagConstraints();
        gbc_btnPF.fill = GridBagConstraints.BOTH;
        gbc_btnPF.insets = new Insets(0, 0, 5, 5);
        gbc_btnPF.gridx = 0;
        gbc_btnPF.gridy = 6;
        btnPF.addActionListener(e -> {
            keypadText = "PF";
            keypadDispaly.setText(keypadText);
        });
        panel.add(btnPF, gbc_btnPF);

        JButton btn0 = new JButton("0");
        GridBagConstraints gbc_btn0 = new GridBagConstraints();
        gbc_btn0.fill = GridBagConstraints.BOTH;
        gbc_btn0.insets = new Insets(0, 0, 5, 5);
        gbc_btn0.gridx = 1;
        gbc_btn0.gridy = 6;
        btn0.addActionListener(e -> {
            keypadText += 0;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btn0, gbc_btn0);

        JButton btnRF = new JButton("RF");
        GridBagConstraints gbc_btnRF = new GridBagConstraints();
        gbc_btnRF.fill = GridBagConstraints.BOTH;
        gbc_btnRF.insets = new Insets(0, 0, 5, 5);
        gbc_btnRF.gridx = 2;
        gbc_btnRF.gridy = 6;
        btnRF.addActionListener(e -> {
            keypadText += 0;
            keypadDispaly.setText(keypadText);
        });
        panel.add(btnRF, gbc_btnRF);

        JButton btnCC = new JButton("Clear");
        GridBagConstraints gbc_btnCC = new GridBagConstraints();
        gbc_btnCC.fill = GridBagConstraints.BOTH;
        gbc_btnCC.insets = new Insets(0, 0, 5, 5);
        gbc_btnCC.gridx = 0;
        gbc_btnCC.gridy = 7;
        btnCC.addActionListener(e -> {
            keypadText = "";
            keypadDispaly.setText(keypadText);
        });
        panel.add(btnCC, gbc_btnCC);

        JButton btnB = new JButton("B");
        GridBagConstraints gbc_btnB = new GridBagConstraints();
        gbc_btnB.fill = GridBagConstraints.BOTH;
        gbc_btnB.insets = new Insets(0, 0, 5, 5);
        gbc_btnB.gridx = 1;
        gbc_btnB.gridy = 7;
        btnB.addActionListener(e -> {
            keypadText = "B";
            keypadDispaly.setText(keypadText);
        });
        panel.add(btnB, gbc_btnB);

        JButton btnSubmit = new JButton("Submit");
        GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
        gbc_btnSubmit.fill = GridBagConstraints.BOTH;
        gbc_btnSubmit.insets = new Insets(0, 0, 5, 5);
        gbc_btnSubmit.gridx = 2;
        gbc_btnSubmit.gridy = 7;
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String floorAliasKioskUsing = (String) FloorCbx.getSelectedItem();
                Floor floor = building.getFloorPosition(floorAliasKioskUsing);
                String dest = keypadDispaly.getText();
                submitFloor(floor, dest);
            }

            private void submitFloor(Floor floor, String dest) {
                building.getLogger().log(Level.INFO, "clicked submitFloor");
                Kiosk kiosk = building.getKioskByFloor(floor);
                kiosk.readKeypad(dest);
            }
        });
        panel.add(btnSubmit, gbc_btnSubmit);
        //=================================================================

        //RFID reader
        JComboBox RFIDCbx = new JComboBox();
        GridBagConstraints gbc_RFIDCbx = new GridBagConstraints();
        gbc_RFIDCbx.insets = new Insets(0, 0, 5, 0);
        gbc_RFIDCbx.fill = GridBagConstraints.HORIZONTAL;
        gbc_RFIDCbx.gridx = 3;
        gbc_RFIDCbx.gridy = 4;
        panel.add(RFIDCbx, gbc_RFIDCbx);


    }

}