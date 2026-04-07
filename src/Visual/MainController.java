package Visual;

import Logico.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;

public class MainController {

   @FXML
   private ComboBox<Paradas> comboOrigen;

   @FXML
   private ComboBox<Paradas> comboDestino;

   @FXML
   private ComboBox<CriterioRuta> comboCriterio;

   @FXML
   private ComboBox<AlgoritmoRuta> comboAlgoritmo;

   @FXML
   private TextArea areaResultado;

   @FXML
   private StackPane contenedorMapa;

   private final SistemaGrafos sistema = new SistemaGrafos();
   private final AlgoritmosGrafos algoritmos = new AlgoritmosGrafos(sistema);
   private RutaResultado ultimaRutaResultado;

   private final ObservableList<Paradas> listaParadas = FXCollections.observableArrayList();
   private final ObservableList<Rutas> listaRutas = FXCollections.observableArrayList();

   private Digraph<Paradas, Rutas> digraph;
   private SmartGraphPanel<Paradas, Rutas> graphView;

   //Inicializa combos, carga datos, configura el área de resultado e inicializa SmartGraph.
   @FXML
   public void initialize() {
      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarListaRutas();
      actualizarCombos();

      comboCriterio.setItems(FXCollections.observableArrayList(CriterioRuta.values()));
      comboCriterio.setValue(CriterioRuta.TIEMPO);

      comboAlgoritmo.setItems(FXCollections.observableArrayList(AlgoritmoRuta.values()));
      comboAlgoritmo.setValue(AlgoritmoRuta.DIJKSTRA);

      areaResultado.setText("Selecciona origen, destino, criterio y algoritmo para buscar la mejor ruta.");

      inicializarSmartGraph();
   }

   //Crea un DigraphEdgeList, añade vértices y aristas, aplica CSS y lo muestra en contenedorMapa.
   private void inicializarSmartGraph() {
      digraph = new DigraphEdgeList<>();

      for (Paradas parada : sistema.getGrafo().keySet()) {
         digraph.insertVertex(parada);
      }

      for (Paradas origen : sistema.getGrafo().keySet()) {
         for (Rutas ruta : sistema.getGrafo().get(origen)) {
            digraph.insertEdge(origen, ruta.getDestino(), ruta);
         }
      }

      SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
      graphView = new SmartGraphPanel<>(digraph, strategy);

      var cssUrl = getClass().getResource("/Recursos/smartgraph.css");
      if (cssUrl != null) {
         graphView.getStylesheets().clear();
         graphView.getStylesheets().add(cssUrl.toExternalForm());
         System.out.println("CSS cargado: " + cssUrl);
      } else {
         System.out.println("No se encontró /Recursos/smartgraph.css");
      }

      contenedorMapa.getChildren().clear();
      contenedorMapa.getChildren().add(graphView);

      Platform.runLater(() -> graphView.init());
   }

   //Recarga todos los datos desde la base de dato
   private void refrescarDesdeDB() {
      sistema.recargarDatos();

      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarListaRutas();
      actualizarCombos();

      inicializarSmartGraph();
   }

   //Actualiza los ComboBox de origen y destino con la lista actual de paradas
   private void actualizarCombos() {
      comboOrigen.setItems(listaParadas);
      comboDestino.setItems(listaParadas);
   }

   //Actualiza la lista observable de rutas recorriendo el grafo
   private void actualizarListaRutas() {
      listaRutas.clear();
      for (Paradas parada : sistema.getGrafo().keySet()) {
         listaRutas.addAll(sistema.getGrafo().get(parada));
      }
   }

