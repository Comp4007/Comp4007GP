package MyApp.misc;

import java.util.Hashtable;
import java.util.logging.Logger;

import MyApp.Building;


//======================================================================
// AppThread
public abstract class AppThread implements Runnable {
    protected String id;
    protected Building building;
    protected MBox mbox = null;
    protected Logger log = null;
    protected Hashtable<Integer, String> queue;

    //------------------------------------------------------------
    // AppThread
    public AppThread(String id, Building building) {
		this.id = id;
		this.building = building;
		log = building.getLogger();
		mbox = new MBox(id, log);
		building.regThread(this);
    } // AppThread


    //------------------------------------------------------------
    // getters
    public MBox getMBox() { return mbox; }
    public String getID() { return id; }
    public Hashtable<Integer, String> getQueue() {return queue;}
    
    public void setQueue(){
    	
    }
} // AppThread
