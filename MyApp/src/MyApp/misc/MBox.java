package MyApp.misc;

import java.util.logging.Logger;
import java.util.ArrayList;


//======================================================================
// MBox
public class MBox {
    private String id;
    private Logger log;
    private ArrayList<Msg> mqueue = new ArrayList<Msg>();
    private int msgCnt = 0;


    //------------------------------------------------------------
    // MBox
    public MBox(String id, Logger log) {
	this.id = id;
	this.log = log;
    } // MBox


    //------------------------------------------------------------
    // send
    public final synchronized void send(Msg msg) {
	msgCnt++;
	mqueue.add(msg);
	log.fine(id + ": send \"" + msg + "\"");
	notify();
    } // send


    //------------------------------------------------------------
    // receive
    public final synchronized Msg receive() {
	// wait if message queue is empty
	if (--msgCnt < 0) {
	    while (true) {
		try {
	
		    wait();
		    break;
		} catch (InterruptedException e) {
		    log.warning(id + ".receive: InterruptedException");

		    if (msgCnt >= 0)
			break;		// msg arrived already
		    else
			continue;	// no msg yet, continue waiting
		}
	    }
	}

	Msg msg = mqueue.remove(0);
	log.fine(id + ": receiveing \"" + msg + "\"");
	return msg;
    } // receive
} // MBox
