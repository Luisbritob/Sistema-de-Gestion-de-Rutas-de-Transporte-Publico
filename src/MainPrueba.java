public class MainPrueba {
   public static void main(String[] args) {
      
      SistemaGrafos sistema = new SistemaGrafos();
      
      // Crear paradas
      Paradas p1 = new Paradas(1, "PUCMM");
      Paradas p2 = new Paradas(2, "Centro Olimpico");
      Paradas p3 = new Paradas(3, "UASD");
      Paradas p4 = new Paradas(4, "Agora Mall");
      
      // Agregar paradas
      sistema.agregarParada(p1);
      sistema.agregarParada(p2);
      sistema.agregarParada(p3);
      sistema.agregarParada(p4);
      
      System.out.println("\n--- GRAFO DESPUES DE AGREGAR PARADAS ---");
      sistema.mostrarGrafo();
      
      // Agregar rutas
      sistema.agregarRuta(p1, p2, 10, 5.5, 25, false);
      sistema.agregarRuta(p2, p3, 8, 4.0, 20, true);
      sistema.agregarRuta(p1, p4, 15, 7.2, 30, false);
      sistema.agregarRuta(p4, p3, 6, 3.5, 18, false);
      
      System.out.println("\n--- GRAFO DESPUES DE AGREGAR RUTAS ---");
      sistema.mostrarGrafo();
      
      // Modificar una ruta
      sistema.modificarRuta(p1, p2, 12, 6.0, 28, true);
      
      System.out.println("\n--- GRAFO DESPUES DE MODIFICAR RUTA P1 -> P2 ---");
      sistema.mostrarGrafo();
      
      // Modificar una parada
      sistema.modificarParada(p4, "BlueMall");
      
      System.out.println("\n--- GRAFO DESPUES DE MODIFICAR PARADA P4 ---");
      sistema.mostrarGrafo();
      
      // Eliminar una ruta
      sistema.eliminarRuta(p2, p3);
      
      System.out.println("\n--- GRAFO DESPUES DE ELIMINAR RUTA P2 -> P3 ---");
      sistema.mostrarGrafo();
      
      // Eliminar una parada
      sistema.eliminarParada(p3);
      
      System.out.println("\n--- GRAFO DESPUES DE ELIMINAR PARADA P3 ---");
      sistema.mostrarGrafo();
   }
}