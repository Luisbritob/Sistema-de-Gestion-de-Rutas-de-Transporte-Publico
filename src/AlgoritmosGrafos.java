import java.util.*;

public class AlgoritmosGrafos {

    private SistemaGrafos sistema;

    public AlgoritmosGrafos(SistemaGrafos sistema) {
        this.sistema = sistema;
    }

    public RutaResultado dijkstraTiempo(Paradas origen, Paradas destino) {
        return dijkstra(origen, destino, "tiempo");
    }

    public RutaResultado dijkstraDistancia(Paradas origen, Paradas destino) {
        return dijkstra(origen, destino, "distancia");
    }

    public RutaResultado dijkstraCosto(Paradas origen, Paradas destino) {
        return dijkstra(origen, destino, "costo");
    }

    private RutaResultado dijkstra(Paradas origen, Paradas destino, String criterio) {
        Map<Paradas, Double> distancias = new HashMap<>();
        Map<Paradas, Paradas> predecesores = new HashMap<>();
        Map<Paradas, Rutas> rutasUsadas = new HashMap<>();
        Set<Paradas> visitados = new HashSet<>();
        PriorityQueue<ParadaDistancia> cola = new PriorityQueue<>();

        for (Paradas parada : sistema.getGrafo().keySet()) {
            distancias.put(parada, Double.MAX_VALUE);
        }
        distancias.put(origen, 0.0);
        cola.add(new ParadaDistancia(origen, 0.0));

        while (!cola.isEmpty()) {
            ParadaDistancia actual = cola.poll();
            Paradas paradaActual = actual.getParada();

            if (visitados.contains(paradaActual)) {
                continue;
            }

            visitados.add(paradaActual);

            if (paradaActual.equals(destino)) {
                break;
            }

            List<Rutas> rutas = sistema.getGrafo().get(paradaActual);
            if (rutas != null) {
                for (Rutas ruta : rutas) {
                    Paradas vecino = ruta.getDestino();
                    if (visitados.contains(vecino)) {
                        continue;
                    }

                    double peso = 0;
                    switch (criterio) {
                        case "tiempo":
                            peso = ruta.getTiempo();
                            break;
                        case "distancia":
                            peso = ruta.getDistancia();
                            break;
                        case "costo":
                            peso = ruta.getCosto();
                            break;
                    }

                    double nuevaDistancia = distancias.get(paradaActual) + peso;

                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        predecesores.put(vecino, paradaActual);
                        rutasUsadas.put(vecino, ruta);
                        cola.add(new ParadaDistancia(vecino, nuevaDistancia));
                    }
                }
            }
        }

        return construirResultado(origen, destino, predecesores, rutasUsadas, distancias, criterio);
    }

    private RutaResultado construirResultado(Paradas origen, Paradas destino,
                                             Map<Paradas, Paradas> predecesores,
                                             Map<Paradas, Rutas> rutasUsadas,
                                             Map<Paradas, Double> distancias,
                                             String criterio) {
        if (!distancias.containsKey(destino) || distancias.get(destino) == Double.MAX_VALUE) {
            return new RutaResultado(false, "No hay ruta disponible entre las paradas especificadas");
        }

        List<Paradas> rutaParadas = new ArrayList<>();
        List<Rutas> rutaRutas = new ArrayList<>();

        Paradas actual = destino;
        while (!actual.equals(origen)) {
            rutaParadas.add(0, actual);
            Rutas ruta = rutasUsadas.get(actual);
            if (ruta != null) {
                rutaRutas.add(0, ruta);
            }
            actual = predecesores.get(actual);
            if (actual == null) {
                return new RutaResultado(false, "Error al reconstruir la ruta");
            }
        }
        rutaParadas.add(0, origen);

        double total = distancias.get(destino);
        return new RutaResultado(true, rutaParadas, rutaRutas, total, criterio);
    }

    private class ParadaDistancia implements Comparable<ParadaDistancia> {
        private Paradas parada;
        private double distancia;

        public ParadaDistancia(Paradas parada, double distancia) {
            this.parada = parada;
            this.distancia = distancia;
        }

        public Paradas getParada() {
            return parada;
        }

        @Override
        public int compareTo(ParadaDistancia otraParada) {
            return Double.compare(this.distancia, otraParada.distancia);
        }
    }

    public static class RutaResultado {
        private boolean exitoso;
        private String mensaje;
        private List<Paradas> rutaParadas;
        private List<Rutas> rutaRutas;
        private double total;
        private String criterio;

        public RutaResultado(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public RutaResultado(boolean exitoso, List<Paradas> rutaParadas, List<Rutas> rutaRutas,
                             double total, String criterio) {
            this.exitoso = exitoso;
            this.rutaParadas = rutaParadas;
            this.rutaRutas = rutaRutas;
            this.total = total;
            this.criterio = criterio;
        }

        public void imprimirRuta() {
            if (!exitoso) {
                System.out.println("Error: " + mensaje);
                return;
            }

            System.out.println("\n=== RUTA ENCONTRADA (por " + criterio + ") ===");
            System.out.println("Total " + criterio + ": " + total);
            System.out.println("Recorrido:");

            for (int i = 0; i < rutaParadas.size() - 1; i++) {
                Paradas desde = rutaParadas.get(i);
                Paradas hasta = rutaParadas.get(i + 1);
                Rutas ruta = rutaRutas.get(i);

                System.out.printf("  %s -> %s | Tiempo: %.1f | Distancia: %.1f | Costo: %.2f | %s\n",
                        desde.getNombre(), hasta.getNombre(), ruta.getTiempo(),
                        ruta.getDistancia(), ruta.getCosto(),
                        ruta.isTransbordo() ? "CON TRANSBORDO" : "SIN TRANSBORDO");
            }

            System.out.println("====================================\n");
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public List<Paradas> getRutaParadas() {
            return rutaParadas;
        }

        public List<Rutas> getRutaRutas() {
            return rutaRutas;
        }

        public double getTotal() {
            return total;
        }
        public String getCriterio() {
            return criterio;
        }
    }
}