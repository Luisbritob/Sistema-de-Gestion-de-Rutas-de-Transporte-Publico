package Logico;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    // CONFIGURACIÓN PARA SUPABASE
    private static final String URL = "jdbc:postgresql://db.myvvaultusceqzpcoies.supabase.co:5432/postgres?sslmode=require";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Ld9FnmwlYcD8njV6"; // Usa la contraseña correcta

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                System.out.println("Conectando a Supabase...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexión exitosa a Supabase!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver de PostgreSQL no encontrado", e);
            } catch (SQLException e) {
                System.err.println("❌ Error de conexión: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static List<Paradas> obtenerTodasParadas() {
        List<Paradas> paradas = new ArrayList<>();
        String sql = "SELECT id, nombre FROM paradas ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paradas.add(new Paradas(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener paradas: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return paradas;
    }

    public static void guardarParada(Paradas parada) {
        String sql = "INSERT INTO paradas (id, nombre) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parada.getId());
            pstmt.setString(2, parada.getNombre());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar parada: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void eliminarParada(Paradas parada) {
        String sql = "DELETE FROM paradas WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, parada.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar parada: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void modificarParada(Paradas parada, String nuevoNombre) {
        String sql = "UPDATE paradas SET nombre = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoNombre);
            pstmt.setInt(2, parada.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar parada: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int obtenerSiguienteIdParada() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM paradas";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente ID: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 1;
    }

    public static List<Rutas> obtenerTodasRutas(Map<Paradas, List<Rutas>> grafo) {
        List<Rutas> rutas = new ArrayList<>();
        String sql = "SELECT origen_id, destino_id, tiempo, distancia, costo, transbordo FROM rutas";

        Map<Integer, Paradas> mapaParadas = new HashMap<>();
        for (Paradas p : grafo.keySet()) {
            mapaParadas.put(p.getId(), p);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int origenId = rs.getInt("origen_id");
                int destinoId = rs.getInt("destino_id");
                Paradas origen = mapaParadas.get(origenId);
                Paradas destino = mapaParadas.get(destinoId);

                if (origen != null && destino != null) {
                    Rutas ruta = new Rutas(origen, destino,
                            rs.getDouble("tiempo"),
                            rs.getDouble("distancia"),
                            rs.getDouble("costo"),
                            rs.getInt("transbordo"));
                    rutas.add(ruta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rutas: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return rutas;
    }

    public static void guardarRuta(Rutas ruta) {
        String sql = "INSERT INTO rutas (origen_id, destino_id, tiempo, distancia, costo, transbordo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ruta.getOrigen().getId());
            pstmt.setInt(2, ruta.getDestino().getId());
            pstmt.setDouble(3, ruta.getTiempo());
            pstmt.setDouble(4, ruta.getDistancia());
            pstmt.setDouble(5, ruta.getCosto());
            pstmt.setInt(6, ruta.getTransbordo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar ruta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void eliminarRuta(Paradas origen, Paradas destino) {
        String sql = "DELETE FROM rutas WHERE origen_id = ? AND destino_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, origen.getId());
            pstmt.setInt(2, destino.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar ruta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void modificarRuta(Rutas ruta) {
        String sql = "UPDATE rutas SET tiempo = ?, distancia = ?, costo = ?, transbordo = ? WHERE origen_id = ? AND destino_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, ruta.getTiempo());
            pstmt.setDouble(2, ruta.getDistancia());
            pstmt.setDouble(3, ruta.getCosto());
            pstmt.setInt(4, ruta.getTransbordo());
            pstmt.setInt(5, ruta.getOrigen().getId());
            pstmt.setInt(6, ruta.getDestino().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar ruta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Conectado a Supabase!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM paradas");
            if (rs.next()) {
                System.out.println("📊 Número de paradas: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}