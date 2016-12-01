package MyApp.panel;

import java.awt.EventQueue;
import java.awt.Color;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import MyApp.misc.RFID;
import javax.swing.JLabel;

public class AdminPanel implements Panel{
	/**
	 * GUI frame
	 */
	private JFrame frame;
	/**
	 * This is a table show RFID data on the frame
	 */
	private JTable table;
	/**
	 * It is store all the RFID in database
	 */
	private String[] flrList;
	/**
	 * Set the RFID database path
	 */
	private final String dbFName = "etc/RFID_DB";
	/**
	 * Set the RFID class object
	 */
	private final RFID rf = new RFID();
	/**
	 * This for user click the row data then store here
	 */
	private String idCheck;

	public AdminPanel(String[] flrList){
		this.flrList = flrList;
		initialize();
	}
	
	/**
	 * Building can use this method to open the admin panel
	 */
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

    /**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        
        // set the model to the table and add original data from RFID DB
        table.setModel(model);
        try{
        	BufferedReader in = new BufferedReader(new FileReader(dbFName));
        	String line;
        	int count = 0;
        	Object[] columns;
        	Object[] row;
        		while((line = in.readLine()) != null)
        		{
        			if(count == 0){
        				columns = line.split(",");
        				model.setColumnIdentifiers(columns);
        			}else{
        				row = line.split(",");
        				model.addRow(row);
        			}
        			count++;
        		}
        	in.close();
        }catch (Exception e){
        	System.out.println("Error :"+e);
        }
       
        // Change A JTable Background Color, Font Size, Font Color, Row Height
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.black);
		
		// create JTextFields
        JTextField textId = new JTextField();
        JTextField textFname = new JTextField();
        JTextField textLname = new JTextField();
        JTextField textAge = new JTextField();
        
        // create JButtons
        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");     
        
        // Layout 
        textId.setBounds(591, 44, 295, 25);
        textFname.setBounds(591, 102, 295, 25);
        textLname.setBounds(591, 160, 295, 25);
        textAge.setBounds(591, 217, 295, 25);
        
        btnAdd.setBounds(488, 381, 100, 25);
        btnUpdate.setBounds(587, 381, 100, 25);
        btnDelete.setBounds(687, 381, 100, 25);
        
        // create JScrollPane
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, 0, 476, 412);
        
        frame.getContentPane().setLayout(null);
        
        frame.getContentPane().add(pane);
        
        // add JTextFields to the jframe
        frame.getContentPane().add(textId);
        frame.getContentPane().add(textFname);
        frame.getContentPane().add(textLname);
        frame.getContentPane().add(textAge);
    
        // add JButtons to the jframe
        frame.getContentPane().add(btnAdd);
        frame.getContentPane().add(btnDelete);
        frame.getContentPane().add(btnUpdate);
        
        // add JLabel to the jframe
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(500, 49, 61, 16);
        frame.getContentPane().add(lblId);
        
        JLabel lblNewLabel = new JLabel("Floor:");
        lblNewLabel.setBounds(500, 107, 61, 16);
        frame.getContentPane().add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("FName:");
        lblNewLabel_1.setBounds(500, 165, 61, 16);
        frame.getContentPane().add(lblNewLabel_1);
        
        JLabel lblNewLabel_2 = new JLabel("LName:");
        lblNewLabel_2.setBounds(500, 222, 61, 16);
        frame.getContentPane().add(lblNewLabel_2);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
            	textId.setText("");
            	textFname.setText("");
            	textLname.setText("");
            	textAge.setText("");
        	}
        });
        btnCancel.setBounds(488, 346, 413, 23);
        frame.getContentPane().add(btnCancel);
        
        JLabel lblErrorDisplay = new JLabel();
        lblErrorDisplay.setBounds(498, 318, 289, 14);
        frame.getContentPane().add(lblErrorDisplay);
        
        JButton btnBackup = new JButton("Backup");
        btnBackup.setBounds(784, 379, 117, 29);
        frame.getContentPane().add(btnBackup);
        
        
        // create an array of objects to set the row data
        Object[] row = new Object[4];
        
    	// get selected row data From table to textfields 
        table.addMouseListener(new MouseAdapter(){
        
        	@Override
        	public void mouseClicked(MouseEvent e){
            
        		// i = the index of the selected row
        		int i = table.getSelectedRow();
            
            	textId.setText(model.getValueAt(i, 0).toString());
            	idCheck = textId.getText();
            	textFname.setText(model.getValueAt(i, 1).toString());
            	textLname.setText(model.getValueAt(i, 2).toString());
            	textAge.setText(model.getValueAt(i, 3).toString());
        	}
        });
        
        // button add row
        btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             
                row[0] = textId.getText();
                row[1] = textFname.getText();
                row[2] = textLname.getText();
                row[3] = textAge.getText();
                
                //Ensure floor name is valid
                if(Arrays.asList(flrList).contains(row[1])){
	                // add row to the model
	                model.addRow(row);
	                String line = row[0]+","+row[1]+","+row[2]+","+row[3];
	                rf.insertData(line);
	                lblErrorDisplay.setText("Insert data sucessfully");
                }else{
                	lblErrorDisplay.setText("Wrong floor input, please try again.");
                }
            }
        });
        
     // button update row
        btnUpdate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             if(idCheck.equals(textId.getText())){
                // i = the index of the selected row
                int i = table.getSelectedRow();
                String line = "";
                if(i >= 0 & Arrays.asList(flrList).contains(textFname.getText())) 
                {
                   model.setValueAt(textId.getText(), i, 0);
                   model.setValueAt(textFname.getText(), i, 1);
                   model.setValueAt(textLname.getText(), i, 2);
                   model.setValueAt(textAge.getText(), i, 3);
                   line = textId.getText()+","+textFname.getText()+","+textLname.getText()+","+textAge.getText();
                   rf.updateData(textId.getText(), line);  
                   lblErrorDisplay.setText("Update data sucessfully");
                }
                else if(!Arrays.asList(flrList).contains(textFname.getText())){
                	lblErrorDisplay.setText("Wrong floor input, please try again.");
                }else{
                    System.out.println("Update Error");
                    lblErrorDisplay.setText("Update Error");
                }
             }else{
            	 lblErrorDisplay.setText("Cannot update id because it is unique");
             }
            }
        });
        
        // button remove row
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            
                // i = the index of the selected row
                int i = table.getSelectedRow();
                String line = "";
                if(i >= 0){
                    // remove a row from jtable
                    model.removeRow(i);
                    line = textId.getText()+","+textFname.getText()+","+textLname.getText()+","+textAge.getText();
                    rf.deleteData(line);
                    lblErrorDisplay.setText("Delete Data Successfully");
                }else{
                    lblErrorDisplay.setText("Delete Error");
                }            
            }
        });
        
        btnBackup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            	try{
            		rf.backUp();
            		lblErrorDisplay.setText("Backup sucessfully");
            	}catch(Exception ex){
            		lblErrorDisplay.setText("Cannot back up the data from database");
            	}
            
            }
        });
        
        //jframe property
        frame.setTitle("RFID Admin Panel");
        frame.setSize(913,452);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

