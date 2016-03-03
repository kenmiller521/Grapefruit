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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
/**
 *
 * @author ken_m
 */
public class GUI extends JFrame{
    JFrame frame;
    JPanel playerButtonsPanel;
    JButton play, pause,stop,back,forward;
    MP3Player player;
    private Thread t;
    private boolean paused;
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    public GUI()
    {
        super("My GUI");
        paused = false;
        player = new MP3Player();
        
        this.setSize(700, 500);
        this.setTitle("Grapefruit Player");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playerButtonsPanel = new JPanel();
        this.add(playerButtonsPanel);
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
        playerButtonsPanel.add(play);
        playerButtonsPanel.add(pause);
        playerButtonsPanel.add(stop);
        playerButtonsPanel.add(back);
        playerButtonsPanel.add(forward);  
        
        
        //Call the function to create a menu bar and add to the frame
        this.setJMenuBar(addMenuBar());
        
        
        
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
    public JMenuBar addMenuBar()
    {
        

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "Basic Functions");
        menuBar.add(menu);
        
        
        //a group of JMenuItems
        menuItem = new JMenuItem("PLACEHOLDER 1",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 2",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 3",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 4",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new exitMenuItem());
        menu.add(menuItem);
        
        //ADD VIEW MENU
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.getAccessibleContext().setAccessibleDescription(
                "Basic Functions");
        menuBar.add(menu);
        //ADD MENU ITEMS BELOW
        menuItem = new JMenuItem("PLACEHOLDER 1",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 2",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 3",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 4",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        
        
        
        //ADD EDIT MENU
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "Basic Functions");
        menuBar.add(menu);
        //ADD MENU ITEMS BELOW
        menuItem = new JMenuItem("PLACEHOLDER 1",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 2",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 3",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        menuItem = new JMenuItem("PLACEHOLDER 4",
                                 KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        
        
        
        
        //ADD PLAYER MENU
        menu = new JMenu("Player");
        menu.setMnemonic(KeyEvent.VK_P);
        menu.getAccessibleContext().setAccessibleDescription(
                "Basic Functions");
        menuBar.add(menu);
        //ADD MENU ITEMS BELOW
        
        //Add a song menu item
        menuItem = new JMenuItem("Open a song",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Add a song",
                                 KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        
        menu.add(menuItem);
        //Delete a song menu item
        menuItem = new JMenuItem("Delete a song",
                                 KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        
        menu.add(menuItem);
        
        return menuBar;
    }
    public class MenuDemo implements ActionListener,ItemListener{
        
        @Override
        public void actionPerformed(ActionEvent e) {
           System.out.println("You clicked " + e.getActionCommand());
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            System.out.println("TEST LISTENER");
          }
        
    }
    public class exitMenuItem implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("EXITING");
            System.exit(0);
        }        
    }
}
