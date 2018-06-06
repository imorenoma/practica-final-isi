package org.urjc.p_final;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bbdd {
	
	public static void eraseBBDD(Connection conn)throws SQLException{
    	// Prepare SQL to create table
    	Statement statement = conn.createStatement();
    	statement.setQueryTimeout(30); // set timeout to 30 sec.
    	statement.executeUpdate("drop table if exists films");	
    	statement.executeUpdate("drop table if exists actors");	
		statement.executeUpdate("drop table if exists works");
		statement.executeUpdate("drop table if exists distances");
    	System.out.println("LLEGO AQUI");
    	statement.executeUpdate("create table films (id_film INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(30), date VARCHAR(5),UNIQUE(title,date))");
    	statement.executeUpdate("create table actors (id_act INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(30), surname VARCHAR(30),UNIQUE(name,surname))");
    	statement.executeUpdate("create table works (id_act INTEGER, id_film INTEGER, PRIMARY KEY(id_act,id_film), FOREIGN KEY(id_act) REFERENCES actors(id_act), FOREIGN KEY(id_film) REFERENCES actors(id_film))");
    	statement.executeUpdate("create table distances (name1 VARCHAR(30), surname1 VARCHAR(30), name2 VARCHAR(30), surname2 VARCHAR(30), distance INTEGER, PRIMARY KEY(name1,surname1,name2,surname2))");
    	
    }
	/*Devuelve el Id, null si no existe, de la tabla dando como parametros:
		(titulo y fecha)  --> Tabla films
		(nombre y apellido) --> Tabla Actors
	*/
	public static String selectMine(Connection conn, String table, String data1, String data2){
    	String sql=""; 
    	String result = null;
    	if (table == "films"){
    		sql = "SELECT * FROM " + table + " WHERE title=? AND date=?"; 
    	}else if(table == "actors"){
    		sql = "SELECT * FROM " + table + " WHERE name=? AND surname=?"; 
    	}else{
    		//No deberia entrar nunca
    		System.out.println("No existe la tabla");
    		//Saltara una excepción en el try catch.
    	}
    	
    	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, data1);
    		pstmt.setString(2, data2);
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    		    // read the result set
    		    //result += "film = " + rs.getString("film") + "\n";
    			//System.out.println("ID_Act = " + rs.getString("id"));
    		    //System.out.println("film = "+rs.getString("film") + "\n");
    		    if (table == "films"){
    		    	//System.out.println("IDFilm = " + rs.getString("id_film"));
        		    //System.out.println("film = "+rs.getString("film") + "\n");
        		    result = rs.getString("id_film");
    		    }else if( table =="actors"){
    		    	//System.out.println("ID_Act = " + rs.getString("id_act"));
        		    //System.out.println("Surname = "+rs.getString("surname") + "\n");
        		    result = rs.getString("id_act");
    		    }
    		    System.out.println("ID Final = "+ result +"\n");
    		}
    		return result;
    	} catch (SQLException e) {
    	    System.out.println(e.getMessage());
    	    return null;
    	}
    }
	//Insertar en la tabla Works o Films o Actors
	public static void insertMine(Connection conn, String table, String data1, String data2){
    	String sql="";
    	System.out.println("Nombre de tabla = " + table);
    	if (table == "films"){
    		System.out.println("TABLA FILMS");
    		sql = "INSERT INTO " + table + "(title,date) VALUES(?,?)";
    	}else if(table == "actors"){
    		System.out.println("TABLA ACTORS");
    		sql = "INSERT INTO " + table + "(name, surname) VALUES(?,?)";
    	}else if(table == "works"){
    		System.out.println("TABLA WORKS");
    		sql = "INSERT INTO " + table + "(id_act, id_film) VALUES(?,?)";
    	}else{
    		System.out.println("No existe la tabla"); //No deberia entrar nunca
    	}
    	
    	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, data1);
    		pstmt.setString(2, data2);
    		pstmt.executeUpdate();
    	} catch (SQLException e) {
    	    System.out.println(e.getMessage());
    	}
    }
    
	
	//introduce en la base de datos Distances, los parametros (nombre,apellido,distancia)
    public static void insertDistances(Connection conn, String data1, String data2, String data3){
    	String sql="";
    	String[] actor1 = parser.parserActor(data1);
    	String[] actor2 = parser.parserActor(data2);
    	sql = "INSERT INTO distances(name1,surname1,name2,surname2,distance) VALUES(?,?,?,?,?)";
    	
    	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, actor1[0]);
    		pstmt.setString(2, actor1[1]);
    		pstmt.setString(3, actor2[0]);
    		pstmt.setString(4, actor2[1]);
    		pstmt.setString(5, data3);
    		pstmt.executeUpdate();
    	} catch (SQLException e) {
    	    System.out.println(e.getMessage());
    	}
    }
    
    
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
