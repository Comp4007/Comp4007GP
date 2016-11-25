package MyApp.misc;

import java.util.logging.Logger;
import java.util.ArrayList;

// JavaSE6Tutorial/docs/CH15.md
// 第 15 章 執行緒（Thread）
// https://github.com/JustinSDK/JavaSE6Tutorial/blob/master/docs/CH15.md

// 我覺得呢個只係 Joe Sir 想話俾我哋聽 `synchronized` 呢個 reserved 嘅運作係點嘅例子咁解，唔一定要我哋用
// -- Charles

// ======================================================================
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
