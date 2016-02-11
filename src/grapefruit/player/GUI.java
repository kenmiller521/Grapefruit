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

/**
 *
 * @author ken_m
 */
public class GUI extends JFrame{
    
    JPanel panel;
    JButton play, pause,stop,back,forward;
    public GUI(){
        super("My GUI");
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
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
        }        
    }
    class stopButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
        }        
    }
    class pauseButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("You pressed " + e.getActionCommand());
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
