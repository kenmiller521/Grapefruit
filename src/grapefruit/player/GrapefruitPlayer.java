/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javazoom.jlgui.basicplayer.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author ken_m
 */
public class GrapefruitPlayer {
    public static MP3Player player;
    public static SQLDatabase db;
    public static GUI gui;
    
    

    /**
     * @param args the command line arguments
     */
   
    public static void main(String[] args) throws IOException, UnsupportedTagException, InvalidDataException, BasicPlayerException, UnsupportedAudioFileException, ClassNotFoundException, SQLException {
       File file = new File("playlistnames.txt");
       //new File("playlistnames.txt").exists() == false
        if (file.createNewFile())
        {
            //file.createNewFile();
            System.out.println("File is created!");
        }
        else
        {
            System.out.println("File already exists.");
        }
       db = new SQLDatabase();
       db.connect();
       player = new MP3Player();
       //See if the path actually works
       /*
       File file = new File(path);
       if(file.canRead())
       {
           player.setPath(path);
           System.out.println("Song found");
           //Get the file with associated path
           Mp3File mp3 = new Mp3File(path); 
           //Get tag info
           ID3v2 id3v2Tag = mp3.getId3v2Tag();
           //Set tag info into variables
           player.setTitle(id3v2Tag.getTitle());
           player.setAlbum(id3v2Tag.getAlbum());
           player.setArtist(id3v2Tag.getArtist());
           player.setYear(id3v2Tag.getYear());
           player.setGenre(id3v2Tag.getGenre());
           player.setComment(id3v2Tag.getComment());
           String url = file.getPath();
           System.out.println("URL" + url);
           /*String title = id3v2Tag.getTitle();
           String album = id3v2Tag.getAlbum();
           String artist = id3v2Tag.getArtist();
           String year = id3v2Tag.getYear();
           String comment = id3v2Tag.getComment();
           int genre = id3v2Tag.getGenre();
           //Print out the information 
           System.out.println("Title: " + player.getTitle() );
           System.out.println("Album: " + player.getAlbum());
           System.out.println("Artist: " + player.getArtist());
           System.out.println("Year: " + player.getYear());
           System.out.println("Comment: " + player.getComment());
           System.out.println("Genre: " + player.getGenre());
           System.out.println(mp3.getFilename());
           */
           //db.findNumbItems();
           //db.addSong();
           
           //CALL GUI LAST TO MAKE SURE ALL SONGS ARE IN THE DATABASE BEFORE RENDERING THE PROGRAM
           gui = new GUI();
           /*
       }
       else
       {
           System.out.println("Error finding file.");
       }
       */
       //System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
       //System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
       //System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
       //System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
       //System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
       //System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
    }    
    
}