public class Rutas {
   private Paradas origen;
   private Paradas destino;
   private double tiempo;
   private double distancia;
   private double costo;
   private boolean transbordo;
   
   public Rutas(Paradas origen, Paradas destino, double tiempo, double distancia, double costo, boolean transbordo) {
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
   
   public boolean isTransbordo() {
      return transbordo;
   }
   
   @Override
   public String toString() {
      return "Ruta{" +
              "origen=" + origen.getNombre() +
              ", destino=" + destino.getNombre() +
              ", tiempo=" + tiempo +
              ", distancia=" + distancia +
              ", costo=" + costo +
              ", transbordo=" + transbordo +
              '}';
   }
}
