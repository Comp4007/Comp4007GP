package MyApp.misc;

import MyApp.building.Building;

import java.util.HashMap;
import java.util.logging.Logger;


//======================================================================
// AppThread
public abstract class AppThread extends Thread {
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
    protected HashMap<Integer, String> queue;

    //------------------------------------------------------------
    // AppThread
    public AppThread(String id, Building building) {
        super(id);
		this.id = id;
		this.building = building;
		log = building.getLogger();
		mbox = new MBox(id, log);
		building.putThread(this);
		queue = new HashMap<Integer, String>();
    } // AppThread


    //------------------------------------------------------------
    // getters
    public MBox getMBox() { return mbox; }
    /**
     * To retrieve the identifier of such object in this respective building.
     */
    public String getID() { return id; }
    public HashMap<Integer, String> getQueue() {return queue;}
    
    public void setQueue() {

    };
}
