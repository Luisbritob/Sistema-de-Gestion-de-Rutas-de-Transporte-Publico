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
      if (parada == null) {
         System.out.println("Error: la parada no puede ser null.");
         return;
      }
      
      if (parada.getNombre() == null || parada.getNombre().trim().isEmpty()) {
         System.out.println("Error: el nombre de la parada no puede estar vacio.");
         return;
      }
      
      if (grafo.containsKey(parada)) {
         System.out.println("Error: ya existe una parada con ese ID.");
         return;
      }
      
      grafo.put(parada, new ArrayList<>());
      System.out.println("Parada agregada correctamente.");
   }
   
   public void eliminarParada(Paradas parada) {
      if (parada == null) {
         System.out.println("Error: la parada no puede ser null.");
         return;
      }
      
      if (!grafo.containsKey(parada)) {
         System.out.println("Error: la parada no existe.");
         return;
      }
      
      grafo.remove(parada);
      
      for (List<Rutas> rutas : grafo.values()) {
         rutas.removeIf(ruta -> ruta.getDestino().equals(parada));
      }
      
      System.out.println("Parada eliminada correctamente.");
   }
   
   public void modificarParada(Paradas parada, String nuevoNombre) {
      if (parada == null) {
         System.out.println("Error: la parada no puede ser null.");
         return;
      }
      
      if (!grafo.containsKey(parada)) {
         System.out.println("Error: la parada no existe.");
         return;
      }
      
      if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
         System.out.println("Error: el nuevo nombre no puede estar vacío.");
         return;
      }
      
      parada.setNombre(nuevoNombre);
      System.out.println("Parada modificada correctamente.");
   }
   
   public void agregarRuta(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, boolean transbordo) {
      if (origen == null || destino == null) {
         System.out.println("Error: el origen y destino no pueden ser null.");
         return;
      }
      
      if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) {
         System.out.println("Error: la parada de origen o destino no existe.");
         return;
      }
      
      if (origen.equals(destino)) {
         System.out.println("Error: una ruta no puede ir de una parada hacia sí misma.");
         return;
      }
      
      if (tiempo < 0 || distancia < 0 || costo < 0) {
         System.out.println("Error: tiempo, distancia y costo no pueden ser negativos.");
         return;
      }
      
      List<Rutas> rutas = grafo.get(origen);
      
      for (Rutas ruta : rutas) {
         if (ruta.getDestino().equals(destino)) {
            System.out.println("Error: ya existe una ruta desde "
                    + origen.getNombre() + " hasta " + destino.getNombre() + ".");
            return;
         }
      }
      
      Rutas nuevaRuta = new Rutas(origen, destino, tiempo, distancia, costo, transbordo);
      rutas.add(nuevaRuta);
      System.out.println("Ruta agregada correctamente.");
   }
   
   public void eliminarRuta(Paradas origen, Paradas destino) {
      if (origen == null || destino == null) {
         System.out.println("Error: el origen y destino no pueden ser null.");
         return;
      }
      
      if (!grafo.containsKey(origen)) {
         System.out.println("Error: la parada de origen no existe.");
         return;
      }
      
      List<Rutas> rutas = grafo.get(origen);
      boolean eliminada = rutas.removeIf(ruta -> ruta.getDestino().equals(destino));
      
      if (!eliminada) {
         System.out.println("Error: no existe una ruta desde "
                 + origen.getNombre() + " hasta " + destino.getNombre() + ".");
         return;
      }
      
      System.out.println("Ruta eliminada correctamente.");
   }
   
   public void modificarRuta(Paradas origen, Paradas destino, double nuevoTiempo, double nuevaDistancia, double nuevoCosto, boolean nuevoTransbordo) {
      if (origen == null || destino == null) {
         System.out.println("Error: el origen y destino no pueden ser null.");
         return;
      }
      
      if (!grafo.containsKey(origen)) {
         System.out.println("Error: la parada de origen no existe.");
         return;
      }
      
      if (nuevoTiempo < 0 || nuevaDistancia < 0 || nuevoCosto < 0) {
         System.out.println("Error: tiempo, distancia y costo no pueden ser negativos.");
         return;
      }
      
      List<Rutas> rutas = grafo.get(origen);
      
      for (Rutas ruta : rutas) {
         if (ruta.getDestino().equals(destino)) {
            ruta.setTiempo(nuevoTiempo);
            ruta.setDistancia(nuevaDistancia);
            ruta.setCosto(nuevoCosto);
            ruta.setTransbordo(nuevoTransbordo);
            
            System.out.println("Ruta modificada correctamente.");
            return;
         }
      }
      
      System.out.println("Error: no existe una ruta desde "
              + origen.getNombre() + " hasta " + destino.getNombre() + ".");
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
