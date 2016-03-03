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
import java.io.File;
import javazoom.jlgui.basicplayer.*;
import java.io.IOException;
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
       
       
       //Code below is for testing only. Once we figure everything out we will
       //clean up the main function by putting things in classes and functions
       
       
       //Get the path of the song, this is an example if the mp3 file
       //is on the desktop.
       String path = "C:/Users/USER/Desktop/SONGNAME.mp3";
       //Connect to SQL Database
       db = new SQLDatabase();
       db.connect();
       player = new MP3Player();
       //See if the path actually works
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
           int genre = id3v2Tag.getGenre();*/
           //Print out the information 
           System.out.println("Title: " + player.getTitle() );
           System.out.println("Album: " + player.getAlbum());
           System.out.println("Artist: " + player.getArtist());
           System.out.println("Year: " + player.getYear());
           System.out.println("Comment: " + player.getComment());
           System.out.println("Genre: " + player.getGenre());
           System.out.println(mp3.getFilename());
           gui = new GUI();
           db.findNumbItems();
           System.out.println(db.getNumbItems());
           //db.addSong();
           //Start the playback
           //MP3Player playback = new MP3Player(path);
           //playback.testPlay(path);
       }
       else
       {
           System.out.println("Error finding file.");
       }
       
       //System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
       //System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
       //System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
       //System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
       //System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
       //System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
    }    
}