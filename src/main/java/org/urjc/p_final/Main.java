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
    	return cabecera + "<div style='color:#FFFFFF'>" + Bbdd.writeBbdd (connection, request.params(":table"));
    }

    public static String erase(Request request, Response response) throws SQLException{
    	String result = cabecera + "<div style='color:#FFFFFF'>Base de datos Eliminada</div></body>";

    	Bbdd.eraseBBDD(connection);
    	return result;
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

    public static Integer distancia(String s, String t) {
    	System.out.println("distancia");
    	PathFinder pf = new PathFinder(graph, s);
    	System.out.println("He hecho pathfinder");
        for (String v : pf.pathTo(t)) {
        	StdOut.println("   " + v);
        	}
        return(pf.distanceTo(t));
    }

    public static String vecinos(String v) {
    	String resultado = "";
    	if (graph.hasVertex(v)) {
            for (String w : graph.adjacentTo(v)) {
                resultado = resultado + w + "<br>";
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
    	
    	get("/pelicula", (req, res) ->
			cabecera
			+"<div style='color:#FFFFFF'>Elija la pelicula para obtener los vecinos:"
			+"<form action='/pelicula' method='post' enctype='text/plain'>"
			+"Pelicula: <input type='text' id='pelicula' name='pelicula'><br>"
			+"<button>Buscar vecinos</button></form></div></body>");
    	
    	get("/actor", (req, res) ->
			cabecera
			+"<div style='color:#FFFFFF'>Elija el actor para obtener los vecinos:"
			+"<form action='/actor' method='post' enctype='text/plain'>"
			+"Actor: <input type='text' id='actor' name='actor'><br>"
			+"<button>Buscar vecinos</button></form></div></body>");

	   	get("/",(req,res) ->{
			String respuesta = cabecera
					+"<a href='/crear_grafo'style=\"color: #cc0000\">Crear grafo</a><br>";
			if (graph.E() != 0) {
				respuesta = respuesta 
						+"<a href='/distancia_grafo'style=\"color: #cc0000\">Distancia grafo</a><br>"
						+"<a href='/pelicula'style=\"color: #cc0000\">Pelicula vecinos</a><br>"
						+"<a href='/actor'style=\"color: #cc0000\">Actor vecinos</a><br>";
			}
			respuesta = respuesta +"<a href='/erase'style=\"color: #cc0000\">Borrar datos</a><br>"
					+"<a href='/bbdd/distances'style=\"color: #cc0000\">ImprimirBBdd distancias</a><br>"
					+"<a href='/bbdd/actors'style=\"color: #cc0000\">ImprimirBBdd Actores</a><br>"
					+"<a href='/bbdd/films'style=\"color: #cc0000\">ImprimirBBdd Peliculas</a><br>"
					+"</center></body>";
		
			return respuesta;
			});

    	post("/crear_grafo", (req, res) -> {
    		Integer opcion = Integer.parseInt(req.body().split("=")[1]);
    		graph = crearGrafo(opcion);
    		String respuesta = cabecera
    				+"<div style='color:#FFFFFF'>Grafo creado</div></body>";
    		return respuesta;
		});

    	post("/distancia_grafo", (req, res) -> {
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

        		//TODO Hay que meter esto en la base de datos (distances)
        		Bbdd.insertDistances(connection, actor1, actor2, Integer.toString(dist));
        		return respuesta; //respuesta
    		}catch (IllegalArgumentException e){
    			 return cabecera
        				+"<div style='color:#FFFFFF'>No existe alguno de los actores que buscas</div></body>";
    		}
		});
    	
    	post("/pelicula", (req, res) -> {
			try {
				String pelicula = req.body().split("\n")[0].split("=")[1].trim();
				graph.validateVertex(pelicula);
				
				String actores = vecinos(pelicula);
				
				String respuesta = cabecera
	    				+"<div style='color:#FFFFFF'>Los actores de la película \"" + pelicula 
	    				+"\" son:<br>" + actores + "</div></body>";
				Bbdd.insertDataFindFilms(connection, pelicula, actores);
	    		return respuesta;
			}catch (IllegalArgumentException e){
   			 	return cabecera +"<div style='color:#FFFFFF'>No existe la película</div></body>";
			}
		});
    	
    	post("/actor", (req, res) -> {
			try {
				String actor = req.body().split("\n")[0].split("=")[1].trim();
				graph.validateVertex(actor);
				String peliculas = vecinos(actor);
				String respuesta = cabecera
	    				+"<div style='color:#FFFFFF'>Las películas de " + actor 
	    				+" son:<br>" + peliculas + "</div></body>";
	    		Bbdd.insertDataFindActor(connection, actor, peliculas);
				return respuesta;
			}catch (IllegalArgumentException e){
   			 	return cabecera +"<div style='color:#FFFFFF'>No existe el actor</div></body>";
			}
		});
    	

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
