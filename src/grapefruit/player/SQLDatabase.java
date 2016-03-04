/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import static grapefruit.player.GrapefruitPlayer.player;
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
    private ResultSetMetaData rsmd;
    private int numbItems;
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
                sql = "CREATE TABLE songs (title VARCHAR(20), album VARCHAR(20), artist VARCHAR (20), pubdate VARCHAR(20), genre integer, comment VARCHAR(50))";
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
    public void addSong() throws SQLException
    {
        try
        {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            //sql = "INSERT INTO songs (TITLE,album,artist,pubdate,genre,comment) VALUES(' + player.getTitle() + "'','" + player.getAlbum()+ "','" +player.getArtist()+ "','" +player.getYear()+ "','" +player.getGenre()+ "','" +player.getComment()+'")");
            sql = "INSERT INTO songs (title, album, artist, pubdate, genre, comment) VALUES('"+player.getTitle() +"','"+ player.getAlbum()+"','"+player.getArtist()+"','"+player.getYear()+"',"+player.getGenre()+",'"+player.getComment()+"')";
            System.out.println(sql.toString());
            //stmt.execute(sql);
            stmt.executeUpdate(sql);
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
               numbItems = rs.getInt("COUNT");
            }

 //Closing the connection
            stmt.close();
            conn.close();
            /*
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM songs");
            numbItems = rs.last() ? rs.getRow() : 0;*/
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
            
    public int getNumbItems()
    {
        return numbItems;
    }
    public int getNumbCols()
    {
        return numbCols;
    }
    public Object[][] populateTable(Object[][] data) throws SQLException
    {
        try
        {
            findNumbItems();
            findNumbCols();
            conn = DriverManager.getConnection(DB_URL);            
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM songs");
            rsmd = rs.getMetaData();
            
            System.out.println(numbItems);
            System.out.println(numbCols);
            /*
            for(int i = 1; i <= getNumbCols(); i++)
            {
                System.out.print(rsmd.getColumnLabel(i)+"\t\t"); 
            }
            
            System.out.println("\n-------------------------------------------------");
            */
            while(rs.next())
            {
                for(int i = 0; i < numbItems; i++)
                {
                    for(int j = 0; j <= numbCols;j++)
                    {
                        if(j == 5)
                        {
                            data[i][j] = rs.getInt(j+1);
                        }
                        else
                        {
                            data[i][j] = rs.getString(j+1);
                        }                        
                    }
                }
                /*
                String t = rs.getString(1);
                String al = rs.getString(2);
                String ar = rs.getString(3);
                String y = rs.getString(4);
                int g = rs.getInt(5);
                String c = rs.getString(6);
                System.out.println(t + "\t\t" + al + "\t\t" + ar + "\t\t" + y + "\t\t" + g + "\t\t" + c);
                */
            }
            rs.close();
            stmt.close();
            conn.close();
            
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return data;
    }
}
