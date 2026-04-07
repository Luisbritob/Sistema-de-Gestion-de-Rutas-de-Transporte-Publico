package Logico;

public class Rutas {
   private final Paradas origen;
   private final Paradas destino;
   private double tiempo;
   private double distancia;
   private double costo;
   private int transbordo;

   //Crea una ruta con sus atributos.
   public Rutas(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, int transbordo) {
      this.origen = origen;
      this.destino = destino;
      this.tiempo = tiempo;
      this.distancia = distancia;
      this.costo = costo;
      this.transbordo = transbordo;
   }

   //Setters y getters
   public Paradas getOrigen() {
      return origen;
   }
   public Paradas getDestino() {
      return destino;
   }
   public double getTiempo() {
      return tiempo;
   }
   public double getDistancia() {
      return distancia;
   }
   public double getCosto() {
      return costo;
   }
   public int getTransbordo() {
      return transbordo;
   }
   public void setTiempo(double tiempo) {
      this.tiempo = tiempo;
   }
   public void setDistancia(double distancia) {
      this.distancia = distancia;
   }
   public void setCosto(double costo) {
      this.costo = costo;
   }
   public void setTransbordo(int transbordo) {
      this.transbordo = transbordo;
   }

   //Retorna una cadena vacía para evitar salida no deseada
   @Override
   public String toString() {
      return "";
   }

   //Retorna el peso correspondiente según el criterio
   public double getPesoSegunCriterio(CriterioRuta criterio) {
      return switch (criterio) {
         case TIEMPO -> tiempo;
         case DISTANCIA -> distancia;
         case COSTO -> costo;
         case TRANSBORDO -> transbordo;
      };
   }
}