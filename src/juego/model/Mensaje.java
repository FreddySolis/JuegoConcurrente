package juego.model;

import java.io.Serializable;

/***
 * 
 * @author Freddy
 */
public class Mensaje implements Serializable {
	
	protected static final long serialVersionUID = 1112122200L;

	public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;
	
	public Mensaje(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public int getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
}
