package juego;

import java.io.IOException;

import juego.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/***
 * 
 * @author Freddy
 */
public class Client extends Application {

	private Stage primaryStage;
	private VBox chatLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Juego");

		initChatLayout();
	}

	private void initChatLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Client.class.getResource("view/ClientGUI.fxml"));
			loader.setController(new ClientController());
			chatLayout = (VBox) loader.load();

			Scene scene = new Scene(chatLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
