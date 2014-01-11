package endpoint;

import java.util.ArrayList;
import java.util.Map;

public class ChatMessage {
	  
	public enum Type {
		INITIALIZE,
		TEXT,
		REQUEST
	}

	ArrayList<Param> additionalParams;
	private Type messageType;
    private String message;
    
    public ChatMessage(){
    }
    public ChatMessage(String message, Type MessageType){
    	this.message=message;
    	this.messageType=MessageType;
    	this.additionalParams=new ArrayList<>();
    }
    
    public ChatMessage(String message, Type MessageType,ArrayList<Param> additionalParams){
    	this.message=message;
    	this.messageType=MessageType;
    	this.additionalParams=additionalParams;
    }
  
    public void setType(Type MessageType) {
    	this.messageType=MessageType;
    }
    public Type getType() {
    	return messageType;
    }
    public ArrayList<Param> getAdditionalParams() {
    	return additionalParams;
    }
    public void appendAdditionalParams(String key,String value){
    	additionalParams.add(new Param(key, value));
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
    
    
    
    /* Before you start screaming on why i created this class,please,read
     * the doc at the beginning of this class							*/    
	public class Param {

		String key;
		String value;

		public Param(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey(){
			return key;
		}
		public String getValue(){
			return value;
		}		
	}
}