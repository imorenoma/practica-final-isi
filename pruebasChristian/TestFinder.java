package org.urjc.p_final;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestPathFinder {

  Graph graph;
  PathFinder finder;

  @Before
	public void setUp() {
		graph = new Graph("resources/routes.txt", " ");
		finder = new PathFinder(g, "ATL");
	}

  @Test (expected = NullPointerException.class)
  public void testNullString () {
    finder = new PathFinder(g, null);
  }

  @Test (expected = NullPointerException.class)
  public void testNullGraph () {
    pf = new PathFinder(null, "ATL");
  }


}
