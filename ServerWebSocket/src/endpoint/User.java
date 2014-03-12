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
	private String Nickname;
	private InetAddress Ip;
	private State CurrState;
	private Session session;
	
	public User(String NickName, State InitialState,Session session){
		this.Nickname=NickName;
		CurrState=InitialState;
		this.session=session;		
	}
	public User(Session session){
		this.session=session;		
	}
	
	public void SetNickname(String Nick){
		this.Nickname=Nick;
	}
	public String GetNickname(){
		return Nickname;
	}
		
	public void SetIp(InetAddress Ip){
		this.Ip=Ip;
	}
	public InetAddress GetIp(){
		return Ip;
	}
	
	public void SetState(State newstate){
		this.CurrState=newstate;
	}
	public State GetState(){
		return CurrState;
	}
	
	public Session GetSession(){
		return session;
	}
}
