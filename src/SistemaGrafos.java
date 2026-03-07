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

   public void eliminarRuta(Paradas origen, Paradas destino) {

      if (!grafo.containsKey(origen)) {
         System.out.println("Error: la parada de origen no existe.");
         return;
      }

      List<Rutas> rutas = grafo.get(origen);

      boolean eliminada = rutas.removeIf(r -> r.getDestino().equals(destino));

      if (eliminada) {
         System.out.println("Ruta eliminada correctamente.");
      } else {
         System.out.println("No existe una ruta entre esas paradas.");
      }
   }

   public void eliminarParada(Paradas parada) {

      if (!grafo.containsKey(parada)) {
         System.out.println("Error: la parada no existe.");
         return;
      }

      grafo.remove(parada);

      for (List<Rutas> rutas : grafo.values()) {
         rutas.removeIf(r -> r.getDestino().equals(parada));
      }

      System.out.println("Parada eliminada correctamente.");
   }

}
