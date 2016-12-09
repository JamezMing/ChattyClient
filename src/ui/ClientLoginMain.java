package ui;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import common.ClientLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientLoginMain extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout; 
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Log In");
        initRootLayout();
    }
	
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientLoginMain.class.getResource("ClientLoginUI.fxml"));
            rootLayout = (AnchorPane) loader.load();
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt"))));
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            ClientLogger.init();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

	public static void main(String[] args) {
		launch(args);
	}
}
