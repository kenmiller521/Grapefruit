/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grapefruit.player;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import static grapefruit.player.GrapefruitPlayer.db;
import static grapefruit.player.GrapefruitPlayer.file;
import static grapefruit.player.GrapefruitPlayer.player;
import static grapefruit.player.SQLDatabase.dispNull;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import static java.awt.datatransfer.DataFlavor.javaFileListFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
/**
 *
 * @author ken_m
 */
public class GUI extends JFrame{
    JFrame frame,playlistframe;
    JPanel playerButtonsPanel,playerButtonsPanelPlaylist;
    JButton play, pause,stop,back,forward,playplaylist,pauseplaylist, stopplaylist, backplaylist, forwardplaylist;
    //MP3Player player;
    private Thread t;
    private boolean paused;
    JTable dataTable,dataTablePlaylist;
    JScrollPane sp;
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    private String columnNames[] = {"Title", "Album","Artist","Year","Genre","Comment","File Path"};
    private int currentSongIndex;
    DefaultTableModel model = new DefaultTableModel();
    //DefaultTableModel modelplaylist = new DefaultTableModel();
    private JMenuItem menuItemAdd,menuItemDelete, menuItemClose;
    private JMenu playlistSubmenu;
    private JPopupMenu popupMenu,playlistPopupMenu;
    private JFileChooser chooser;
    private JTree tree;
    private JScrollPane treeView;
    private JSplitPane splitPane;
    static final int FPS_MIN = 0;
    static final int FPS_MAX = 60;
    static final int FPS_INIT = 15;
    private int volumeLevel;
    private JSlider volumeSlider,volumeSliderPlaylist;
    private String playlistName = "songs";
    private String previouslySelectedPlaylistName = "songs";
    private JTextField createPlaylistTextField;
    private JFrame createPlaylistframe;
    private DefaultMutableTreeNode playlistNode;
    private DefaultMutableTreeNode playlists;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String line; //The line in the text file to be read
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode top;
    private JFrame createPlaylistFrame;
    private DefaultTableModel modelPlaylist;
    //private JTable dataTablePlaylist;
    private JScrollPane spPlaylist;
    private TreePath[] paths;
    private BufferedReader br;
    private PrintWriter pw;
    private boolean isNotInMainLibraryAndAdded;
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
        playlistframe = new JFrame();
        playlistframe.setSize(800,500);
        playlistframe.setTitle("Grapfruit Player New Window");
      
