/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import static grapefruit.player.GrapefruitPlayer.gui;
import static grapefruit.player.GrapefruitPlayer.player;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.sql.PreparedStatement;


/**
 *
 * @author ken_m
 */
public class SQLDatabase {
    private String pathArray[];
    private static String USER;
    private static String PASS;
    private static String DBNAME;
    private String databaseName;
    private boolean DBExists;
    private String sql;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private ResultSetMetaData rsmd;
    private int numbRows;
    private int numbCols;
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
            //dropTable();
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
     public static int dispNullInt(int input){
         if( input == -1)
             return 0;
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
                DBExists = "songs".equals(rs.getString(3));
            }
            return DBExists;
    }
    public void createTable() throws SQLException
    {
        try
        {
            if(!dbExists())
            {
                System.out.println("CREATING TABLE songs");
                stmt = conn.createStatement();
                sql = "CREATE TABLE songs (title VARCHAR(40), album VARCHAR(40), artist VARCHAR (40), pubdate VARCHAR(4), genre integer, comment VARCHAR(50),path VARCHAR(200))";
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
            //e.printStackTrace();
        }
        
    }
    public void dropTable() throws SQLException
    {
        try
        {
            System.out.println("DROPPING TEMP TABLE AUTHORS");
            stmt = conn.createStatement();
            sql = "DROP TABLE songs";
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
      public void deleteSong(String title) throws SQLException, FileNotFoundException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            sql = "DELETE FROM songs WHERE Title=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            int deleteCount = pstmt.executeUpdate();
            stmt.close();
            System.out.println("Deletion successful");
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void addSong() throws SQLException, FileNotFoundException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            //sql = "INSERT INTO songs (TITLE,album,artist,pubdate,genre,comment) VALUES(' + player.getTitle() + "'','" + player.getAlbum()+ "','" +player.getArtist()+ "','" +player.getYear()+ "','" +player.getGenre()+ "','" +player.getComment()+'")");
           /* sql = "INSERT INTO songs (title, album, artist, pubdate, genre, comment) VALUES('"+player.getTitle() +"','"+ 
                    player.getAlbum()+"','"+
                    player.getArtist()+"','"+
                    player.getYear()+"',"+
                    player.getGenre()+",'"+
                    player.getComment()+",'"+
                    "yes"+"')";*/
            sql = "INSERT INTO songs (title, album,artist,pubdate,genre,comment,path) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,player.getTitle());
            pstmt.setString(2, player.getAlbum());
            pstmt.setString(3, player.getArtist());
            pstmt.setString(4, player.getYear());
            pstmt.setInt(5, player.getGenre());
            pstmt.setString(6, player.getComment());
            pstmt.setString(7, player.getPath());
            pstmt.execute();
            System.out.println(sql.toString());
            
            //stmt.execute(sql);
            /*
            InputStream inputStream = new FileInputStream(new File(player.getPath()));
            sql = "INSERT INTO songs (path) values (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,sql);
            pstmt.executeUpdate();
            */
            //stmt.executeUpdate(sql);
            
            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void findNumbItems() throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
             rs = stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM SONGS");
            while(rs.next()) {
               numbRows = rs.getInt("COUNT");
            }

 //Closing the connection
            stmt.close();
            conn.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }        
    }
    public void findNumbCols() throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from songs");
            rsmd = rs.getMetaData();
            numbCols = rsmd.getColumnCount();
            stmt.close();
            conn.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
            
    public int getNumbRows()
    {
        return numbRows;
    }
    public int getNumbCols()
    {
        return numbCols;
    }
    public Object[][] populateTable(int rows, int cols) throws SQLException
    {
        Object[][]temp = new Object[rows][cols];
        try
        {
            conn = DriverManager.getConnection(DB_URL);            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM songs");
            rsmd = rs.getMetaData();
            
            int i = 0;
            while(rs.next())
            {
                
                    String t = rs.getString(1);
                    String al = rs.getString(2);
                    String ar = rs.getString(3);
                    String y = rs.getString(4);
                    int g = rs.getInt(5);
                    String c = rs.getString(6);
                    String p = rs.getString(7);
                   temp[i][0] = dispNull(t);
                   temp[i][1] = dispNull(al);
                   temp[i][2] = dispNull(ar);
                   temp[i][3] = dispNull(y);
                   temp[i][4] = dispNullInt(g);
                   temp[i][5] = dispNull(c);
                   temp[i][6] = dispNull(p);
                   i++;              
            }
            rs.close();
            stmt.close();
            conn.close();
            
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return temp;
    }
}
