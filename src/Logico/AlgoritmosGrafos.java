package Logico;

import java.util.*;

public class AlgoritmosGrafos {

    private SistemaGrafos sistema;

    public AlgoritmosGrafos(SistemaGrafos sistema) {
        this.sistema = sistema;
    }

    //Con Dijkstra encuentra la ruta óptima entre origen y destino usando PriorityQueue.
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

    //Encuentra la ruta óptima permitiendo pesos negativos.
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

    //Calcula la ruta óptima util para grafos densos
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

    //Eeconstruye la secuencia de paradas desde la matriz de "siguiente" generada por Floyd-Warshall
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

    //Busca y retorna la ruta directa entre dos paradas
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

    //Construye un objeto RutaResultado a partir de los mapas y rutas usadas generados por Dijkstra o Bellman-Ford.
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
}