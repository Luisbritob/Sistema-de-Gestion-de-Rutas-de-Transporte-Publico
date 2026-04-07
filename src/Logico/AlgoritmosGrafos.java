package Logico;

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
    
    public RutaResultado calcularMejorRutaBellmanFord(Paradas origen, Paradas destino, CriterioRuta criterio) {
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
        
        for (Paradas parada : grafo.keySet()) {
            distancias.put(parada, Double.MAX_VALUE);
        }
        
        distancias.put(origen, 0.0);
        
        int cantidadVertices = grafo.size();
        
        for (int i = 0; i < cantidadVertices - 1; i++) {
            boolean huboCambio = false;
            
            for (Paradas u : grafo.keySet()) {
                if (distancias.get(u) == Double.MAX_VALUE) continue;
                
                for (Rutas ruta : grafo.get(u)) {
                    Paradas v = ruta.getDestino();
                    double peso = ruta.getPesoSegunCriterio(criterio);
                    double nuevaDistancia = distancias.get(u) + peso;
                    
                    if (nuevaDistancia < distancias.get(v)) {
                        distancias.put(v, nuevaDistancia);
                        predecesores.put(v, u);
                        rutasUsadas.put(v, ruta);
                        huboCambio = true;
                    }
                }
            }
            
            if (!huboCambio) {
                break;
            }
        }
        
        for (Paradas u : grafo.keySet()) {
            if (distancias.get(u) == Double.MAX_VALUE) continue;
            
            for (Rutas ruta : grafo.get(u)) {
                Paradas v = ruta.getDestino();
                double peso = ruta.getPesoSegunCriterio(criterio);
                
                if (distancias.get(u) + peso < distancias.get(v)) {
                    return new RutaResultado(false, "Se detectó un ciclo negativo en el grafo.");
                }
            }
        }
        
        return construirResultado(origen, destino, predecesores, rutasUsadas, distancias, criterio);
    }
    
    public RutaResultado calcularMejorRutaFloydWarshall(Paradas origen, Paradas destino, CriterioRuta criterio) {
        if (origen == null || destino == null) {
            return new RutaResultado(false, "Error: origen o destino no pueden ser null.");
        }
        
        Map<Paradas, List<Rutas>> grafo = sistema.getGrafo();
        if (!grafo.containsKey(origen) || !grafo.containsKey(destino)) {
            return new RutaResultado(false, "Error: origen o destino no existen en el sistema.");
        }
        
        List<Paradas> vertices = new ArrayList<>(grafo.keySet());
        int n = vertices.size();
        
        Map<Paradas, Integer> indiceParada = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indiceParada.put(vertices.get(i), i);
        }
        
        double[][] dist = new double[n][n];
        Integer[][] siguiente = new Integer[n][n];
        
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.MAX_VALUE);
            dist[i][i] = 0.0;
            siguiente[i][i] = i;
        }
        
        for (Paradas u : grafo.keySet()) {
            int i = indiceParada.get(u);
            for (Rutas ruta : grafo.get(u)) {
                Paradas v = ruta.getDestino();
                int j = indiceParada.get(v);
                
                double peso = ruta.getPesoSegunCriterio(criterio);
                
                if (peso < dist[i][j]) {
                    dist[i][j] = peso;
                    siguiente[i][j] = j;
                }
            }
        }
        
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == Double.MAX_VALUE) continue;
                
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Double.MAX_VALUE) continue;
                    
                    double nuevaDistancia = dist[i][k] + dist[k][j];
                    if (nuevaDistancia < dist[i][j]) {
                        dist[i][j] = nuevaDistancia;
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
        
        int origenIdx = indiceParada.get(origen);
        int destinoIdx = indiceParada.get(destino);
        
        if (siguiente[origenIdx][destinoIdx] == null) {
            return new RutaResultado(false, "No hay ruta disponible entre " + origen.getNombre() + " y " + destino.getNombre());
        }
        
        List<Paradas> rutaParadas = reconstruirRutaFloyd(vertices, siguiente, origenIdx, destinoIdx);
        if (rutaParadas.isEmpty()) {
            return new RutaResultado(false, "No se pudo reconstruir la ruta con Floyd-Warshall.");
        }
        
        List<Rutas> rutaRutas = new ArrayList<>();
        for (int i = 0; i < rutaParadas.size() - 1; i++) {
            Paradas desde = rutaParadas.get(i);
            Paradas hasta = rutaParadas.get(i + 1);
            
            Rutas rutaEncontrada = buscarRutaDirecta(desde, hasta);
            if (rutaEncontrada != null) {
                rutaRutas.add(rutaEncontrada);
            }
        }
        
        return new RutaResultado(true, rutaParadas, rutaRutas, dist[origenIdx][destinoIdx], criterio);
    }
    
    private List<Paradas> reconstruirRutaFloyd(List<Paradas> vertices, Integer[][] siguiente, int origenIdx, int destinoIdx) {
        List<Paradas> ruta = new ArrayList<>();
        
        if (siguiente[origenIdx][destinoIdx] == null) {
            return ruta;
        }
        
        int actual = origenIdx;
        ruta.add(vertices.get(actual));
        
        while (actual != destinoIdx) {
            actual = siguiente[actual][destinoIdx];
            if (actual == -1 || actual >= vertices.size()) {
                return new ArrayList<>();
            }
            ruta.add(vertices.get(actual));
        }
        
        return ruta;
    }
    
    private Rutas buscarRutaDirecta(Paradas origen, Paradas destino) {
        List<Rutas> rutas = sistema.getGrafo().get(origen);
        if (rutas == null) return null;
        
        for (Rutas r : rutas) {
            if (r.getDestino().equals(destino)) {
                return r;
            }
        }
        return null;
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
        
        public String obtenerResumen() {
            if (!exitoso) {
                return "Error: " + mensaje;
            }
            
            StringBuilder resumen = new StringBuilder();
            
            double tiempoTotal = 0;
            double distanciaTotal = 0;
            double costoTotal = 0;
            int transbordosTotales = 0;
            
            resumen.append("Ruta encontrada por ").append(criterio).append(":\n\n");
            
            resumen.append("Recorrido completo:\n");
            for (int i = 0; i < rutaParadas.size(); i++) {
                resumen.append(rutaParadas.get(i).getNombre());
                if (i < rutaParadas.size() - 1) {
                    resumen.append(" -> ");
                }
            }
            
            resumen.append("\n\nDetalle por tramo:\n");
            
            for (int i = 0; i < rutaRutas.size(); i++) {
                Rutas ruta = rutaRutas.get(i);
                Paradas desde = rutaParadas.get(i);
                Paradas hasta = rutaParadas.get(i + 1);
                
                tiempoTotal += ruta.getTiempo();
                distanciaTotal += ruta.getDistancia();
                costoTotal += ruta.getCosto();
                transbordosTotales += ruta.getTransbordo();
                
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
            
            resumen.append("\nTotales:\n");
            resumen.append(String.format("Tiempo total: %.1f\n", tiempoTotal));
            resumen.append(String.format("Distancia total: %.1f\n", distanciaTotal));
            resumen.append(String.format("Costo total: %.2f\n", costoTotal));
            resumen.append(String.format("Transbordos totales: %d\n", transbordosTotales));
            
            resumen.append("\nValor optimizado por ").append(criterio.toString().toLowerCase())
                    .append(": ").append(total);
            
            return resumen.toString();
        }
    }
}