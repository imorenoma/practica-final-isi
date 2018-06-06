package org.urjc.p_final;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.StringTokenizer;

import javax.servlet.MultipartConfigElement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.urjc.p_final.parser;

// This code is quite dirty. Use it just as a hello world example
// to learn how to use JDBC and SparkJava to upload a file, store
// it in a DB, and do a SQL SELECT query
public class Main {

    // Connection to the SQLite database. Used by insert and select methods.
    // Initialized in main
    private static Connection connection;

    static String cabecera = "<body background='http://thewongcouple.com/our-wedding/images/CinemaBackground.jpg'>"
			+ "<center><h1 style=\"color:#8D8A8A;\">MOVIE DB</h1>";

    // Used to illustrate how to route requests to methods instead of
    // using lambda expressions
    public static String doSelect(Request request, Response response) {
    	return select (connection, request.params(":table"),
                                   request.params(":film"));
    }

    public static String erase(Request request, Response response) throws SQLException{
    	// Prepare SQL to create table
		Statement statement = connection.createStatement();
    	statement.setQueryTimeout(30); // set timeout to 30 sec.
    	statement.executeUpdate("drop table if exists films");
    	statement.executeUpdate("drop table if exists actors");
		statement.executeUpdate("drop table if exists works");
    	System.out.println("LLEGO AQUI");
    	statement.executeUpdate("create table films (id_film INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(30), date VARCHAR(5),UNIQUE(title,date))");
    	statement.executeUpdate("create table actors (id_act INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(30), surname VARCHAR(30),UNIQUE(name,surname))");
    	statement.executeUpdate("create table works (id_act INTEGER, id_film INTEGER, PRIMARY KEY(id_act,id_film), FOREIGN KEY(id_act) REFERENCES actors(id_act), FOREIGN KEY(id_film) REFERENCES actors(id_film))");
    	return "Base de datos Eliminada";
    }
	 //select Inicial
    public static String select(Connection conn, String table, String film) {
	String sql = "SELECT * FROM " + table + " WHERE film=?";

	String result = new String();

	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		pstmt.setString(1, film);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
		    // read the result set
		    //result += "film = " + rs.getString("film") + "\n";
		    System.out.println("film = "+rs.getString("film") + "\n");

		    result += rs.getString("actor") + "<br>";
		    System.out.println("actor = "+rs.getString("actor")+"\n");
		}
	    } catch (SQLException e) {
	    System.out.println(e.getMessage());
	}

	return result;
    }

    //Insert inicial
    public static void insert(Connection conn, String film, String actor) {
	String sql = "INSERT INTO films(film, actor) VALUES(?,?)";

	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		pstmt.setString(1, film);
		pstmt.setString(2, actor);
		pstmt.executeUpdate();
	    } catch (SQLException e) {
	    System.out.println(e.getMessage());
	}
    }

	 public static String selectNew(Connection conn, String table, String data1, String data2){
    	String sql;
    	String result = null;
    	if (table == "films"){
    		sql = "SELECT * FROM " + table + " WHERE title=? AND date=?";
    	}else if(table == "actors"){
    		sql = "SELECT * FROM " + table + " WHERE name=? AND surname=?";
    	}else{
    		sql="BBBB"; //No deberia entrar aqui nunca (salta error en el try de despues)
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

    public static void insertNew(Connection conn, String table, String data1, String data2){
		String sql="";
     	String id_result;
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

    public static String prueba(Request request, Response response) {
    	String name;
    	String body;
    	String s;
    	System.out.println("Entro en funcion prueba");

    	body = request.body();

    	//Esta en plan Ñapa porque no me sale el resquest.queryParam
    	////////////////////////////////////////////////////////////
    	StringTokenizer tokenizer = new StringTokenizer(body, "=");

	    // First token is the film name(year)
	    String parametro = tokenizer.nextToken();
    	name = tokenizer.nextToken();
    	///////////////////////////////////////////////////////////////

    	System.out.println("Body = " +  body);
    	System.out.println("Nombre = " +  name);
    	return "HOLA";
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
    parser prueba = new parser();
	port(getHerokuAssignedPort());

	// Connect to SQLite sample.db database
	// connection will be reused by every query in this simplistic example
	connection = DriverManager.getConnection("jdbc:sqlite:sample.db");

	// In this case we use a Java 8 method reference to specify
	// the method to be called when a GET /:table/:film HTTP request
	// Main::doWork will return the result of the SQL select
	// query. It could've been programmed using a lambda
	// expression instead, as illustrated in the next sentence.
	get("/:table/:film", Main::doSelect);

	//prueba borrar base de datos
	get("/erase", Main::erase);

	// In this case we use a Java 8 Lambda function to process the
	// GET /upload_films HTTP request, and we return a form
	get("/upload_films", (req, res) ->
		cabecera
	    + "<form action='/upload' method='post' enctype='multipart/form-data'>"
	    + "<input type='file' name='uploaded_films_file' accept='.txt'>"
	    + "<button>Upload file</button></form></body>");
	// You must use the name "uploaded_films_file" in the call to
	// getPart to retrieve the uploaded file. See next call:

	get("/",(req,res) ->
			cabecera
			+ "<a href='/upload_films'style=\"color: #cc0000\">Subir archivo</a><br>"
			+ "<a href='/erase'style=\"color: #cc0000\">Borrar datos</a><br>"
			+ "<form action='/buscarpelicula' method='post' enctype='text/plain'>"
			+ "<input type='text' name='nombre'>"
			+ "<button>Buscar Pelicula</button></form>"
			+ "</center></body>"
			+ "<form action='/prueba' method='post' enctype='text/plain'>"
			+ "<input type='text' name='nombre'>"
			+ "<button>prueba</button></form>");



	post("/prueba", Main::prueba);

	post("/buscarpelicula", (req, res) -> {
		String[] result = req.body().split("=");
	    res.redirect("/films/" + result[1]);
		return 0;
		});


	// Retrieves the file uploaded through the /upload_films HTML form
	// Creates table and stores uploaded file in a two-columns table
	post("/upload", (req, res) -> {
		req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
		String result = "File uploaded!";
		try (InputStream input = req.raw().getPart("uploaded_films_file").getInputStream()) {
			// getPart needs to use the same name "uploaded_films_file" used in the form

			/* ESTA PARTE DE AQUI CREO QUE SE PUEDE CEPILLAR
			// Prepare SQL to create table
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			//statement.executeUpdate("drop table if exists films");
			//statement.executeUpdate("create table films (film string, actor string, PRIMARY KEY(film,actor))");
*/
			// Read contents of input stream that holds the uploaded file
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			String s;
			while ((s = br.readLine()) != null) {
			    System.out.println(s);

			    // Tokenize the film name and then the actors, separated by "/"
			    StringTokenizer tokenizer = new StringTokenizer(s, "/");

			    // First token is the film name(year)
			    String film = tokenizer.nextToken();

			    //Tendria que llamar a perserfilm(film) --> Saca solo la peli.
			    String prueba2 =prueba.parserFilm(film);
			    System.out.println("Prueba2 =" + prueba2);
			    //llamar a parserFecha(film) -->saque la fecha
			    String Fecha = prueba.parserFecha(film);
			    System.out.println("Fecha =" + Fecha);
			    // Now get actors and insert them
			    while (tokenizer.hasMoreTokens()) {
				insert(connection, film, tokenizer.nextToken());
			    }
			}
			input.close();
		    }
		return result;
	    });

    }

    static int getHerokuAssignedPort() {
	ProcessBuilder processBuilder = new ProcessBuilder();
	if (processBuilder.environment().get("PORT") != null) {
	    return Integer.parseInt(processBuilder.environment().get("PORT"));
	}
	return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
    }
}
