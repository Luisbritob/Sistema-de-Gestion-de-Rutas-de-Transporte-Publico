import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Mainfx extends Application {
   
   @Override
   public void start(Stage stage) {
      Label mensaje = new Label("JavaFX está funcionando bien");
      
      VBox root = new VBox();
      root.getChildren().add(mensaje);
      
      Scene scene = new Scene(root, 400, 200);
      
      stage.setTitle("Prueba JavaFX");
      stage.setScene(scene);
      stage.show();
   }
   
   public static void main(String[] args) {
      launch(args);
   }
}
