package Logico;

import java.util.Objects;

public class Paradas {
   
   private int id;
   private String nombre;
   private String localizacion;
   
   public Paradas(int id, String nombre, String localizacion) {
      this.id = id;
      this.nombre = nombre;
      this.localizacion = localizacion;
   }
   
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