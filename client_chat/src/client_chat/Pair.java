package client_chat;

import java.io.Serializable;

public class Pair<X, Y> implements Serializable {
	private X x;
	private Y y;

	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getFirst() {
		return x;
	}

	public Y getSecond() {
		return y;
	}
}
