package MyApp.misc;

import java.util.Hashtable;
import java.util.logging.Logger;

import MyApp.Building;


//======================================================================
// AppThread
public abstract class AppThread implements Runnable {
	/**
	 * Represents that the identifier for such object in the building elevator and kiosk system.
	 */
    protected String id;
    /**
     * Reference to the parent building for such object holds in.
     */
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
    /**
     * To retrieve the identifier of such object in this respective building.
     */
    public String getID() { return id; }
    public Hashtable<Integer, String> getQueue() {return queue;}
    
    public void setQueue(){
    	
    }
} // AppThread
