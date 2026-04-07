package Logico;

import java.util.Objects;

//Representa una parada con su distancia acumulada.
public class ParadaDistancia implements Comparable<ParadaDistancia> {

    private final Paradas parada;
    private final double distancia;

    public ParadaDistancia(Paradas parada, double distancia) {
        this.parada = Objects.requireNonNull(parada, "La parada no puede ser null");
        this.distancia = distancia;
    }

    public Paradas getParada() {
        return parada;
    }

    //Se usa Comparable para ordenar por distancia
    @Override
    public int compareTo(ParadaDistancia otra) {
        return Double.compare(this.distancia, otra.distancia);
    }

    //Compara dos objetos por parada y distancia.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ParadaDistancia that = (ParadaDistancia) obj;
        return Double.compare(that.distancia, distancia) == 0 &&
                Objects.equals(parada, that.parada);
    }

    //Retorna el hash code basado en parada y distancia.
    @Override
    public int hashCode() {
        return Objects.hash(parada, distancia);
    }

    //Retorna en formato
    @Override
    public String toString() {
        return String.format("%s (%.2f)", parada.getNombre(), distancia);
    }
}