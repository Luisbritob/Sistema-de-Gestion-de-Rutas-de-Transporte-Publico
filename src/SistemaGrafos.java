import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

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

   public List<Paradas> calcularMejorRuta(Paradas origen, Paradas destino, CriterioRuta criterio) {
      if (origen == null || destino == null) {
         System.out.println("Error: origen o destino no pueden ser null.");
         return new ArrayList<>();
      }

      if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) {
         System.out.println("Error: origen o destino no existen en el sistema.");
         return new ArrayList<>();
      }

      Map<Paradas, Double> distancias = new HashMap<>();
      Map<Paradas, Paradas> anteriores = new HashMap<>();
      PriorityQueue<Paradas> cola = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

      for (Paradas parada : grafo.keySet()) {
         distancias.put(parada, Double.MAX_VALUE);
      }

      distancias.put(origen, 0.0);
      cola.add(origen);

      while (!cola.isEmpty()) {
         Paradas actual = cola.poll();

         if (actual.equals(destino)) {
            break;
         }

         for (Rutas ruta : grafo.get(actual)) {
            Paradas vecino = ruta.getDestino();
            double nuevoPeso = distancias.get(actual) + ruta.getPesoSegunCriterio(criterio);

            if (nuevoPeso < distancias.get(vecino)) {
               distancias.put(vecino, nuevoPeso);
               anteriores.put(vecino, actual);

               cola.remove(vecino);
               cola.add(vecino);
            }
         }
      }

      List<Paradas> camino = new ArrayList<>();
      if (!anteriores.containsKey(destino) && !origen.equals(destino)) {
         return camino;
      }

      for (Paradas paradaActual = destino; paradaActual != null; paradaActual = anteriores.get(paradaActual)) {
         camino.add(0, paradaActual);
      }

      return camino;
   }

   public String obtenerResumenRuta(List<Paradas> camino) {
      if (camino == null || camino.isEmpty()) {
         return "No se encontró una ruta.";
      }

      double tiempoTotal = 0;
      double distanciaTotal = 0;
      double costoTotal = 0;
      boolean huboTransbordo = false;

      StringBuilder rutaTexto = new StringBuilder();

      for (int i = 0; i < camino.size(); i++) {
         rutaTexto.append(camino.get(i).getNombre());
         if (i < camino.size() - 1) {
            rutaTexto.append(" -> ");
         }
      }

      for (int i = 0; i < camino.size() - 1; i++) {
         Paradas origen = camino.get(i);
         Paradas destino = camino.get(i + 1);

         for (Rutas ruta : grafo.get(origen)) {
            if (ruta.getDestino().equals(destino)) {
               tiempoTotal += ruta.getTiempo();
               distanciaTotal += ruta.getDistancia();
               costoTotal += ruta.getCosto();
               if (ruta.isTransbordo()) {
                  huboTransbordo = true;
               }
               break;
            }
         }
      }

      return "Ruta: " + rutaTexto + "\n" +
              "Tiempo total: " + tiempoTotal + "\n" +
              "Distancia total: " + distanciaTotal + "\n" +
              "Costo total: " + costoTotal + "\n" +
              "Transbordo: " + (huboTransbordo ? "Sí" : "No");
   }

}
