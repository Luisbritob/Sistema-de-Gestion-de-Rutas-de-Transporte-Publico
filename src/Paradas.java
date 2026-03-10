import java.util.Objects;

public class Paradas {
   
   private int id;
   private String nombre;
   
   public Paradas(int id, String nombre) {
      this.id = id;
      this.nombre = nombre;
   }
   
   public int getId() {
      return id;
   }
   
   public String getNombre() {
      return nombre;
   }
   
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   
   @Override
   public String toString() {
      return nombre;
   }
   
   @Override
   public boolean equals(Object otroObjeto) {
      if (this == otroObjeto) return true;
      if (otroObjeto == null || getClass() != otroObjeto.getClass()) return false;
      Paradas parada = (Paradas) otroObjeto;
      return id == parada.id;
   }
   
   @Override
   public int hashCode() {
      return Objects.hash(id);
   }
}
