package juego.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import juego.model.Mensaje;

/***
 * 
 * @author Freddy
 */
public class Server {
	private static int uniqueId;
	private ArrayList<ClientThread> clientsConnected;
	private ServerController serverController;
	private SimpleDateFormat sdf;
	private int port;
	private boolean keepGoing;


	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerController serverController) {

		this.serverController = serverController;

		this.port = port;

		sdf = new SimpleDateFormat("HH:mm:ss");

		clientsConnected = new ArrayList<ClientThread>();
	}

	public void start() {
		keepGoing = true;

		try 
		{

			ServerSocket serverSocket = new ServerSocket(port);


			while(keepGoing) 
			{

				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();

				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket); 
				clientsConnected.add(t); 
				t.start();
			}

			try {
				serverSocket.close();
				for(int i = 0; i < clientsConnected.size(); ++i) {
					ClientThread tc = clientsConnected.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					}
					catch(IOException ioE) {

					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}

		catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		

	public void stop() {
		keepGoing = false;

		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}

	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		serverController.appendEvent(time + "\n");
	}

	private synchronized void broadcast(String message) {

		String time = sdf.format(new Date());
		String messageLf;
		if (message.contains("WHOISIN") || message.contains("REMOVE")){
			messageLf = message;
		} else {
			messageLf = time + " " + message + "\n";

		}


		for(int i = clientsConnected.size(); --i >= 0;) {
			ClientThread ct = clientsConnected.get(i);
			if(!ct.writeMsg(messageLf)) {
				clientsConnected.remove(i);
				serverController.remove(ct.username);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}


	synchronized void remove(int id) {
		for(int i = 0; i < clientsConnected.size(); ++i) {
			ClientThread ct = clientsConnected.get(i);
			if(ct.id == id) {
				serverController.remove(ct.username);
				ct.writeMsg(ct.username + ":REMOVE");
				clientsConnected.remove(i);
				return;
			}
		}
	}


	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;

		String username;

		Mensaje cm;

		String date;


		ClientThread(Socket socket) {

			id = ++uniqueId;
			this.socket = socket;

			System.out.println("Hilo creando objetos para lectura y escritura...");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());

				username = (String) sInput.readObject();
				serverController.addUser(username);
				broadcast(username + ":WHOISIN"); 
				writeMsg(username + ":WHOISIN");
				for(ClientThread client : clientsConnected) {
					writeMsg(client.username + ":WHOISIN");
				}
				display(username + " Se dejó caer.");
			}
			catch (IOException e) {
				display("Error en el server: " + e);
				return;
			}

			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}


		public void run() {

			boolean keepGoing = true;
			while(keepGoing) {

				try {
					cm = (Mensaje) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Error leyendo: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}

				switch(cm.getType()) {

				case Mensaje.MESSAGE:
                                        int x = (int)(Math.random() * ((950 - 150) + 1)) + 150;
                                        int y = (int)(Math.random() * ((625 - 40) + 1)) + 40;
					broadcast(username + " Anotó. ?"+ x +"?" + y  );
					break;
				case Mensaje.LOGOUT:
					display(username + " c murió.");
					broadcast(username + ":REMOVE");
					keepGoing = false;
					break;
				}
			}

			remove(id);
			close();
		}

		private void close() {

			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}


		private boolean writeMsg(String msg) {

			if(!socket.isConnected()) {
				close();
				return false;
			}

			try {
				sOutput.writeObject(msg);
			}

			catch(IOException e) {
				display("Error enviando puntuacion a: " + username);
				display(e.toString());
			}
			return true;
		}
	}
}



