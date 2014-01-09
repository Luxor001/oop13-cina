package endpoint;

public class ChatMessage {
	  
	public enum Type {
		INITIALIZE,
		TEXT,
		REQUEST
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