import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Mainfx extends Application {
   
   @Override
   public void start(Stage stage) {
      
      // Fondo principal
      VBox root = new VBox(30);
      root.setAlignment(Pos.TOP_CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;"); // Blue Fog
      
      // ===== LOGO =====
      ImageView logoView;
      try {
         // Asegúrate de tener la imagen en la carpeta resources o en la raíz del proyecto
         Image logo = new Image("logo.png");
         logoView = new ImageView(logo);
         logoView.setFitWidth(220);
         logoView.setPreserveRatio(true);
      } catch (Exception e) {
         // Si no encuentra el logo, muestra un placeholder
         Label logoPlaceholder = new Label("LOGO");
         logoPlaceholder.setStyle(
                 "-fx-font-size: 40px;" +
                         "-fx-font-weight: bold;" +
                         "-fx-text-fill: #0F1C3F;" +
                         "-fx-background-color: white;" +
                         "-fx-border-color: #0F1C3F;" +
                         "-fx-border-width: 4px;" +
                         "-fx-padding: 40 90 40 90;"
         );
         
         StackPane logoPane = new StackPane(logoPlaceholder);
         root.getChildren().add(logoPane);
         
         // Nombre de la empresa
         Label nombreEmpresa = new Label("ROUTEFINDER");
         nombreEmpresa.setStyle(
                 "-fx-font-size: 28px;" +
                         "-fx-font-weight: bold;" +
                         "-fx-text-fill: #0F1C3F;" +
                         "-fx-background-color: white;" +
                         "-fx-border-color: #0F1C3F;" +
                         "-fx-border-width: 3px;" +
                         "-fx-padding: 8 40 8 40;"
         );
         
         root.getChildren().add(nombreEmpresa);
         root.getChildren().add(crearPanelBotones());
         
         Scene scene = new Scene(root, 900, 700);
         stage.setTitle("RouteFinder");
         stage.setScene(scene);
         stage.show();
         return;
      }
      
      StackPane logoPane = new StackPane(logoView);
      logoPane.setPadding(new Insets(10));
      logoPane.setStyle(
              "-fx-background-color: white;" +
                      "-fx-border-color: #0F1C3F;" +
                      "-fx-border-width: 4px;"
      );
      
      // ===== NOMBRE EMPRESA =====
      Label nombreEmpresa = new Label("ROUTEFINDER");
      nombreEmpresa.setStyle(
              "-fx-font-size: 28px;" +
                      "-fx-font-weight: bold;" +
                      "-fx-text-fill: #0F1C3F;" +   // Pantone 276 C aprox
                      "-fx-background-color: white;" +
                      "-fx-border-color: #0F1C3F;" +
                      "-fx-border-width: 3px;" +
                      "-fx-padding: 8 40 8 40;"
      );
      
      // ===== AGREGAR TODO =====
      root.getChildren().addAll(
              logoPane,
              nombreEmpresa,
              crearPanelBotones()
      );
      
      Scene scene = new Scene(root, 900, 700);
      stage.setTitle("RouteFinder");
      stage.setScene(scene);
      stage.show();
   }
   
   private GridPane crearPanelBotones() {
      Button btnAgregarParada = crearBoton("Agregar parada");
      Button btnAgregarRuta = crearBoton("Agregar ruta");
      
      Button btnModificarParada = crearBoton("Modificar parada");
      Button btnModificarRuta = crearBoton("Modificar ruta");
      
      Button btnEliminarParada = crearBoton("Eliminar parada");
      Button btnEliminarRuta = crearBoton("Eliminar ruta");
      
      // Eventos temporales
      btnAgregarParada.setOnAction(e -> mostrarMensaje("Agregar parada", "Aquí irá la ventana para agregar parada."));
      btnAgregarRuta.setOnAction(e -> mostrarMensaje("Agregar ruta", "Aquí irá la ventana para agregar ruta."));
      
      btnModificarParada.setOnAction(e -> mostrarMensaje("Modificar parada", "Aquí irá la ventana para modificar parada."));
      btnModificarRuta.setOnAction(e -> mostrarMensaje("Modificar ruta", "Aquí irá la ventana para modificar ruta."));
      
      btnEliminarParada.setOnAction(e -> mostrarMensaje("Eliminar parada", "Aquí irá la ventana para eliminar parada."));
      btnEliminarRuta.setOnAction(e -> mostrarMensaje("Eliminar ruta", "Aquí irá la ventana para eliminar ruta."));
      
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(70);
      grid.setVgap(35);
      
      grid.add(btnAgregarParada, 0, 0);
      grid.add(btnAgregarRuta, 1, 0);
      
      grid.add(btnModificarParada, 0, 1);
      grid.add(btnModificarRuta, 1, 1);
      
      grid.add(btnEliminarParada, 0, 2);
      grid.add(btnEliminarRuta, 1, 2);
      
      return grid;
   }
   
   private Button crearBoton(String texto) {
      Button button = new Button(texto);
      button.setPrefWidth(240);
      button.setPrefHeight(65);
      
      button.setStyle(
              "-fx-background-color: white;" +
                      "-fx-text-fill: #0F1C3F;" +
                      "-fx-font-size: 20px;" +
                      "-fx-font-weight: bold;" +
                      "-fx-border-color: #63666A;" +   // Cool Gray
                      "-fx-border-width: 4px;" +
                      "-fx-background-radius: 0;" +
                      "-fx-border-radius: 0;"
      );
      
      button.setOnMouseEntered(e ->
              button.setStyle(
                      "-fx-background-color: #63666A;" +
                              "-fx-text-fill: white;" +
                              "-fx-font-size: 20px;" +
                              "-fx-font-weight: bold;" +
                              "-fx-border-color: #0F1C3F;" +
                              "-fx-border-width: 4px;" +
                              "-fx-background-radius: 0;" +
                              "-fx-border-radius: 0;"
              )
      );
      
      button.setOnMouseExited(e ->
              button.setStyle(
                      "-fx-background-color: white;" +
                              "-fx-text-fill: #0F1C3F;" +
                              "-fx-font-size: 20px;" +
                              "-fx-font-weight: bold;" +
                              "-fx-border-color: #63666A;" +
                              "-fx-border-width: 4px;" +
                              "-fx-background-radius: 0;" +
                              "-fx-border-radius: 0;"
              )
      );
      
      return button;
   }
   
   private void mostrarMensaje(String titulo, String mensaje) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(titulo);
      alert.setHeaderText(null);
      alert.setContentText(mensaje);
      alert.showAndWait();
   }
   
   public static void main(String[] args) {
      launch(args);
   }
}