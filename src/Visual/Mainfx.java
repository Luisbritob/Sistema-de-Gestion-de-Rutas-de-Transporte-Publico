package Visual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Mainfx extends Application {
   
   //Carga el archivo FXML MainView.fxml, y lO muestra
   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visual/MainView.fxml"));
      Scene scene = new Scene(loader.load(), 1200, 800);
      
      stage.setScene(scene);
      stage.show();
   }
   
}