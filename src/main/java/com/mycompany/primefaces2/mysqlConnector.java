/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author michael
 */
public class mysqlConnector {
  private static final String URL = "jdbc:mysql://localhost:3306/dbMspace";
  private static final String USER = "MySQL";
  private static final String PASSWORD ="mysql123"; 
  public static Connection getConnection() throws SQLException{
      return DriverManager.getConnection(URL,USER,PASSWORD);
  }
  
    
}
