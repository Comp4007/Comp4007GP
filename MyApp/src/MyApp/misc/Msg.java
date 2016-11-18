package MyApp.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private int type;
    private String details;


    //------------------------------------------------------------
    // Msg
    public Msg(String sender, int type, String details) {
	this.sender = sender;
	this.type = type;
	this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getters
    public String getSender()  { return sender; }
    public int    getType()    { return type; }
    public String getDetails() { return details; }


    //------------------------------------------------------------
    // toString
    public String toString() {
	return sender + "(" + type + ") -- " + details;
    } // toString
} // Msg
