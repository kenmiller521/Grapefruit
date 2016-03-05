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
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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
    private int genre;
    private String path;
    
    
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
        genre = id3v2Tag.getGenre();
        
    }
    public void testPlay(String filename)
    {
        try 
        {
            File file = new File(filename);
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

    private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException,                                                                                                LineUnavailableException
    {
      data = new byte[4096];
      line = getLine(targetFormat); 
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
        
        path = filePath;
        mp3 = new Mp3File(path);
        id3v2Tag = mp3.getId3v2Tag();
        title = id3v2Tag.getTitle();
        album = id3v2Tag.getAlbum();
        artist = id3v2Tag.getArtist();
        year = id3v2Tag.getYear();
        comment = id3v2Tag.getComment();
        genre = id3v2Tag.getGenre();
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
            testPlay(path);
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
    public int getGenre()
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
    public void setGenre(int i)
    {
        genre = i;
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
}
