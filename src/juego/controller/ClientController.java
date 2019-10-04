package juego.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import juego.model.Mensaje;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/***
 * 
 * @author Freddy
 */
public class ClientController {

	@FXML
	private Button btnLogin;
	@FXML
	private Button btnLogout;
	@FXML
	private TextArea txtAreaServerMsgs;
	@FXML
	private TextField txtHostIP;
	@FXML
	private TextField txtUsername;
	@FXML
	private ListView<String> listUser;
        @FXML
        private ImageView kirby;

	private ObservableList<String> users;

	private boolean connected;
	private String server, username;
	private int port;

	private ObjectInputStream sInput;		
	private ObjectOutputStream sOutput;		
	private Socket socket;


	public void login() {
		port = 1500;
		server = txtHostIP.getText();
		System.out.println(server);
		username = txtUsername.getText();
		if(!start())
			return;
		connected = true;
		btnLogin.setDisable(true);
		btnLogout.setDisable(false);
		txtUsername.setEditable(false);
		txtHostIP.setEditable(false);
	}


	public void logout() {
		if (connected) {
			Mensaje msg = new Mensaje(Mensaje.LOGOUT, "");
			try {
				sOutput.writeObject(msg);
				btnLogout.setDisable(false);
				btnLogin.setDisable(true);
				txtUsername.setEditable(true);
				txtHostIP.setEditable(true);
			}
			catch(IOException e) {
				display("Error al registrar con el servidor: " + e);
			}
		}
	}

	public void sendMessage() {
            String test = "Hola";
            
		if (connected) {
			Mensaje msg = new Mensaje(Mensaje.MESSAGE, test);
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				display("Error al registrar con el servidor: " + e);
			}
		}
	}


	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error conectando al server:" + ec);
			return false;
		}

		String msg = "Conectado a " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Error creando nueva entrada: " + eIO);
			return false;
		}

		new ListenFromServer().start();
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Error al loguearse: " + eIO);
			disconnect();
			return false;
		}
		return true;
	}


	private void display(String msg) {
		txtAreaServerMsgs.appendText(msg + "\n");
	}

	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
		try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} 


		connectionFailed();

	}

	public void connectionFailed() {
		btnLogin.setDisable(false);
		btnLogout.setDisable(true);
		txtHostIP.setEditable(true);
		connected = false;
	}


	class ListenFromServer extends Thread {

		public void run() {
			users =	FXCollections.observableArrayList();
			listUser.setItems(users);
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					String[] split = msg.split(":");
					if (split[1].equals("WHOISIN")) {
						Platform.runLater(() -> {
							users.add(split[0]);
						});;
					} else if (split[1].equals("REMOVE")) {
						Platform.runLater(() -> {
							users.remove(split[0]);
						});
					} else{//En este else es donde se mueve el Kirby
                                                String nmsg[] = msg.split("\n")[0].split("\\?");
                                                int x = Integer.parseInt(nmsg[1]);
                                                int y = Integer.parseInt(nmsg[2]);
                                                kirby.setLayoutX((double)x);
                                                kirby.setLayoutY((double)y);
						txtAreaServerMsgs.appendText(nmsg[0]+"\n");
					}
				}
				catch(IOException e) {
					display("El server c murió");
					connectionFailed();
					Platform.runLater(() -> {
						listUser.setItems(null);
					});
					break;
				}

				catch(ClassNotFoundException e2) {

				}
			}
		}
	}
}
