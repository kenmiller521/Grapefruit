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
import static grapefruit.player.SQLDatabase.dispNull;
import static grapefruit.player.SQLDatabase.dispNullInt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private int currentSongIndex;
    DefaultTableModel model = new DefaultTableModel();
    private JMenuItem menuItemAdd,menuItemDelete, menuItemClose;
    private JPopupMenu popupMenu;
    private JFileChooser chooser;
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
        popupMenu = new JPopupMenu();
       
        menuItemAdd = new JMenuItem("Add New Song");
        menuItemDelete = new JMenuItem("Delete Song");
        menuItemClose = new JMenuItem("Cancel");
       
        menuItemAdd.addActionListener(new MenuTableListener());
        menuItemDelete.addActionListener(new MenuTableListener());
        menuItemClose.addActionListener(new MenuTableListener());
       
        popupMenu.add(menuItemAdd);
        popupMenu.add(menuItemDelete);
        popupMenu.add(menuItemClose);
        
       /* Object[][] data = {
            {player.getTitle(),player.getAlbum(),player.getArtist(),player.getYear(),player.getComment()},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"}};*/
        db.findNumbItems();
        db.findNumbCols();
       // Object[][] data = new Object[db.getNumbItems()][db.getNumbCols()];
        //db.populateTable(data);
        Object data[][] = db.populateTable(db.getNumbRows(),db.getNumbCols());
        
        //for(int i =0; i < db.getNumbItems(); i++)
        //    for(int j = 0; j < db.getNumbCols(); j++)
