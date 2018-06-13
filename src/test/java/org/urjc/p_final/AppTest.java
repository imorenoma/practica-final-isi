package org.urjc.p_final;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/**
 * Unit test for simple App.
 */
public class AppTest{
	//La pelicula es NULL
	@Test (expected=NullPointerException.class)
	public void NullFilmTitleTest() {
		String film = null;
		parser.parserFilm(film);
	}


	//La pelicula tiene un formato incorrecto
	@Test (expected=IllegalArgumentException.class)
	public void FilmIllegalArgumentTitleTest() {
		String film = "2 Fast 2 Furious";
		parser.parserFilm(film);
	}

	//Se consigue el nombre de la peli correctamente
	@Test
	public void FilmFoundTitleTest() {
		String film = "2 Fast 2 Furious (2003)";
		assertEquals(parser.parserFilm(film), "2 Fast 2 Furious");
	}

	//Test para Fecha
	@Test (expected=NullPointerException.class)
	public void NullFilmDateTest() {
		String film = null;
		parser.parserFecha(film);
	}


	//La pelicula tiene un formato incorrecto
	@Test (expected=IllegalArgumentException.class)
	public void FilmIllegalArgumentDateTest() {
		String film = "2 Fast 2 Furious";
		parser.parserFecha(film);
	}

	//Se consigue la fecha de la peli correctamente
	@Test
	public void FilmFoundDateTest() {
		String film = "2 Fast 2 Furious (2003)";
		assertEquals(parser.parserFecha(film), "2003");
	}
	//Test Actores. parserActor
  @Test (expected=NullPointerException.class)
	public void NullActorTest() {
		String actor = null;
		parser.parserActor(actor);
  }

  //Actor con nombre y apellido
  @Test
	public void ActorFoundNameSurnameTest() {
	  String Actor = "Walker,Paul";
	  String[] Act = parser.parserActor(Actor);

	  assertEquals(Act[0], "Paul");
	  assertEquals(Act[1], "Walker");
	}

  //actor solo nombre
/*
  public void ActorFoundNameNotSurnameTest() {
	  String Actor = "Ludacris";
	  String[] Act = parser.parserActor(Actor);

	  assertEquals(Act[0], "Ludacris");
	  assertEquals(Act[1], "");
	}
	*/
}
