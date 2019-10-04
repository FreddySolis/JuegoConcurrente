package juego.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/***
 * 
 * @author Freddy
 */
public class ServerController {
	@FXML
	private TextArea txtAreaEventLog;
	@FXML
	private ListView<String> listUsersConnected;
	@FXML
	private Button btnStartServer;
	@FXML
	private Button btnStopServer;

	public Server server;

	private ObservableList<String> users;

	public void startServer() {
		// create a new Server
		server = new Server(1500, this);
		users = FXCollections.observableArrayList();
		listUsersConnected.setItems(users);
		new ServerRunning().start();
		btnStartServer.setDisable(true);
		btnStopServer.setDisable(false);
	}

	public void stopServer() {
		if(server != null) {
			server.stop();
			btnStopServer.setDisable(true);
			btnStartServer.setDisable(false);
			listUsersConnected.setItems(null);
			server = null;
			return;
		}
	}


	class ServerRunning extends Thread {
		public void run() {
			server.start();         
			appendEvent("Server muerto\n");
			server = null;
			users = null;
		}
	}

	public void addUser(String user) {
		Platform.runLater(() -> {
			users.add(user);
		});
	}
	public void appendEvent(String string) {
		txtAreaEventLog.appendText(string);
	}

	public void remove(String username) {
		Platform.runLater(() -> {
			users.remove(username);
		});
	}
}