//                System.out.println(data[i][j]);
        /*Object[][] data = {
            {"test","test","test","test","test"},
            {"test","test","test","test","test"},
            {"test","test","test","test","test"}};*/
         model = new DefaultTableModel(data, columnNames);
       // dataTable = new JTable(data,columnNames);
        dataTable = new JTable(model);
    //   dataTable.addMouseListener(new TableMouseListener(dataTable));
        dataTable.setComponentPopupMenu(popupMenu);
        //dataTable = new JTable(data,columnNames);
        sp = new JScrollPane(dataTable);
        dataTable.setFillsViewportHeight(true);
        dataTable.getSelectionModel().addListSelectionListener(new rowSelector());
        dataTable.setModel(model);
        
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
       class MenuTableListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
                 JMenuItem menu = (JMenuItem)e.getSource();
        if(dataTable.getSelectedRow() > -1){
    
              System.out.println("Selected: " + e.getActionCommand());
           if (e.getActionCommand() == "Add New Song"){
            try {
               
                openFileExplorerAndAddedSong(); 
               
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedTagException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidDataException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Add song selected");
        } else if (e.getActionCommand()=="Delete Song") {
            int selectedRow = dataTable.getSelectedRow();
             try {
                 String s = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
                 System.out.println(s);
                           db.deleteSong(s);
               
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            final int c = dataTable.getSelectedRow();
            model.removeRow(c);
            System.out.println("Delete song selected");
      } else if (e.getActionCommand() =="Cancel") {
          System.out.println("Close is selected");
        }
        }
        }
        
        
    }
    class playButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            //TEMP SPOT TO TEST BUTTON FUNCTIONALITIES
            
            
            if(player.isRunning())
            {
                if(t.isAlive()) 
                {                    
                    try 
                    {
                        player.printMp3Info();
                        player.stopPlay();
                        player = null;
                        player = new MP3Player();
                        player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                        if(t.isAlive())
                        {                                     
                            t = new Thread(player,"test");
                            t.start();
                        }
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (UnsupportedTagException ex) 
                    {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (InvalidDataException ex) 
                    {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                          
                }
            }
            else
            {    
                try 
                {
                    player.printMp3Info();
                    //player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());                  
                    t = new Thread(player,"test");
                    t.start();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (UnsupportedTagException ex) 
                {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (InvalidDataException ex) 
                {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            }                    
        }        
    }
    class stopButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            //if(player.isActive() == true)
            //{
                try 
                {
                    player.stopPlay();
                    if(t.isAlive())
                        System.out.println("STILL ALIVE");
                    else
                        System.out.println("NOT ALIVE");
                } 
                catch (IOException ex) 
                {
                    //Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    
    class pauseButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
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
        public void actionPerformed(ActionEvent e) 
        {
            System.out.println(dataTable.getRowCount());
            int maxRows = dataTable.getRowCount();
            System.out.println(maxRows);
            System.out.println(dataTable.getSelectedRow());
            //compare if top, if so then move to bottom, stop current music then play;
            if(currentSongIndex == 0)
            {
                System.out.println("WRAPPING AROUND");
                try 
                {
                    currentSongIndex = dataTable.getRowCount()-1;
                    
                    player.printMp3Info();
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedTagException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidDataException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                try 
                {
                    //simply play previous song
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    System.out.println("Current position: " + currentSongIndex);
                    player.printMp3Info();
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                    }                    
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
    class forwardButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            int maxRows = dataTable.getRowCount();
            System.out.println("Maxrows: " + maxRows);
            System.out.println("Current Song Index: " + currentSongIndex);
            if(currentSongIndex == maxRows-1)
            {
                System.out.println("WRAPPING AROUND");
                currentSongIndex = 0;
                player.printMp3Info();
                try {
                    player.stopPlay();
                    player.printMp3Info();
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedTagException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidDataException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                 try 
                {
                    System.out.println("Current position: " + currentSongIndex);
                    player.printMp3Info();
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getValueAt(currentSongIndex+=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                    }
                    
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
        menuItem = new JMenuItem("Open song",
                                 KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new openAndPlayMenuButton());
        
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Add song",
                                 KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new addSongMenuButton());
        
        menu.add(menuItem);
        //Delete a song menu item
        menuItem = new JMenuItem("Delete song",
                                 KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        menuItem.addActionListener(new deleteMenuButton());
        
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
            
            currentSongIndex = dataTable.getSelectedRow();
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
    public boolean openFileExplorerAndAddedSong() throws IOException, UnsupportedTagException, InvalidDataException, SQLException
    {
        boolean temp = false;
        chooser = new JFileChooser();
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
            System.out.println("Rows1: " +db.getNumbRows());    
            temp = true;
        }
        return temp;
    }
    public void addSongToTable()
    {
        Object temp[] = {dispNull(player.getTitle()),
            dispNull(player.getAlbum()),
            dispNull(player.getArtist()),
            dispNull(player.getYear()),
            dispNullInt(player.getGenre()),
            dispNull(player.getComment()),
            dispNull(player.getPath())};
        model.addRow(temp);
    }
    public class addSongMenuButton implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            try 
            {
                if(openFileExplorerAndAddedSong()) 
                {
                    db.addSong();
                    addSongToTable();
                }
                else
                {
                    System.out.println("CLOSED EXPLORER BEFORE ADDING");
                }
                
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (UnsupportedTagException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (InvalidDataException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   
    public class openAndPlayMenuButton implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {            
            try 
            {                
                
                if(openFileExplorerAndAddedSong())
                {
                    addSongToTable();
                    player.printMp3Info();
                    if(player.isActive())
                    {
                        player.stopPlay();
                    }                
                    player = null;
                    player = new MP3Player();
                    player.setPath(chooser.getSelectedFile().getPath());                     
                    t = new Thread(player,"test");
                    t.start();
                }
                else
                {
                    System.out.println("CLOSED EXPLORER BEFORE ADDING");
                }
                
            }       
            catch (IOException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (UnsupportedTagException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (InvalidDataException ex) 
            {                
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public class deleteMenuButton implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            int selectedRow = dataTable.getSelectedRow();
            try 
            {
                if(selectedRow != -1)
                {
                    String s = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
                    System.out.println(s);
                    db.deleteSong(s);
                    final int c = dataTable.getSelectedRow();
                    model.removeRow(c);
                    System.out.println("Delete song selected");
                }
                else
                {
                    System.out.println("MAKE SELECTION TO DELETE");
                }
               
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }
}