package endpoint;

import java.util.ArrayList;


public class ChatMessage {
	  
	public enum Type {
		INITIALIZE,
		TEXT,
		REQUEST,
		NEWUSER,
		USERDISCONNECTED,
		USERLIST
	}
	public enum RequestType{
		
	}

	private Param additionalParams;//=new Param();
	private Type messageType;
    private String message;
    
    public ChatMessage(){
    }
    public ChatMessage(String message, Type MessageType){
    	this.message=message;
    	this.messageType=MessageType;
    //	this.additionalParams=new Param();
    }
    
    public ChatMessage(String message, Type MessageType,Param additionalParams){
    	this.message=message;
    	this.messageType=MessageType;
    //	this.additionalParams=additionalParams;
    }
  
    public void setType(Type MessageType) {
    	this.messageType=MessageType;
    }
    public Type getType() {
    	return messageType;
    }
    public Param getAdditionalParams() {
    	additionalParams=new Param();
    	return additionalParams;
    } 
    public boolean isParamSet() {
    	if(additionalParams.equals(null))
    		return false;
    	else
    		return true;
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
		private String nickname;
		private Boolean visibility;
		private ArrayList<String> usersList=new ArrayList<String>();
		
		public Param(){
			
		}
		
		public void setNickname(String Nick){
			nickname=Nick;
		}
		public String getNickname(){
			return nickname;
		}
		
		public void SetVisibility(Boolean visible){
			visibility=visible;;
		}
		public Boolean getVisibility(){
			return visibility;
		}
		public ArrayList<String> getUsersList(){
			return usersList;
		}
		public void appendUser(String UserNickname){
			usersList.add(UserNickname);
		}		
	}
}