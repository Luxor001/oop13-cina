package endpoint;

import java.util.ArrayList;



public class ChatMessage {
	  
	public enum Type {
		INITIALIZE,
		TEXT, 		 
		NEWUSER, 
		USERDISCONNECTED,
		USERLIST,
		DISCONNECTING,
		REQUESTPRIVATECHAT,
		REQUESTEDPRIVATECHAT,
		YESPRIVATECHAT,
		NOPRIVATECHAT
	}


	private Param additionalParams;
	private Type messageType;
    private String message;
    
    public ChatMessage(){
    }
    public ChatMessage(String message, Type MessageType){
    	this.message=message;
    	this.messageType=MessageType;
    }
    
    public ChatMessage(String message, Type MessageType,Param additionalParam){
    	this.message=message;
    	this.messageType=MessageType;    	
    	this.additionalParams=additionalParam;    	
    }
  
    public void setType(Type MessageType) {
    	this.messageType=MessageType;
    }
    public Type getType() {
    	return messageType;
    }


    public Param getAdditionalParams() {
    	if(additionalParams == null)
    		additionalParams=new Param();
    	return additionalParams;
    } 
    
    public void setAdditionalParams(Param params){
    	additionalParams=new endpoint.ChatMessage.Param();
    	this.additionalParams=params;
    }
    public boolean isParamSet() {
    	if(additionalParams == null)
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
	public static class Param {
		private String nickname;
		private Boolean visibility;
		private ArrayList<String> usersList=new ArrayList<String>();
		private String ip;
			
		public Param(){
			
		}
		
		public void setNickname(String Nick){
			nickname=Nick;
		}
		
		public void SetVisibility(Boolean visible){
			visibility=visible;
		}		

		public void setIP(String ip){
			this.ip=ip;
		}		
		
		
		public Boolean getVisibility(){
			return visibility;
		}
		public ArrayList<String> getUsersList(){
			return usersList;
		}			
		
		public String getIP(){
			return ip;
		}		

		public String getNickname(){
			return nickname;
		}
		public void appendUser(String UserNickname){
			usersList.add(UserNickname);
		}	
	}
}