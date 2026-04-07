package Logico;

import java.util.Objects;

public class Paradas {
   
   private final int id;
   private String nombre;
   private String localizacion;

   //Crea una parada con ID, nombre y localización.
   public Paradas(int id, String nombre, String localizacion) {
      this.id = id;
      this.nombre = nombre;
      this.localizacion = localizacion;
   }

   //Setters y getters
   public int getId() {
      return id;
   }
   public String getNombre() {
      return nombre;
   }
   public String getLocalizacion() {
      return localizacion;
   }
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   public void setLocalizacion(String localizacion) {
      this.localizacion = localizacion;
   }

   //Retorna el nombre de la parada como texto.
   @Override
   public String toString() {
      return nombre;
   }

   //Compara dos paradas por su ID.
   @Override
   public boolean equals(Object otroObjeto) {
      if (this == otroObjeto) return true;
      if (otroObjeto == null || getClass() != otroObjeto.getClass()) return false;
      Paradas parada = (Paradas) otroObjeto;
      return id == parada.id;
   }

   //Retorna el hash basado en el ID.
   @Override
   public int hashCode() {
      return Objects.hash(id);
   }
}