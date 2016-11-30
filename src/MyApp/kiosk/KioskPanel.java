package MyApp.kiosk;

import MyApp.building.Building;
import MyApp.misc.RFID;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Color;

public class KioskPanel implements Panel{
	private JFrame frmKoiskPanel;
	private JTextField display;
	private Building building;
	private String[] floorList;
	private String[] displayText;
	private String[] RFIDlist;
	private Kiosk kiosk;
	private int kioskNum;
	private RFID rfid;
	@Override
	public void showInfo() {
		EventQueue.invokeLater(() -> {
            try {
            	frmKoiskPanel.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}
	

	public void dismissInfo() {
		EventQueue.invokeLater(() -> {
			try {
				frmKoiskPanel.dispatchEvent(new WindowEvent(frmKoiskPanel, WindowEvent.WINDOW_CLOSING));
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
		displayText = new String[floorList.length];
		rfid = new RFID();
		RFIDlist = rfid.getAllTheId().parallelStream().toArray(String[]::new);
		kiosk = (Kiosk) building.getThread("k0");
		kioskNum = 0;
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
		frmKoiskPanel.getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{83, 156, 0, 0};
		gbl_panel.rowHeights = new int[]{23, 53, 0, 35, 78, 0, 39, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		//Label Floor
		JLabel lblNewLabel = new JLabel("Floor");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
	
		//floor combobox
		JComboBox FloorCbx = new JComboBox(floorList);
		GridBagConstraints gbc_FloorCbx = new GridBagConstraints();
		gbc_FloorCbx.fill = GridBagConstraints.HORIZONTAL;
		gbc_FloorCbx.insets = new Insets(0, 0, 5, 5);
		gbc_FloorCbx.gridx = 1;
		gbc_FloorCbx.gridy = 0;
		FloorCbx.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	kioskNum = Arrays.asList(floorList).indexOf(FloorCbx.getSelectedItem().toString());
		    	kiosk = (Kiosk) building.getThread("k" + kioskNum);
		    }
		});
		panel.add(FloorCbx, gbc_FloorCbx);
		
		//Text field for display keypad result
		display = new JTextField();
		display.setText(displayText[kioskNum]);
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
		ActionListener displayListener = new ActionListener() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	display.setText(displayText[kioskNum]);
	        }
	    };
	    Timer timer = new Timer(1000, displayListener);
	    timer.start();
		
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
		//=================================================================
		
		//RFID reader
		JComboBox <String> RFIDCbx = new JComboBox<String>(RFIDlist);
		RFIDCbx.setEditable(true);
		GridBagConstraints gbc_RFIDCbx = new GridBagConstraints();
		gbc_RFIDCbx.insets = new Insets(0, 0, 5, 0);
		gbc_RFIDCbx.fill = GridBagConstraints.HORIZONTAL;
		gbc_RFIDCbx.gridx = 2;
		gbc_RFIDCbx.gridy = 4;
		panel.add(RFIDCbx, gbc_RFIDCbx);
		//update rfid list for ever minutes
		ActionListener rfidUpdateListener = new ActionListener() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	RFIDlist = rfid.getAllTheId().parallelStream().toArray(String[]::new);
	        	DefaultComboBoxModel model = new DefaultComboBoxModel( RFIDlist );
	        	RFIDCbx.setModel( model );
	        }
	    };
	    Timer timer2 = new Timer(60000, rfidUpdateListener);
	    timer2.start();
	    
		JButton btnSummit = new JButton("Keypad Summit");
		GridBagConstraints gbc_btnSummit = new GridBagConstraints();
		gbc_btnSummit.gridwidth = 2;
		gbc_btnSummit.fill = GridBagConstraints.BOTH;
		gbc_btnSummit.insets = new Insets(0, 0, 5, 5);
		gbc_btnSummit.gridx = 0;
		gbc_btnSummit.gridy = 5;
		btnSummit.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  if(Arrays.asList(floorList).contains(Keypadbox.getSelectedItem().toString())){
				      building.getLogger().log(Level.INFO, FloorCbx.getSelectedItem().toString() + "'s koisk clicked submitFloor.");
					  displayText[kioskNum] = kiosk.readKeypad(Keypadbox.getSelectedItem().toString() );
				  }else{
					  displayText[kioskNum] = "Wrong Floor Input, please try again.";
				  }
			  }
		} );
		panel.add(btnSummit, gbc_btnSummit);
		
		JButton btnRfidSummit = new JButton("RFID Summit");
		GridBagConstraints gbc_btnRfidSummit = new GridBagConstraints();
		gbc_btnRfidSummit.insets = new Insets(0, 0, 5, 0);
		gbc_btnRfidSummit.fill = GridBagConstraints.BOTH;
		gbc_btnRfidSummit.gridx = 2;
		gbc_btnRfidSummit.gridy = 5;
		panel.add(btnRfidSummit, gbc_btnRfidSummit);
		btnRfidSummit.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  building.getLogger().log(Level.INFO, FloorCbx.getSelectedItem().toString() + "'s koisk pass RFID id " + RFIDCbx.getSelectedItem().toString());
				  displayText[kioskNum] = kiosk.readRFID(RFIDCbx.getSelectedItem().toString());
			  }
		} );
		
		JButton btnEE = new JButton("Enter elevator");
		GridBagConstraints gbc_btnEE = new GridBagConstraints();
		gbc_btnEE.fill = GridBagConstraints.BOTH;
		gbc_btnEE.gridwidth = 3;
		gbc_btnEE.insets = new Insets(0, 0, 0, 5);
		gbc_btnEE.gridx = 0;
		gbc_btnEE.gridy = 6;
		btnEE.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  kiosk.elevatorIn();
			  }
		} );
		panel.add(btnEE, gbc_btnEE);
	}
	
	protected void updateDisplay(String text,int id){
		displayText[id] = text;
	}
}