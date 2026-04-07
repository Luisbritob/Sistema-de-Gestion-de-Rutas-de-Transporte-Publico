package Visual;

import Logico.*;
import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
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
   
   @FXML
   private HBox leyendaRutas;
   
   private final SistemaGrafos sistema = new SistemaGrafos();
   private final AlgoritmosGrafos algoritmos = new AlgoritmosGrafos(sistema);
   private final ObservableList<Paradas> listaParadas = FXCollections.observableArrayList();
   
   private SmartGraphPanel<Paradas, Rutas> graphView;
   
   /**
    * Inicializa la interfaz principal al cargar el controlador.
    * Aquí se cargan las paradas disponibles, se llenan los ComboBox,
    * se establecen valores por defecto para criterio y algoritmo,
    * se oculta la leyenda de rutas alternativas y se construye el grafo visual.
    */
   @FXML
   public void initialize() {
      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarCombos();
      
      comboCriterio.setItems(FXCollections.observableArrayList(CriterioRuta.values()));
      comboCriterio.setValue(CriterioRuta.TIEMPO);
      
      comboAlgoritmo.setItems(FXCollections.observableArrayList(AlgoritmoRuta.values()));
      comboAlgoritmo.setValue(AlgoritmoRuta.DIJKSTRA);
      
      if (leyendaRutas != null) {
         leyendaRutas.setVisible(false);
         leyendaRutas.setManaged(false);
      }
      
      areaResultado.setText("Selecciona origen, destino, criterio y algoritmo para buscar la mejor ruta.");
      
      inicializarSmartGraph();
   }
   
   /**
    * Construye el grafo visual a partir de la información almacenada en el sistema.
    * Inserta todas las paradas como vértices y todas las rutas como aristas.
    * Luego crea el panel visual del grafo, aplica el archivo CSS si existe,
    * lo coloca dentro del contenedor del mapa y finalmente inicializa la interacción
    * gráfica con JavaFX.
    */
   private void inicializarSmartGraph() {
      Digraph<Paradas, Rutas> digraph = new DigraphEdgeList<>();
      
      for (Paradas parada : sistema.getGrafo().keySet()) {
         digraph.insertVertex(parada);
      }
      
      for (Paradas origen : sistema.getGrafo().keySet()) {
         for (Rutas ruta : sistema.getGrafo().get(origen)) {
            digraph.insertEdge(origen, ruta.getDestino(), ruta);
         }
      }
      
      SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
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
      
      Platform.runLater(() -> {
         graphView.init();
         configurarInteraccionParadas();
      });
   }
   
   /**
    * Configura la interacción del usuario con las paradas del grafo.
    * Cuando el usuario hace doble clic sobre una parada,
    * se abre una ventana con opciones para modificar la parada,
    * eliminarla, agregar una ruta desde ella o eliminar una ruta existente.
    */
   private void configurarInteraccionParadas() {
      graphView.setVertexDoubleClickAction(graphVertex -> {
         Paradas parada = graphVertex.getUnderlyingVertex().element();
         
         Alert opciones = new Alert(Alert.AlertType.CONFIRMATION);
         opciones.setTitle("Opciones de parada");
         opciones.setHeaderText("Parada: " + parada.getNombre());
         opciones.setContentText("Selecciona una acción:");
         
         ButtonType btnModificar = new ButtonType("Modificar parada");
         ButtonType btnEliminar = new ButtonType("Eliminar parada");
         ButtonType btnAgregarRuta = new ButtonType("Agregar ruta");
         ButtonType btnModificarRuta = new ButtonType("Modificar ruta");
         ButtonType btnEliminarRuta = new ButtonType("Eliminar ruta");
         ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
         
         opciones.getButtonTypes().setAll(
                 btnModificar,
                 btnEliminar,
                 btnAgregarRuta,
                 btnModificarRuta,
                 btnEliminarRuta,
                 btnCancelar
         );
         
         opciones.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnModificar) {
               mostrarVentanaModificarParada(parada);
            } else if (respuesta == btnEliminar) {
               mostrarVentanaEliminarParada(parada);
            } else if (respuesta == btnAgregarRuta) {
               mostrarVentanaAgregarRutaDesde(parada);
            } else if (respuesta == btnModificarRuta) {
                  mostrarVentanaModificarRuta(parada);
            } else if (respuesta == btnEliminarRuta) {
               mostrarVentanaEliminarRutaDesde(parada);
            }
         });
      });
   }
   
   /**
    * Muestra una ventana para editar los datos de una parada existente.
    * Permite cambiar el nombre y la localización de la parada seleccionada.
    * Antes de guardar, valida que ambos campos no estén vacíos.
    * Si la modificación es correcta, actualiza el sistema y refresca el grafo
    * sin reconstruir completamente la visualización.
    *
    * @param paradaSeleccionada parada que el usuario desea modificar.
    */
   private void mostrarVentanaModificarParada(Paradas paradaSeleccionada) {
      Stage ventana = new Stage();
      ventana.setTitle("Modificar Parada");
      
      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("MODIFICAR PARADA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      Label lblParada = new Label("Parada: " + paradaSeleccionada.getNombre());
      lblParada.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      
      TextField txtNuevoNombre = new TextField(paradaSeleccionada.getNombre());
      txtNuevoNombre.setPromptText("Nuevo nombre de la parada");
      txtNuevoNombre.setPrefWidth(300);
      
      TextField txtNuevaLocalizacion = new TextField(paradaSeleccionada.getLocalizacion());
      txtNuevaLocalizacion.setPromptText("Nueva localización de la parada");
      txtNuevaLocalizacion.setPrefWidth(300);
      
      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnModificar.setOnAction(_ -> {
         String nuevoNombre = txtNuevoNombre.getText().trim();
         String nuevaLocalizacion = txtNuevaLocalizacion.getText().trim();
         
         if (nuevoNombre.isEmpty() || nuevaLocalizacion.isEmpty()) {
            mostrarAlerta("Error", "El nuevo nombre y la nueva localización no pueden estar vacíos.");
            return;
         }
         
         sistema.modificarParada(paradaSeleccionada, nuevoNombre, nuevaLocalizacion);
         refrescarSinReiniciarGrafo();
         
         areaResultado.setText("Parada modificada correctamente:\nNuevo nombre: " + nuevoNombre +
                 "\nNueva localización: " + nuevaLocalizacion);
         mostrarAlerta("Éxito", "Parada modificada correctamente.");
         ventana.close();
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(_ -> ventana.close());
      
      HBox botones = new HBox(15, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(lblTitulo, lblParada, txtNuevoNombre, txtNuevaLocalizacion, botones);
      
      Scene scene = new Scene(root, 450, 350);
      ventana.setScene(scene);
      ventana.show();
   }
   
   private void mostrarVentanaModificarRuta(Paradas origenFijo) {
      Stage ventana = new Stage();
      ventana.setTitle("Modificar Ruta");
      
      VBox root = new VBox(15);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("MODIFICAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      Label lblOrigen = new Label("Origen: " + origenFijo.getNombre());
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      
      ComboBox<Paradas> comboDestinoVentana = new ComboBox<>(listaParadas);
      comboDestinoVentana.setPromptText("Selecciona destino");
      comboDestinoVentana.setPrefWidth(280);
      
      TextField txtTiempo = new TextField();
      txtTiempo.setPromptText("Nuevo tiempo");
      
      TextField txtDistancia = new TextField();
      txtDistancia.setPromptText("Nueva distancia");
      
      TextField txtCosto = new TextField();
      txtCosto.setPromptText("Nuevo costo");
      
      TextField txtTransbordos = new TextField();
      txtTransbordos.setPromptText("Nuevos transbordos");
      
      Button btnModificar = new Button("Modificar");
      btnModificar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
      
      btnModificar.setOnAction(_ -> {
         Paradas destino = comboDestinoVentana.getValue();
         
         if (destino == null) {
            mostrarAlerta("Error", "Debes seleccionar destino.");
            return;
         }
         
         if (origenFijo.equals(destino)) {
            mostrarAlerta("Error", "Origen y destino no pueden ser iguales.");
            return;
         }
         
         try {
            double tiempo = Double.parseDouble(txtTiempo.getText().trim());
            double distancia = Double.parseDouble(txtDistancia.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            int transbordos = Integer.parseInt(txtTransbordos.getText().trim());
            
            if (tiempo < 0 || distancia < 0 || costo < 0 || transbordos < 0) {
               mostrarAlerta("Error", "Los valores no pueden ser negativos.");
               return;
            }
            
            sistema.modificarRuta(origenFijo, destino, tiempo, distancia, costo, transbordos);
            
            refrescarSinReiniciarGrafo();
            
            areaResultado.setText("Ruta modificada:\n" +
                    origenFijo.getNombre() + " -> " + destino.getNombre());
            
            mostrarAlerta("Éxito", "Ruta modificada correctamente.");
            ventana.close();
            
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Todos los valores deben ser numéricos.");
         }
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnModificar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              lblOrigen,
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
   
   /**
    * Muestra una confirmación para eliminar una parada del sistema.
    * Si el usuario confirma, la parada se elimina tanto del sistema lógico
    * como de la interfaz. Además, limpia la selección si la parada eliminada
    * estaba escogida como origen o destino.
    */
   private void mostrarVentanaEliminarParada(Paradas paradaSeleccionada) {
      Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
      confirmacion.setTitle("Eliminar Parada");
      confirmacion.setHeaderText("¿Eliminar la parada?");
      confirmacion.setContentText(paradaSeleccionada.getNombre());
      
      confirmacion.showAndWait().ifPresent(respuesta -> {
         if (respuesta == ButtonType.OK) {
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
         }
      });
   }
   
   /**
    * Muestra una ventana para agregar una nueva ruta saliendo desde una parada fija.
    * El usuario selecciona el destino e introduce tiempo, distancia, costo y cantidad
    * de transbordos. Se valida que el destino exista, que no sea el mismo origen,
    * que los valores sean numéricos y que no sean negativos.
    */
   private void mostrarVentanaAgregarRutaDesde(Paradas origenFijo) {
      Stage ventana = new Stage();
      ventana.setTitle("Agregar Ruta");
      
      VBox root = new VBox(15);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("AGREGAR NUEVA RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      Label lblOrigen = new Label("Origen: " + origenFijo.getNombre());
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      
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
         Paradas destino = comboDestinoVentana.getValue();
         
         if (destino == null) {
            mostrarAlerta("Error", "Debes seleccionar destino.");
            return;
         }
         
         if (origenFijo.equals(destino)) {
            mostrarAlerta("Error", "El origen y el destino no pueden ser la misma parada.");
            return;
         }
         
         try {
            double tiempo = Double.parseDouble(txtTiempo.getText().trim());
            double distancia = Double.parseDouble(txtDistancia.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            int transbordos = Integer.parseInt(txtTransbordos.getText().trim());
            
            if (tiempo < 0 || distancia < 0 || costo < 0 || transbordos < 0) {
               mostrarAlerta("Error", "Los valores no pueden ser negativos.");
               return;
            }
            
            sistema.agregarRuta(origenFijo, destino, tiempo, distancia, costo, transbordos);
            refrescarSinReiniciarGrafo();
            
            mostrarAlerta("Éxito", "Ruta agregada correctamente.");
            ventana.close();
            
         } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Tiempo, distancia, costo y transbordos deben ser números válidos.");
         }
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(_ -> ventana.close());
      
      HBox botones = new HBox(15, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(
              lblTitulo,
              lblOrigen,
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
   
   /**
    * Muestra una ventana para eliminar una ruta que sale desde una parada fija.
    * El usuario elige la parada destino de la ruta que desea borrar.
    * Se valida que se haya escogido un destino válido y diferente del origen.
    * Luego se elimina la ruta y se actualiza el grafo visual.
    * @param origenFijo parada origen desde la cual se desea eliminar una ruta.
    */
   private void mostrarVentanaEliminarRutaDesde(Paradas origenFijo) {
      Stage ventana = new Stage();
      ventana.setTitle("Eliminar Ruta");
      
      VBox root = new VBox(20);
      root.setAlignment(Pos.CENTER);
      root.setPadding(new Insets(25));
      root.setStyle("-fx-background-color: #A7B3BF;");
      
      Label lblTitulo = new Label("ELIMINAR RUTA");
      lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F1C3F;");
      
      Label lblOrigen = new Label("Origen: " + origenFijo.getNombre());
      lblOrigen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
      
      ComboBox<Paradas> comboDestinoVentana = new ComboBox<>(listaParadas);
      comboDestinoVentana.setPromptText("Selecciona destino");
      comboDestinoVentana.setPrefWidth(300);
      
      Button btnEliminar = new Button("Eliminar");
      btnEliminar.setStyle("-fx-background-color: #0F1C3F; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnEliminar.setOnAction(e -> {
         Paradas destino = comboDestinoVentana.getValue();
         
         if (destino == null) {
            mostrarAlerta("Error", "Debes seleccionar destino.");
            return;
         }
         
         if (origenFijo.equals(destino)) {
            mostrarAlerta("Error", "Origen y destino no pueden ser la misma parada.");
            return;
         }
         
         sistema.eliminarRuta(origenFijo, destino);
         refrescarSinReiniciarGrafo();
         
         areaResultado.setText("Ruta eliminada correctamente:\n" +
                 origenFijo.getNombre() + " -> " + destino.getNombre());
         
         mostrarAlerta("Éxito", "Ruta eliminada correctamente.");
         ventana.close();
      });
      
      Button btnCancelar = new Button("Cancelar");
      btnCancelar.setStyle("-fx-background-color: #63666A; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30;");
      btnCancelar.setOnAction(e -> ventana.close());
      
      HBox botones = new HBox(15, btnEliminar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(lblTitulo, lblOrigen, comboDestinoVentana, botones);
      
      Scene scene = new Scene(root, 450, 260);
      ventana.setScene(scene);
      ventana.show();
   }
   
   /**
    * Refresca los datos mostrados en pantalla sin reconstruir completamente el grafo.
    * Esta opción es útil cuando la estructura general del grafo sigue siendo válida,
    * pero se necesita actualizar la lista de paradas y redibujar la visualización.
    */
   private void refrescarSinReiniciarGrafo() {
      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarCombos();
      
      if (graphView != null) {
         graphView.update();
      }
   }
   
   /**
    * Recarga los datos desde la base de datos y reconstruye por completo el grafo visual.
    * Se usa cuando la estructura del sistema cambia de manera importante,
    * por ejemplo al eliminar o agregar una parada, y se necesita reconstruir
    * la visualización desde cero.
    */
   private void refrescarDesdeDB() {
      sistema.recargarDatos();
      listaParadas.setAll(sistema.getGrafo().keySet());
      actualizarCombos();
      inicializarSmartGraph();
   }
   
   /**
    * Actualiza los ComboBox de origen y destino con la lista actual de paradas.
    * Esto mantiene sincronizada la interfaz con el estado real del sistema.
    */
   private void actualizarCombos() {
      comboOrigen.setItems(listaParadas);
      comboDestino.setItems(listaParadas);
   }
   
   /**
    * Muestra una alerta informativa reutilizable.
    */
   private void mostrarAlerta(String titulo, String mensaje) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(titulo);
      alert.setHeaderText(null);
      alert.setContentText(mensaje);
      alert.showAndWait();
   }
   
   /**
    * Calcula una ruta según el algoritmo seleccionado por el usuario.
    * Encapsula la lógica de selección del algoritmo para evitar repetir
    * condicionales en otros métodos del controlador.
    */
   private RutaResultado calcularSegunAlgoritmo(Paradas origen, Paradas destino, CriterioRuta criterio, AlgoritmoRuta algoritmo) {
      switch (algoritmo) {
         case BELLMAN_FORD:
            return algoritmos.calcularMejorRutaBellmanFord(origen, destino, criterio);
         case FLOYD_WARSHALL:
            return algoritmos.calcularMejorRutaFloydWarshall(origen, destino, criterio);
         case DIJKSTRA:
         default:
            return algoritmos.calcularMejorRuta(origen, destino, criterio);
      }
   }
   
   /**
    * Busca la mejor ruta entre dos paradas según el criterio y el algoritmo escogidos.
    * Primero valida que todos los datos requeridos estén seleccionados y que
    * origen y destino sean distintos. Luego calcula la ruta, muestra el resumen
    * en el área de resultados y resalta visualmente el camino encontrado en el mapa.
    */
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
      
      RutaResultado resultado = calcularSegunAlgoritmo(origen, destino, criterio, algoritmo);
      
      if (!resultado.isExitoso()) {
         areaResultado.setText(resultado.getMensaje());
         limpiarEstilosGrafo();
         
         if (leyendaRutas != null) {
            leyendaRutas.setVisible(false);
            leyendaRutas.setManaged(false);
         }
         return;
      }
      
      areaResultado.setText(
              "Algoritmo usado: " + algoritmo + "\n\n" +
                      resultado.obtenerResumen()
      );
      
      limpiarEstilosGrafo();
      resaltarRuta(resultado.getRutaParadas());
      resaltarOrigenDestino(origen, destino);
      
      if (leyendaRutas != null) {
         leyendaRutas.setVisible(false);
         leyendaRutas.setManaged(false);
      }
   }
   
   /**
    * Calcula y muestra la ruta principal junto con rutas alternativas.
    * La ruta principal se calcula usando el criterio seleccionado por el usuario,
    * mientras que las alternativas se calculan usando los demás criterios disponibles.
    * Además de mostrar la información textual, pinta cada ruta en el mapa con estilos
    * diferentes y habilita la leyenda correspondiente.
    */
   @FXML
   private void verRutasAlternativas() {
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
      
      RutaResultado principal = calcularSegunAlgoritmo(origen, destino, criterio, algoritmo);
      if (!principal.isExitoso()) {
         areaResultado.setText(principal.getMensaje());
         
         if (leyendaRutas != null) {
            leyendaRutas.setVisible(false);
            leyendaRutas.setManaged(false);
         }
         return;
      }
      
      mostrarRutasAlternativasEnMapa(origen, destino, criterio, algoritmo);
      
      areaResultado.setText(
              "Algoritmo usado: " + algoritmo + "\n" +
                      "Criterio principal: " + criterio + "\n\n" +
                      "=== RUTA PRINCIPAL ===\n\n" +
                      principal.obtenerResumen() +
                      "\n\n=== RUTAS ALTERNATIVAS ===\n\n" +
                      obtenerRutasAlternativas(origen, destino, algoritmo)
      );
      
      if (leyendaRutas != null) {
         leyendaRutas.setVisible(true);
         leyendaRutas.setManaged(true);
      }
   }
   
   /**
    * Restablece el estilo visual por defecto de todos los vértices y aristas del grafo.
    * Se usa antes de volver a pintar rutas nuevas para evitar que queden resaltados
    * de búsquedas anteriores.
    */
   private void limpiarEstilosGrafo() {
      if (graphView == null) return;
      
      for (Paradas p : sistema.getGrafo().keySet()) {
         graphView.getStylableVertex(p).setStyleClass("vertex");
      }
      
      for (Paradas origen : sistema.getGrafo().keySet()) {
         for (Rutas r : sistema.getGrafo().get(origen)) {
            graphView.getStylableEdge(r).setStyleClass("edge");
         }
      }
   }
   
   /**
    * Pinta una ruta cualquiera en el grafo con las clases CSS indicadas.
    * Este método es reutilizable para la ruta principal y para las rutas alternativas.
    * Aplica una clase a los vértices del camino y otra a las aristas entre paradas consecutivas.
    */
   private void pintarRuta(List<Paradas> camino, String claseVertex, String claseEdge) {
      if (camino == null || camino.isEmpty() || graphView == null) {
         return;
      }
      
      for (Paradas p : camino) {
         graphView.getStylableVertex(p).setStyleClass(claseVertex);
      }
      
      for (int i = 0; i < camino.size() - 1; i++) {
         Paradas a = camino.get(i);
         Paradas b = camino.get(i + 1);
         
         for (Rutas r : sistema.getGrafo().get(a)) {
            if (r.getDestino().equals(b)) {
               graphView.getStylableEdge(r).setStyleClass(claseEdge);
               break;
            }
         }
      }
   }
   
   /**
    * Muestra en el mapa la ruta principal y las rutas alternativas.
    * La ruta principal se dibuja con el estilo principal y las demás rutas,
    * calculadas con criterios distintos, se dibujan con estilos alternativos.
    * Finalmente, se vuelven a destacar visualmente el origen y el destino.
    */
   private void mostrarRutasAlternativasEnMapa(Paradas origen, Paradas destino, CriterioRuta criterioPrincipal, AlgoritmoRuta algoritmo) {
      limpiarEstilosGrafo();
      
      RutaResultado principal = calcularSegunAlgoritmo(origen, destino, criterioPrincipal, algoritmo);
      if (principal.isExitoso()) {
         pintarRuta(principal.getRutaParadas(), "myVertex", "myEdge");
      }
      
      if (criterioPrincipal != CriterioRuta.TIEMPO) {
         RutaResultado alt1 = calcularSegunAlgoritmo(origen, destino, CriterioRuta.TIEMPO, algoritmo);
         if (alt1.isExitoso()) {
            pintarRuta(alt1.getRutaParadas(), "altVertex1", "altEdge1");
         }
      }
      
      if (criterioPrincipal != CriterioRuta.COSTO) {
         RutaResultado alt2 = calcularSegunAlgoritmo(origen, destino, CriterioRuta.COSTO, algoritmo);
         if (alt2.isExitoso()) {
            pintarRuta(alt2.getRutaParadas(), "altVertex2", "altEdge2");
         }
      }
      
      if (criterioPrincipal != CriterioRuta.TRANSBORDO) {
         RutaResultado alt3 = calcularSegunAlgoritmo(origen, destino, CriterioRuta.TRANSBORDO, algoritmo);
         if (alt3.isExitoso()) {
            pintarRuta(alt3.getRutaParadas(), "altVertex3", "altEdge3");
         }
      }
      
      resaltarOrigenDestino(origen, destino);
   }
   
   /**
    * Resalta una ruta específica como la ruta principal.
    * Primero limpia los estilos existentes para evitar superposición visual.
    * Luego pinta las paradas del camino y las aristas entre ellas con las clases
    * principales definidas en el CSS.
    */
   private void resaltarRuta(List<Paradas> camino) {
      if (camino == null || camino.isEmpty() || graphView == null) {
         return;
      }
      
      limpiarEstilosGrafo();
      
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
   
   /**
    * Destaca visualmente la parada de origen y la de destino.
    * Esto ayuda al usuario a identificar rápidamente desde dónde inicia
    * y dónde termina la búsqueda realizada.
    */
   private void resaltarOrigenDestino(Paradas origen, Paradas destino) {
      if (graphView == null) return;
      
      if (origen != null) {
         graphView.getStylableVertex(origen).setStyleClass("origenVertex");
      }
      
      if (destino != null) {
         graphView.getStylableVertex(destino).setStyleClass("destinoVertex");
      }
   }
   
   /**
    * Genera un texto con las rutas calculadas para los distintos criterios.
    * Se usa para mostrar al usuario un resumen completo de las alternativas
    * por tiempo, costo y transbordo, usando el mismo algoritmo seleccionado.
    * @param origen parada inicial.
    * @param destino parada final.
    * @param algoritmo algoritmo con el que se evaluarán los criterios.
    * @return cadena de texto con el resumen de todas las rutas alternativas.
    */
   private String obtenerRutasAlternativas(Paradas origen, Paradas destino, AlgoritmoRuta algoritmo) {
      StringBuilder sb = new StringBuilder();
      
      CriterioRuta[] criterios = {
              CriterioRuta.TIEMPO,
              CriterioRuta.COSTO,
              CriterioRuta.TRANSBORDO
      };
      
      for (CriterioRuta criterioAlt : criterios) {
         RutaResultado resultadoAlt = calcularSegunAlgoritmo(origen, destino, criterioAlt, algoritmo);
         
         sb.append("=== Ruta por ").append(criterioAlt).append(" ===\n");
         
         if (resultadoAlt.isExitoso()) {
            sb.append(resultadoAlt.obtenerResumen()).append("\n\n");
         } else {
            sb.append(resultadoAlt.getMensaje()).append("\n\n");
         }
      }
      
      return sb.toString();
   }
   
   /**
    * Método conectado desde la vista para abrir la ventana de agregar parada.
    * Se deja separado para que el evento del botón en FXML sea simple
    * y delegue la lógica visual al método correspondiente.
    */
   @FXML
   private void agregarParada() {
      mostrarVentanaAgregarParada();
   }
   
   /**
    * Muestra una ventana para registrar una nueva parada en el sistema.
    * Solicita nombre y localización, valida que ambos campos estén completos,
    * crea la nueva instancia de Paradas con un ID generado desde la base de datos,
    * la agrega al sistema y reconstruye la interfaz para reflejar el cambio.
    */
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
      
      btnGuardar.setOnAction(_ -> {
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
      btnCancelar.setOnAction(_ -> ventana.close());
      
      HBox botones = new HBox(20, btnGuardar, btnCancelar);
      botones.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(lblTitulo, txtNombre, txtLocalizacion, botones);
      
      Scene scene = new Scene(root, 500, 360);
      ventana.setScene(scene);
      ventana.show();
   }
}