   //Muestra una ventana de alerta con título y mensaje
   private void mostrarAlerta(String titulo, String mensaje) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(titulo);
      alert.setHeaderText(null);
      alert.setContentText(mensaje);
      alert.showAndWait();
   }

   //Abre el CRUD de gestión de rutas
   @FXML
   private void rutas() {
      mostrarVentanaRutas();
   }

   //Abre el CRUD de gestión de paradas
   @FXML
   private void paradas() {
      mostrarVentanaParadas();
   }

   //Obtiene origen, destino, criterio y algoritmo seleccionados y muestra el resultado en areaResultado
   @FXML
   private void buscarRuta() {
      Paradas origen = comboOrigen.getValue();
      Paradas destino = comboDestino.getValue();
      CriterioRuta criterio = comboCriterio.getValue();
      AlgoritmoRuta algoritmo = comboAlgoritmo.getValue();

      if (origen == null || destino == null || criterio == null || algoritmo == null) {
         areaResultado.setText("Debes seleccionar origen, destino, criterio y algoritmo.");
         return;
      }

      if (origen.equals(destino)) {
         areaResultado.setText("El origen y el destino no pueden ser la misma parada.");
         return;
      }

      RutaResultado resultado;

      switch (algoritmo) {
         case BELLMAN_FORD:
            resultado = algoritmos.calcularMejorRutaBellmanFord(origen, destino, criterio);
            break;
         case FLOYD_WARSHALL:
            resultado = algoritmos.calcularMejorRutaFloydWarshall(origen, destino, criterio);
            break;
         case DIJKSTRA:
         default:
            resultado = algoritmos.calcularMejorRuta(origen, destino, criterio);
            break;
      }

      if (!resultado.isExitoso()) {
         ultimaRutaResultado = null;
         areaResultado.setText(resultado.getMensaje());
         return;
      }

      ultimaRutaResultado = resultado;
      areaResultado.setText("Algoritmo usado: " + algoritmo + "\n\n" + resultado.obtenerResumen());

      resaltarRuta(resultado.getRutaParadas());
   }

   //Resalta visualmente la ruta encontrada en el grafo
   private void resaltarRuta(List<Paradas> camino) {
      if (camino == null || camino.isEmpty() || graphView == null) {
         return;
      }

      for (Paradas p : sistema.getGrafo().keySet()) {
         graphView.getStylableVertex(p).setStyleClass("vertex");
      }

      for (Paradas origen : sistema.getGrafo().keySet()) {
         for (Rutas r : sistema.getGrafo().get(origen)) {
            graphView.getStylableEdge(r).setStyleClass("edge");
         }
      }

      for (Paradas p : camino) {
         graphView.getStylableVertex(p).setStyleClass("myVertex");
      }

      for (int i = 0; i < camino.size() - 1; i++) {
         Paradas a = camino.get(i);
         Paradas b = camino.get(i + 1);

         for (Rutas r : sistema.getGrafo().get(a)) {
            if (r.getDestino().equals(b)) {
               graphView.getStylableEdge(r).setStyleClass("myEdge");
               break;
            }
         }
      }
   }

   //Crea y muestra ventana modal con opciones
   private void mostrarVentanaRutas() {
      Stage ventana = new Stage();
      ventana.setTitle("Opciones de Rutas");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label titulo = new Label("GESTIÓN DE RUTAS");
      titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      Button btnAgregar = new Button("Agregar ruta");
      btnAgregar.setPrefWidth(220);
      btnAgregar.setPrefHeight(45);
      btnAgregar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnAgregar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaAgregarRuta();
      });

      Button btnModificar = new Button("Modificar ruta");
      btnModificar.setPrefWidth(220);
      btnModificar.setPrefHeight(45);
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnModificar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaModificarRuta();
      });

      Button btnEliminar = new Button("Eliminar ruta");
      btnEliminar.setPrefWidth(220);
      btnEliminar.setPrefHeight(45);
      btnEliminar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnEliminar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaEliminarRuta();
      });

      Button btnCerrar = new Button("Cerrar");
      btnCerrar.setPrefWidth(220);
      btnCerrar.setPrefHeight(40);
      btnCerrar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
      btnCerrar.setOnAction(e -> ventana.close());

      root.getChildren().addAll(titulo, btnAgregar, btnModificar, btnEliminar, btnCerrar);

      Scene scene = new Scene(root, 380, 330);
      ventana.setScene(scene);
      ventana.show();
   }

   //Crea y muestra ventana modal con opciones
   private void mostrarVentanaParadas() {
      Stage ventana = new Stage();
      ventana.setTitle("Opciones de Paradas");

      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");

      Label titulo = new Label("GESTIÓN DE PARADAS");
      titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");

      Button btnAgregar = new Button("Agregar parada");
      btnAgregar.setPrefWidth(220);
      btnAgregar.setPrefHeight(45);
      btnAgregar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnAgregar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaAgregarParada();
      });

      Button btnModificar = new Button("Modificar parada");
      btnModificar.setPrefWidth(220);
      btnModificar.setPrefHeight(45);
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnModificar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaModificarParada();
      });

      Button btnEliminar = new Button("Eliminar parada");
      btnEliminar.setPrefWidth(220);
      btnEliminar.setPrefHeight(45);
      btnEliminar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      btnEliminar.setOnAction(e -> {
         ventana.close();
         mostrarVentanaEliminarParada();
      });

      Button btnCerrar = new Button("Cerrar");
      btnCerrar.setPrefWidth(220);
      btnCerrar.setPrefHeight(40);
      btnCerrar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
      btnCerrar.setOnAction(e -> ventana.close());

      root.getChildren().addAll(titulo, btnAgregar, btnModificar, btnEliminar, btnCerrar);

      Scene scene = new Scene(root, 380, 330);
      ventana.setScene(scene);
      ventana.show();
   }

   //Ventana con formulario para agregar nueva parada
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

      TextField txtLocalizacion = new TextField();
      txtLocalizacion.setPromptText("Localización de la parada");
      txtLocalizacion.setPrefWidth(300);
      txtLocalizacion.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

      Button btnGuardar = new Button("Guardar");
      btnGuardar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");

      btnGuardar.setOnAction(e -> {
         String nombre = txtNombre.getText().trim();
         String localizacion = txtLocalizacion.getText().trim();

         if (nombre.isEmpty() || localizacion.isEmpty()) {
            mostrarAlerta("Error", "El nombre y la localización no pueden estar vacíos.");
            return;
         }

         Paradas nuevaParada = new Paradas(
                 Database.obtenerSiguienteIdParada(),
                 nombre,
                 localizacion
         );

         sistema.agregarParada(nuevaParada);
         refrescarDesdeDB();

         mostrarAlerta("Éxito", "Parada agregada correctamente.");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(20, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().addAll(lblTitulo, txtNombre, txtLocalizacion, botones);

      Scene scene = new Scene(root, 500, 360);
      ventana.setScene(scene);
      ventana.show();
   }

   //Ventana con formulario para agregar nueva ruta
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
            refrescarDesdeDB();

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

   //Ventana con ComboBox para seleccionar y eliminar una parada
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

         if (comboOrigen.getValue() != null && comboOrigen.getValue().equals(paradaSeleccionada)) {
            comboOrigen.setValue(null);
         }

         if (comboDestino.getValue() != null && comboDestino.getValue().equals(paradaSeleccionada)) {
            comboDestino.setValue(null);
         }

         refrescarDesdeDB();

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

   //Ventana con ComboBoxes para seleccionar origen y destino y eliminar una ruta
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
         refrescarDesdeDB();

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

   //Ventana con ComboBox para seleccionar parada y campos para modificar nombre y localización
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

      TextField txtNuevaLocalizacion = new TextField();
      txtNuevaLocalizacion.setPromptText("Nueva localización de la parada");
      txtNuevaLocalizacion.setPrefWidth(300);

      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnModificar.setOnAction(e -> {
         Paradas paradaSeleccionada = comboParadas.getValue();
         String nuevoNombre = txtNuevoNombre.getText().trim();
         String nuevaLocalizacion = txtNuevaLocalizacion.getText().trim();

         if (paradaSeleccionada == null) {
            mostrarAlerta("Error", "Debes seleccionar una parada.");
            return;
         }

         if (nuevoNombre.isEmpty() || nuevaLocalizacion.isEmpty()) {
            mostrarAlerta("Error", "El nuevo nombre y la nueva localización no pueden estar vacíos.");
            return;
         }

         sistema.modificarParada(paradaSeleccionada, nuevoNombre, nuevaLocalizacion);
         refrescarDesdeDB();

         areaResultado.setText("Parada modificada correctamente:\nNuevo nombre: " + nuevoNombre +
                 "\nNueva localización: " + nuevaLocalizacion);
         mostrarAlerta("Éxito", "Parada modificada correctamente.");
         ventana.close();
      });

      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());

      HBox botones = new HBox(15, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);

      root.getChildren().addAll(lblTitulo, comboParadas, txtNuevoNombre, txtNuevaLocalizacion, botones);

      Scene scene = new Scene(root, 450, 380);
      ventana.setScene(scene);
      ventana.show();
   }

   //Ventana con ComboBoxes para seleccionar ruta y campos para modificar
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
            refrescarDesdeDB();

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
}