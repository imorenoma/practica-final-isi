package org.urjc.p_final;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.*;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


//public static String selectMine(Connection conn, String table, String data1, String data2) --> Busca un elemento en la base de datos
//public static void insertMine(Connection conn, String table, String data1, String data2) --> Insertar en la tabla Works o Films o Actors
//insertDataFindFilms(Connection conn, String pelicula, String actores) --> guardar en la base de datos
//insertDataFindActor(Connection conn, String actor, String peliculas) --> guardar en la base de datos
//insertDistances(Connection conn, String data1, String data2, String data3) --> guardar en la base de datos
//sizeTable(Connection conn, String table) --> Devuelve el numero de elementos de la tabla seleccionada
//printFilms(ResultSet rs) throws SQLException --> Imprime el mensaje que despues se ve en la pagina Html (Tabla Films)
//printActors(ResultSet rs) throws SQLException --> Imprime el mensaje que despues se ve en la pagina Html (Tabla Actors)
//printDistances(ResultSet rs) throws SQLException --> Imprime el mensaje que despues se ve en la pagina Html (Tabla Distances)
//writeBbdd(Connection conn, String table) --> Funcion que selecciona que se va a escribir por pantalla


public class TestDDBB {

  private static Connection connection;

  @Before
	public void SetUp() {

    connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
      Bbdd.eraseBBDD(connection); // Prepare SQL to create table
    } catch(SQLException e {
	      System.err.println(e.getMessage());
	    }
	}

  @After
  public void tearDown() {
    try {
      if(connection != null){
        connection.close();
      }
    } catch(SQLException e) {
        System.err.println(e);
    }

////////////////////////////////////////////////////////////////////////////////////////////
/////////// VER SI INSERTMINE DEVUELVE NullPointerException SI LE METEMOS UN NULL //////////
////////////////////////////////////////////////////////////////////////////////////////////

  @Test (expected=NullPointerException.class)
	public void testInsertNullFilm() throws SQLException {
		Bbdd.insertMine(connection, "films", "10 Things I Hate About You", null); // año null
	}

  @Test (expected=NullPointerException.class)
	public void testInsertNullActor() throws SQLException {
		Bbdd.insertMine(connection, "actors", null, "Kidman"); // nombre null
	}

  @Test (expected=NullPointerException.class)
  public void testInsertNullWorks() throws SQLException {
    Bbdd.insertMine(connection, "actors", null, 3); // id_pelicula null
  }

  @Test
	public void testFilmUnknown() throws SQLException {
		Bbdd.insertMine(connection, "films", "15 Minutes", "(2001)");
		assertEquals("Should be OK.",Bbdd.selectMine(connection, "films", "28 Minutes", "(2001)"),null); // confirmar si INSERTMINE devuelve NULL si no lo encuentra en la tabla
	}

  @Test
  public void testRightFilm() throws SQLException {
    Bbdd.insertMine(connection, "films", "20000 Leagues Under the Sea", "(1954)");
    assertEquals("Should be OK.",Bbdd.selectMine(connection, "films", "20000 Leagues Under the Sea", "(1954)"), 1); // confirmar si INSERTMINE devuelve el ID correcto, 1 en este caso
  }

  @Test
	public void testActorUnknown() throws SQLException {
		Bbdd.insertMine(connection, "actors", "Nicole", "Kidman");
		assertEquals("Should be OK.",Bbdd.selectMine(connection, "actors", "Robert", "De Niro"),null); // confirmar si INSERTMINE devuelve NULL si no lo encuentra en la tabla
	}

  @Test
  public void testRightActor() throws SQLException {
    Bbdd.insertMine(connection, "actors", "Tom", "Cruise");
    assertEquals("Should be OK.",Bbdd.selectMine(connection, "actors", "Tom", "Cruise"), 1); // confirmar si INSERTMINE devuelve el ID correcto, 1 en este caso
  }

}






}
