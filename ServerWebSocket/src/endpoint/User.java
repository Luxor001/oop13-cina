package endpoint;

import java.net.InetAddress;

import javax.websocket.Session;

/**
 * Represent a user istance for the server. 
 * It contains the Session object, which represent the connection stream
 * of a client and it's vital for the server to succesfully perform the 
 * action of sending messages.
 * */
public  class User {
	
	public enum State{
		VISIBLE,
		OCCUPIED,
		SNOOZE,
		INVISIBLE		
	}
	private String nickName;
	private InetAddress ip;
	private State currState;
	private Session session;
	
	public User(String nickName, State initialState,Session session){
		this.nickName=nickName;
		currState=initialState;
		this.session=session;		
	}
	public User(Session session){
		this.session=session;		
	}
	
	public void SetNickname(String nick){
		this.nickName=nick;
	}
	public String GetNickname(){
		return nickName;
	}
		
	public void SetIp(InetAddress ip){
		this.ip=ip;
	}
	public InetAddress GetIp(){
		return ip;
	}
	
	public void SetState(State newstate){
		this.currState=newstate;
	}
	public State GetState(){
		return currState;
	}
	
	public Session GetSession(){
		return session;
	}
}
