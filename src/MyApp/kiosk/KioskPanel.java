package MyApp.kiosk;

import MyApp.Building;
import MyApp.Floor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

import MyApp.panel.Panel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class KioskPanel implements Panel{
	private String keypadText = "";
	private String infoText = "";	
	private JFrame frame;
	private JTextField keypadDispaly;
	private JTextField infoDisplay;
	private Building building;
	private String[] floorList;

	public void showInfo() {
		EventQueue.invokeLater(() -> {
            try {
                frame.setVisible(true);
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
		gbl_panel.rowHeights = new int[]{23, 39, 19, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
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
		btn1.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 1;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn1, gbc_btn1);
		
		JButton btn2 = new JButton("2");
		GridBagConstraints gbc_btn2 = new GridBagConstraints();
		gbc_btn2.fill = GridBagConstraints.BOTH;
		gbc_btn2.insets = new Insets(0, 0, 5, 5);
		gbc_btn2.gridx = 1;
		gbc_btn2.gridy = 5;
		btn2.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 2;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn2, gbc_btn2);
		
		JButton btn3 = new JButton("3");
		GridBagConstraints gbc_btn3 = new GridBagConstraints();
		gbc_btn3.fill = GridBagConstraints.BOTH;
		gbc_btn3.insets = new Insets(0, 0, 5, 5);
		gbc_btn3.gridx = 2;
		gbc_btn3.gridy = 5;
		btn3.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 3;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn3, gbc_btn3);
		
		JButton btn4 = new JButton("4");
		GridBagConstraints gbc_btn4 = new GridBagConstraints();
		gbc_btn4.fill = GridBagConstraints.BOTH;
		gbc_btn4.insets = new Insets(0, 0, 5, 5);
		gbc_btn4.gridx = 0;
		gbc_btn4.gridy = 4;
		btn4.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 4;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn4, gbc_btn4);
		
		JButton btn5 = new JButton("5");
		GridBagConstraints gbc_btn5 = new GridBagConstraints();
		gbc_btn5.fill = GridBagConstraints.BOTH;
		gbc_btn5.insets = new Insets(0, 0, 5, 5);
		gbc_btn5.gridx = 1;
		gbc_btn5.gridy = 4;
		btn5.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 5;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn5, gbc_btn5);
		
		JButton btn6 = new JButton("6");
		GridBagConstraints gbc_btn6 = new GridBagConstraints();
		gbc_btn6.fill = GridBagConstraints.BOTH;
		gbc_btn6.insets = new Insets(0, 0, 5, 5);
		gbc_btn6.gridx = 2;
		gbc_btn6.gridy = 4;
		btn6.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 6;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn6, gbc_btn6);
		
		JButton btn7 = new JButton("7");
		GridBagConstraints gbc_btn7 = new GridBagConstraints();
		gbc_btn7.fill = GridBagConstraints.BOTH;
		gbc_btn7.insets = new Insets(0, 0, 5, 5);
		gbc_btn7.gridx = 0;
		gbc_btn7.gridy = 3;
		btn7.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) { 
			  keypadText += 7;
			  keypadDispaly.setText(keypadText);
			  } 
			} );
		panel.add(btn7, gbc_btn7);

		
		JButton btn8 = new JButton("8");
		GridBagConstraints gbc_btn8 = new GridBagConstraints();
		gbc_btn8.fill = GridBagConstraints.BOTH;
		gbc_btn8.insets = new Insets(0, 0, 5, 5);
		gbc_btn8.gridx = 1;
		gbc_btn8.gridy = 3;
		btn8.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 8;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn8, gbc_btn8);
		
		JButton btn9 = new JButton("9");
		GridBagConstraints gbc_btn9 = new GridBagConstraints();
		gbc_btn9.fill = GridBagConstraints.BOTH;
		gbc_btn9.insets = new Insets(0, 0, 5, 5);
		gbc_btn9.gridx = 2;
		gbc_btn9.gridy = 3;
		btn9.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText += 9;
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btn9, gbc_btn9);
		
		JLabel lblRfidReader = new JLabel("RFID READER");
		GridBagConstraints gbc_lblRfidReader = new GridBagConstraints();
		gbc_lblRfidReader.insets = new Insets(0, 0, 5, 0);
		gbc_lblRfidReader.gridx = 3;
		gbc_lblRfidReader.gridy = 3;
		panel.add(lblRfidReader, gbc_lblRfidReader);
		
		JButton btnCC = new JButton("C");
		GridBagConstraints gbc_btnCC = new GridBagConstraints();
		gbc_btnCC.fill = GridBagConstraints.BOTH;
		gbc_btnCC.insets = new Insets(0, 0, 0, 5);
		gbc_btnCC.gridx = 0;
		gbc_btnCC.gridy = 6;
		btnCC.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  keypadText = "";
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btnCC,gbc_btnCC);
		
		JButton btnDelete = new JButton("Del");
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.fill = GridBagConstraints.BOTH;
		gbc_btnDelete.insets = new Insets(0, 0, 0, 5);
		gbc_btnDelete.gridx = 1;
		gbc_btnDelete.gridy = 6;
		btnDelete.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				    if (keypadText != null && keypadText.length() > 0 ) {
				    	keypadText = keypadText.substring(0, keypadText.length()-1);
				      }
				  keypadDispaly.setText(keypadText);
				  } 
				} );
		panel.add(btnDelete, gbc_btnDelete);

		JButton btnSummit = new JButton("Summit");
		GridBagConstraints gbc_btnSummit = new GridBagConstraints();
		gbc_btnSummit.fill = GridBagConstraints.BOTH;
		gbc_btnSummit.insets = new Insets(0, 0, 0, 5);
		gbc_btnSummit.gridx = 2;
		gbc_btnSummit.gridy = 6;
		btnDelete.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  summitFloor();
			  }

			private void summitFloor() {
				// TODO Auto-generated method stub
				((Kiosk) building.getThread("e1")).readKeypad(FloorCbx.getSelectedItem().toString());
			}
		} );
		panel.add(btnSummit, gbc_btnSummit);
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