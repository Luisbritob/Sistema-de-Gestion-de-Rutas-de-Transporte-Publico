package Logico;

public class Rutas {
   private Paradas origen;
   private Paradas destino;
   private double tiempo;
   private double distancia;
   private double costo;
   private int transbordo;
   
   public Rutas(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, int transbordo) {
      this.origen = origen;
      this.destino = destino;
      this.tiempo = tiempo;
      this.distancia = distancia;
      this.costo = costo;
      this.transbordo = transbordo;
   }
   
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
      this.transbordo = this.transbordo;
   }
   
   @Override
   public String toString() {
      return "";
   }
   
   public double getPesoSegunCriterio(CriterioRuta criterio) {
      switch (criterio) {
         case TIEMPO:
            return tiempo;
         case DISTANCIA:
            return distancia;
         case COSTO:
            return costo;
         case TRANSBORDO:
            return transbordo;
         default:
            return tiempo;
      }
   }
}