package juego;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import juego.controller.ServerController;

/***
 * 
 * @author Freddy
 */
public class Server extends Application {
	
	private Stage primaryStage;
	private VBox serverLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Server");

		initServerLayout();
	}

	private void initServerLayout() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Client.class.getResource("view/ServerGUI.fxml"));
			ServerController serverController = new ServerController();
			loader.setController(serverController);
			serverLayout = (VBox) loader.load();

			Scene scene = new Scene(serverLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					if (serverController.server != null) {
						serverController.server.stop();
						serverController.server = null;
					}
				}
			});        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
