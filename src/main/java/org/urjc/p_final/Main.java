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
    public static Graph graph;
    static String cabecera = "<body background='http://thewongcouple.com/our-wedding/images/CinemaBackground.jpg'>"
			+"<center><h1>"
			+"<a href='/' style='color: #8D8A8A;text-decoration: none'>MOVIE DB</h1></a></body>";
    // Used to illustrate how to route requests to methods instead of
    // using lambda expressions
    public static String doSelect(Request request, Response response) {
    	return select (connection, request.params(":table"),
                                   request.params(":film"));
    }

  //Para utilizar la url para imprimir el numero
    public static String printSize(Request request, Response response) {
    	return ("Entradas de la Base de datos " + request.params(":table") + " = " + Bbdd.sizeTable (connection, request.params(":table")));
    }
    public static String doWrite(Request request, Response response) {
    	return Bbdd.writeBbdd (connection, request.params(":table"));
    }

    public static String erase(Request request, Response response) throws SQLException{
    	// Prepare SQL to create table
    	/*
    	Statement statement = connection.createStatement();
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
    	*/
		String result = cabecera + "<div style='color:#FFFFFF'>Base de datos Eliminada</div></body>";

    	Bbdd.eraseBBDD(connection);
    	return result;
    }
    public static String pruebaDistancia(Request request, Response response) throws SQLException{
    	String actr = "Diesel,Vin";
    	String actr1 = "Walker,Paul";
    	String dist = "5";
    	Bbdd.insertDistances(connection, actr, actr1, dist);
    	return "Supongo que estara guardado";

    }
    //Select de serie
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


    //Insert que venia hecho
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

    public static Graph crearGrafo(Integer opcion) {
    	Graph grafo = null;
    	String directory = "resources/data/imdb-data/";
    	String fichero;
    	switch (opcion) {
		case 1:
			fichero = directory + "cast.06.txt";
			break;
		case 2:
			fichero = directory + "cast.00-06.txt";
			break;
		case 3:
			fichero = directory + "cast.G.txt";
			break;
		case 4:
			fichero = directory + "cast.PG.txt";
			break;
		case 5:
			fichero = directory + "cast.PG13.txt";
			break;
		case 6:
			fichero = directory + "cast.mpaa.txt";
			break;
		case 7:
			fichero = directory + "cast.action.txt";
			break;
		case 8:
			fichero = directory + "cast.rated.txt";
			break;
		case 9:
			fichero = directory + "cast.all.txt";
			break;
		default:
			fichero = "";
			break;
    	}
    	grafo = new Graph(fichero, "/");
    	return grafo;
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

    public static Integer distancia(String s, String t) {
    	System.out.println("distancia");
    	PathFinder pf = new PathFinder(graph, s);
    	System.out.println("He hecho pathfinder");
        for (String v : pf.pathTo(t)) {
        	StdOut.println("   " + v);
        	}
        return(pf.distanceTo(t));
    }

    public static String vecinos() {
    	String v = "Bacon, Kevin";		//TODO Lo mismo que en distancia, obtenerlos no como variables.
    	//String v = "Apollo 13 (1995)";	//Funciona tanto para peliculas como para actores.
    	String resultado = "";
    	if (graph.hasVertex(v)) {
            for (String w : graph.adjacentTo(v)) {
                resultado = resultado + "  " + w;
            }
        }
    	return resultado;
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
    	//parser prueba = new parser();
    	port(getHerokuAssignedPort());

    	// Connect to SQLite sample.db database
    	// connection will be reused by every query in this simplistic example
    	connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
    	graph = new Graph();
    	//graph = crearGrafo(1);

    	//Integer dist = distancia("Bacon, Kevin","Kidman, Nicole",graph);
		//System.out.println("Distancia = " + dist);
    	// In this case we use a Java 8 method reference to specify
    	// the method to be called when a GET /:table/:film HTTP request
    	// Main::doWork will return the result of the SQL select
    	// query. It could've been programmed using a lambda

    	// expression instead, as illustrated in the next sentence.
    	//URLS BASES DE DATOS
    	//Prueba escribir en la pantalla las cosas de las bases de datos
    	get("/bbdd/:table", Main::doWrite); //Hay que ponerlos antes que doSelect porque sino coge la /bbdd como tabla
    	get("/num/:table", Main::printSize);
    	//prueba borrar base de datos
    	get("/erase", Main::erase);
    	//Prueba guardar en distancia
    	get("/pruebaDistancia", Main::pruebaDistancia);
    	//dO sELECT QUE VENIA DE SERIE
    	get("/:table/:film", Main::doSelect);

    	get("/crear_grafo", (req, res) ->
    		cabecera
    		+"<div style='color:#FFFFFF'>Elija el archivo para crear el grafo:</div>"
    		+"<form action='/crear_grafo' method='post'>"
    		+"<select name='files'>"
    		+"<option value='1'>Año 2006</option>"
    		+"<option value='2'>Desde año 2000</option>"
    		+"<option value='3'>Calificadas G por MPAA</option>"
    		+"<option value='4'>Calificadas PG por MPAA</option>"
    		+"<option value='5'>Calificadas PG-13 por MPAA</option>"
    		+"<option value='6'>Calificadas por MPAA</option>"
    		+"<option value='7'>Acción</option>"
    		+"<option value='8'>Populares</option>"
    		+"<option value='9'>Todas</option>"
    		+"</select><input type='submit' value='Crear grafo'></form></body>");

    	get("/distancia_grafo", (req, res) ->
			cabecera
			+"<div style='color:#FFFFFF'>Elija los actores para obtener la distancia:"
			+"<form action='/distancia_grafo' method='post' enctype='text/plain'>"
			+"Actor 1: <input type='text' id='actor1' name='actor1'><br>"
			+"Actor 2: <input type='text' name='actor2'><br>"
			+"<button>Calcular distancia</button></form></div></body>");

	// In this case we use a Java 8 Lambda function to process the
	// GET /upload_films HTTP request, and we return a form
    	get("/upload_films", (req, res) ->
    		cabecera
    		+"<form action='/upload' method='post' enctype='multipart/form-data' style='color: #FFFFFF'>"
    		+"<input type='file' name='uploaded_films_file' accept='.txt'>"
    		+"<button>Upload file</button></form></body>");
	// You must use the name "uploaded_films_file" in the call to
	// getPart to retrieve the uploaded file. See next call:

    	get("/",(req,res) ->
			cabecera
			+"<a href='/upload_films'style=\"color: #cc0000\">Subir archivo</a><br>"
			+"<a href='/crear_grafo'style=\"color: #cc0000\">Crear grafo</a><br>"
			+"<a href='/distancia_grafo'style=\"color: #cc0000\">Distancia grafo</a><br>"
			+"<a href='/erase'style=\"color: #cc0000\">Borrar datos</a><br>"
			+"<form action='/buscarpelicula' method='post' enctype='text/plain'>"
			+"<input type='text' name='nombre'>"
			+"<button>Buscar Pelicula</button></form>"
			+"</center></body>"
			+"<form action='/prueba' method='post' enctype='text/plain'>"
			+"<input type='text' name='nombre'>"
			+"<button>prueba</button></form>");

    	post("/crear_grafo", (req, res) -> {
    		Integer opcion = Integer.parseInt(req.body().split("=")[1]);
    		graph = crearGrafo(opcion);
    		//System.out.println(graph);
    		//System.out.println(vecinos());

    		String actor1 = "Bacon, Kevin";
    		String actor2 = "Kidman, Nicole";
    		Integer dist = distancia(actor1, actor2);
    		System.out.println(dist);


    		String respuesta = cabecera
    				+"<div style='color:#FFFFFF'>Grafo creado</div></body>";
    		return respuesta;
		});

    	post("/distancia_grafo", (req, res) -> {
    		//Integer opcion = Integer.parseInt(req.body().split("=")[1]);
    		//System.out.println(distancia());
			try{
    			String actor1 = req.body().split("\n")[0].split("=")[1];
        		String actor2 = req.body().split("\n")[1].split("=")[1];
        		//Hago trim para quitar los espacios del principio y del final
        		actor1 = actor1.trim();
        		actor2 = actor2.trim();
            	graph.validateVertex(actor1);
            	graph.validateVertex(actor2);
        		int dist = distancia(actor1, actor2);
        		String respuesta = cabecera
        				+"<div style='color:#FFFFFF'>La distancia entre "
        				+ actor1 + " y " + actor2 + " es de: " + dist + "</div></body>";
        		//System.out.println(req.body().split("=\n")[3]);

        		//Hay que meter esto en la base de datos (distances)
        		Bbdd.insertDistances(connection, actor1, actor2, Integer.toString(dist));
        		return respuesta; //respuesta
    		}catch (IllegalArgumentException e){
    			 return cabecera
        				+"<div style='color:#FFFFFF'>No existe alguno de los actores que buscas</div></body>";
    		}
		});

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
    		String result = cabecera + "<div style='color:#FFFFFF'>File uploaded!</div></body>";
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
    				String film_name =parser.parserFilm(film);
    				//System.out.println("Despues ParserFilm NameFilm =" + film_name);
    				//llamar a parserFecha(film) -->saque la fecha
    				String film_date = parser.parserFecha(film);
    				//System.out.println("DEspuesParserFilm DateFilm =" + film_date);
    				// Now get actors and insert them
    				//Insertar film y que me devuelva el id_film
    				Bbdd.insertMine(connection,"films",film_name, film_date); //Me deberia devolver el id de la peli. Deberia imprimier el Id de la peli que acabo de meter
    				String id_Film = Bbdd.selectMine(connection, "films", film_name, film_date);
    				while (tokenizer.hasMoreTokens()) {
    					//Me tiene que hacer el parserActor
    					String[] actor = parser.parserActor(tokenizer.nextToken());
    					Bbdd.insertMine(connection,"actors",actor[0],actor[1]);
    					String id_actor = Bbdd.selectMine(connection,"actors",actor[0],actor[1]);
    					//insertar en la tabla works
    					Bbdd.insertMine(connection,"works",id_actor,id_Film);
    					//insert(connection, film, tokenizer.nextToken());
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
