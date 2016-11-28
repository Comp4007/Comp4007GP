package MyApp.panel;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window.Type;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;

public class AdminPanel implements Panel{
	private JFrame frame;
	private JTable table;
	private final String dbFName = "etc/RFID_DB";

	public AdminPanel(){
		initialize();
	}
	
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
        textId.setBounds(516, 44, 100, 25);
        textFname.setBounds(516, 102, 100, 25);
        textLname.setBounds(516, 160, 100, 25);
        textAge.setBounds(516, 234, 100, 25);
        
        btnAdd.setBounds(349, 381, 100, 25);
        btnUpdate.setBounds(449, 381, 100, 25);
        btnDelete.setBounds(548, 381, 100, 25);
        
        // create JScrollPane
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, 0, 348, 412);
        
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
        lblId.setBounds(383, 49, 61, 16);
        frame.getContentPane().add(lblId);
        
        JLabel lblNewLabel = new JLabel("Floor:");
        lblNewLabel.setBounds(383, 107, 61, 16);
        frame.getContentPane().add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("FName:");
        lblNewLabel_1.setBounds(383, 165, 61, 16);
        frame.getContentPane().add(lblNewLabel_1);
        
        JLabel lblNewLabel_2 = new JLabel("LName:");
        lblNewLabel_2.setBounds(383, 239, 61, 16);
        frame.getContentPane().add(lblNewLabel_2);
        
        // create an array of objects to set the row data
        Object[] row = new Object[4];
        
    	// get selected row data From table to textfields 
        table.addMouseListener(new MouseAdapter(){
        
        	@Override
        	public void mouseClicked(MouseEvent e){
            
        		// i = the index of the selected row
        		int i = table.getSelectedRow();
            
            	textId.setText(model.getValueAt(i, 0).toString());
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
                
                // add row to the model
                model.addRow(row);
            }
        });
        
     // button update row
        btnUpdate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             
                // i = the index of the selected row
                int i = table.getSelectedRow();
                
                if(i >= 0) 
                {
                   model.setValueAt(textId.getText(), i, 0);
                   model.setValueAt(textFname.getText(), i, 1);
                   model.setValueAt(textLname.getText(), i, 2);
                   model.setValueAt(textAge.getText(), i, 3);
                }
                else{
                    System.out.println("Update Error");
                }
            }
        });
        
        // button remove row
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            
                // i = the index of the selected row
                int i = table.getSelectedRow();
                if(i >= 0){
                    // remove a row from jtable
                    model.removeRow(i);
                }
                else{
                    System.out.println("Delete Error");
                }
            }
        });
        
        //jframe property
        frame.setTitle("RFID Admin Panel");
        frame.setSize(667,448);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

