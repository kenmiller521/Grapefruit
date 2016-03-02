/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.lang.Runnable;
import grapefruit.player.MP3Player;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ken_m
 */
public class GUI extends JFrame{
    
    JPanel panel;
    JButton play, pause,stop,back,forward;
    MP3Player player;
    private Thread t;
    private boolean paused;
    public GUI()
    {
        super("My GUI");
        paused = false;
        player = new MP3Player();
        
        this.setSize(700, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        this.add(panel);
        play = new JButton("Play");
        play.addActionListener(new playButtonListener());
        pause = new JButton("Pause");
        pause.addActionListener(new pauseButtonListener());
        stop = new JButton("Stop");
        stop.addActionListener(new stopButtonListener());
        back = new JButton("Back");
        back.addActionListener(new backButtonListener());
        forward = new JButton("Forward");
        forward.addActionListener(new forwardButtonListener());
        panel.add(play);
        panel.add(pause);
        panel.add(stop);
        panel.add(back);
        panel.add(forward);
        
        
        
        setVisible(true);
    }
    
    class playButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            //TEMP SPOT TO TEST BUTTON FUNCTIONALITIES
            player.setPath("C:/Users/USER/Desktop/SONGNAME.mp3");
            if(player.isRunning())
            {
                System.out.println("ALREADY ACTIVE");
            }
            else
            {    
                t = new Thread(player, "test");
                t.start();
            }                       
        }        
    }
    class stopButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
            if(player.isActive() == true)
            {
                try {
                    player.stopPlay();
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    class pauseButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(player.isActive() == true)
            {
                System.out.println("PAUSE PLAY");
                player.userPressedPause();
                //player.pausePlay();
            }
            else
            {
                System.out.println("RESUME PLAY");
                player.userPressedPlay();
            }
        }        
    }
    class backButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
        }        
    }
    class forwardButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
        }        
    }
}
