package org.urjc.p_final;

public class parser {

	public static String parserFilm(String linea){
		//Quita hasta el ultimo espacio
		int indice;
		indice = linea.lastIndexOf('(');
		if (indice == -1){
			throw new IllegalArgumentException("Formato incorrecto de la pelicula");
		}else{
			//System.out.println("Indice = " + indice);
			linea = linea.substring(0, indice-1);
			return linea;
		}

	}

	public static String parserFecha(String linea){
		String fecha;
		int indice;

		indice = linea.lastIndexOf('(');
		if (indice == -1){
			throw new IllegalArgumentException("Formato incorrecto de la pelicula");
		}else{
			fecha = linea.substring(indice+1, linea.length()-1);
			//System.out.println("fecha =" + fecha);
			return fecha;
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "$300 y tickets (2002)";
		//Pruebas parserFilm
		/*
		String fin = parserFilm(s);
		System.out.println(fin);
		System.out.println(fin.length());
		System.out.println(fin.charAt(13));
		*/
		//Pruebas ParserFecha
		String fecha = parserFecha(s);
		System.out.println(fecha);

	}

}
