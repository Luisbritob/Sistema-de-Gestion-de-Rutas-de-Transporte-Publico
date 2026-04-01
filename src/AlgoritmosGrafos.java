import java.util.*;

public class AlgoritmosGrafos {
    
    private SistemaGrafos sistema;
    
    public AlgoritmosGrafos(SistemaGrafos sistema) {
        this.sistema = sistema;
    }
    
    public RutaResultado calcularMejorRuta(Paradas origen, Paradas destino, CriterioRuta criterio) {
        if (origen == null || destino == null) {
            return new RutaResultado(false, "Error: origen o destino no pueden ser null.");
        }
        
        Map<Paradas, List<Rutas>> grafo = sistema.getGrafo();
        if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) {
            return new RutaResultado(false, "Error: origen o destino no existen en el sistema.");
        }
        
        Map<Paradas, Double> distancias = new HashMap<>();
        Map<Paradas, Paradas> predecesores = new HashMap<>();
        Map<Paradas, Rutas> rutasUsadas = new HashMap<>();
        Set<Paradas> visitados = new HashSet<>();
        PriorityQueue<ParadaDistancia> cola = new PriorityQueue<>();
        
        for (Paradas parada : grafo.keySet()) {
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
            
            List<Rutas> rutas = grafo.get(paradaActual);
            if (rutas != null) {
                for (Rutas ruta : rutas) {
                    Paradas vecino = ruta.getDestino();
                    
                    if (visitados.contains(vecino)) {
                        continue;
                    }
                    
                    double peso = ruta.getPesoSegunCriterio(criterio);
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
                                             CriterioRuta criterio) {
        if (!distancias.containsKey(destino) || distancias.get(destino) == Double.MAX_VALUE) {
            return new RutaResultado(false, "No hay ruta disponible entre " + origen.getNombre() + " y " + destino.getNombre());
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
                return new RutaResultado(false, "Error interno: no se pudo reconstruir la ruta.");
            }
        }
        
        rutaParadas.add(0, origen);
        
        double totalAcumulado = distancias.get(destino);
        return new RutaResultado(true, rutaParadas, rutaRutas, totalAcumulado, criterio);
    }
    
    private static class ParadaDistancia implements Comparable<ParadaDistancia> {
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
        public int compareTo(ParadaDistancia otra) {
            return Double.compare(this.distancia, otra.distancia);
        }
    }
    
    public static class RutaResultado {
        private boolean exitoso;
        private String mensaje;
        private List<Paradas> rutaParadas;
        private List<Rutas> rutaRutas;
        private double total;
        private CriterioRuta criterio;
        
        public RutaResultado(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }
        
        public RutaResultado(boolean exitoso, List<Paradas> rutaParadas, List<Rutas> rutaRutas,
                             double total, CriterioRuta criterio) {
            this.exitoso = exitoso;
            this.rutaParadas = rutaParadas;
            this.rutaRutas = rutaRutas;
            this.total = total;
            this.criterio = criterio;
        }
        
        public String obtenerResumen() {
            if (!exitoso) {
                return "Error: " + mensaje;
            }
            
            StringBuilder resumen = new StringBuilder();
            resumen.append("Ruta encontrada por ").append(criterio).append(":\n");
            resumen.append("Total ").append(criterio.toString().toLowerCase()).append(": ").append(total).append("\n\n");
            resumen.append("Recorrido:\n");
            
            for (int i = 0; i < rutaParadas.size() - 1; i++) {
                Paradas desde = rutaParadas.get(i);
                Paradas hasta = rutaParadas.get(i + 1);
                Rutas ruta = rutaRutas.get(i);
                
                resumen.append(String.format(
                        "  %s -> %s | T: %.1f | D: %.1f | C: %.2f | Transbordos: %d\n",
                        desde.getNombre(),
                        hasta.getNombre(),
                        ruta.getTiempo(),
                        ruta.getDistancia(),
                        ruta.getCosto(),
                        ruta.getTransbordo()
                ));
            }
            
            return resumen.toString();
        }
        
        public boolean isExitoso() {
            return exitoso;
        }
        
        public String getMensaje() {
            return mensaje;
        }
    }
}