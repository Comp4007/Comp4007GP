package MyApp.timer;

import MyApp.misc.*;
import MyApp.Building;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

//======================================================================
// Timer
public class Timer extends AppThread {
    private final int ticks;
    private static MBox timerMBox = null;
    private Ticker ticker = null;
    private ArrayList<ActiveTimer> timerList = null;

    //------------------------------------------------------------
    // Timer
    public Timer(String id, Building appkickstarter) {
        super(id, appkickstarter);
	ticker = new Ticker(getMBox());
	timerMBox = getMBox();
	timerList = new ArrayList<ActiveTimer>();
	ticks = new Integer(building.getProperty("TimerTicks"));
    } // Timer


    //------------------------------------------------------------
    // run
    public void run() {
	log.info("Timer starting...");
	new Thread(ticker).start();

	while (true) {
	    Msg msg = mbox.receive();

	    if (msg.getSender().equals("Ticker")) {
		chkTimeout();
	    } else {
		if (msg.getDetails().startsWith("set timer, ")) {
		    set(msg);
		} else if (msg.getDetails().startsWith("cancel timer, ")) {
		    cancel(msg);
		} else {
		    String eMsg = "Invalid command for Timer: "+msg;
		    throw (new RuntimeException(eMsg));
		}
	    }
	}
    } // run


    //------------------------------------------------------------
    // chkTimeout
    private void chkTimeout() {
	long currentTime = (new Date()).getTime();
	ArrayList<ActiveTimer> timeoutTimers = new ArrayList<ActiveTimer>();
	log.info("Timer chk...");

	for (ActiveTimer timer : timerList) {
	    if (timer.timeout(currentTime)) {
		timeoutTimers.add(timer);
	    }
	}

	for (ActiveTimer timer : timeoutTimers) {
	    int timerID = timer.getTimerID();
	    String caller = timer.getCaller();
	    MBox mbox = building.getThread(caller).getMBox();
	    mbox.send(new Msg("Timer", 999, "["+timerID+"]: Time's up!"));
	    timerList.remove(timer);
	}
    } // chkTimeout


    //------------------------------------------------------------
    // ticker
    private class Ticker implements Runnable {
	private MBox timerMBox = null;

	//----------------------------------------
	// ticker
	public Ticker(MBox timerMBox) {
	    this.timerMBox = timerMBox;
	} // Ticker


	//----------------------------------------
	// run
	public void run() {
	    while (true) {
		try {
		    Thread.sleep(ticks);
		} catch (Exception e) {};
		mbox.send(new Msg("Ticker", 0, "tick"));
	    }
	} // run
    } // ticker


    //------------------------------------------------------------
    // ActiveTimer
    private static class ActiveTimer {
	private int  timerID;
	private long wakeupTime;
	private String caller;

	//----------------------------------------
	// ActiveTimer
	public ActiveTimer(int timerID, long wakeupTime, String caller) {
	    this.timerID = timerID;
	    this.wakeupTime = wakeupTime;
	    this.caller = caller;
	} // ActiveTimer

	//----------------------------------------
	// getters
	public int    getTimerID() { return this.timerID; }
	public String getCaller()  { return this.caller; }

	//----------------------------------------
	// timeout
	public boolean timeout(long currentTime) {
	    return currentTime > wakeupTime;
	} // timeout
    } // ActiveTimer


    //------------------------------------------------------------
    // setTimer
    public static int setTimer(String id, long sleepTime) {
	int timerID = (new Random()).nextInt(9000) + 1000;
	timerMBox.send(new Msg(id, 0, "set timer, "+sleepTime+", "+timerID));
	return timerID;
    } // setTimer


    //------------------------------------------------------------
    // set
    private void set(Msg msg) {
	String details = msg.getDetails().substring(11);

	// get timerID
	String timerIDStr = details.substring(details.indexOf(", ")+2);
	int timerID = new Integer(timerIDStr).intValue();

	// get wakeup time
	String sleepTimeStr = details.substring(0, details.indexOf(", "));
	long sleepTime = new Long(sleepTimeStr).longValue();
	long wakeupTime = (new Date()).getTime() + sleepTime;

	// get caller
	String caller = msg.getSender();

	// add this new timer to timer list
	timerList.add(new ActiveTimer(timerID, wakeupTime, caller));
	log.info(id+": "+caller+" setting timer: "+
		"["+sleepTime+"], ["+timerID+"]");
    } // set


    //------------------------------------------------------------
    // cancelTimer
    public static void cancelTimer(String id, int timerID) {
	timerMBox.send(new Msg(id, 1, "cancel timer, "+timerID));
    } // cancelTimer


    //------------------------------------------------------------
    // cancel
    private void cancel(Msg msg) {
	// get timerID
	String details = msg.getDetails();
	String timerIDStr = details.substring(details.indexOf(", ")+2);
	int timerID = new Integer(timerIDStr).intValue();

	// get caller
	String caller = msg.getSender();

	ActiveTimer cancelTimer = null;

	for (ActiveTimer timer : timerList) {
	    if (timer.getTimerID() == timerID) {
		if (timer.getCaller().equals(caller)) {
		    cancelTimer = timer;
		    break;
		}
	    }
	}

	if (cancelTimer != null) {
	    timerList.remove(cancelTimer);
	    log.info(id+": "+caller+" cancelling timer: "+"["+timerID+"]");
	} else {
	    log.info(id+": "+caller+" cancelling timer: "+"["+timerID+"]"+
		    " TIMER NOT FOUND!!");
	}

    } // cancel
} // Timer
