import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController {
   
   @FXML
   private ComboBox<Paradas> comboOrigen;
   
   @FXML
   private ComboBox<Paradas> comboDestino;
   
   @FXML
   private ComboBox<CriterioRuta> comboCriterio;
   
   @FXML
   private TextArea areaResultado;
   
   private SistemaGrafos sistema = new SistemaGrafos();
   private AlgoritmosGrafos algoritmos = new AlgoritmosGrafos(sistema);
   
   private ObservableList<Paradas> listaParadas = FXCollections.observableArrayList();
   private ObservableList<Rutas> listaRutas = FXCollections.observableArrayList();
   
   private int nextIdParada = 1;
   
   @FXML
   public void initialize() {
      inicializarDatosEjemplo();
      actualizarCombos();
      
      comboCriterio.setItems(FXCollections.observableArrayList(CriterioRuta.values()));
      comboCriterio.setValue(CriterioRuta.TIEMPO);
      
      areaResultado.setText("Selecciona un origen, destino y criterio para buscar la mejor ruta.");
   }
   
   private void inicializarDatosEjemplo() {
      Paradas p1 = new Paradas(nextIdParada++, "PUCMM");
      Paradas p2 = new Paradas(nextIdParada++, "Centro Olímpico");
      Paradas p3 = new Paradas(nextIdParada++, "UASD");
      Paradas p4 = new Paradas(nextIdParada++, "Ágora Mall");
      Paradas p5 = new Paradas(nextIdParada++, "BlueMall");
      
      sistema.agregarParada(p1);
      sistema.agregarParada(p2);
      sistema.agregarParada(p3);
      sistema.agregarParada(p4);
      sistema.agregarParada(p5);
      
      sistema.agregarRuta(p1, p2, 5, 9, 45, 0);
      sistema.agregarRuta(p2, p3, 5, 9, 45, 1);
      
      sistema.agregarRuta(p1, p4, 10, 3, 30, 0);
      sistema.agregarRuta(p4, p3, 10, 3, 30, 0);
      
      sistema.agregarRuta(p1, p5, 8, 7, 8, 0);
      sistema.agregarRuta(p5, p3, 8, 7, 8, 1);
      
      sistema.agregarRuta(p2, p4, 4, 2, 12, 0);
      sistema.agregarRuta(p4, p5, 3, 2, 6, 0);
      sistema.agregarRuta(p5, p4, 3, 2, 6, 0);
      
      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarListaRutas();
   }
   
   private void actualizarCombos() {
      comboOrigen.setItems(listaParadas);
      comboDestino.setItems(listaParadas);
   }
   
   private void actualizarListaRutas() {
      listaRutas.clear();
      for (Paradas parada : sistema.getGrafo().keySet()) {
         listaRutas.addAll(sistema.getGrafo().get(parada));
      }
   }
   
   @FXML
   private void buscarRuta() {
      Paradas origen = comboOrigen.getValue();
      Paradas destino = comboDestino.getValue();
      CriterioRuta criterio = comboCriterio.getValue();
      
      if (origen == null || destino == null || criterio == null) {
         areaResultado.setText("Debes seleccionar origen, destino y criterio.");
         return;
      }
      
      if (origen.equals(destino)) {
         areaResultado.setText("El origen y el destino no pueden ser la misma parada.");
         return;
      }
      
      AlgoritmosGrafos.RutaResultado resultado = algoritmos.calcularMejorRuta(origen, destino, criterio);
      
      if (!resultado.isExitoso()) {
         areaResultado.setText(resultado.getMensaje());
         return;
      }
      
      areaResultado.setText(resultado.obtenerResumen());
   }
   
   private void mostrarAlerta(String titulo, String mensaje) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(titulo);
      alert.setHeaderText(null);
      alert.setContentText(mensaje);
      alert.showAndWait();
   }
   
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
         actualizarCombos();
         
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
      
      VBox root = new VBox(15);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("AGREGAR NUEVA RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      ComboBox<Paradas> comboOrigenVentana = new ComboBox<>(listaParadas);
      comboOrigenVentana.setPromptText("Selecciona origen");
      comboOrigenVentana.setPrefWidth(280);
      
      ComboBox<Paradas> comboDestinoVentana = new ComboBox<>(listaParadas);
      comboDestinoVentana.setPromptText("Selecciona destino");
      comboDestinoVentana.setPrefWidth(280);
      
      TextField txtTiempo = new TextField();
      txtTiempo.setPromptText("Tiempo");
      txtTiempo.setPrefWidth(280);
      
      TextField txtDistancia = new TextField();
      txtDistancia.setPromptText("Distancia");
      txtDistancia.setPrefWidth(280);
      
      TextField txtCosto = new TextField();
      txtCosto.setPromptText("Costo");
      txtCosto.setPrefWidth(280);
      
      TextField txtTransbordos = new TextField();
      txtTransbordos.setPromptText("Cantidad de transbordos");
      txtTransbordos.setPrefWidth(280);
      
      Button btnGuardar = new Button("Guardar");
      btnGuardar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnGuardar.setOnAction(e -> {
         Paradas origen = comboOrigenVentana.getValue();
         Paradas destino = comboDestinoVentana.getValue();
         
         if (origen == null || destino == null) {
            mostrarAlerta("Error", "Debes seleccionar origen y destino.");
            return;
         }
         
         if (origen.equals(destino)) {
            mostrarAlerta("Error", "El origen y el destino no pueden ser la misma parada.");
            return;
         }
         
         try {
            double tiempo = Double.parseDouble(txtTiempo.getText().trim());
            double distancia = Double.parseDouble(txtDistancia.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            int transbordos = Integer.parseInt(txtTransbordos.getText().trim());
            
            if (tiempo < 0 || distancia < 0 || costo < 0) {
               mostrarAlerta("Error", "Tiempo, distancia y costo no pueden ser negativos.");
               return;
            }
            
            if (transbordos < 0) {
               mostrarAlerta("Error", "Los transbordos no pueden ser negativos.");
               return;
            }
            
            sistema.agregarRuta(origen, destino, tiempo, distancia, costo, transbordos);
            actualizarListaRutas();
            
            mostrarAlerta("Éxito", "Ruta agregada correctamente.");
            ventana.close();
            
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Tiempo, distancia, costo y transbordos deben ser números válidos.");
         }
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              comboOrigenVentana,
              comboDestinoVentana,
              txtTiempo,
              txtDistancia,
              txtCosto,
              txtTransbordos,
              botones
      );
      
      Scene scene = new Scene(root, 450, 500);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaVerGrafo() {
      Stage ventana = new Stage();
      ventana.setTitle("Ver Grafo");
      
      VBox root = new VBox(20);
      root.setPadding(new Insets(20));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label titulo = new Label("VISUALIZACIÓN DEL GRAFO");
      titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      Label lblParadas = new Label("Paradas");
      lblParadas.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      TableView<Paradas> tablaParadas = new TableView<>();
      tablaParadas.setItems(listaParadas);
      tablaParadas.setPrefHeight(200);
      
      TableColumn<Paradas, Integer> colIdParada = new TableColumn<>("ID");
      colIdParada.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
      colIdParada.setPrefWidth(100);
      
      TableColumn<Paradas, String> colNombreParada = new TableColumn<>("Nombre");
      colNombreParada.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
      colNombreParada.setPrefWidth(250);
      
      tablaParadas.getColumns().addAll(colIdParada, colNombreParada);
      
      Label lblRutas = new Label("Rutas");
      lblRutas.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      TableView<Rutas> tablaRutas = new TableView<>();
      tablaRutas.setItems(listaRutas);
      tablaRutas.setPrefHeight(250);
      
      TableColumn<Rutas, String> colOrigen = new TableColumn<>("Origen");
      colOrigen.setCellValueFactory(cellData ->
              new javafx.beans.property.SimpleStringProperty(
                      cellData.getValue().getOrigen().getNombre()
              ));
      colOrigen.setPrefWidth(150);
      
      TableColumn<Rutas, String> colDestino = new TableColumn<>("Destino");
      colDestino.setCellValueFactory(cellData ->
              new javafx.beans.property.SimpleStringProperty(
                      cellData.getValue().getDestino().getNombre()
              ));
      colDestino.setPrefWidth(150);
      
      TableColumn<Rutas, Double> colTiempo = new TableColumn<>("Tiempo");
      colTiempo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tiempo"));
      colTiempo.setPrefWidth(100);
      
      TableColumn<Rutas, Double> colDistancia = new TableColumn<>("Distancia");
      colDistancia.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("distancia"));
      colDistancia.setPrefWidth(100);
      
      TableColumn<Rutas, Double> colCosto = new TableColumn<>("Costo");
      colCosto.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("costo"));
      colCosto.setPrefWidth(100);
      
      TableColumn<Rutas, Integer> colTransbordos = new TableColumn<>("Transbordos");
      colTransbordos.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transbordos"));
      colTransbordos.setPrefWidth(120);
      
      tablaRutas.getColumns().addAll(
              colOrigen, colDestino, colTiempo, colDistancia, colCosto, colTransbordos
      );
      
      Button btnCerrar = new Button("Cerrar");
      btnCerrar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCerrar.setOnAction(e -> ventana.close());
      
      HBox cajaBoton = new HBox(btnCerrar);
      cajaBoton.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              titulo,
              lblParadas,
              tablaParadas,
              lblRutas,
              tablaRutas,
              cajaBoton
      );
      
      Scene scene = new Scene(root, 750, 650);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaEliminarParada() {
      Stage ventana = new Stage();
      ventana.setTitle("Eliminar Parada");
      
      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("ELIMINAR PARADA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      ComboBox<Paradas> comboParadas = new ComboBox<>(listaParadas);
      comboParadas.setPromptText("Selecciona la parada a eliminar");
      comboParadas.setPrefWidth(300);
      
      Button btnEliminar = new Button("Eliminar");
      btnEliminar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnEliminar.setOnAction(e -> {
         Paradas paradaSeleccionada = comboParadas.getValue();
         
         if (paradaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una parada.");
            return;
         }
         
         sistema.eliminarParada(paradaSeleccionada);
         listaParadas.remove(paradaSeleccionada);
         
         actualizarListaRutas();
         actualizarCombos();
         
         if (comboOrigen.getValue() != null && comboOrigen.getValue().equals(paradaSeleccionada)) {
            comboOrigen.setValue(null);
         }
         
         if (comboDestino.getValue() != null && comboDestino.getValue().equals(paradaSeleccionada)) {
            comboDestino.setValue(null);
         }
         
         areaResultado.setText("Parada eliminada correctamente: " + paradaSeleccionada.getNombre());
         mostrarAlerta("Éxito", "Parada eliminada correctamente.");
         ventana.close();
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnEliminar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(lblTitulo, comboParadas, botones);
      
      Scene scene = new Scene(root, 450, 250);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaEliminarRuta() {
      Stage ventana = new Stage();
      ventana.setTitle("Eliminar Ruta");
      
      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("ELIMINAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      ComboBox<Paradas> comboOrigenVentana = new ComboBox<>(listaParadas);
      comboOrigenVentana.setPromptText("Selecciona origen");
      comboOrigenVentana.setPrefWidth(300);
      
      ComboBox<Paradas> comboDestinoVentana = new ComboBox<>(listaParadas);
      comboDestinoVentana.setPromptText("Selecciona destino");
      comboDestinoVentana.setPrefWidth(300);
      
      Button btnEliminar = new Button("Eliminar");
      btnEliminar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnEliminar.setOnAction(e -> {
         Paradas origen = comboOrigenVentana.getValue();
         Paradas destino = comboDestinoVentana.getValue();
         
         if (origen == null || destino == null) {
            mostrarAlerta("Error", "Debes seleccionar origen y destino.");
            return;
         }
         
         if (origen.equals(destino)) {
            mostrarAlerta("Error", "Origen y destino no pueden ser la misma parada.");
            return;
         }
         
         sistema.eliminarRuta(origen, destino);
         actualizarListaRutas();
         
         areaResultado.setText("Ruta eliminada correctamente:\n" +
                 origen.getNombre() + " -> " + destino.getNombre());
         
         mostrarAlerta("Éxito", "Ruta eliminada correctamente.");
         ventana.close();
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnEliminar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              comboOrigenVentana,
              comboDestinoVentana,
              botones
      );
      
      Scene scene = new Scene(root, 450, 300);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaModificarParada() {
      Stage ventana = new Stage();
      ventana.setTitle("Modificar Parada");
      
      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("MODIFICAR PARADA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      ComboBox<Paradas> comboParadas = new ComboBox<>(listaParadas);
      comboParadas.setPromptText("Selecciona la parada");
      comboParadas.setPrefWidth(300);
      
      TextField txtNuevoNombre = new TextField();
      txtNuevoNombre.setPromptText("Nuevo nombre de la parada");
      txtNuevoNombre.setPrefWidth(300);
      
      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnModificar.setOnAction(e -> {
         Paradas paradaSeleccionada = comboParadas.getValue();
         String nuevoNombre = txtNuevoNombre.getText().trim();
         
         if (paradaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una parada.");
            return;
         }
         
         if (nuevoNombre.isEmpty()) {
            mostrarAlerta("Error", "El nuevo nombre no puede estar vacío.");
            return;
         }
         
         sistema.modificarParada(paradaSeleccionada, nuevoNombre);
         
         actualizarCombos();
         actualizarListaRutas();
         
         areaResultado.setText("Parada modificada correctamente:\n" +
                 "Nuevo nombre: " + nuevoNombre);
         
         mostrarAlerta("Éxito", "Parada modificada correctamente.");
         ventana.close();
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              comboParadas,
              txtNuevoNombre,
              botones
      );
      
      Scene scene = new Scene(root, 450, 320);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaModificarRuta() {
      Stage ventana = new Stage();
      ventana.setTitle("Modificar Ruta");
      
      VBox root = new VBox(15);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("MODIFICAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      ComboBox<Paradas> comboOrigenVentana = new ComboBox<>(listaParadas);
      comboOrigenVentana.setPromptText("Selecciona origen");
      comboOrigenVentana.setPrefWidth(280);
      
      ComboBox<Paradas> comboDestinoVentana = new ComboBox<>(listaParadas);
      comboDestinoVentana.setPromptText("Selecciona destino");
      comboDestinoVentana.setPrefWidth(280);
      
      TextField txtTiempo = new TextField();
      txtTiempo.setPromptText("Nuevo tiempo");
      txtTiempo.setPrefWidth(280);
      
      TextField txtDistancia = new TextField();
      txtDistancia.setPromptText("Nueva distancia");
      txtDistancia.setPrefWidth(280);
      
      TextField txtCosto = new TextField();
      txtCosto.setPromptText("Nuevo costo");
      txtCosto.setPrefWidth(280);
      
      TextField txtTransbordos = new TextField();
      txtTransbordos.setPromptText("Nueva cantidad de transbordos");
      txtTransbordos.setPrefWidth(280);
      
      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnModificar.setOnAction(e -> {
         Paradas origen = comboOrigenVentana.getValue();
         Paradas destino = comboDestinoVentana.getValue();
         
         if (origen == null || destino == null) {
            mostrarAlerta("Error", "Debes seleccionar origen y destino.");
            return;
         }
         
         if (origen.equals(destino)) {
            mostrarAlerta("Error", "Origen y destino no pueden ser la misma parada.");
            return;
         }
         
         try {
            double nuevoTiempo = Double.parseDouble(txtTiempo.getText().trim());
            double nuevaDistancia = Double.parseDouble(txtDistancia.getText().trim());
            double nuevoCosto = Double.parseDouble(txtCosto.getText().trim());
            int nuevosTransbordos = Integer.parseInt(txtTransbordos.getText().trim());
            
            if (nuevoTiempo < 0 || nuevaDistancia < 0 || nuevoCosto < 0) {
               mostrarAlerta("Error", "Tiempo, distancia y costo no pueden ser negativos.");
               return;
            }
            
            if (nuevosTransbordos < 0) {
               mostrarAlerta("Error", "Los transbordos no pueden ser negativos.");
               return;
            }
            
            sistema.modificarRuta(origen, destino, nuevoTiempo, nuevaDistancia, nuevoCosto, nuevosTransbordos);
            actualizarListaRutas();
            
            areaResultado.setText("Ruta modificada correctamente:\n" +
                    origen.getNombre() + " -> " + destino.getNombre());
            
            mostrarAlerta("Éxito", "Ruta modificada correctamente.");
            ventana.close();
            
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Tiempo, distancia, costo y transbordos deben ser números válidos.");
         }
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              comboOrigenVentana,
              comboDestinoVentana,
              txtTiempo,
              txtDistancia,
              txtCosto,
              txtTransbordos,
              botones
      );
      
      Scene scene = new Scene(root, 450, 500);
      ventana.setScene(scene);
      ventana.show();
   }
   
   @FXML
   private void agregarParada() {
      mostrarVentanaAgregarParada();
   }
   
   @FXML
   private void agregarRuta() {
      mostrarVentanaAgregarRuta();
   }
   
   @FXML
   private void modificarParada() {
      mostrarVentanaModificarParada();
   }
   
   @FXML
   private void modificarRuta() {
      mostrarVentanaModificarRuta();
   }
   
   @FXML
   private void eliminarParada() {
      mostrarVentanaEliminarParada();
   }
   
   @FXML
   private void eliminarRuta() {
      mostrarVentanaEliminarRuta();
   }
   
   @FXML
   private void verGrafo() {
      mostrarVentanaVerGrafo();
   }
}