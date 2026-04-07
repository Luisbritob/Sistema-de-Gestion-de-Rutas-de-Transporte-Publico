package Logico;

import java.util.ArrayList;
import java.util.List;


//Contiene la ruta encontrada y el criterio utilizado.
public class RutaResultado {

    private final boolean exitoso;
    private final String mensaje;
    private final List<Paradas> rutaParadas;
    private final List<Rutas> rutaRutas;
    private final double total;
    private final CriterioRuta criterio;

    // Constructor para resultados fallidos
    public RutaResultado(boolean exitoso, String mensaje) {
        this(exitoso, mensaje, new ArrayList<>(), new ArrayList<>(), 0.0, null);
    }

    // Constructor completo para resultados exitosos
    public RutaResultado(boolean exitoso, List<Paradas> rutaParadas,
                         List<Rutas> rutaRutas, double total,
                         CriterioRuta criterio) {
        this(exitoso, null, rutaParadas, rutaRutas, total, criterio);
    }

    // Constructor privado que centraliza la lógica
    private RutaResultado(boolean exitoso, String mensaje,
                          List<Paradas> rutaParadas, List<Rutas> rutaRutas,
                          double total, CriterioRuta criterio) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.rutaParadas = rutaParadas != null ? rutaParadas : new ArrayList<>();
        this.rutaRutas = rutaRutas != null ? rutaRutas : new ArrayList<>();
        this.total = total;
        this.criterio = criterio;
    }

    // Getters
    public boolean isExitoso() {
        return exitoso;
    }
    public String getMensaje() {
        return mensaje;
    }
    public List<Paradas> getRutaParadas() {
        return new ArrayList<>(rutaParadas); // Copia defensiva
    }
    public List<Rutas> getRutaRutas() {
        return new ArrayList<>(rutaRutas); // Copia defensiva
    }
    public double getTotal() {
        return total;
    }
    public CriterioRuta getCriterio() {
        return criterio;
    }

    //Genera un resumen detallado de la ruta encontrada
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
                    desde.getNombre(), hasta.getNombre(),
                    ruta.getTiempo(), ruta.getDistancia(),
                    ruta.getCosto(), ruta.getTransbordo()
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

    //Retorna el resultado
    @Override
    public String toString() {
        if (!exitoso) {
            return "RutaResultado{fallido='" + mensaje + "'}";
        }
        return String.format("RutaResultado{total=%.2f, paradas=%d, criterio=%s}",
                total, rutaParadas.size(), criterio);
    }
}