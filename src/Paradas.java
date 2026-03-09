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
      return "Parada{id=" + id + ", nombre='" + nombre + "'}";
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Paradas parada = (Paradas) o;
      return id == parada.id;
   }
   
   @Override
   public int hashCode() {
      return Objects.hash(id);
   }
}
