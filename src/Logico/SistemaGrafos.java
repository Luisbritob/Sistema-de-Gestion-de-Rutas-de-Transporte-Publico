package Logico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaGrafos {

   private Map<Paradas, List<Rutas>> grafo;

   public SistemaGrafos() {
      grafo = new HashMap<>();
      cargarDatosDesdeDB();
   }

   private void cargarDatosDesdeDB() {
      List<Paradas> paradas = Database.obtenerTodasParadas();
      for (Paradas p : paradas) {
         grafo.put(p, new ArrayList<>());
      }

      List<Rutas> rutas = Database.obtenerTodasRutas(grafo);
      for (Rutas r : rutas) {
         grafo.get(r.getOrigen()).add(r);
      }
   }

   public void recargarDatos() {
      grafo.clear();
      cargarDatosDesdeDB();
   }

   public void agregarParada(Paradas parada) {
      if (parada == null) return;
      if (grafo.containsKey(parada)) return;

      grafo.put(parada, new ArrayList<>());
      Database.guardarParada(parada);
   }

   public void eliminarParada(Paradas parada) {
      if (!grafo.containsKey(parada)) return;

      grafo.remove(parada);
      Database.eliminarParada(parada);
      recargarDatos();
   }

   public void modificarParada(Paradas parada, String nuevoNombre) {
      if (!grafo.containsKey(parada)) return;

      parada.setNombre(nuevoNombre);
      Database.modificarParada(parada, nuevoNombre);
   }
   
   public void agregarRuta(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, int transbordo) {
      if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) return;
      
      for (Rutas r : grafo.get(origen)) {
         if (r.getDestino().equals(destino)) {
            return;
         }
      }
      
      Rutas nuevaRuta = new Rutas(origen, destino, tiempo, distancia, costo, transbordo);
      grafo.get(origen).add(nuevaRuta);
      Database.guardarRuta(nuevaRuta);
   }

   public void eliminarRuta(Paradas origen, Paradas destino) {
      List<Rutas> rutas = grafo.get(origen);
      rutas.removeIf(r -> r.getDestino().equals(destino));
      Database.eliminarRuta(origen, destino);
   }

   public void modificarRuta(Paradas origen, Paradas destino, double nuevoTiempo, double nuevaDistancia, double nuevoCosto, int nuevoTransbordo) {
      List<Rutas> rutas = grafo.get(origen);
      for (Rutas r : rutas) {
         if (r.getDestino().equals(destino)) {
            r.setTiempo(nuevoTiempo);
            r.setDistancia(nuevaDistancia);
            r.setCosto(nuevoCosto);
            r.setTransbordo(nuevoTransbordo);
            Database.modificarRuta(r);
            break;
         }
      }
   }

   public Map<Paradas, List<Rutas>> getGrafo() {
      return grafo;
   }
}