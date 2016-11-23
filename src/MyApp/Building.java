package MyApp;

import java.lang.Thread;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Hashtable;

import MyApp.elevator.*;
import MyApp.kiosk.*;
import MyApp.misc.*;
import MyApp.timer.*;

//======================================================================
// AppKickstarter
public class Building {
    private final String cfgFName = "etc/MyApp.cfg";
    private Logger log = null;
    private Hashtable<String, AppThread> appThreads = null;
    private Properties cfgProps = null;

    //------------------------------------------------------------
    // main
    public static void main(String args[]) {
    	Building building = new Building();
    	building.startApp();
    } // main


    //------------------------------------------------------------
    // AppKickstarter
    public Building() {
	// read system config from property file
		try {
		    cfgProps = new Properties();
		    FileInputStream in = new FileInputStream(cfgFName);
		    cfgProps.load(in);
		    in.close();
		} catch (FileNotFoundException e) {
		    System.out.println("Failed to open config file ("+cfgFName+").");
		    System.exit(-1);
		} catch (IOException e) {
		    System.out.println("Error reading config file ("+cfgFName+").");
		    System.exit(-1);
		}

		// get and configure logger
		ConsoleHandler conHd = new ConsoleHandler();
		conHd.setFormatter(new LogFormatter());
		log = Logger.getLogger(Building.class.getName());
		log.setUseParentHandlers(false);
		log.addHandler(conHd);
		log.setLevel(Level.INFO);
		appThreads = new Hashtable<String, AppThread>();
    } // AppKickstarter


    //------------------------------------------------------------
    // startApp
    public void startApp() {
		// create threads
		Timer timer = new Timer("timer", this);
		
		Kiosk.koiskCount = new Integer(this.getProperty("Kiosks"));
		for(int i = 0; i < Kiosk.koiskCount; i ++){
			Kiosk kiosk = new Kiosk("k" + i, this);
			new Thread(kiosk).start();
		}
		
		Elevator.elevatorCount = new Integer(this.getProperty("Elevators"));
		for(int i = 0; i < Elevator.elevatorCount; i ++){
			Elevator elevator = new Elevator("e" + i, this);
			new Thread(elevator).start();
		}
		// start threads
		
		
		new Thread(timer).start();
	    } // startApp
	
	
	    //------------------------------------------------------------
	    // getLogger
	    public Logger getLogger() {
		return log;
    } // getLogger


    //------------------------------------------------------------
    // regThread
    public void regThread(AppThread appThread) {
    	appThreads.put(appThread.getID(), appThread);
    } // regThread


    //------------------------------------------------------------
    
    // getThread
    public AppThread getThread(String id) {
    	return appThreads.get(id);
    } // getThread
    
    //getElevatorQueue()
    public String getElevatorQueue(){
    	String geq = "";
    
    	for(int i = 0; i < Elevator.elevatorCount; i++){
    		Hashtable<Integer, String> rq =  this.getThread("e" + i).getQueue();
    		
    		 gr += "Elevator "+i+ ": " + rq +"\n";
    	}
    	return geq;
    }


    //------------------------------------------------------------
    // getProperty
    public String getProperty(String property) {
    	return cfgProps.getProperty(property);
    } // getThread
    
    public ArrayList<ElevatorStatus> getElevatorStatus(){
    	ArrayList<ElevatorStatus> es = null;
    	for(int i = 0; i < Elevator.elevatorCount; i++){
    		es.add(((Elevator)(this.getThread("e" + 1))).getStatus());
    	}
    	
    	return es;
    }
    
    //get result for controlpanel
    public String getKioskQueue(){
    	String gkq = "";
    	
    	for(int i = 0; i < Kiosk.koiskCount; i++){
    		Hashtable<Integer, String> rq =  this.getThread("k" + i).getQueue();
    		//demo of how to get queue of kiosk, 
    		//can also use this to get queue of elevator
    		 gr += "Floor "+i+ ": " + rq +"\n";
    	}
    	
    	//ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
    	//Algorithm stuff
    	
    	return gkq;
    }
    
    //get result for kiosk
    public String getResult(int floor, String id){
    	
    	for(int i = 0; i < Kiosk.koiskCount; i++){
    		Hashtable<Integer, String> rq =  this.getThread("k" + i).getQueue();
    		//demo of how to get queue of kiosk, 
    		//can also use this to get queue of elevator
    	}
    	
    	//ElevatorStatus status = ((Elevator)(this.getThread("e" + 1))).getStatus();
    	//Algorithm stuff
    	
    	return "";//for duplicated request
    }
} // AppKickstarter
