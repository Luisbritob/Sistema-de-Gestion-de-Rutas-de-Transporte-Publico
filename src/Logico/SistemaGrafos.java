package Logico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaGrafos {
   
   private Map<Paradas, List<Rutas>> grafo;

   //Inicializa el grafo y carga datos desde la base de datos.
   public SistemaGrafos() {
      grafo = new HashMap<>();
      cargarDatosDesdeDB();
   }

   //Carga todas las paradas y rutas desde la base de datos al grafo en memoria.
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

   //Limpia el grafo y recarga todos los datos desde la base de datos.
   public void recargarDatos() {
      grafo.clear();
      cargarDatosDesdeDB();
   }

   //Agrega una parada al grafo y la guarda en la base de datos.
   public void agregarParada(Paradas parada) {
      if (parada == null) return;
      if (grafo.containsKey(parada)) return;
      
      grafo.put(parada, new ArrayList<>());
      Database.guardarParada(parada);
   }

   //Elimina una parada del grafo y de la base de datos, recargando después.
   public void eliminarParada(Paradas parada) {
      if (!grafo.containsKey(parada)) return;
      
      grafo.remove(parada);
      Database.eliminarParada(parada);
      recargarDatos();
   }

   //Modifica una parada en memoria y en la base de datos.
   public void modificarParada(Paradas parada, String nuevoNombre, String nuevaLocalizacion) {
      if (!grafo.containsKey(parada)) return;
      
      parada.setNombre(nuevoNombre);
      parada.setLocalizacion(nuevaLocalizacion);
      Database.modificarParada(parada, nuevoNombre, nuevaLocalizacion);
   }

   //Agrega una ruta al grafo y la guarda en la base de datos.
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

   //Elimina una ruta del grafo y de la base de datos.
   public void eliminarRuta(Paradas origen, Paradas destino) {
      List<Rutas> rutas = grafo.get(origen);
      rutas.removeIf(r -> r.getDestino().equals(destino));
      Database.eliminarRuta(origen, destino);
   }

   //Modifica una ruta en memoria y en la base de datos.
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

   //Retorna el mapa que representa el grafo.
   public Map<Paradas, List<Rutas>> getGrafo() {
      return grafo;
   }
}