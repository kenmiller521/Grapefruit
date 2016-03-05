/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import static grapefruit.player.GrapefruitPlayer.db;
import static grapefruit.player.GrapefruitPlayer.player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.lang.String;
import grapefruit.player.MP3Player;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.lang.Object;
import static java.nio.file.Files.delete;
import java.sql.SQLException;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author ken_m
 */
public class GUI extends JFrame{
    JFrame frame;
    JPanel playerButtonsPanel;
    JButton play, pause,stop,back,forward;
    //MP3Player player;
    private Thread t;
    private boolean paused;
    JTable dataTable;
    JScrollPane sp;
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    private String columnNames[] = {"Title", "Album","Artist","Year","Genre","Comment","File Path"};
    /**
     *
     * @throws IOException
     * @throws UnsupportedTagException
     * @throws InvalidDataException
     * @throws SQLException
     */
    @SuppressWarnings("empty-statement")
    public GUI() throws IOException, UnsupportedTagException, InvalidDataException, SQLException
    {
        super("My GUI");
        paused = false;
        this.setSize(700, 500);
        this.setTitle("Grapefruit Player");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playerButtonsPanel = new JPanel();
        this.add(playerButtonsPanel,BorderLayout.SOUTH);
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
        //JList list = new JList();
        
        
       /* Object[][] data = {
            {player.getTitle(),player.getAlbum(),player.getArtist(),player.getYear(),player.getComment()},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"}};*/
        db.findNumbItems();
        db.findNumbCols();
       // Object[][] data = new Object[db.getNumbItems()][db.getNumbCols()];
        //db.populateTable(data);
        Object data[][] = db.populateTable(db.getNumbItems(),db.getNumbCols());
        
        //for(int i =0; i < db.getNumbItems(); i++)
        //    for(int j = 0; j < db.getNumbCols(); j++)
//                System.out.println(data[i][j]);
        /*Object[][] data = {
            {"test","test","test","test","test"},
            {"test","test","test","test","test"},
            {"test","test","test","test","test"}};*/
        dataTable = new JTable(data,columnNames);
        sp = new JScrollPane(dataTable);
        dataTable.setFillsViewportHeight(true);
        dataTable.getSelectionModel().addListSelectionListener(new rowSelector());
        //sp = new JScrollPane();
        this.add(sp);
        //this.add(sp);
        //this.add(list);
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
            
            if(player.isRunning())
            {
                try 
                {
                    player.stopPlay();
                    t = new Thread(player, "test");
                    t.start();
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        menuItem = new JMenuItem("Play",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Play the song");
        menuItem.addActionListener(new playButtonListener());
        
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Pause",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new pauseButtonListener());
        menu.add(menuItem);
        menuItem = new JMenuItem("Stop",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new stopButtonListener());
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Skip to next",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
        menu.add(menuItem);
        menuItem = new JMenuItem("Skip to previous",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new MenuDemo());
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
        menuItem.addActionListener(new addSongMenuButton());
        
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
        public void actionPerformed(ActionEvent e) 
        {
            
            System.out.println("EXITING");
            System.exit(0);            
        }        
    }
    public class rowSelector implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) 
        {
            //GET TAG INFORMATION TO PLAY SONG
            if(!e.getValueIsAdjusting())
            {
                if (dataTable.getSelectedRow() > -1) 
                {
                    
                    try 
                    {
                        player.setPath(dataTable.getValueAt(dataTable.getSelectedRow(), 6).toString());
                        player.printMp3Info();
                    } catch (IOException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedTagException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidDataException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    public void openFileExplorer() throws IOException, UnsupportedTagException, InvalidDataException, SQLException
    {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files","mp3");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) 
        {
            System.out.println("You chose to open this file: " +
            chooser.getSelectedFile().getName());
            System.out.println("File Path: " + chooser.getSelectedFile().getPath());
            player.setPath(chooser.getSelectedFile().getPath());
            System.out.println("PRINTING INFO");
            player.printMp3Info();
            db.addSong();
            //dataTable.setVisible(false);
            //dataTable.setVisible(true);
        }
    }
        public class addSongMenuButton implements ActionListener
        {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                openFileExplorer(); 
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedTagException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidDataException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
                }
    }
}