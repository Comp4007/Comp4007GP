package MyApp.panel;

import MyApp.building.Building;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;


import javax.swing.JButton;
import javax.swing.JTextArea;

public class XControlPanel implements Panel{
	//private JFrame cFrame;
	private JFrame frame;
	private JTextArea textarea;
	private JButton refresh, dismiss;
	private Building building;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public XControlPanel(Building building){
		this.building = building;
		initialize();
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
		sp.setVisible(true);
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
}
