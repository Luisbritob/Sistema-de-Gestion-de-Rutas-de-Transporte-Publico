import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaGrafos {
   
   private Map<Paradas, List<Rutas>> grafo;
   
   public SistemaGrafos() {
      grafo = new HashMap<>();
   }
   
   public void agregarParada(Paradas parada) {
      if (!grafo.containsKey(parada)) {
         grafo.put(parada, new ArrayList<>());
      }
   }
   
   public void agregarRuta(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, boolean transbordo) {
      if (grafo.containsKey(origen) && grafo.containsKey(destino)) {
         Rutas nuevaRuta = new Rutas(origen, destino, tiempo, distancia, costo, transbordo);
         grafo.get(origen).add(nuevaRuta);
      } else {
         System.out.println("Error: la parada de origen o destino no existe.");
      }
   }
   
   public void mostrarGrafo() {
      for (Paradas parada : grafo.keySet()) {
         System.out.println("Desde " + parada.getNombre() + ":");
         for (Rutas ruta : grafo.get(parada)) {
            System.out.println("   -> " + ruta);
         }
      }
   }
   
   public Map<Paradas, List<Rutas>> getGrafo() {
      return grafo;
   }
}
