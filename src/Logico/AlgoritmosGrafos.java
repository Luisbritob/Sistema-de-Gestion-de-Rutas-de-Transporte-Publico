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
    
    //Construye un objeto RutaResultado a partir de los mapas y rutas usadas generados por Dijkstra
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