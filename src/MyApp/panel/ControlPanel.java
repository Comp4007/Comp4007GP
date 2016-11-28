package MyApp.panel;

import MyApp.building.Building;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JTextArea;

public class ControlPanel implements Panel{
	//private JFrame cFrame;
	private JFrame frame;
	private JTextArea textarea;
	private JButton refresh, dismiss;
	private Building building;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public ControlPanel(Building building){
		this.building = building;
		initialize();
		start();
	}

	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	/*	cFrame = new JFrame();
		cFrame.setTitle("Control Panel");
		
		JPanel panel = new JPanel();
		cFrame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("New label");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(46)
					.addComponent(lblNewLabel)
					.addContainerGap(617, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(44)
					.addComponent(lblNewLabel)
					.addContainerGap(349, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);*/
		
		textarea= new JTextArea();
		textarea.setEditable(false);
		JScrollPane sp = new JScrollPane(textarea);
		sp.setBounds(10,10,460,590);
		refresh = new JButton();
		dismiss = new JButton();
		refresh.setText("refresh");
		dismiss.setText("dismiss");
		frame = new JFrame();
		frame.setTitle("Control Panel");
		frame.setBounds(100, 100, 500, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		refresh.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  	showInfo();
			  }
		} );
		
		dismiss.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  dismissInfo();
			  }
		} );
		frame.getContentPane().add(sp);
		//frame.add(refresh);
		//frame.add(dismiss);
		frame.setVisible(true);
		sp.setVisible(true);
		
	}
	
	private void start() {
		try{
			while(true){
				showInfo();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void showInfo() {
		/*
		System.out.println("Elevator status :");
		//System.out.println(building.getElevatorStatus());//sample
		System.out.println();
		
		System.out.println("Elevator queue :");
		//System.out.printf(building.getElevatorQueueString());//sample
		System.out.println();
		
		System.out.println("Kiosk queue: ");
		System.out.printf(building.getKioskQueueString());*/
		textarea.setText("");
		
		String info = "";
		
		info += "Elevator status:\n";
		//info += building.getElevatorStatus();
		info += "\n\n";
		
		info += "Elevator queue:\n";
		info +=building.getElevatorQueueString();
		info += "\n\n";
		
		info += "Kiosk queue:\n";
		info +=building.getKioskQueueString();
				
		textarea.append(info);

		frame.setVisible(true);
		
	}
    @Override
    public void dismissInfo() {
    	textarea.setText("");
    }
}
