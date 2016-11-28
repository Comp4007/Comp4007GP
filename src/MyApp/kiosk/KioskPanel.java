package MyApp.kiosk;

import MyApp.building.Building;
import java.util.Random;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;

import MyApp.panel.Panel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;

public class KioskPanel implements Panel{
	private JFrame frmKoiskPanel;
	private JTextField display;
	private Building building;
	private String[] floorList;
	private final String dbFName = "etc/RFID_DB";
	private String displayText = "";
	private String[] RFIDlist;

	public void showInfo() {
		EventQueue.invokeLater(() -> {
            try {
            	frmKoiskPanel.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}
	
	/**
	 * Constructor of Kiosk panel 
	 * @param building
	 */
	public KioskPanel(Building building) {
		this.building = building;
		floorList = building.getFloorNames();
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKoiskPanel = new JFrame();
		frmKoiskPanel.setTitle("Koisk Panel");
		frmKoiskPanel.setBounds(100, 100, 507, 323);
		frmKoiskPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmKoiskPanel.getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{83, 156, 0, 0};
		gbl_panel.rowHeights = new int[]{23, 53, 0, 45, 69, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		//Label Floor
		JLabel lblNewLabel = new JLabel("Floor");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		System.out.println(Arrays.toString(floorList));
		//floor combobox
		JComboBox FloorCbx = new JComboBox(floorList);
		GridBagConstraints gbc_FloorCbx = new GridBagConstraints();
		gbc_FloorCbx.fill = GridBagConstraints.HORIZONTAL;
		gbc_FloorCbx.insets = new Insets(0, 0, 5, 5);
		gbc_FloorCbx.gridx = 1;
		gbc_FloorCbx.gridy = 0;
		panel.add(FloorCbx, gbc_FloorCbx);
		
		//Text field for display keypad result
		display = new JTextField();
		System.out.println(displayText);
		display.setText(displayText);
		display.setBackground(Color.WHITE);
		display.setHorizontalAlignment(SwingConstants.CENTER);
		display.setEditable(false);
		GridBagConstraints gbc_display = new GridBagConstraints();
		gbc_display.fill = GridBagConstraints.BOTH;
		gbc_display.gridwidth = 3;
		gbc_display.insets = new Insets(0, 0, 5, 0);
		gbc_display.gridx = 0;
		gbc_display.gridy = 1;
		panel.add(display, gbc_display);
		display.setColumns(1);
		
		JLabel lblKeypad = new JLabel("Keypad");
		GridBagConstraints gbc_lblKeypad = new GridBagConstraints();
		gbc_lblKeypad.gridwidth = 2;
		gbc_lblKeypad.insets = new Insets(0, 0, 5, 5);
		gbc_lblKeypad.gridx = 0;
		gbc_lblKeypad.gridy = 3;
		panel.add(lblKeypad, gbc_lblKeypad);
		
		JLabel lblRfidReader = new JLabel("RFID READER");
		GridBagConstraints gbc_lblRfidReader = new GridBagConstraints();
		gbc_lblRfidReader.insets = new Insets(0, 0, 5, 0);
		gbc_lblRfidReader.gridx = 2;
		gbc_lblRfidReader.gridy = 3;
		panel.add(lblRfidReader, gbc_lblRfidReader);

		JComboBox Keypadbox = new JComboBox(floorList);
		Keypadbox.setEditable(true);
		GridBagConstraints gbc_Keypadbox = new GridBagConstraints();
		gbc_Keypadbox.gridwidth = 2;
		gbc_Keypadbox.insets = new Insets(0, 0, 5, 5);
		gbc_Keypadbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_Keypadbox.gridx = 0;
		gbc_Keypadbox.gridy = 4;
		panel.add(Keypadbox, gbc_Keypadbox);
		
		JButton btnSummit = new JButton("Keypad Summit");
		GridBagConstraints gbc_btnSummit = new GridBagConstraints();
		gbc_btnSummit.gridwidth = 2;
		gbc_btnSummit.fill = GridBagConstraints.BOTH;
		gbc_btnSummit.insets = new Insets(0, 0, 0, 5);
		gbc_btnSummit.gridx = 0;
		gbc_btnSummit.gridy = 5;
		btnSummit.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  if(Arrays.asList(floorList).contains(Keypadbox.getSelectedItem().toString())){
					  displayText = ((Kiosk) building.getThread("k" + Arrays.asList(floorList).indexOf(FloorCbx.getSelectedItem().toString()) ) ).readKeypad(Keypadbox.getSelectedItem().toString() );
				  
					display.setText(displayText);
				  }else{
					  displayText = "Wrong Floor Input, please try again.";
					  display.setText(displayText);
				  }
			  }
		} );
		panel.add(btnSummit, gbc_btnSummit);
		
		//=================================================================
		
		//RFID reader
		JComboBox RFIDCbx = new JComboBox();
		RFIDCbx.setEditable(true);
		GridBagConstraints gbc_RFIDCbx = new GridBagConstraints();
		gbc_RFIDCbx.insets = new Insets(0, 0, 5, 0);
		gbc_RFIDCbx.fill = GridBagConstraints.HORIZONTAL;
		gbc_RFIDCbx.gridx = 2;
		gbc_RFIDCbx.gridy = 4;
		panel.add(RFIDCbx, gbc_RFIDCbx);
		
		JButton btnRfidSummit = new JButton("RFID Summit");
		GridBagConstraints gbc_btnRfidSummit = new GridBagConstraints();
		gbc_btnRfidSummit.fill = GridBagConstraints.BOTH;
		gbc_btnRfidSummit.gridx = 2;
		gbc_btnRfidSummit.gridy = 5;
		panel.add(btnRfidSummit, gbc_btnRfidSummit);
		btnRfidSummit.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  	Random ran = new Random();    
				  	displayText = String.valueOf(ran.nextInt());
				  	display.setText(displayText);
			  }
		} );
	}
	
	protected void updateDisplay(String text){
		displayText = text;
		display.setText(displayText);
		display.validate();
	}
}