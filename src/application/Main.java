package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	//53656261737469616e2053
	
	public static Stage stage;
	
	double xOffset = 0;
	double yOffset = 0;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			stage = primaryStage;
			AnchorPane root = new AnchorPane();
			root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			scene.setOnMousePressed(e -> {
				
                xOffset = primaryStage.getX() - e.getScreenX();
                yOffset = primaryStage.getY() - e.getScreenY();
				
			});
			
			scene.setOnMouseDragged(e -> {
                
                if(e.getPickResult().getIntersectedNode() instanceof Canvas == false) {
                	primaryStage.setX(e.getScreenX() + xOffset);
                    primaryStage.setY(e.getScreenY() + yOffset);
                }
                
				
			});
			
			primaryStage.setScene(scene);
			
			primaryStage.setResizable(false);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));
		System.out.println("java.runtime.version: " + System.getProperties().get("java.runtime.version"));

		launch(args);
	}
}
