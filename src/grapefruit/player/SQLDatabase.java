/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import static grapefruit.player.GrapefruitPlayer.gui;
import static grapefruit.player.GrapefruitPlayer.player;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private String playlistName;
    private PreparedStatement pstmt;
    private FileWriter fileWriter;
    private String line; //The line in the text file to be read
    
    public SQLDatabase() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException
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
     /*
     public static int dispNullInt(int input){
         if( input == -1)
             return 0;
         else
             return input;
             
     }*/
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
                sql = "CREATE TABLE songs (title VARCHAR(40), album VARCHAR(40), artist VARCHAR (40), pubdate VARCHAR(4), genre VARCHAR(30), comment VARCHAR(50),path VARCHAR(200))";
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
    public void dropTable(String libName) throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            sql = "DROP TABLE " + libName;
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
      public void deleteSong(String libName, String title) throws SQLException, FileNotFoundException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            sql = "DELETE FROM "+libName+" WHERE Title=?";
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
    public void addSong(String libName) throws SQLException, FileNotFoundException
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
            sql = "INSERT INTO "+libName+" (title, album,artist,pubdate,genre,comment,path) VALUES(?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, player.getTitle());
            pstmt.setString(2, player.getAlbum());
            pstmt.setString(3, player.getArtist());
            pstmt.setString(4, player.getYear());
            pstmt.setString(5, player.getGenre());
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
    public void findNumbItems(String libName) throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
             rs = stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM " + libName);
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
    public void findNumbCols(String libName) throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from " + libName);
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
    public Object[][] populateTable(String libName, int rows, int cols) throws SQLException
    {
        Object[][]temp = new Object[rows][cols];
        try
        {
            conn = DriverManager.getConnection(DB_URL);            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + libName);
            rsmd = rs.getMetaData();
            
            int i = 0;
            while(rs.next())
            {
                
                    String t = rs.getString(1);
                    String al = rs.getString(2);
                    String ar = rs.getString(3);
                    String y = rs.getString(4);
                    String g = rs.getString(5);
                    String c = rs.getString(6);
                    String p = rs.getString(7);
                   temp[i][0] = dispNull(t);
                   temp[i][1] = dispNull(al);
                   temp[i][2] = dispNull(ar);
                   temp[i][3] = dispNull(y);
                   temp[i][4] = dispNull(g);
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
    public void createPlaylist(String pName) throws IOException
    {
        playlistName = pName;
        try
        {
            System.out.println("CREATING TABLE " + playlistName);
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            sql = "CREATE TABLE "+ playlistName + " (title VARCHAR(40), album VARCHAR(40), artist VARCHAR (40), pubdate VARCHAR(4), genre VARCHAR(30), comment VARCHAR(50),path VARCHAR(200))";
            pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            stmt.close();
            fileWriter = new FileWriter("playlistnames.txt",true);
            fileWriter.write(playlistName);
            fileWriter.append(System.lineSeparator());
            fileWriter.close();
        }
        catch(SQLException e)
        {
            //System.out.println("TABLE ALREADY EXISTS");
            e.printStackTrace();
        }         
    }
}
