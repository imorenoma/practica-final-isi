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
    
}


}
