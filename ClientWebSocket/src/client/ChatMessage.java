package client;
/*
 * @(#)ChatMessage.java        1.0 02/12/2013
 *
 * Belli Stefano 0000652935, Cozzolino Francesco 0000xxxxxxx
 * 
 */


/* Class that describes the actual messages sent from (and to) the users
 * The below class is then converted in json format by MessageEncoder.java class
 * 
 */
public class ChatMessage {
	  
	
	enum Type {
		INITIALIZE,		/* Used to Registry a new user to the channel */
		TEXT,			/* Duh. */
		REQUEST			/* Used to make special requests. Needs implementation */ 
	}
	
	private Type MessageType;
    private String message;
 
    
    public ChatMessage(String message, Type MessageType){
    	this.message=message;
    	this.MessageType=MessageType;
    	
    }
    public void setType(Type MessageType) {
    	this.MessageType=MessageType;
    }
    public Type getType() {
    	return MessageType;
    }
 
    /**
     * Return message
     * @return the message
     */
    public String getMessage() {
        return message;
    }
 
    /**
     * Set message
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}