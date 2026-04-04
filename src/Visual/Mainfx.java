package Visual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.nio.file.Paths;

public class Mainfx extends Application {

   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader = new FXMLLoader(Paths.get("Recursos/MainView.fxml").toUri().toURL());
      Scene scene = new Scene(loader.load());

      stage.setTitle("RouteFinder");
      stage.setScene(scene);
      stage.show();
   }

   public static void main(String[] args) {
      launch();
   }
}