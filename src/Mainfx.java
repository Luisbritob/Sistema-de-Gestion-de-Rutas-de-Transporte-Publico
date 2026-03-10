import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class Mainfx extends Application {

   private SistemaGrafos sistema = new SistemaGrafos();
   private AlgoritmosGrafos algoritmos = new AlgoritmosGrafos(sistema);
   private ObservableList<Paradas> listaParadas = FXCollections.observableArrayList();
   private ObservableList<Rutas> listaRutas = FXCollections.observableArrayList();
   private int nextIdParada = 1;

   @Override
   public void start(Stage stage) {

      // Inicializar con algunos datos de ejemplo
      inicializarDatosEjemplo();

      // Fondo principal
      VBox root = new VBox(30);
      root.setAlignment(Pos.TOP_CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;"); // Blue Fog

      // ===== LOGO =====
      ImageView logoView;
      try {
         Image logo = new Image("logo.png");
         logoView = new ImageView(logo);
         logoView.setFitWidth(350);
         logoView.setPreserveRatio(true);
         root.getChildren().add(logoView);
      } catch (Exception e) {
         Label logoPlaceholder = new Label("ROUTEFINDER");
         logoPlaceholder.setStyle(
                 "-fx-font-size: 40px;" +
                         "-fx-font-weight: bold;" +
                         "-fx-text-fill: #0F1C3F;" +
                         "-fx-background-color: white;" +
                         "-fx-border-color: #0F1C3F;" +
                         "-fx-border-width: 4px;" +
                         "-fx-padding: 40 90 40 90;"
         );
         root.getChildren().add(logoPlaceholder);
      }

      // ===== PANEL DE BOTONES PRINCIPALES =====
      root.getChildren().add(crearPanelBotones());

      // ===== PANEL DE BÚSQUEDA DE RUTAS =====
      root.getChildren().add(crearPanelBusquedaRutas());

      Scene scene = new Scene(root, 1000, 800);
      stage.setTitle("RouteFinder - Sistema de Transporte");
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

      Button btnVerGrafo = crearBoton("Ver grafo");

      // Eventos
      btnAgregarParada.setOnAction(e -> mostrarVentanaAgregarParada());
      btnAgregarRuta.setOnAction(e -> mostrarVentanaAgregarRuta());

      btnModificarParada.setOnAction(e -> mostrarVentanaModificarParada());
      btnModificarRuta.setOnAction(e -> mostrarVentanaModificarRuta());

      btnEliminarParada.setOnAction(e -> mostrarVentanaEliminarParada());
      btnEliminarRuta.setOnAction(e -> mostrarVentanaEliminarRuta());

      btnVerGrafo.setOnAction(e -> mostrarVentanaVerGrafo());

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

      grid.add(btnVerGrafo, 0, 3, 2, 1);
      GridPane.setColumnSpan(btnVerGrafo, 2);
      btnVerGrafo.setPrefWidth(500);

      return grid;
   }

   private GridPane crearPanelBusquedaRutas() {
      GridPane panel = new GridPane();
      panel.setAlignment(Pos.CENTER);
      panel.setHgap(20);
      panel.setVgap(15);
      panel.setPadding(new Insets(20));
      panel.setStyle("-fx-background-color: white; -fx-border-color: #63666A; -fx-border-width: 3px;");

      Label lblTitulo = new Label("BUSCAR MEJOR RUTA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      GridPane.setColumnSpan(lblTitulo, 4);
      panel.add(lblTitulo, 0, 0);

      Label lblOrigen = new Label("Origen:");
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      panel.add(lblOrigen, 0, 1);

      ComboBox<Paradas> cmbOrigen = new ComboBox<>();
      cmbOrigen.setItems(listaParadas);
      cmbOrigen.setPrefWidth(200);
      panel.add(cmbOrigen, 1, 1);

      Label lblDestino = new Label("Destino:");
      lblDestino.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      panel.add(lblDestino, 2, 1);

      ComboBox<Paradas> cmbDestino = new ComboBox<>();
      cmbDestino.setItems(listaParadas);
      cmbDestino.setPrefWidth(200);
      panel.add(cmbDestino, 3, 1);

      Label lblCriterio = new Label("Criterio:");
      lblCriterio.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      panel.add(lblCriterio, 0, 2);

      ComboBox<CriterioRuta> cmbCriterio = new ComboBox<>();
      cmbCriterio.setItems(FXCollections.observableArrayList(CriterioRuta.values()));
      cmbCriterio.setValue(CriterioRuta.TIEMPO);
      cmbCriterio.setPrefWidth(200);
      panel.add(cmbCriterio, 1, 2);

      Button btnBuscar = new Button("Buscar Ruta");
      btnBuscar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnBuscar.setOnAction(e -> {
         if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar origen y destino");
            return;
         }
         buscarRuta(cmbOrigen.getValue(), cmbDestino.getValue(), cmbCriterio.getValue());
      });
      panel.add(btnBuscar, 2, 2, 2, 1);

      return panel;
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
                      "-fx-border-color: #63666A;" +
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

   // ==================== VENTANAS DE AGREGAR ====================

   private void mostrarVentanaAgregarParada() {
      Stage ventana = new Stage();
      ventana.setTitle("Agregar Parada");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("AGREGAR NUEVA PARADA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      TextField txtNombre = new TextField();
      txtNombre.setPromptText("Nombre de la parada");
      txtNombre.setPrefWidth(300);
      txtNombre.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

      Button btnGuardar = new Button("Guardar");
      btnGuardar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnGuardar.setOnAction(e -> {
         String nombre = txtNombre.getText().trim();
         if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre no puede estar vacío");
            return;
         }

         Paradas nuevaParada = new Paradas(nextIdParada++, nombre);
         sistema.agregarParada(nuevaParada);
         listaParadas.add(nuevaParada);

         mostrarAlerta("Éxito", "Parada agregada correctamente");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().addAll(lblTitulo, txtNombre, botones);

      Scene scene = new Scene(root, 500, 300);
      ventana.setScene(scene);
      ventana.show();
   }

   private void mostrarVentanaAgregarRuta() {
      Stage ventana = new Stage();
      ventana.setTitle("Agregar Ruta");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("AGREGAR NUEVA RUTA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(15);
      grid.setVgap(15);

      Label lblOrigen = new Label("Origen:");
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblOrigen, 0, 0);

      ComboBox<Paradas> cmbOrigen = new ComboBox<>();
      cmbOrigen.setItems(listaParadas);
      cmbOrigen.setPrefWidth(200);
      grid.add(cmbOrigen, 1, 0);

      Label lblDestino = new Label("Destino:");
      lblDestino.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblDestino, 2, 0);

      ComboBox<Paradas> cmbDestino = new ComboBox<>();
      cmbDestino.setItems(listaParadas);
      cmbDestino.setPrefWidth(200);
      grid.add(cmbDestino, 3, 0);

      Label lblTiempo = new Label("Tiempo:");
      lblTiempo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblTiempo, 0, 1);

      TextField txtTiempo = new TextField();
      txtTiempo.setPromptText("minutos");
      txtTiempo.setPrefWidth(150);
      grid.add(txtTiempo, 1, 1);

      Label lblDistancia = new Label("Distancia:");
      lblDistancia.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblDistancia, 2, 1);

      TextField txtDistancia = new TextField();
      txtDistancia.setPromptText("km");
      txtDistancia.setPrefWidth(150);
      grid.add(txtDistancia, 3, 1);

      Label lblCosto = new Label("Costo:");
      lblCosto.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblCosto, 0, 2);

      TextField txtCosto = new TextField();
      txtCosto.setPromptText("$");
      txtCosto.setPrefWidth(150);
      grid.add(txtCosto, 1, 2);

      CheckBox chkTransbordo = new CheckBox("Requiere transbordo");
      chkTransbordo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(chkTransbordo, 2, 2, 2, 1);

      root.getChildren().addAll(lblTitulo, grid);

      Button btnGuardar = new Button("Guardar");
      btnGuardar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnGuardar.setOnAction(e -> {
         if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar origen y destino");
            return;
         }

         try {
            double tiempo = Double.parseDouble(txtTiempo.getText());
            double distancia = Double.parseDouble(txtDistancia.getText());
            double costo = Double.parseDouble(txtCosto.getText());

            sistema.agregarRuta(cmbOrigen.getValue(), cmbDestino.getValue(),
                    tiempo, distancia, costo, chkTransbordo.isSelected());

            listaRutas.clear();
            actualizarListaRutas();

            mostrarAlerta("Éxito", "Ruta agregada correctamente");
            ventana.close();
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Los valores numéricos no son válidos");
         }
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().add(botones);

      Scene scene = new Scene(root, 800, 500);
      ventana.setScene(scene);
      ventana.show();
   }

   // ==================== VENTANAS DE MODIFICAR ====================

   private void mostrarVentanaModificarParada() {
      if (listaParadas.isEmpty()) {
         mostrarAlerta("Error", "No hay paradas para modificar");
         return;
      }

      Stage ventana = new Stage();
      ventana.setTitle("Modificar Parada");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("MODIFICAR PARADA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      ComboBox<Paradas> cmbParada = new ComboBox<>();
      cmbParada.setItems(listaParadas);
      cmbParada.setPromptText("Seleccione una parada");
      cmbParada.setPrefWidth(300);

      TextField txtNuevoNombre = new TextField();
      txtNuevoNombre.setPromptText("Nuevo nombre");
      txtNuevoNombre.setPrefWidth(300);
      txtNuevoNombre.setDisable(true);

      cmbParada.setOnAction(e -> {
         Paradas seleccionada = cmbParada.getValue();
         if (seleccionada != null) {
            txtNuevoNombre.setDisable(false);
            txtNuevoNombre.setText(seleccionada.getNombre());
         }
      });

      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnModificar.setOnAction(e -> {
         Paradas seleccionada = cmbParada.getValue();
         String nuevoNombre = txtNuevoNombre.getText().trim();

         if (seleccionada == null || nuevoNombre.isEmpty()) {
            mostrarAlerta("Error", "Debe seleccionar una parada y escribir un nombre");
            return;
         }

         sistema.modificarParada(seleccionada, nuevoNombre);
         listaParadas.setAll(listaParadas); // Refrescar lista

         mostrarAlerta("Éxito", "Parada modificada correctamente");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().addAll(lblTitulo, cmbParada, txtNuevoNombre, botones);

      Scene scene = new Scene(root, 500, 400);
      ventana.setScene(scene);
      ventana.show();
   }

   private void mostrarVentanaModificarRuta() {
      if (listaParadas.size() < 2) {
         mostrarAlerta("Error", "Se necesitan al menos 2 paradas para modificar rutas");
         return;
      }

      Stage ventana = new Stage();
      ventana.setTitle("Modificar Ruta");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("MODIFICAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(15);
      grid.setVgap(15);

      Label lblOrigen = new Label("Origen:");
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblOrigen, 0, 0);

      ComboBox<Paradas> cmbOrigen = new ComboBox<>();
      cmbOrigen.setItems(listaParadas);
      cmbOrigen.setPrefWidth(200);
      grid.add(cmbOrigen, 1, 0);

      Label lblDestino = new Label("Destino:");
      lblDestino.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblDestino, 2, 0);

      ComboBox<Paradas> cmbDestino = new ComboBox<>();
      cmbDestino.setItems(listaParadas);
      cmbDestino.setPrefWidth(200);
      grid.add(cmbDestino, 3, 0);

      Button btnBuscar = new Button("Buscar Ruta");
      btnBuscar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
      btnBuscar.setOnAction(e -> {
         if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
            mostrarAlerta("Error", "Seleccione origen y destino");
            return;
         }

         // Buscar la ruta para cargar sus datos
         List<Rutas> rutas = sistema.getGrafo().get(cmbOrigen.getValue());
         if (rutas != null) {
            for (Rutas ruta : rutas) {
               if (ruta.getDestino().equals(cmbDestino.getValue())) {
                  // Cargar datos en los campos
                  // Esto requeriría campos de texto adicionales
                  mostrarAlerta("Información", "Ruta encontrada. Proceda a modificar los valores.");
                  return;
               }
            }
         }
         mostrarAlerta("Error", "No existe una ruta entre esas paradas");
      });
      grid.add(btnBuscar, 4, 0);

      Label lblTiempo = new Label("Nuevo Tiempo:");
      lblTiempo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblTiempo, 0, 1);

      TextField txtTiempo = new TextField();
      txtTiempo.setPromptText("minutos");
      txtTiempo.setPrefWidth(150);
      grid.add(txtTiempo, 1, 1);

      Label lblDistancia = new Label("Nueva Distancia:");
      lblDistancia.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblDistancia, 2, 1);

      TextField txtDistancia = new TextField();
      txtDistancia.setPromptText("km");
      txtDistancia.setPrefWidth(150);
      grid.add(txtDistancia, 3, 1);

      Label lblCosto = new Label("Nuevo Costo:");
      lblCosto.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblCosto, 0, 2);

      TextField txtCosto = new TextField();
      txtCosto.setPromptText("$");
      txtCosto.setPrefWidth(150);
      grid.add(txtCosto, 1, 2);

      CheckBox chkTransbordo = new CheckBox("Requiere transbordo");
      chkTransbordo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(chkTransbordo, 2, 2, 2, 1);

      root.getChildren().addAll(lblTitulo, grid);

      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnModificar.setOnAction(e -> {
         if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
            mostrarAlerta("Error", "Seleccione origen y destino");
            return;
         }

         try {
            double tiempo = Double.parseDouble(txtTiempo.getText());
            double distancia = Double.parseDouble(txtDistancia.getText());
            double costo = Double.parseDouble(txtCosto.getText());

            sistema.modificarRuta(cmbOrigen.getValue(), cmbDestino.getValue(),
                    tiempo, distancia, costo, chkTransbordo.isSelected());

            listaRutas.clear();
            actualizarListaRutas();

            mostrarAlerta("Éxito", "Ruta modificada correctamente");
            ventana.close();
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Los valores numéricos no son válidos");
         }
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().add(botones);

      Scene scene = new Scene(root, 1000, 500);
      ventana.setScene(scene);
      ventana.show();
   }

   // ==================== VENTANAS DE ELIMINAR ====================

   private void mostrarVentanaEliminarParada() {
      if (listaParadas.isEmpty()) {
         mostrarAlerta("Error", "No hay paradas para eliminar");
         return;
      }

      Stage ventana = new Stage();
      ventana.setTitle("Eliminar Parada");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("ELIMINAR PARADA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      ComboBox<Paradas> cmbParada = new ComboBox<>();
      cmbParada.setItems(listaParadas);
      cmbParada.setPromptText("Seleccione una parada");
      cmbParada.setPrefWidth(300);

      Label lblAdvertencia = new Label("¿Está seguro que desea eliminar esta parada?\nSe eliminarán también todas las rutas asociadas.");
      lblAdvertencia.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
      lblAdvertencia.setVisible(false);

      cmbParada.setOnAction(e -> lblAdvertencia.setVisible(true));

      Button btnEliminar = new Button("Eliminar");
      btnEliminar.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnEliminar.setOnAction(e -> {
         Paradas seleccionada = cmbParada.getValue();
         if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una parada");
            return;
         }

         sistema.eliminarParada(seleccionada);
         listaParadas.remove(seleccionada);
         listaRutas.clear();
         actualizarListaRutas();

         mostrarAlerta("Éxito", "Parada eliminada correctamente");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnEliminar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().addAll(lblTitulo, cmbParada, lblAdvertencia, botones);

      Scene scene = new Scene(root, 500, 350);
      ventana.setScene(scene);
      ventana.show();
   }

   private void mostrarVentanaEliminarRuta() {
      if (listaParadas.size() < 2) {
         mostrarAlerta("Error", "Se necesitan al menos 2 paradas para eliminar rutas");
         return;
      }

      Stage ventana = new Stage();
      ventana.setTitle("Eliminar Ruta");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(30));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("ELIMINAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(15);
      grid.setVgap(15);

      Label lblOrigen = new Label("Origen:");
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblOrigen, 0, 0);

      ComboBox<Paradas> cmbOrigen = new ComboBox<>();
      cmbOrigen.setItems(listaParadas);
      cmbOrigen.setPrefWidth(200);
      grid.add(cmbOrigen, 1, 0);

      Label lblDestino = new Label("Destino:");
      lblDestino.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      grid.add(lblDestino, 2, 0);

      ComboBox<Paradas> cmbDestino = new ComboBox<>();
      cmbDestino.setItems(listaParadas);
      cmbDestino.setPrefWidth(200);
      grid.add(cmbDestino, 3, 0);

      root.getChildren().addAll(lblTitulo, grid);

      Button btnEliminar = new Button("Eliminar");
      btnEliminar.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnEliminar.setOnAction(e -> {
         if (cmbOrigen.getValue() == null || cmbDestino.getValue() == null) {
            mostrarAlerta("Error", "Seleccione origen y destino");
            return;
         }

         sistema.eliminarRuta(cmbOrigen.getValue(), cmbDestino.getValue());
         listaRutas.clear();
         actualizarListaRutas();

         mostrarAlerta("Éxito", "Ruta eliminada correctamente");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnEliminar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().add(botones);

      Scene scene = new Scene(root, 800, 350);
      ventana.setScene(scene);
      ventana.show();
   }

   // ==================== VENTANA VER GRAFO ====================

   private void mostrarVentanaVerGrafo() {
      Stage ventana = new Stage();
      ventana.setTitle("Visualización del Grafo");

      VBox root = new VBox(20);
      root.setAlignment(Pos.TOP_CENTER);
      root.setPadding(new Insets(20));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("ESTADO ACTUAL DEL SISTEMA");
      lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      TabPane tabPane = new TabPane();

      // Tab de Paradas
      Tab tabParadas = new Tab("Paradas");
      tabParadas.setClosable(false);

      TableView<Paradas> tablaParadas = new TableView<>();
      tablaParadas.setItems(listaParadas);

      TableColumn<Paradas, Integer> colId = new TableColumn<>("ID");
      colId.setCellValueFactory(new PropertyValueFactory<>("id"));
      colId.setPrefWidth(100);

      TableColumn<Paradas, String> colNombre = new TableColumn<>("Nombre");
      colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
      colNombre.setPrefWidth(300);

      tablaParadas.getColumns().addAll(colId, colNombre);
      tablaParadas.setPrefHeight(300);

      tabParadas.setContent(tablaParadas);

      // Tab de Rutas
      Tab tabRutas = new Tab("Rutas");
      tabRutas.setClosable(false);

      actualizarListaRutas();

      TableView<Rutas> tablaRutas = new TableView<>();
      tablaRutas.setItems(listaRutas);

      TableColumn<Rutas, String> colOrigen = new TableColumn<>("Origen");
      colOrigen.setCellValueFactory(cellData ->
              new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOrigen().getNombre()));
      colOrigen.setPrefWidth(150);

      TableColumn<Rutas, String> colDestino = new TableColumn<>("Destino");
      colDestino.setCellValueFactory(cellData ->
              new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDestino().getNombre()));
      colDestino.setPrefWidth(150);

      TableColumn<Rutas, Double> colTiempo = new TableColumn<>("Tiempo");
      colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
      colTiempo.setPrefWidth(100);

      TableColumn<Rutas, Double> colDistancia = new TableColumn<>("Distancia");
      colDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
      colDistancia.setPrefWidth(100);

      TableColumn<Rutas, Double> colCosto = new TableColumn<>("Costo");
      colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));
      colCosto.setPrefWidth(100);

      TableColumn<Rutas, Boolean> colTransbordo = new TableColumn<>("Transbordo");
      colTransbordo.setCellValueFactory(new PropertyValueFactory<>("transbordo"));
      colTransbordo.setPrefWidth(100);

      tablaRutas.getColumns().addAll(colOrigen, colDestino, colTiempo, colDistancia, colCosto, colTransbordo);
      tablaRutas.setPrefHeight(300);

      tabRutas.setContent(tablaRutas);

      tabPane.getTabs().addAll(tabParadas, tabRutas);

      Button btnCerrar = new Button("Cerrar");
      btnCerrar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCerrar.setOnAction(e -> ventana.close());

      root.getChildren().addAll(lblTitulo, tabPane, btnCerrar);

      Scene scene = new Scene(root, 900, 600);
      ventana.setScene(scene);
      ventana.show();
   }

   // ==================== FUNCIÓN DE BÚSQUEDA ====================

   private void buscarRuta(Paradas origen, Paradas destino, CriterioRuta criterio) {
      AlgoritmosGrafos.RutaResultado resultado = null;

      switch (criterio) {
         case TIEMPO:
            resultado = algoritmos.dijkstraTiempo(origen, destino);
            break;
         case DISTANCIA:
            resultado = algoritmos.dijkstraDistancia(origen, destino);
            break;
         case COSTO:
            resultado = algoritmos.dijkstraCosto(origen, destino);
            break;
      }

      if (resultado != null && resultado.isExitoso()) {
         mostrarResultadoRuta(resultado);
      } else {
         String mensaje = (resultado != null) ? resultado.getMensaje() : "No se pudo encontrar una ruta";
         mostrarAlerta("Ruta no encontrada", mensaje);
      }
   }

   private void mostrarResultadoRuta(AlgoritmosGrafos.RutaResultado resultado) {
      Stage ventana = new Stage();
      ventana.setTitle("Resultado de Búsqueda");

      VBox root = new VBox(20);
      root.setAlignment(Pos.TOP_CENTER);
      root.setPadding(new Insets(20));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label lblTitulo = new Label("RUTA ENCONTRADA");
      lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      // Mostrar el resultado usando el método imprimirRuta pero capturando en String
      TextArea txtResultado = new TextArea();
      txtResultado.setEditable(false);
      txtResultado.setPrefHeight(300);
      txtResultado.setStyle("-fx-font-size: 14px; -fx-font-family: monospace;");

      StringBuilder sb = new StringBuilder();
      sb.append("=== RUTA ENCONTRADA (por ").append(resultado.getCriterio()).append(") ===\n");
      sb.append("Total ").append(resultado.getCriterio()).append(": ").append(resultado.getTotal()).append("\n");
      sb.append("Recorrido:\n");

      for (int i = 0; i < resultado.getRutaParadas().size() - 1; i++) {
         Paradas desde = resultado.getRutaParadas().get(i);
         Paradas hasta = resultado.getRutaParadas().get(i + 1);
         Rutas ruta = resultado.getRutaRutas().get(i);

         sb.append(String.format("  %s -> %s | Tiempo: %.1f | Distancia: %.1f | Costo: %.2f | %s\n",
                 desde.getNombre(), hasta.getNombre(), ruta.getTiempo(),
                 ruta.getDistancia(), ruta.getCosto(),
                 ruta.isTransbordo() ? "CON TRANSBORDO" : "SIN TRANSBORDO"));
      }

      txtResultado.setText(sb.toString());

      Button btnCerrar = new Button("Cerrar");
      btnCerrar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCerrar.setOnAction(e -> ventana.close());

      root.getChildren().addAll(lblTitulo, txtResultado, btnCerrar);

      Scene scene = new Scene(root, 800, 500);
      ventana.setScene(scene);
      ventana.show();
   }

   // ==================== FUNCIONES AUXILIARES ====================

   private void inicializarDatosEjemplo() {
      // Crear algunas paradas de ejemplo
      Paradas p1 = new Paradas(nextIdParada++, "Centro");
      Paradas p2 = new Paradas(nextIdParada++, "Estación Norte");
      Paradas p3 = new Paradas(nextIdParada++, "Universidad");
      Paradas p4 = new Paradas(nextIdParada++, "Hospital");
      Paradas p5 = new Paradas(nextIdParada++, "Aeropuerto");

      sistema.agregarParada(p1);
      sistema.agregarParada(p2);
      sistema.agregarParada(p3);
      sistema.agregarParada(p4);
      sistema.agregarParada(p5);

      listaParadas.addAll(p1, p2, p3, p4, p5);

      // Crear algunas rutas de ejemplo
      sistema.agregarRuta(p1, p2, 15, 5.5, 2.5, false);
      sistema.agregarRuta(p2, p3, 10, 3.2, 1.8, false);
      sistema.agregarRuta(p1, p3, 30, 12.0, 5.0, true);
      sistema.agregarRuta(p3, p4, 8, 2.1, 1.2, false);
      sistema.agregarRuta(p4, p5, 25, 18.5, 8.5, false);
      sistema.agregarRuta(p2, p5, 40, 25.0, 12.0, true);

      actualizarListaRutas();
   }

   private void actualizarListaRutas() {
      listaRutas.clear();
      for (List<Rutas> rutas : sistema.getGrafo().values()) {
         listaRutas.addAll(rutas);
      }
   }

   private void mostrarAlerta(String titulo, String mensaje) {
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