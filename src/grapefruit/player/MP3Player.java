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
import static grapefruit.player.GrapefruitPlayer.gui;
import java.io.File;
import java.io.IOException;
import static java.nio.file.Files.delete;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

/**
 *
 * @author ken_m
 */
public class MP3Player implements Runnable
{
    private SourceDataLine line;
    private AudioInputStream din;
    private boolean temp;
    private byte[] data;
    private int nBytesRead,nBytesWritten;
    private SourceDataLine res;
    private AudioInputStream in;
    private boolean paused = false;
    private Object lock;    
    private Mp3File mp3;
    private ID3v2 id3v2Tag;
    private String title;
    private String album;
    private String artist;
    private String year;
    private String comment;
    private String genre;
    private String path;
    private String hms;
    private int gain;
    private FloatControl volumeController;
    private File file;
    private String timeElapsed;
    private AudioFileFormat baseFileFormat;
    private Map properties;
    private Long duration,timeLeft,percDone;
    
    public MP3Player() throws IOException, UnsupportedTagException, InvalidDataException
    {
        lock = new Object();
        
    }
    public MP3Player(String filePath) throws IOException, UnsupportedTagException, InvalidDataException
    {
        lock = new Object();
        path = filePath;
        mp3 = new Mp3File(filePath);
        id3v2Tag = mp3.getId3v2Tag(); 
        title = id3v2Tag.getTitle();
        album = id3v2Tag.getAlbum();
        artist = id3v2Tag.getArtist();
        year = id3v2Tag.getYear();
        comment = id3v2Tag.getComment();
        genre = id3v2Tag.getGenreDescription();
        
    }
    public void testPlay(String filename)
    {
        try 
        {
            file = new File(filename);
            in= AudioSystem.getAudioInputStream(file);
            din = null;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                                                                                          baseFormat.getSampleRate(),
                                                                                          16,
                                                                                          baseFormat.getChannels(),
                                                                                          baseFormat.getChannels() * 2,
                                                                                          baseFormat.getSampleRate(),
                                                                                          false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            // Play now. 
            rawplay(decodedFormat, din);
            in.close();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } 

    private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException,                                                                                                LineUnavailableException,                                                                                                 UnsupportedAudioFileException
    {
      data = new byte[4096];
      line = getLine(targetFormat); 
      volumeController = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
      if (line != null)
      {
        // Start
        line.start();
        nBytesRead = 0;
        nBytesWritten = 0;
        while (nBytesRead != -1)
        {
            nBytesRead = din.read(data, 0, data.length);
            if (nBytesRead != -1) 
                nBytesWritten = line.write(data, 0, nBytesRead);
            synchronized (lock) 
            {
                while ((nBytesRead = din.read(data, 0, data.length)) != -1) 
                {
                    while (paused) 
                    {
                        if(line.isRunning()) 
                        {
                            line.stop();
                        }
                        try 
                        {
                            lock.wait();
                        }
                        catch(InterruptedException e) 
                        {
                        }                        
                    }	
		if(!line.isRunning()) 
                {
			line.start();
		}
                line.write(data, 0, nBytesRead);     
                gui.redrawProgress(line.getMicrosecondPosition());
                //timeElapsed = timePassed(line.getMicrosecondPosition());
                volumeController.setValue((float) gain);
                    
                }
            }
        }
        // Stop
        line.drain();
        line.stop();
        line.close();
        din.close();
      } 
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
    {
      res = null;
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
      res = (SourceDataLine) AudioSystem.getLine(info);
      res.open(audioFormat);
      return res;
    } 
    
    public boolean isActive()
    {
        try
        {
            if(line.isActive() == true)
                temp = true;
            else 
                temp = false;
        }
        catch (NullPointerException e)
        {
        }
        finally
        {
            return temp;
        }
    }
    public boolean isOpen()
    {
        try
        {
            if(line.isOpen() == true)
                temp = true;
            else 
                temp = false;
        }
        catch (NullPointerException e)
        {
        }
        finally
        {
            return temp;
        }
    }
    public boolean isRunning()
    {
        try
        {
            if(line.isRunning() == true)
                temp = true;
            else 
                temp = false;
        }
        catch (NullPointerException e)
        {
        }
        finally
        {
            return temp;
        }
    }
    public void stopPlay() throws IOException
    {
        if(line.isActive())
        {
            paused = true;
            line.stop();
            line.flush();
            line.close();
            din.close();
            in.close();            
        }
    }
    
    public void userPressedPause() 
    {
        paused = true;
    }
    
    public void userPressedPlay() 
    {
        synchronized(lock) 
        {
            paused = false;
            lock.notifyAll();
        }    
    }
    public void setPath(String filePath) throws IOException, UnsupportedTagException, InvalidDataException
    {
        file = new File(filePath);
        path = filePath;
        mp3 = new Mp3File(path);
        id3v2Tag = mp3.getId3v2Tag();
        title = id3v2Tag.getTitle();
        album = id3v2Tag.getAlbum();
        artist = id3v2Tag.getArtist();
        year = id3v2Tag.getYear();
        comment = id3v2Tag.getComment();
        genre = id3v2Tag.getGenreDescription();
    }
    public String getPath()
    {
        return path;
    }
    @Override
    public void run() 
    {
        
        if(paused == true)
        {
            synchronized(lock) 
            {
                paused = false;
                lock.notifyAll();
            }
        }
        else
        {
            testPlay(path);
        }
        
            
    }
    public String getTitle()
    {
        return title;
    }
    public String getAlbum()
    {
        return album;
    }
    public String getArtist()
    {
        return artist;
    }
    public String getYear()
    {
        return year;
    }
    public String getComment()
    {
        return comment;
    }
    public String getGenre()
    {
        return genre;
    }
    public void setTitle(String t)
    {
        title = t;
    }
    public void setAlbum(String a)
    {
        album = a;
    }
    public void setArtist(String a)
    {
        artist = a;
    }
    public void setYear(String y)
    {
        year = y;
    }
    public void setGenre(String s)
    {
        genre = s;
    }
    public void setComment(String c)
    {
        comment = c;
    }
    public void printMp3Info()
    {
        System.out.println("Title: " + getTitle());
        System.out.println("Album: " + getAlbum());
        System.out.println("Artist: " + getArtist());
        System.out.println("Year: " + getYear());
        System.out.println("Genre: " + getGenre());
        System.out.println("Comment: " + getComment());
    }
    public void setVolume(int g)
    {
        gain = g-60;
        if(gain == -60)
            gain = -80;
    }
    public int getVolume()
    {
        return gain;
    }
    public String getTimeElapsed()
    {
        return timeElapsed;
    }
    public String getSongDuration() throws UnsupportedAudioFileException, IOException
    {
        //File file = new File("filename.mp3");
        baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
        properties = baseFileFormat.properties();
        duration = (Long) properties.get("duration");
        System.out.println("DURATION: " + duration);
        hms = String.format("%02d:%02d:%02d", TimeUnit.MICROSECONDS.toHours(duration),
            TimeUnit.MICROSECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MICROSECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));   
        return hms;
    }
    public String timeLeft(Long microsec) throws UnsupportedAudioFileException, IOException
    {
        baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
        properties = baseFileFormat.properties();
        duration = (Long) properties.get("duration");
        timeLeft = duration-microsec;
        hms = String.format("%02d:%02d:%02d", TimeUnit.MICROSECONDS.toHours(timeLeft),
            TimeUnit.MICROSECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MICROSECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1));   
        return hms;
    }
    //gets the time that has passed since song started
    public String timePassed(Long microsec)
    {
        hms = String.format("%02d:%02d:%02d", TimeUnit.MICROSECONDS.toHours(microsec),
            TimeUnit.MICROSECONDS.toMinutes(microsec) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MICROSECONDS.toSeconds(microsec) % TimeUnit.MINUTES.toSeconds(1));  
        return hms;
    }
    public int percentDone(Long microsec)
    {
        if(microsec != 0)
        {
            percDone = (100*(TimeUnit.MICROSECONDS.toSeconds(duration)-TimeUnit.MICROSECONDS.toSeconds(duration-microsec))/TimeUnit.MICROSECONDS.toSeconds(duration));
            if(percDone == 100)
                return 0;
            else
                return (int) (100*(TimeUnit.MICROSECONDS.toSeconds(duration)-TimeUnit.MICROSECONDS.toSeconds(duration-microsec))/TimeUnit.MICROSECONDS.toSeconds(duration));
        }
        return 0;
    }
    }
