/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import java.sql.*;
import java.util.Scanner;


/**
 *
 * @author ken_m
 */
public class SQLDatabase {
    private static String USER;
    private static String PASS;
    private static String DBNAME;
    private String databaseName;
    private boolean DBExists;
    private String sql;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    static final String displayFormat="%-5s%-15s%-15s%-15s\n";
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";
    
    public SQLDatabase() throws ClassNotFoundException, SQLException
    {
        DBExists = false;
        USER = "cecs343";
        PASS = "csulb";
        DBNAME = "songs";
    }
    public void connect() throws ClassNotFoundException, SQLException
    {
        DB_URL = DB_URL + DBNAME + ";user="+ USER + ";password=" + PASS;
        System.out.println(DB_URL);
        conn = null; //initialize the connection
        stmt = null;  //initialize the statement that we're using
        try 
        {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            createTable();
            dropTable();
        }
        catch (SQLException se) 
        {
            //Handle errors for JDBC
            se.printStackTrace();
        } 
        catch (Exception e) 
        {
            //Handle errors for Class.forName
            e.printStackTrace();
        } 
        finally 
        {
            //finally block used to close resources
            try 
            {
                if (stmt != null) 
                {
                    stmt.close();
                }
            } 
            catch (SQLException se2) 
            {
            }// nothing we can do
            try 
            {
                if (conn != null) 
                {
                    conn.close();
                }
            } 
            catch (SQLException se) 
            {
                se.printStackTrace();
            }//end finally try
        }//end try
    }
     public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }
    public void testConnection() throws SQLException
    {
        try
        {
            if(!conn.isClosed())
            {
                System.out.println("CONNECTION WORKS");
            }
        }
        catch(SQLException e)
        {
            System.out.println("NOT CONNECTED");
        }
    }
    public boolean dbExists() throws SQLException
    {
        DatabaseMetaData md = conn.getMetaData();
            rs = md.getTables(null, null, "%", null);
            while (rs.next()) 
            {
                //System.out.println(rs.getString(3));
                DBExists = "AUTHORS".equals(rs.getString(3));
            }
            return DBExists;
    }
    public void createTable() throws SQLException
    {
        try
        {
            if(!dbExists())
            {
                System.out.println("CREATING TEMP TABLE AUTHORS");
                stmt = conn.createStatement();
                sql = "CREATE TABLE authors (au_id VARCHAR(20), au_fname VARCHAR(20), au_lname VARCHAR (20), phone VARCHAR(9))";
                stmt.execute(sql);
                stmt.close();
            }
            else                
            {
                System.out.println("TABLE DOESN'T EXIST");
            }
        }
        catch(SQLException e)
        {
            System.out.println("TABLE ALREADY EXISTS");
        }
        
    }
    public void dropTable() throws SQLException
    {
        try
        {
            System.out.println("DROPPING TEMP TABLE AUTHORS");
            stmt = conn.createStatement();
            sql = "DROP TABLE authors";
            stmt.executeUpdate(sql);

            if(!dbExists())
            {
                System.out.println("TABLE DOES NOT EXIST");                       
            }
            else
            {
                System.out.println("TABLE EXISTS");
            }
            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}