        this.setSize(800, 500);
        this.setTitle("Grapefruit Player");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playerButtonsPanel = new JPanel();
        playerButtonsPanelPlaylist = new JPanel();
        this.add(playerButtonsPanel,BorderLayout.SOUTH);
        playlistframe.add(playerButtonsPanelPlaylist,BorderLayout.SOUTH);
        play = new JButton("Play");
        playplaylist = new JButton("Play");
        playplaylist.addActionListener(new playButtonPlaylistListener());
        play.addActionListener(new playButtonListener());
        pause = new JButton("Pause");
        pause.addActionListener(new pauseButtonListener());
        pauseplaylist = new JButton("Pause");
        pauseplaylist.addActionListener(new pauseButtonListener());
        stop = new JButton("Stop");
        stopplaylist = new JButton("Stop");
        stopplaylist.addActionListener(new stopButtonListener());
        stop.addActionListener(new stopButtonListener());
        back = new JButton("Back");
        backplaylist = new JButton("Back");
        backplaylist.addActionListener(new backButtonPlaylistListener());
        back.addActionListener(new backButtonListener());
        forward = new JButton("Forward");
        forwardplaylist = new JButton("Forward");
        forwardplaylist.addActionListener(new forwardButtonPlaylistListener());
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
        popupMenu.addSeparator();
        playlistSubmenu = new JMenu("Add to Playlist");
        fileReader = new FileReader("playlistnames.txt");
        bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null)
        {            
             menuItem = new JMenuItem(line);
             menuItem.addActionListener(new addSongToPlaylistFromPopupMenu());
             playlistSubmenu.add(menuItem);
        }
        popupMenu.add(playlistSubmenu);
        
       /* Object[][] data = {
            {player.getTitle(),player.getAlbum(),player.getArtist(),player.getYear(),player.getComment()},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"},
            {"TEST","test","test","test","test"}};*/
        db.findNumbItems("songs");
        db.findNumbCols("songs");
       // Object[][] data = new Object[db.getNumbItems()][db.getNumbCols()];
        //db.populateTable(data);
        Object data[][] = db.populateTable("songs", db.getNumbRows(),db.getNumbCols());
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
        dataTable.setDragEnabled(true);
        dataTable.setDropMode(DropMode.INSERT_ROWS);
        dataTable.setTransferHandler(new TableRowTransferHandler(dataTable));
        //dataTable = new JTable(data,columnNames);
        sp = new JScrollPane(dataTable);
        dataTable.setFillsViewportHeight(true);
        dataTable.getSelectionModel().addListSelectionListener(new rowSelector());
        dataTable.setModel(model);
        
        
         modelPlaylist = new DefaultTableModel(data, columnNames);
       // dataTable = new JTable(data,columnNames);
        dataTablePlaylist = new JTable(modelPlaylist);
    //   dataTable.addMouseListener(new TableMouseListener(dataTable));
        dataTablePlaylist.setComponentPopupMenu(popupMenu);
        dataTablePlaylist.setDragEnabled(true);
        dataTablePlaylist.setDropMode(DropMode.INSERT_ROWS);
        dataTablePlaylist.setTransferHandler(new TableRowPlaylistTransferHandler(dataTablePlaylist));
        //dataTable = new JTable(data,columnNames);
        sp = new JScrollPane(dataTablePlaylist);
        dataTablePlaylist.setFillsViewportHeight(true);
        dataTablePlaylist.getSelectionModel().addListSelectionListener(new rowSelector());
        dataTablePlaylist.setModel(modelPlaylist);
        dataTablePlaylist.removeColumn(dataTablePlaylist.getColumnModel().getColumn(6));
        dataTable.setAutoCreateRowSorter(true);
        dataTablePlaylist.setAutoCreateRowSorter(true);
        //this.add(sp);
        //this.add(sp);
        //this.add(list);
        volumeSlider = new JSlider(JSlider.HORIZONTAL,
                                      FPS_MIN, FPS_MAX, FPS_INIT);
        volumeSlider.addChangeListener(new volumeSliderListener());
        volumeSlider.setValue((int) (FPS_MAX/1.5));
               volumeSliderPlaylist = new JSlider(JSlider.HORIZONTAL,
                                     FPS_MIN, FPS_MAX, FPS_INIT);
        volumeSliderPlaylist.addChangeListener(new volumeSliderListener());
        volumeSliderPlaylist.setValue((int) (FPS_MAX/1.5));
        //volumeSlider.setMajorTickSpacing(10);
        //volumeSlider.setMinorTickSpacing(1);
        //volumeSlider.setPaintTicks(true);
        //volumeSlider.setPaintLabels(true);
        playerButtonsPanel.add(play);
        playerButtonsPanel.add(pause);
        playerButtonsPanel.add(stop);
        playerButtonsPanel.add(back);
        playerButtonsPanel.add(forward);  
        playerButtonsPanel.add(volumeSlider);
        playerButtonsPanelPlaylist.add(playplaylist);
        playerButtonsPanelPlaylist.add(pauseplaylist);
        playerButtonsPanelPlaylist.add(stopplaylist);
        playerButtonsPanelPlaylist.add(backplaylist);
        playerButtonsPanelPlaylist.add(forwardplaylist);  
        playerButtonsPanelPlaylist.add(volumeSliderPlaylist);

        
        
        //Call the function to create a menu bar and add to the frame
        this.setJMenuBar(addMenuBar());
        playlistframe.setJMenuBar(addMenuBar());
        
        top = new DefaultMutableTreeNode("Grapefruit");
        tree = new JTree(top);
        treeModel = (DefaultTreeModel)tree.getModel();
        createNodes(top);
        treeView = new JScrollPane(tree);
        
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new treeListener());
        
        playlistPopupMenu = new JPopupMenu();
        menuItem = new JMenuItem("Open in New Window");
        playlistPopupMenu.add(menuItem);
        menuItem.addActionListener(new OpenInNewWindow());
        menuItem = new JMenuItem("Delete Playlist");
        playlistPopupMenu.add(menuItem);
        menuItem.addActionListener(new DeletePlaylist());
        tree.setComponentPopupMenu(playlistPopupMenu);
        //treeView.setComponentPopupMenu(playlistPopupMenu);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           treeView, sp);
        splitPane.setDividerLocation(140);
        this.add(splitPane);
        //this.add(treeView, BorderLayout.WEST);
        
        setVisible(true);
        
    }
    class OpenInNewWindow implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            System.out.println("You clicked Open Window!");
            
            
            try 
            {
                createPlaylistNewWindowTableView(playlistName);
                createPlaylistTableView("songs");
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            playlistframe.setVisible(true);
        }
    } 
    class DeletePlaylist implements ActionListener
    {
        private TreePath[] paths;
        @Override
        public void actionPerformed(ActionEvent e)
        {
            
            
            try 
            {
                System.out.println("You clicked Delete Playlist!");
                //Delete from tree
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (tree.getModel());
                paths = tree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) 
                {
                    node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
                    model.removeNodeFromParent(node);
                }
            
                //Delete from Database
                db.dropTable(playlistName);
                
                //Delete from text file
                File inputFile = new File("playlistnames.txt");
                File tempFile = new File("myTempFile.txt");

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String lineToRemove = playlistName;
                String currentLine;

                while((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if(trimmedLine.equals(lineToRemove)) continue;
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
                boolean successful = tempFile.renameTo(inputFile);
                writer.close(); 
                reader.close(); 
                
                
                FileReader fr = null;
                FileWriter fw = null;
                try 
                {
                    fr = new FileReader("myTempFile.txt");
                    fw = new FileWriter("playlistnames.txt");
                    int c = fr.read();
                    while(c!=-1) {
                        fw.write(c);
                        c = fr.read();
                    }                
                } finally {
                    close(fr);
                    close(fw);
                }
                  
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
     public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch(IOException e) {
            //...
        }
    }
    class addSongToPlaylistFromPopupMenu implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            playlistName = e.getActionCommand();
            player.printMp3Info();
            try 
            {
                //db.addSong(playlistName);
                addSongToPlaylistTable();
                //addSongToTable();
            }
            catch (SQLException ex) 
            {
                //Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            } 
            catch (FileNotFoundException ex) 
            {
                //Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
            
        }
    
    }
    class MenuTableListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JMenuItem menu = (JMenuItem)e.getSource();
            if(dataTable.getSelectedRow() > -1 ||dataTablePlaylist.getSelectedRow() > -1)
            {
                System.out.println("Selected: " + e.getActionCommand());
                if (e.getActionCommand() == "Add New Song")
                {
                    try 
                    {
                        openFileExplorerAndAddedSong(); 
                        db.addSong(playlistName);
                        addSongToTable();
                        addSongToPlaylistTable();
                        //createPlaylistNewWindowTableView(playlistName);
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
                    System.out.println("Add song selected");
                } 
                else if (e.getActionCommand()=="Delete Song") 
                {
                    int selectedRowPlaylist = dataTablePlaylist.getSelectedRow();
                    int selectedRow = dataTable.getSelectedRow();
                    try 
                    {
                        String spl = dataTablePlaylist.getValueAt(dataTablePlaylist.getSelectedRow(), 0).toString();
                        System.out.println(spl);
                        db.deleteSong(previouslySelectedPlaylistName, spl);
                        modelPlaylist.removeRow(dataTablePlaylist.getSelectedRow());
                        
                        String s = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
                        System.out.println(s);
                        db.deleteSong(playlistName, s);
                        model.removeRow(dataTablePlaylist.getSelectedRow());
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (SQLException ex) 
                    {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    final int c = dataTable.getSelectedRow();
                    model.removeRow(c);
                    System.out.println("Delete song selected");
                } 
                else if (e.getActionCommand() =="Cancel") 
                {
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
                        
                        player.stopPlay();
                        player = null;
                        player = new MP3Player();
                        player.setPath(dataTable.getModel().getValueAt(dataTable.getSelectedRow(), 6).toString());
                        player.printMp3Info();
                        //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                        if(t.isAlive())
                        {                                     
                            t = new Thread(player,"test");
                            t.start();
                            player.setVolume(volumeLevel);
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
                    
                    //player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getModel().getValueAt(dataTable.getSelectedRow(),6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());                  
                    t = new Thread(player,"test");
                    t.start();
                    player.setVolume(volumeLevel);
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
    class playButtonPlaylistListener implements ActionListener
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
                        
                        player.stopPlay();
                        player = null;
                        player = new MP3Player();
                        player.setPath(dataTablePlaylist.getModel().getValueAt(dataTablePlaylist.getSelectedRow(), 6).toString());
                        player.printMp3Info();
                        //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                        if(t.isAlive())
                        {                                     
                            t = new Thread(player,"test");
                            t.start();
                            player.setVolume(volumeLevel);
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
                    
                    //player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTablePlaylist.getModel().getValueAt(dataTablePlaylist.getSelectedRow(),6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());                  
                    t = new Thread(player,"test");
                    t.start();
                    player.setVolume(volumeLevel);
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
                    
                    
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getModel().getValueAt(currentSongIndex, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
                    
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getModel().getValueAt(currentSongIndex-=1, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
    class backButtonPlaylistListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            System.out.println(dataTablePlaylist.getRowCount());
            int maxRows = dataTablePlaylist.getRowCount();
            System.out.println(maxRows);
            System.out.println(dataTablePlaylist.getSelectedRow());
            //compare if top, if so then move to bottom, stop current music then play;
            if(currentSongIndex == 0)
            {
                System.out.println("WRAPPING AROUND");
                try 
                {
                    currentSongIndex = dataTablePlaylist.getRowCount()-1;
                    
                   
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTablePlaylist.getModel().getValueAt(currentSongIndex, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
                    
                    player.stopPlay();
                   // player = null;
                    //player = new MP3Player();
                    player.setPath(dataTablePlaylist.getModel().getValueAt(currentSongIndex-=1, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
                    
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getModel().getValueAt(currentSongIndex, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTable.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
                  
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTable.getModel().getValueAt(currentSongIndex+=1, 6).toString());
                    player.printMp3Info();
                   // player.setPath(dataTable.getValueAt(currentSongIndex+=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
    class forwardButtonPlaylistListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            int maxRows = dataTablePlaylist.getRowCount();
            System.out.println("Maxrows: " + maxRows);
            System.out.println("Current Song Index: " + currentSongIndex);
            if(currentSongIndex == maxRows-1)
            {
                System.out.println("WRAPPING AROUND");
                currentSongIndex = 0;
                player.printMp3Info();
                try {
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTablePlaylist.getModel().getValueAt(currentSongIndex, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTablePlaylist.g)
                    //player.setPath(dataTablePlaylist.getValueAt(currentSongIndex, 6).toString());
                    //player.setPath(dataTable.getValueAt(currentSongIndex-=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
                    player.stopPlay();
                    player = null;
                    player = new MP3Player();
                    player.setPath(dataTablePlaylist.getModel().getValueAt(currentSongIndex+=1, 6).toString());
                    player.printMp3Info();
                    //player.setPath(dataTablePlaylist.getValueAt(currentSongIndex+=1, 6).toString());
                    if(t.isAlive())
                    {                      
                        t = new Thread(player,"test");
                        t.start();
                        player.setVolume(volumeLevel);
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
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Create Playlist", KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Creates a playlist");
         menuItem.addActionListener(new createPlaylistButton());
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
            currentSongIndex = dataTablePlaylist.getSelectedRow();
            currentSongIndex = dataTable.getSelectedRow();
            System.out.println(currentSongIndex);
            //GET TAG INFORMATION TO PLAY SONG
            if(!e.getValueIsAdjusting())
            {
                if (currentSongIndex > -1) 
                {
                    
                    try 
                    {
                        player.setPath(dataTable.getModel().getValueAt(dataTable.getSelectedRow(), 6).toString());
                        //player.setPath(dataTable.getValueAt(dataTable.getSelectedRow(), 6).toString());
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
    public class rowSelectorNewWindow implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) 
        {
            
            currentSongIndex = dataTablePlaylist.getSelectedRow();
            //GET TAG INFORMATION TO PLAY SONG
            if(!e.getValueIsAdjusting())
            {
                if (dataTablePlaylist.getSelectedRow() > -1) 
                {
                    
                    try 
                    {
                        //player.setPath(dataTablePlaylist.getValueAt(dataTablePlaylist.getSelectedRow(), 6).toString());
                        player.setPath(dataTablePlaylist.getModel().getValueAt(dataTablePlaylist.getSelectedRow(), 6).toString());
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
    public void addSongToTable() throws SQLException
    {
        Object temp[] = {dispNull(player.getTitle()),
            dispNull(player.getAlbum()),
            dispNull(player.getArtist()),
            dispNull(player.getYear()),
            dispNull(player.getGenre()),
            dispNull(player.getComment()),
            dispNull(player.getPath())};
        model.addRow(temp);        
    }
    public void addSongToPlaylistTable() throws SQLException, FileNotFoundException
    {
        Object temp[] = {dispNull(player.getTitle()),
            dispNull(player.getAlbum()),
            dispNull(player.getArtist()),
            dispNull(player.getYear()),
            dispNull(player.getGenre()),
            dispNull(player.getComment()),
            dispNull(player.getPath())};
        addSongToMainLibrary();
        modelPlaylist.addRow(temp); 
        db.addSong(previouslySelectedPlaylistName);
        if(isNotInMainLibraryAndAdded == true)
            model.addRow(temp);
        isNotInMainLibraryAndAdded = false;
    }
    public void addSongToMainLibrary() throws SQLException, FileNotFoundException
    {
       if(db.inLibrary(player.getTitle(), player.getArtist()))
       {           
           isNotInMainLibraryAndAdded = false;
           System.out.println("IN");
       }
       else
       {
           if(playlistName != "songs")
           {
               playlistName = "songs";
               System.out.println("$$$$$$$ "+ playlistName);
               System.out.println("********* "+ previouslySelectedPlaylistName);
               db.addSong(playlistName);
               
               isNotInMainLibraryAndAdded = true;
               
           }
           else
           {
               //if playlistName IS "songs"
               System.out.println("&&&&&&&&&&& "+ playlistName);
               db.addSong(playlistName);
               isNotInMainLibraryAndAdded = true;
               //db.addSong(previouslySelectedPlaylistName);
           }
           
               
           System.out.println("NOT IN");
       }
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
                    db.addSong(playlistName);
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
                    player.setVolume(volumeLevel);
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
            int selectedRowPlaylist = dataTablePlaylist.getSelectedRow();
            try 
            {
                if(selectedRow != -1)
                {
                    String s = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
                    System.out.println(s);
                    db.deleteSong(playlistName, s);
                    final int c = dataTable.getSelectedRow();
                    model.removeRow(c);
                    modelPlaylist.removeRow(c);
                    System.out.println("Delete song selected");
                }
               /* if(selectedRowPlaylist != -1){
                     String r = dataTablePlaylist.getValueAt(dataTablePlaylist.getSelectedRow(), 0).toString();
                     db.deleteSong(playlistName,r);
                     final int d = dataTablePlaylist.getSelectedRow();
                     modelplaylist.removeRow(d);
                }
               */ else
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
    public class createPlaylistButton implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
           //Prompt user for playlist name
            createPlaylistFrame = new JFrame();
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JLabel createPlaylistLabel = new JLabel("Playlist Name: ");
            panel.add(createPlaylistLabel);
            createPlaylistTextField = new JTextField(20);
            createPlaylistTextField.setEditable(true);
            panel.add(createPlaylistTextField);
            
            JButton doneButton = new JButton("Done");
            doneButton.addActionListener(new doneButtonCreatePlaylistListener());
            panel.add(doneButton);
            
            
            
            createPlaylistFrame.add(panel);
            createPlaylistFrame.setTitle("Create Playlist");
            createPlaylistFrame.setSize(350,100);
            createPlaylistFrame.setResizable(false);
            createPlaylistFrame.setVisible(true);
            createPlaylistFrame.getRootPane().setDefaultButton(doneButton);
        }
        
    }
    public class doneButtonCreatePlaylistListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            playlistName = createPlaylistTextField.getText();
            try {
                db.createPlaylist(playlistName);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(playlistName);   
            //Add the playlist to the popup menu
            menuItem = new JMenuItem(playlistName);
            menuItem.addActionListener(new addSongToPlaylistFromPopupMenu());
            playlistSubmenu.add(menuItem);
            //Add the playlist to the tree and refresh the tree
            playlistNode = new DefaultMutableTreeNode(playlistName);
            playlists.add(playlistNode);
            treeModel.reload(playlists);
            createPlaylistFrame.dispose();
            try {
                createPlaylistTableView(playlistName);
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }
    public class TableRowTransferHandler extends TransferHandler 
    {

        private TableRowTransferHandler(JTable dataTable) 
        {

        }
        public boolean canImport(TransferHandler.TransferSupport info) 
        {
        // Check for String flavor
            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) 
            {
                return false;
            }
            return true;
        }
        public boolean importData(TransferHandler.TransferSupport info) 
        {
            if (!info.isDrop()) 
            {
                return false;
            }
            Transferable t = info.getTransferable();
            File file = null;
            try
            {
                java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                
                for(File f : l)
                {
                    file = f;
                }
                player.setPath(file.getPath());
                player.printMp3Info();
                db.addSong(playlistName);
                addSongToTable();
                
            }
            catch(Exception e)
            {
                System.out.println("DOES NOT SUPPORT THAT INPUT");
                e.printStackTrace();
            }    
            
            return true;
        }
    }
    public class TableRowPlaylistTransferHandler extends TransferHandler 
    {

        private TableRowPlaylistTransferHandler(JTable dataTable) 
        {

        }
        public boolean canImport(TransferHandler.TransferSupport info) 
        {
        // Check for String flavor
            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) 
            {
                return false;
            }
            return true;
        }
        public boolean importData(TransferHandler.TransferSupport info) 
        {
            if (!info.isDrop()) 
            {
                return false;
            }
            Transferable t = info.getTransferable();
            File file = null;
            try
            {
                java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                
                for(File f : l)
                {
                    file = f;
                }
                player.setPath(file.getPath());
                player.printMp3Info();
                
                addSongToPlaylistTable();
                
            }
            catch(Exception e)
            {
                System.out.println("DOES NOT SUPPORT THAT INPUT");
                e.printStackTrace();
            }    
            
            return true;
        }
    }
    private void createNodes(DefaultMutableTreeNode top) throws IOException
    {
        DefaultMutableTreeNode library = null;
        playlists = null;
        playlistNode = null;
        
        library = new DefaultMutableTreeNode("Library");
        top.add(library);
        playlists = new DefaultMutableTreeNode("Playlists");
        fileReader = new FileReader("playlistnames.txt");
        bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null)
        {            
            playlistNode = new DefaultMutableTreeNode(line);
            playlists.add(playlistNode);
        }
        top.add(playlists);
        
       
    }
    
    class treeListener implements TreeSelectionListener
    {

        @Override
        public void valueChanged(TreeSelectionEvent e) 
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        
            if(node == null)
            return;
        
            Object nodeInfo = node.getUserObject();
            if(node.isLeaf())
            {
                System.out.println("You clicked " + node.toString());
                if(node.toString() == "Library")
                {
                    playlistName = "songs";
                }
                else
                {
                    playlistName = node.toString();
                    previouslySelectedPlaylistName = node.toString();
                }
                try {
                    createPlaylistTableView(playlistName);
                } catch (SQLException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                System.out.println("You clicked " + node.toString());
                playlistName = node.toString();
            }
        }        
    }
    class volumeSliderListener implements ChangeListener
    {

        @Override
        public void stateChanged(ChangeEvent e) 
        {
            JSlider source = (JSlider)e.getSource();
            if(!source.getValueIsAdjusting())
            {
                volumeLevel = (int)source.getValue();
                player.setVolume(volumeLevel);
            }
        }
    }
    public void createPlaylistTableView(String libName) throws FileNotFoundException, SQLException, IOException
    {
        splitPane.remove(sp);
        
        db.findNumbItems(libName);
        db.findNumbCols(libName);
        
        Object data[][] = db.populateTable(libName, db.getNumbRows(),db.getNumbCols());
        
        model = new DefaultTableModel(data, columnNames);
        dataTable = new JTable(model);
        dataTable.setComponentPopupMenu(popupMenu);
        dataTable.setDragEnabled(true);
        dataTable.setDropMode(DropMode.INSERT_ROWS);
        dataTable.setTransferHandler(new TableRowTransferHandler(dataTable));
        sp = new JScrollPane(dataTable);
        dataTable.setFillsViewportHeight(true);
        dataTable.getSelectionModel().addListSelectionListener(new rowSelector());
        dataTable.setModel(model);
        dataTable.removeColumn(dataTable.getColumnModel().getColumn(6));
        splitPane.add(sp);
        splitPane.setDividerLocation(140);
        dataTable.setAutoCreateRowSorter(true);
        setVisible(true);
        
    }
    public void createPlaylistNewWindowTableView(String libName) throws FileNotFoundException, SQLException, IOException
    {
        
        //playlistframe.remove(spPlaylistWindow);
        //splitPane.remove(sp);
        
        db.findNumbItems(libName);
        db.findNumbCols(libName);
        
        Object data[][] = db.populateTable(libName, db.getNumbRows(),db.getNumbCols());
        
        modelPlaylist = new DefaultTableModel(data, columnNames);
        dataTablePlaylist = new JTable(modelPlaylist);
        dataTablePlaylist.setComponentPopupMenu(popupMenu);
        dataTablePlaylist.setDragEnabled(true);
        dataTablePlaylist.setDropMode(DropMode.INSERT_ROWS);
        dataTablePlaylist.setTransferHandler(new TableRowPlaylistTransferHandler(dataTablePlaylist));
        spPlaylist = new JScrollPane(dataTablePlaylist);
        dataTablePlaylist.setFillsViewportHeight(true);
        dataTablePlaylist.getSelectionModel().addListSelectionListener(new rowSelectorNewWindow());
        dataTablePlaylist.setModel(modelPlaylist);
        dataTablePlaylist.removeColumn(dataTablePlaylist.getColumnModel().getColumn(6));
        dataTablePlaylist.setAutoCreateRowSorter(true);
        playlistframe.add(spPlaylist);
        //splitPane.add(sp);
        //splitPane.setDividerLocation(140);
        
        //setVisible(true);/
        
    }
   
}