package org.urjc.p_final;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;


// METODOS PATHFINDER

// boolean hasPathTo(String v) -- > is v reachable from the source s?
// int distanceTo(String v) -- > return the length of the shortest path from v to s
// Iterable<String> pathTo(String v) -- > return the shortest path from v to s as an Iterable


public class TestPathFinder {

  Graph graph;
  PathFinder finder;

  @Before
	public void setUp() {
		graph = new Graph("resources/routes.txt", " ");
		finder = new PathFinder(graph, "JFK");
	}

  @Test (expected = NullPointerException.class)
  public void testNullString () {
    finder = new PathFinder(graph, null);
  }

  @Test (expected = NullPointerException.class)
  public void testNullGraph () {
    finder = new PathFinder(null, "JFK");
  }

  @Test
  public void testUnknownString() {
    assertEquals("Return the length og the shortest path from unknown vertex.", finder.distanceTo("GFK"), Integer.MAX_VALUE);
    assertEquals("Return the shortest path from unknown vertex.", finder.pathTo("GFK").toString(), "");
  }

  @Test
  public void testRightPath () {
    //finder = new PathFinder(graph, "JFK");
    assertEquals("Should be fine.", finder.pathTo("DFW").toString(), "JFK ORD DFW "); //expected <JFK ORD DFW[ ]>
  }

  @Test
  public void testWrongPath () {
    //finder = new PathFinder(graph, "JFK");
    assertThat("Should be KO.", finder.pathTo("MCO").toString(), not("JFK ATL MCO "));
  }

  @Test
  public void testHappyRightPath () {
    //finder = new PathFinder(graph, "JFK");
    assertEquals("Should be OK.", finder.pathTo("MCO").toString(), "JFK MCO "); //expected <JFK MCO[ ]>
    assertEquals("Should be OK.", finder.distanceTo("MCO"), 1);
  }

  @Test
  public void testHappyWrongPath () {
    //finder = new PathFinder(graph, "JFK");
    assertThat("Should be KO.", finder.pathTo("MCO").toString(), not("JFK ATL MCO "));
    assertThat("Should be KO.", finder.distanceTo("MCO"), not(2));
    assertEquals("Should be KO.", finder.distanceTo("MCO"), 2);
  }


}
