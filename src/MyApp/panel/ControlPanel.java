package MyApp.panel;

import MyApp.building.Building;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;

public class ControlPanel implements Panel{
	private JFrame cFrame;
	
	private Building building;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public ControlPanel(Building building){
		this.building = building;
		initialize();
	}

	public void showInfo() {
		// TODO Auto-generated method stub
		System.out.println("Elevator status :");
		//System.out.println(building.getElevatorStatus());//sample
		System.out.println();
		
		System.out.println("Elevator queue :");
		//System.out.printf(building.getElevatorQueueString());//sample
		System.out.println();
		
		System.out.println("Kiosk queue: ");
		System.out.printf(building.getKioskQueueString());
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		cFrame = new JFrame();
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
		panel.setLayout(gl_panel);
	}
	
    @Override
    public void dismissInfo() {

    }
}
