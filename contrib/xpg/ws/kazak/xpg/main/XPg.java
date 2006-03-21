/**
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK 
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * CLASS XPg v 0.1                                                   
 * Descripcion:
 * Esta clase es la principal de la aplicacion. Se encarga de iniciar 
 * la Interfaz Grafica, manejar los eventos de rat�n y teclado para la interfaz 
 * e instanciar las clases necesarias para realizar los diversas funciones 
 * relacionadas con bases de datos.
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *                                                                   
 * Fecha: 2001/10/01                                                 
 *
 * Autores: Beatriz Flori�n  - bettyflor@kazak.ws                    
 *          Gustavo Gonzalez - xtingray@kazak.ws                     
 *          Angela Sandobal  - angesand@libertad.univalle.edu.co     
 */
package ws.kazak.xpg.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ws.kazak.xpg.db.ConnectionInfo;
import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableFieldRecord;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.menu.AlterGroup;
import ws.kazak.xpg.menu.AlterUser;
import ws.kazak.xpg.menu.CreateDB;
import ws.kazak.xpg.menu.CreateGroup;
import ws.kazak.xpg.menu.CreateTable;
import ws.kazak.xpg.menu.CreateUser;
import ws.kazak.xpg.menu.DropDB;
import ws.kazak.xpg.menu.DropGroup;
import ws.kazak.xpg.menu.DropTable;
import ws.kazak.xpg.menu.DropUser;
import ws.kazak.xpg.menu.DumpDb;
import ws.kazak.xpg.menu.DumpTable;
import ws.kazak.xpg.menu.TablesGrant;
import ws.kazak.xpg.misc.file.BuildConfigFile;
import ws.kazak.xpg.misc.file.ConfigFileReader;
import ws.kazak.xpg.misc.file.ExtensionFilter;
import ws.kazak.xpg.misc.help.About;
import ws.kazak.xpg.misc.input.ChooseIdiom;
import ws.kazak.xpg.misc.input.ChooseIdiomButton;
import ws.kazak.xpg.misc.input.ErrorDialog;
import ws.kazak.xpg.misc.input.ExportSeparatorField;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;
import ws.kazak.xpg.misc.input.UpdateDBTree;
import ws.kazak.xpg.queries.HotQueries;
import ws.kazak.xpg.queries.Queries;
import ws.kazak.xpg.queries.SQLFuncBasic;
import ws.kazak.xpg.queries.SQLFunctionDataStruc;
import ws.kazak.xpg.records.Records;
import ws.kazak.xpg.report.ReportDesigner;
import ws.kazak.xpg.structure.Structures;
import ws.kazak.xpg.utilities.Path;

import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

public class XPg extends JFrame implements ActionListener, SwingConstants,
		FocusListener, KeyListener, MouseListener {

	JFrame program;
	JMenuBar menuX; // Barra de menus desplegables
	JToolBar iconBar; // Barra de iconos
	JPanel Global; // Panel de contenido
	JSplitPane Ppal; // Panel redimensionable grande entre el monitor de logs
						// y la interfaz

	JTextArea LogWin; // Monitor de Eventos
	DefaultTreeModel treeModel;
	DefaultMutableTreeNode top, category1, globalLeaf; // Nodos del Arbol de
														// conexi�n
	JTree tree; // HostTree de la estructura de la conexi�n a PostgreSQL
	JTabbedPane tabbedPane; // Carpetas donde se desplegar� la informaci�n
							// de tables, datos y queries

	PGConnection pgconn; // Objeto de la clase PGConnection que maneja la
							// conexion
	ConnectionInfo online; // Objeto de la clase ConnectionInfo que define la
							// estructura de datos de la conexion
	ChooseIdiom language;
	boolean connected = false;
	JSplitPane splitPpal; // Panel redimensionable entre el arbol de bases de
							// datos y las carpetas
	JScrollPane treeView;
	DefaultTreeCellRenderer renderer;
	JButton connect, disconnect; // iconos de conexion
	JButton newDB, dropDB; // icons de bases de datos
	JButton newTable, dropTable, dumpTable, changeIdiom; // iconos de tablas
	JMenu connection, dataBase, tables, query, admin, help, group, sub_user; // Menues
																			// desplegables
	JMenuItem connectItem, disconnectItem, exitItem, newDBItem, dropDBItem,
			sub_permi; // items de los Menues connection y dataBase

	JMenuItem newTableItem, dropTableItem, dumpTableItem, addFieldItem; // items
																		// del
																		// JMenu
																		// tables

	JMenuItem editFieldItem, dropFieldItem, insertRecordItem, updateRecordItem,
			delRecordItem;// items del JMenu tables

	JMenuItem newGroupItem, alterGroupItem, dropGroupItem;

	JMenuItem newUserItem, alterUserItem, dropUserItem;

	JMenuItem grantItem, revokeItem;

	JMenuItem newQryItem, saveQryItem, openQryItem, hqItem, runQryItem,
			helpItem, aboutItem; // items de los JMenu query y help

	Language idiom = new Language();

	ConnectionWatcher guard;

	String ActiveDataB = "";

	String[] permissions;

	String xlanguage = "";

	String DBComponentName = "";

	Vector vecConn = new Vector();

	Vector dbNames = new Vector();

	Vector evaluatedDB = new Vector();

	Structures structuresPanel;

	Records recordsPanel;

	Queries queriesPanel;

	int ActivedTabbed = 2;

	int OldCompType = -1;

	int numOldTables = 0;

	int DBComponentType = -1;

	Table currentTable;

	Vector indices;

	JPopupMenu popup, popupDB;

	String startDate = "";

	String OS = "";

	String configPath = "xpg.cfg";

	String xpgHome = "";

	boolean networkLink = false;

	/**
	 * METODO CONSTRUCTOR public XPg() Arma y ensambla los elementos de la
	 * interfaz gr�fica
	 */
	public XPg() {

		super("XPg - PostgreSQL GUI"); // Llama al m�todo de la clase padre
										// (Frame) que espera como par�metro
										// una cadena

		JPanel pe = new JPanel();
		pe.setLayout(new BorderLayout());
		URL gURL = getClass().getResource("/icons/xpg.png");
		JLabel labelx = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(gURL)), JLabel.CENTER);
		pe.add(labelx, BorderLayout.CENTER);

		JWindow first = new JWindow(XPg.this);
		first.getContentPane().add(pe);
		first.setSize(216, 237);
		first.setLocation(232, 220);
		first.setVisible(true);

		ConfigFileReader readLang = new ConfigFileReader();
		OS = System.getProperty("os.name");

		if (OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD")) {

			String UHome = System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".xpg";
			File file = new File(UHome);
			configPath = UHome + System.getProperty("file.separator")
					+ "xpg.cfg";

			if (!file.exists()) {

				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "logs");
				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "queries");
				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "reports");
				file.mkdir();
				readLang.Create_File(configPath);
			} else
				readLang = new ConfigFileReader(configPath, 1);
		}

		if (OS.startsWith("Windows")) {
			System.setProperty("xpgHome", Path.getPathxpg());
			xpgHome = System.getProperty("xpgHome");
			String home = xpgHome + System.getProperty("file.separator");
			configPath = home + "xpg.cfg";
			File file = new File(configPath);

			if (!file.exists())
				readLang.Create_File(configPath);
			else
				readLang = new ConfigFileReader(configPath, 1);

			file = new File(home + System.getProperty("file.separator")
					+ "logs");

			if (!file.exists())
				file.mkdir();

			file = new File(home + System.getProperty("file.separator")
					+ "queries");

			if (!file.exists())
				file.mkdir();

			file = new File(home + System.getProperty("file.separator")
					+ "reports");

			if (!file.exists())
				file.mkdir();
		}

		// Leer del archivo de configuraci�n el idioma actual
		xlanguage = readLang.getIdiom();

		// Si actualmente no se guarda ning�n idioma
		// mostrar ventana inicial para escoger idioma
		if (xlanguage.equals("none")) {

			language = new ChooseIdiom(XPg.this);
			language.pack();
			language.setLocationRelativeTo(XPg.this);
			language.show();
			xlanguage = language.getIdiom();
			idiom.CargarLenguaje(xlanguage);
			writeFile(xlanguage);
		} else
			idiom.CargarLenguaje(xlanguage);

		getContentPane().setLayout(new BorderLayout()); // Parte el FRAME
		menuX = new JMenuBar(); // Se crea un nuevo objeto menu desplegable
		iconBar = new JToolBar(SwingConstants.HORIZONTAL); // Se crea un nuevo
															// objeto barra de
															// iconos
		iconBar.setFloatable(false); // Se hace la barra de iconos fija
		Global = new JPanel();
		setJMenuBar(menuX); // A�adiendo el menu desplegable al Frame
		CreateMenu(); // Se definen los componentes del menu desplegable
		CreateToolBar(); // Se definen los componentes de los iconos de la
							// ventana principal
		HostTree(); // Crea el HostTree a desplegar
		Folders(); // Crea los Folders a desplegar

		// cambiar los iconos por defecto del arbol
		renderer = new DefaultTreeCellRenderer();
		URL imgURL = getClass().getResource("/icons/16_DB_Open.png");
		renderer.setOpenIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		imgURL = getClass().getResource("/icons/16_DB.png");
		renderer.setClosedIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		imgURL = getClass().getResource("/icons/16_table.png");
		renderer.setLeafIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));

		tree.setCellRenderer(renderer);

		// Crear un scroll pane y a�ade el arbol a este
		treeView = new JScrollPane(tree);

		// Crear el split pane a�adiendo a la izq. el arbol y a la derecha las
		// pesta�as
		splitPpal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPpal.setLeftComponent(treeView);
		splitPpal.setRightComponent(tabbedPane);
		splitPpal.setOneTouchExpandable(true); // el SplitPane muestra
												// controles que permiten al
		// usuario ocultar uno de los componentes y asignar todo el espacio al
		// otro
		// Dar el tama�o m�nimo para los dos componentes del split pane
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));
		tabbedPane.setMinimumSize(new Dimension(400, 640));
		tabbedPane.setEnabled(false); // Deshabilitar las carpetas

		splitPpal.setDividerLocation(135); // Selecciona u obtiene la
											// posici�n actual del divisor.
		splitPpal.setPreferredSize(new Dimension(200, 400)); // Tama�o
																// preferido
																// para el split
																// pane

		// Armar el monitor de eventos
		JButton upTitle = new JButton(Language.getWord("LOGMON"));
		upTitle.setToolTipText(Language.getWord("PRESSCL"));

		upTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int pi = Ppal.getDividerLocation();

				if (pi == 430)
					Ppal.setDividerLocation(0);
				else
					Ppal.setDividerLocation(430);
			}
		});

		JPanel downP = new JPanel();
		downP.setLayout(new BorderLayout());

		LogWin.setEditable(false);
		JScrollPane winCover = new JScrollPane(LogWin);
		downP.add(upTitle, BorderLayout.NORTH);
		downP.add(winCover, BorderLayout.CENTER);

		// A�ade al panel Global al norte la barra de iconos y en el centro el
		// split
		Global.setLayout(new BorderLayout());
		Global.add(iconBar, BorderLayout.NORTH);

		splitPpal.setMinimumSize(new Dimension(0, 0));
		downP.setMinimumSize(new Dimension(0, 25));

		Ppal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		Ppal.setOneTouchExpandable(true); // el SplitPane muestra controles
		Ppal.setTopComponent(splitPpal);
		Ppal.setBottomComponent(downP);
		Ppal.setDividerLocation(430);
		Global.add(Ppal, BorderLayout.CENTER);

		setBackground(Color.lightGray);// define el color de fondo para el
										// Frame
		getContentPane().add("Center", Global);// A�ade al Frame ppal el
												// panel Global
		pack();
		setSize(680, 600); // Tama�o inicial del Frame
		setVisible(true); // mostrar el Frame
		first.setVisible(false);

	} // Fin constructor

	/**
	 * METODO public void CreateMenu() Crea Menu desplegable
	 */
	public void CreateMenu() {

		JMenuItem Item; // Crea un item (palabra) de un menu

		connection = new JMenu(Language.getWord("CONNEC")); // Crea el menu de
															// conexi�n
		connection.setMnemonic(Language.getNemo("NEMO-CONNEC"));
		menuX.add(connection); // Adiciona el menu al menu desplegable

		dataBase = new JMenu(Language.getWord("DB")); // Crea el menu de Base
														// de Dato
		dataBase.setMnemonic(Language.getNemo("NEMO-DB")); // Establece un
															// atajo
		menuX.add(dataBase); // Adiciona el menu al menu desplegable

		tables = new JMenu(Language.getWord("TABLE")); // Crea un menu llamado
														// Table
		tables.setMnemonic(Language.getNemo("NEMO-TABLE")); // Establece un
															// atajo
		menuX.add(tables); // Adiciona el menu al menu desplegable

		query = new JMenu(Language.getWord("QUERY"));// Crea un menu llamado
														// Query
		query.setMnemonic(Language.getNemo("NEMO-QUERY")); // Establece un
															// atajo
		menuX.add(query); // Adiciona el menu al menu desplegable

		admin = new JMenu(Language.getWord("ADMIN"));// Crea un menu llamado
														// admin
		admin.setMnemonic(Language.getNemo("NEMO-ADMIN")); // Establece un
															// atajo
		menuX.add(admin); // Adiciona el menu al menu desplegable

		help = new JMenu(Language.getWord("HELP"));// Crea un menu llamado Help
		help.setMnemonic(Language.getNemo("NEMO-HELP")); // Establece un
															// atajo
		menuX.add(help); // Adiciona el menu al menu desplegable

		/*----------Items Menu connection----------*/
		URL imgURL = getClass().getResource("/icons/16_connect.png");
		connectItem = new JMenuItem(Language.getWord("CONNE2"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		connectItem.setActionCommand("ItemConnect");
		connectItem.addActionListener(this);
		connection.add(connectItem);
		connectItem.setMnemonic(Language.getNemo("NEMO-CONNE2"));

		imgURL = getClass().getResource("/icons/16_disconnect.png");
		disconnectItem = new JMenuItem(Language.getWord("DISCON"),
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		disconnectItem.setActionCommand("ItemDisconnect");
		disconnectItem.addActionListener(this);
		connection.add(disconnectItem);
		disconnectItem.setMnemonic(Language.getNemo("NEMO-DISCON"));
		disconnectItem.setEnabled(false);

		connection.addSeparator();

		imgURL = getClass().getResource("/icons/16_exit.png");
		exitItem = new JMenuItem(Language.getWord("EXIT"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		exitItem.setActionCommand("ItemExit");
		exitItem.addActionListener(this);
		connection.add(exitItem);
		exitItem.setMnemonic(Language.getNemo("NEMO-EXIT"));

		/*----------Items Menu Database ----------*/
		imgURL = getClass().getResource("/icons/16_NewDB.png");
		newDBItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newDBItem.setActionCommand("ItemCreateDB");
		newDBItem.addActionListener(this);
		dataBase.add(newDBItem);
		newDBItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_DropDB.png");
		dropDBItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropDBItem.setActionCommand("ItemDropDB");
		dropDBItem.addActionListener(this);
		dataBase.add(dropDBItem);
		dropDBItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		/*----------Items Menu Table----------*/
		imgURL = getClass().getResource("/icons/16_NewTable.png");
		newTableItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newTableItem.setActionCommand("ItemCreateTable");
		newTableItem.addActionListener(this);
		tables.add(newTableItem);
		newTableItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_DropTable.png");
		dropTableItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropTableItem.setActionCommand("ItemDropTable");
		dropTableItem.addActionListener(this);
		tables.add(dropTableItem);
		dropTableItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		imgURL = getClass().getResource("/icons/16_Dump.png");
		dumpTableItem = new JMenuItem(Language.getWord("DUMP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dumpTableItem.setActionCommand("ItemDumpTable");
		dumpTableItem.addActionListener(this);
		tables.add(dumpTableItem);
		dumpTableItem.setMnemonic(Language.getNemo("NEMO-DUMP"));

		imgURL = getClass().getResource("/icons/16_Grant.png");
		sub_permi = new JMenuItem(Language.getWord("PERMI"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		sub_permi.setActionCommand("ItemGrant");
		sub_permi.addActionListener(this);
		tables.add(sub_permi);
		sub_permi.setMnemonic(Language.getNemo("NEMO-PERMI"));

		/*----------Items Sub-Menu group----------*/
		group = new JMenu(Language.getWord("GROUP"));

		imgURL = getClass().getResource("/icons/16_NewGroup.png");
		newGroupItem = new JMenuItem(Language.getWord("CREATE"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newGroupItem.setActionCommand("ItemCreateGroup");
		newGroupItem.addActionListener(this);
		group.add(newGroupItem);
		newGroupItem.setMnemonic(Language.getNemo("NEMO-CREATE"));

		imgURL = getClass().getResource("/icons/16_AlterGroup.png");
		alterGroupItem = new JMenuItem(Language.getWord("ALTER"),
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		alterGroupItem.setActionCommand("ItemAlterGroup");
		alterGroupItem.addActionListener(this);
		group.add(alterGroupItem);
		alterGroupItem.setMnemonic(Language.getNemo("NEMO-ALTER"));

		imgURL = getClass().getResource("/icons/16_DropGroup.png");
		dropGroupItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropGroupItem.setActionCommand("ItemDropGroup");
		dropGroupItem.addActionListener(this);
		group.add(dropGroupItem);
		dropGroupItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		admin.add(group);
		group.setMnemonic(Language.getNemo("NEMO-GROUP"));
		admin.addSeparator();

		/*----------Items Sub-Menu user----------*/
		sub_user = new JMenu(Language.getWord("USER"));

		imgURL = getClass().getResource("/icons/16_NewUser.png");
		newUserItem = new JMenuItem(Language.getWord("CREATE"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newUserItem.setActionCommand("ItemCreateUser");
		newUserItem.addActionListener(this);
		sub_user.add(newUserItem);
		newUserItem.setMnemonic(Language.getNemo("NEMO-CREATE"));

		imgURL = getClass().getResource("/icons/16_AlterUser.png");
		alterUserItem = new JMenuItem(Language.getWord("ALTER"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		alterUserItem.setActionCommand("ItemAlterUser");
		alterUserItem.addActionListener(this);
		sub_user.add(alterUserItem);
		alterUserItem.setMnemonic(Language.getNemo("NEMO-ALTER"));

		imgURL = getClass().getResource("/icons/16_DropUser.png");
		dropUserItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropUserItem.setActionCommand("ItemDropUser");
		dropUserItem.addActionListener(this);
		sub_user.add(dropUserItem);
		dropUserItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		admin.add(sub_user);
		sub_user.setMnemonic(Language.getNemo("NEMO-USER"));

		/*----------Items Menu query----------*/
		imgURL = getClass().getResource("/icons/16_new.png");
		newQryItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newQryItem.setActionCommand("ItemCreateQry");
		newQryItem.addActionListener(this);
		query.add(newQryItem);
		newQryItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_Load.png");
		openQryItem = new JMenuItem(Language.getWord("OPEN"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		openQryItem.setActionCommand("ItemOpenQry");
		openQryItem.addActionListener(this);
		query.add(openQryItem);
		openQryItem.setMnemonic(Language.getNemo("NEMO-OPEN"));

		imgURL = getClass().getResource("/icons/16_HQ.png");
		hqItem = new JMenuItem(Language.getWord("HQ"), new ImageIcon(Toolkit
				.getDefaultToolkit().getImage(imgURL)));
		hqItem.setActionCommand("ItemHQ");
		hqItem.addActionListener(this);
		query.add(hqItem);
		hqItem.setMnemonic(Language.getNemo("NEMO-HQ"));

		/*----------Items Menu help----------*/
		imgURL = getClass().getResource("/icons/16_Help.png");
		helpItem = new JMenuItem(Language.getWord("HELP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		helpItem.setActionCommand("ItemContenido");
		helpItem.addActionListener(this);
		help.add(helpItem);
		helpItem.setMnemonic(Language.getNemo("NEMO-HELP2"));

		help.addSeparator();

		aboutItem = new JMenuItem(Language.getWord("ABOUT") + "...");
		aboutItem.setActionCommand("ItemAbout");
		aboutItem.addActionListener(this);
		help.add(aboutItem);
		aboutItem.setMnemonic(Language.getNemo("NEMO-ABOUT"));

		switchJMenus(false);
	}

	/**
	 * METODO CreateToolBar Crea Barra de Iconos
	 */
	public void CreateToolBar() {

		JToolBar.Separator line1 = new JToolBar.Separator();
		JToolBar.Separator line2 = new JToolBar.Separator();
		JToolBar.Separator line3 = new JToolBar.Separator();
		/*------------ Botones Conexion ------------*/
		URL imgURL = getClass().getResource("/icons/16_connect.png");
		connect = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		connect.setActionCommand("ButtonConnect");
		connect.addActionListener(this);
		connect.setToolTipText(Language.getWord("CONNE2"));
		iconBar.add(connect);

		imgURL = getClass().getResource("/icons/16_disconnect.png");
		disconnect = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		disconnect.setActionCommand("ButtonDisconnect");
		disconnect.addActionListener(this);
		disconnect.setToolTipText(Language.getWord("DISCON"));
		iconBar.add(disconnect);
		disconnect.setEnabled(false);

		iconBar.add(line1);
		/*------------ Botones Base de Datos ------------*/
		imgURL = getClass().getResource("/icons/16_NewDB.png");
		newDB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				imgURL)));
		newDB.setActionCommand("ButtonNewDB");
		newDB.addActionListener(this);
		newDB.setToolTipText(Language.getWord("NEWDB"));
		iconBar.add(newDB);
		newDB.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_DropDB.png");
		dropDB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dropDB.setActionCommand("ButtonDropDB");
		dropDB.addActionListener(this);
		dropDB.setToolTipText(Language.getWord("DROPDB"));
		iconBar.add(dropDB);
		dropDB.setEnabled(false);

		iconBar.add(line2);
		/*------------ Botones Tabla ------------*/
		imgURL = getClass().getResource("/icons/16_NewTable.png");
		newTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		newTable.setActionCommand("ButtonNewTable");
		newTable.addActionListener(this);
		newTable.setToolTipText(Language.getWord("NEWT"));
		iconBar.add(newTable);
		newTable.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_DropTable.png");
		dropTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dropTable.setActionCommand("ButtonDropTable");
		dropTable.addActionListener(this);
		dropTable.setToolTipText(Language.getWord("DROPT"));
		iconBar.add(dropTable);
		dropTable.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_Dump.png");
		dumpTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dumpTable.setActionCommand("ButtonDumpTable");
		dumpTable.addActionListener(this);
		dumpTable.setToolTipText(Language.getWord("DUMPT"));
		iconBar.add(dumpTable);
		dumpTable.setEnabled(false);

		iconBar.add(line3);

		/*------------ Botones Lenguaje ------------*/
		imgURL = getClass().getResource("/icons/16_Language.png");
		changeIdiom = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		changeIdiom.setActionCommand("ButtonChangeLanguage");
		changeIdiom.addActionListener(this);
		changeIdiom.setToolTipText(Language.getWord("CHANGE_L"));
		iconBar.add(changeIdiom);

	}

	/**
	 * METODO actionPerformed Manejador de Eventos para la barra de botones y el
	 * menu desplegable
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {

		if (e.getActionCommand().equals("ItemRename")) {

			TreePath selPath = tree.getSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();
			final String oldName = node.toString();
			tree.setEditable(true);
			final JTextField rename = new JTextField();
			tree.setCellEditor(new DefaultCellEditor(rename));
			tree.startEditingAtPath(selPath);
			rename.requestFocus();

			rename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String newName = rename.getText();

					if (newName.indexOf(" ") != -1) {

						rename.setText(oldName);
						JOptionPane.showMessageDialog(XPg.this, Language
								.getWord("NOCHART"),
								Language.getWord("ERROR!"),
								JOptionPane.ERROR_MESSAGE);

						return;
					}

					rename.setText(newName);
					String result = "";

					if (newName.length() == 0) {

						TreePath selPath = tree.getSelectionPath();
						tree.startEditingAtPath(selPath);
					} else {
						int index = dbNames.indexOf(ActiveDataB);
						PGConnection konn = (PGConnection) vecConn
								.elementAt(index);
						result = konn.SQL_Instruction("ALTER TABLE \""
								+ oldName + "\" RENAME TO \"" + newName + "\"");

						if (result.equals("OK")) {

							DBComponentName = newName;
							String owner = konn.getOwner(newName);
							structuresPanel.setLabel(ActiveDataB, newName,
									owner);
							recordsPanel.setLabel(ActiveDataB, newName, owner);
						} else {
							result = result.substring(0, result.length() - 1);
							rename.setText(oldName);
						}
					}
					addTextLogMonitor(Language.getWord("EXEC")
							+ "ALTER TABLE \"" + oldName + "\" RENAME TO \""
							+ newName + "\"\"");
					addTextLogMonitor(Language.getWord("RES") + result);
				}
			});

			return;
		}

		if (e.getActionCommand().equals("ItemDelete")) {

			GenericQuestionDialog killtb = new GenericQuestionDialog(XPg.this,
					Language.getWord("YES"), Language.getWord("NO"), Language
							.getWord("BOOLDELTB"), Language
							.getWord("MESGDELTB")
							+ DBComponentName + "?");

			boolean sure = killtb.getSelecction();
			String result = "";

			if (sure) {

				int index = dbNames.indexOf(ActiveDataB);
				PGConnection konn = (PGConnection) vecConn.elementAt(index);
				result = konn.SQL_Instruction("DROP TABLE \"" + DBComponentName
						+ "\"");

				if (result.equals("OK")) {

					TreePath selPath = tree.getSelectionPath();
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
							.getLastPathComponent());
					DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
							.getParent();
					treeModel.removeNodeFromParent(currentNode);

					if (NodeDB.getChildCount() == 0) {
						DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
								Language.getWord("NOTABLES"));
						NodeDB.add(nLeaf);
					}

				} else
					result = result.substring(0, result.length() - 1);

				addTextLogMonitor(Language.getWord("EXEC") + "DROP TABLE \""
						+ DBComponentName + "\";\"");
				addTextLogMonitor(Language.getWord("RES") + result);
				tabbedPane.setSelectedIndex(2);
				tabbedPane.setEnabledAt(0, false);
				tabbedPane.setEnabledAt(1, false);
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDump")) {

			String s = "file:" + System.getProperty("user.home");
			File file;
			boolean Rewrite = true;
			String FileName = "";
			JFileChooser fc = new JFileChooser(s);
			ExtensionFilter filter = new ExtensionFilter("sql", Language
					.getWord("SQLF"));
			fc.addChoosableFileFilter(filter);

			int returnVal = fc.showDialog(XPg.this, Language.getWord("SAVE"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				file = fc.getSelectedFile();
				FileName = file.getAbsolutePath();

				if (file.exists()) {

					GenericQuestionDialog win = new GenericQuestionDialog(
							XPg.this, Language.getWord("YES"), Language
									.getWord("NO"), Language.getWord("ADV"),
							Language.getWord("FILE") + " '" + FileName + "'"
									+ Language.getWord("SEQEXIS2") + " "
									+ Language.getWord("OVWR"));
					Rewrite = win.getSelecction();
				}

				if (Rewrite) {

					try {
						int index = dbNames.indexOf(ActiveDataB);
						PGConnection konn = (PGConnection) vecConn
								.elementAt(index);
						String dataStr = createTableSQL(konn
								.getSpecStrucTable(DBComponentName));

						if (!FileName.endsWith(".sql"))
							FileName += ".sql";

						PrintStream sqlFile = new PrintStream(
								new FileOutputStream(FileName));
						sqlFile.print(dataStr);
						sqlFile.close();
					} catch (Exception ex) {
						System.out.println("Error: " + ex);
						ex.printStackTrace();
					}
				}
			}
			return;
		}

		if (e.getActionCommand().equals("ItemExport")) {

			int index = dbNames.indexOf(ActiveDataB);
			PGConnection konn = (PGConnection) vecConn.elementAt(index);
			int regs = Count(DBComponentName, konn);
			addTextLogMonitor(Language.getWord("EXEC")
					+ "SELECT count(*) FROM " + DBComponentName + "\"");
			addTextLogMonitor(Language.getWord("NUMR") + DBComponentName
					+ "' : " + regs);

			if (regs > 100) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("LOTREG")
						+ DBComponentName + Language.getWord("LOTREG2"),
						Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			Vector result = konn.TableQuery("SELECT * FROM " + DBComponentName);
			Vector colnames = konn.getTableHeader();
			String res = "";

			if (!konn.queryFail())
				res = "OK";
			else {
				res = konn.problem;
				res = res.substring(0, res.length() - 1);
			}

			addTextLogMonitor(Language.getWord("EXEC") + "SELECT * FROM "
					+ DBComponentName + "\"");
			addTextLogMonitor(Language.getWord("RES") + res);
			ReportDesigner format = new ReportDesigner(XPg.this, colnames,
					result, LogWin, DBComponentName, konn);

			return;
		}

		if (e.getActionCommand().equals("ItemExToFile")) {

			ExportSeparatorField little = new ExportSeparatorField(XPg.this);

			if (little.isDone()) {

				String limiter = little.getLimiter();
				String s = "file:" + System.getProperty("user.home");
				File file;
				boolean Rewrite = true;
				String FileName = "";
				JFileChooser fc = new JFileChooser(s);
				int returnVal = fc.showDialog(XPg.this, Language
						.getWord("EXPTO"));

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					file = fc.getSelectedFile();
					FileName = file.getAbsolutePath();

					if (file.exists()) {

						GenericQuestionDialog win = new GenericQuestionDialog(
								XPg.this, Language.getWord("YES"), Language
										.getWord("NO"),
								Language.getWord("ADV"), Language
										.getWord("FILE")
										+ " '"
										+ FileName
										+ "'"
										+ Language.getWord("SEQEXIS2")
										+ " "
										+ Language.getWord("OVWR"));
						Rewrite = win.getSelecction();
					}

					if (Rewrite) {

						try {
							int index = dbNames.indexOf(ActiveDataB);
							PGConnection connection = (PGConnection) vecConn
									.elementAt(index);

							Vector result = connection
									.TableQuery("SELECT * FROM "
											+ DBComponentName);
							Vector colnames = connection.getTableHeader();
							String resultString = "OK";

							if (connection.queryFail()) {
								resultString = connection.getProblemString();
								resultString = resultString.substring(0,
										resultString.length() - 1);
							}

							addTextLogMonitor(Language.getWord("EXEC")
									+ "SELECT * FROM " + DBComponentName + "\"");
							addTextLogMonitor(Language.getWord("RES")
									+ resultString);
							Table structT = connection
									.getSpecStrucTable(DBComponentName);
							TableHeader tableHeader = structT.getTableHeader();
							PrintStream exportFile = new PrintStream(
									new FileOutputStream(FileName));
							printFile(exportFile, result, colnames, limiter,
									tableHeader);
						} catch (Exception ex) {
							System.out.println("Error: " + ex);
							ex.printStackTrace();
						}
					}

				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemPopCloseDB")) {

			if (DBComponentName.equals(pgconn.getDBname())) {

				GenericQuestionDialog killtb = new GenericQuestionDialog(
						XPg.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDISC"),
						Language.getWord("WDIS"));

				boolean sure = killtb.getSelecction();

				if (sure)
					Disconnect();

				return;
			}

			int pos = dbNames.indexOf(DBComponentName);
			PGConnection pgTmp = (PGConnection) vecConn.remove(pos);
			pgTmp.close();
			dbNames.remove(pos);
			addTextLogMonitor(Language.getWord("CLSDB") + DBComponentName + "'");
			Object raiz = treeModel.getRoot();
			int k = treeModel.getChildCount(raiz);

			for (int i = 0; i < k; i++) {

				Object o = treeModel.getChild(raiz, i);

				if (DBComponentName.equals(o.toString())) {
					treeModel.removeNodeFromParent((MutableTreeNode) o);
					break;
				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemPopDeleteDB")) {

			if (DBComponentName.equals(pgconn.getDBname())) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("INVOP"), Language.getWord("INFO"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			GenericQuestionDialog killtb = new GenericQuestionDialog(XPg.this,
					Language.getWord("YES"), Language.getWord("NO"), Language
							.getWord("BOOLDELTB"), Language
							.getWord("MESGDELDB")
							+ DBComponentName + "?");

			boolean sure = killtb.getSelecction();

			if (sure) {

				int pos = dbNames.indexOf(DBComponentName);
				PGConnection tempo = (PGConnection) vecConn.remove(pos);
				tempo.close();
				dbNames.remove(pos);
				// Eliminando BD
				String result = pgconn.SQL_Instruction("DROP DATABASE \""
						+ DBComponentName + "\"");
				addTextLogMonitor(Language.getWord("EXEC") + "DROP DATABASE \""
						+ DBComponentName + "\";\"");

				if (result.equals("OK")) {

					TreePath selPath = tree.getSelectionPath();
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
							.getLastPathComponent());
					DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
							.getParent();
					treeModel.removeNodeFromParent(currentNode);
					addTextLogMonitor(Language.getWord("RES") + result);
				} else {
					String tmp = result.substring(0, result.length() - 1);
					addTextLogMonitor(Language.getWord("RES") + tmp);
					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("ERRORPOS")
							+ tmp, Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);
				}

			}

			return;
		}

		if (e.getActionCommand().equals("ItemPopDumpDB")) {

			int index = dbNames.indexOf(ActiveDataB);
			PGConnection dbDump = (PGConnection) vecConn.elementAt(index);

			int numTables = dbDump.getNumTables();

			if (numTables > 0) {

				DumpDb proto = new DumpDb(XPg.this, ActiveDataB, dbDump);
				/*
				 * proto.pack(); proto.setLocationRelativeTo(XPg.this);
				 * proto.setVisible(true);
				 */

				if (proto.wellDone)
					addTextLogMonitor(Language.getWord("DUMPT1")
							+ proto.getTables() + Language.getWord("DUMPT2")
							+ proto.getDBName() + Language.getWord("DUMPT3")
							+ proto.getFile() + "'");

			} else {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("TNTAB")
						+ ActiveDataB + "'", Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			}

			return;
		}

		/*------------ Evento Conexion --------------*/
		if (e.getActionCommand().equals("ItemConnect")
				|| e.getActionCommand().equals("ButtonConnect")) {

			NConnect();
			return;
		}

		/*------------ Evento Desconectar --------------*/
		if (e.getActionCommand().equals("ItemDisconnect")
				|| e.getActionCommand().equals("ButtonDisconnect")) {

			Disconnect();
			return;
		}

		/*------------ Evento Salir --------------*/
		if (e.getActionCommand().equals("ItemExit")) {

			if (connected) {

				GenericQuestionDialog exitWin = new GenericQuestionDialog(
						XPg.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLEXIT"),
						Language.getWord("MESGEXIT"));

				boolean sure = exitWin.getSelecction();

				if (sure) {
					SaveLog();
					closePGSockets();
					System.exit(0);
				}
				return;
			} else
				System.exit(0);

		}

		/*------------ Evento Base de Datos ------------*/
		if (e.getActionCommand().equals("ItemCreateDB")
				|| e.getActionCommand().equals("ButtonNewDB")) {

			CreateDB newDB = new CreateDB(XPg.this, pgconn, LogWin);

			if (newDB.isDone()) {

				ConnectionInfo tmp = new ConnectionInfo(online.getHost(), newDB
						.getDBname(), online.getUser(), online.getPassword(),
						online.getPort(), online.requireSSL());
				PGConnection proofConn = new PGConnection(tmp);

				if (!proofConn.Fail()) {

					dbNames.add(newDB.getDBname());
					vecConn.add(proofConn);

					// Insertando nueva base de datos en el arbol
					DefaultMutableTreeNode dbLeaf = new DefaultMutableTreeNode(
							tmp.getDatabase());
					DefaultMutableTreeNode noTables = new DefaultMutableTreeNode(
							Language.getWord("NOTABLES"));
					dbLeaf.add(noTables);
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
							.getRoot();
					treeModel.insertNodeInto(dbLeaf, parent, parent
							.getChildCount());

					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("OKCREATEDB1")
							+ tmp.getDatabase()
							+ "\" \n"
							+ Language.getWord("OKCREATEDB2"), Language
							.getWord("OK"), JOptionPane.INFORMATION_MESSAGE);
				} else {
					String msg = Language.getWord("OKCREATEDB1")
							+ tmp.getDatabase() + "\" "
							+ Language.getWord("OKCREATEDB2") + "\n"
							+ Language.getWord("NNACESS") + "\n"
							+ Language.getWord("NNCONTACT");
					JOptionPane.showMessageDialog(XPg.this, msg, Language
							.getWord("INFO"), JOptionPane.INFORMATION_MESSAGE);
				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropDB")
				|| e.getActionCommand().equals("ButtonDropDB")) {

			// Formando el vector de las bases de datos de las cuales el usuario
			// es propietario
			String sqlCmmd = "SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' AND datname != '"
					+ online.getDatabase() + " ORDER by datname';";
			Vector DBNames = pgconn.TableQuery(sqlCmmd);
			addTextLogMonitor(Language.getWord("EXEC") + sqlCmmd);

			if (DBNames.size() == 0) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("NDBS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			if (DBNames.size() == 1) {

				Vector o = (Vector) DBNames.elementAt(0);
				String db = (String) o.elementAt(0);

				if (db.equals(online.getDatabase())) {

					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("OIDBC"), Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			DropDB dropDB = new DropDB(XPg.this, DBNames);

			// Si el usuario presiono el boton DROP

			if (dropDB.confirmDropDB()) {

				int pos = 0;
				boolean inTree = false;

				if (dbNames.contains(dropDB.comboText)) {

					inTree = true;
					pos = dbNames.indexOf(dropDB.comboText);
				}

				// Cerrar la conexion de la base de datos a borrar si existe
				if (inTree) {

					PGConnection pgTmp = (PGConnection) vecConn.remove(pos);
					pgTmp.close();
					dbNames.remove(pos);
				}

				// Eliminando BD
				String result = pgconn.SQL_Instruction("DROP DATABASE \""
						+ dropDB.comboText + "\"");
				addTextLogMonitor(Language.getWord("EXEC") + "DROP DATABASE "
						+ dropDB.comboText + "\";\"");

				if (result.equals("OK")) {

					if (inTree) {

						Object raiz = treeModel.getRoot();
						int k = treeModel.getChildCount(raiz);

						for (int i = 0; i < k; i++) {

							Object o = treeModel.getChild(raiz, i);

							if (dropDB.comboText.equals(o.toString())) {
								treeModel
										.removeNodeFromParent((MutableTreeNode) o);
								break;
							}
						}
					}

					addTextLogMonitor(Language.getWord("RES") + result);

					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("OKDROPDB1")
							+ dropDB.comboText + Language.getWord("OKDROPDB2"),
							Language.getWord("OK"),
							JOptionPane.INFORMATION_MESSAGE);

				} else {
					String tmp = result.substring(0, result.length() - 1);
					addTextLogMonitor(Language.getWord("RES") + tmp);
					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("ERRORPOS")
							+ tmp, Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);
				}
			}

			return;
		}

		/*------------ Evento Tabla ------------*/
		if (e.getActionCommand().equals("ItemCreateTable")
				|| e.getActionCommand().equals("ButtonNewTable")) {

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			CreateTable winTable = new CreateTable(XPg.this, dbNames, vecConn,
					ActiveDataB, LogWin);

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (winTable.getWellDone()) {

				Object raiz = treeModel.getRoot();
				int k = treeModel.getChildCount(raiz);

				for (int i = 0; i < k; i++) {

					Object o = treeModel.getChild(raiz, i);

					if (winTable.dbn.equals(o.toString())) {

						Object[] lista = { raiz, o };
						TreePath rama = new TreePath(lista);

						if (tree.isExpanded(rama)) {

							DefaultMutableTreeNode node = (DefaultMutableTreeNode) rama
									.getLastPathComponent();
							DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) node
									.getFirstChild();

							if (leaf.toString().startsWith(
									Language.getWord("NOTABLES"))) {
								treeModel
										.removeNodeFromParent((MutableTreeNode) leaf);
							}

							DefaultMutableTreeNode TLeaf = new DefaultMutableTreeNode(
									winTable.CurrentTable);
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) o;
							treeModel.insertNodeInto(TLeaf, parent, parent
									.getChildCount());
							tree.expandPath(rama);
						}
					}
				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropTable")
				|| e.getActionCommand().equals("ButtonDropTable")) {

			DropTable dT = new DropTable(XPg.this, dbNames, vecConn, LogWin);

			Vector delTables = dT.getDeletedTables();
			int size = delTables.size();
			int count = 0;

			while (!delTables.isEmpty()) {

				count++;
				String tableTarget = (String) delTables.remove(0);
				Object root = treeModel.getRoot();
				int k = treeModel.getChildCount(root);

				for (int i = 0; i < k; i++) {

					Object o = treeModel.getChild(root, i);

					if (dT.dbx.equals(o.toString())) {

						Object[] nodeList = { root, o };
						TreePath branch = new TreePath(nodeList);

						if (tree.isExpanded(branch)) {

							int p = treeModel.getChildCount(o);

							for (int j = 0; j < p; j++) {

								Object t = treeModel.getChild(o, j);

								if (tableTarget.equals(t.toString())) {

									TreePath selPath = tree.getSelectionPath();
									DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
											.getLastPathComponent();

									if (count == size) {

										structuresPanel.setNullTable();
										structuresPanel.setLabel("", "", "");
										recordsPanel.setLabel("", "", "");
										structuresPanel.activeToolBar(false);
										structuresPanel.activeIndexPanel(false);
										tabbedPane.setEnabledAt(0, false);
										tabbedPane.setEnabledAt(1, false);
										tabbedPane.setSelectedIndex(2);
										tree.setSelectionPath(branch);
									}

									treeModel
											.removeNodeFromParent((MutableTreeNode) t);
									DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) (branch
											.getLastPathComponent());

									if (NodeDB.getChildCount() == 0) {

										DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
												Language.getWord("NOTABLES"));
										NodeDB.add(nLeaf);
									}
									break;
								}
							}
						}
					}
				}// fin for
			} // fin while delTables.isEmpty()

			return;
		}

		if (e.getActionCommand().equals("ItemDumpTable")
				|| e.getActionCommand().equals("ButtonDumpTable")) {

			DumpTable proto = new DumpTable(XPg.this, dbNames, vecConn);
			/*
			 * proto.pack(); proto.setLocationRelativeTo(XPg.this);
			 * proto.setVisible(true);
			 */

			if (proto.isDone())
				addTextLogMonitor(Language.getWord("DUMPT1")
						+ proto.getTables() + Language.getWord("DUMPT2")
						+ proto.getDBName() + Language.getWord("DUMPT3")
						+ proto.getFile() + "'");

			return;

		}

		/*---------- Evento Consulta --------*/
		if (e.getActionCommand().equals("ItemCreateQry")) {

			int carpeta = tabbedPane.getSelectedIndex();

			if (carpeta != 2) {

				tabbedPane.setSelectedIndex(2);
				queriesPanel.NewQuery();
			}

			return;
		}

		if (e.getActionCommand().equals("ItemOpenQry")) {

			int carpeta = tabbedPane.getSelectedIndex();

			if (carpeta != 2)
				tabbedPane.setSelectedIndex(2);

			queriesPanel.LoadQuery();

			return;
		}

		if (e.getActionCommand().equals("ItemHQ")) {

			HotQueries hotQ = new HotQueries(XPg.this);

			if (hotQ.isWellDone())
				queriesPanel.loadSQL(hotQ.getSQL(), hotQ.isReady());

			return;
		}

		/* ---------------Evento Admin -------------- */
		if (e.getActionCommand().equals("ItemCreateUser")) {

			CreateUser cUser = new CreateUser(XPg.this, pgconn, LogWin);
			/*
			 * cUser.pack(); cUser.setLocationRelativeTo(XPg.this);
			 * cUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemAlterUser")) {

			AlterUser aUser = new AlterUser(XPg.this, pgconn, LogWin);
			/*
			 * aUser.pack(); aUser.setLocationRelativeTo(XPg.this);
			 * aUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemDropUser")) {

			DropUser dUser = new DropUser(XPg.this, pgconn, LogWin);
			/*
			 * dUser.pack(); dUser.setLocationRelativeTo(XPg.this);
			 * dUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemCreateGroup")) {

			CreateGroup cGroup = new CreateGroup(XPg.this, pgconn, LogWin);
			/*
			 * cGroup.pack(); cGroup.setLocationRelativeTo(XPg.this);
			 * cGroup.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemAlterGroup")) {

			String as[] = pgconn.getGroups();

			if (as.length == 0) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("NGRPS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			} else {

				AlterGroup aGroup = new AlterGroup(XPg.this, pgconn, LogWin);
				/*
				 * aGroup.pack(); aGroup.setLocationRelativeTo(XPg.this);
				 * aGroup.show();
				 */
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropGroup")) {

			String as[] = pgconn.getGroups();

			if (as.length == 0) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("NGRPS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				DropGroup dGroup = new DropGroup(XPg.this, pgconn, LogWin);
			}

			return;
		}

		if (e.getActionCommand().equals("ItemGrant")) {

			int index = dbNames.indexOf(ActiveDataB);

			if (index == -1) {

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("PCDBF"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			PGConnection konn = (PGConnection) vecConn.elementAt(index);

			String[] tb;

			if (permissions[1].equals("false"))
				tb = konn.getTablesNames(true);
			else
				tb = konn.getTablesNames(false);

			if ((tb.length < 1) && (!permissions[1].equals("true")))

				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("NOTOW")
						+ ActiveDataB + "'", Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			else {
				if (tb.length > 0) {
					TablesGrant perm = new TablesGrant(XPg.this, konn, LogWin,
							tb);
				} else {
					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("NODBC")
							+ ActiveDataB + "\"!", Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			return;
		}

		/*------------ Evento Ayuda ------------*/
		if (e.getActionCommand().equals("ItemContenido")) {

			/*
			 * xpgHelp hlp = new xpgHelp(xpgHome); hlp.pack();
			 * hlp.setLocationRelativeTo(XPg.this); hlp.setVisible(true);
			 */

			JOptionPane.showMessageDialog(XPg.this, Language.getWord("UIMO"),
					Language.getWord("INFO"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (e.getActionCommand().equals("ItemAbout")) {

			About dialog = new About(XPg.this);
			dialog.setVisible(true);

			return;
		}

		/*------------ Evento Lenguaje ------------*/
		if (e.getActionCommand().equals("ButtonChangeLanguage")) {

			ChooseIdiomButton language = new ChooseIdiomButton(XPg.this, LogWin);
			/*
			 * language.pack(); language.setLocationRelativeTo(XPg.this);
			 * language.show();
			 */

			if (language.getSave()) {

				xlanguage = language.getIdiom();
				JOptionPane.showMessageDialog(XPg.this, Language
						.getWord("NEXT_TIME"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
				writeFile(xlanguage);
			}

			return;
		}

	}

	/**
	 * METODO HostTree Crea Arbol del Servidor
	 */
	public void HostTree() {

		top = new DefaultMutableTreeNode(Language.getWord("DSCNNTD"));
		category1 = new DefaultMutableTreeNode(Language.getWord("NODB"));
		top.add(category1);

		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(renderer);
		tree.collapseRow(0);

		popup = new JPopupMenu();

		JMenuItem Item = new JMenuItem(Language.getWord("RNAME"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemRename");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("DUMP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemDump");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("EXPORTAB"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemExToFile");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("EXPORREP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemExport");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("DROP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemDelete");
		Item.addActionListener(this);
		popup.add(Item);

		popupDB = new JPopupMenu();

		Item = new JMenuItem(Language.getWord("DUMP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopDumpDB");
		Item.addActionListener(this);
		popupDB.add(Item);

		Item = new JMenuItem(Language.getWord("CLOSE"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopCloseDB");
		Item.addActionListener(this);
		popupDB.add(Item);

		Item = new JMenuItem(Language.getWord("DROP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopDeleteDB");
		Item.addActionListener(this);
		popupDB.add(Item);

	}

	/**
	 * METODO Folders Crea Pesta�as
	 */
	public void Folders() {

		URL imgURL = getClass().getResource("/icons/16_table.png");
		ImageIcon iconTable = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		imgURL = getClass().getResource("/icons/16_Datas.png");
		ImageIcon iconRecord = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		imgURL = getClass().getResource("/icons/16_SQL.png");
		ImageIcon iconQuery = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		tabbedPane = new JTabbedPane();

		LogWin = new JTextArea(5, 0);

		structuresPanel = new Structures(XPg.this, LogWin);
		tabbedPane.addTab(Language.getWord("TABLE"), iconTable,
				structuresPanel, Language.getWord("TABLE"));

		recordsPanel = new Records(XPg.this, LogWin);
		tabbedPane.addTab(Language.getWord("RECS"), iconRecord, recordsPanel,
				Language.getWord("RECS"));

		SQLFunctionDataStruc[] fList = funcDataStruct();
		SQLFuncBasic[] fbasic = funcBasicStruct();
		queriesPanel = new Queries(XPg.this, LogWin, fList, fbasic);

		tabbedPane.addTab(Language.getWord("QUERYS"), iconQuery, queriesPanel,
				Language.getWord("QUERYS"));
	}

	/**
	 * METODO ReConfigFile Re-escribe el archivo de configuracion
	 */
	public void ReConfigFile(ConnectionInfo online, Vector ListRegs,
			int lastUsed) {

		ConnectionInfo tmp = (ConnectionInfo) ListRegs.elementAt(lastUsed);
		boolean noNew = true;
		int pos = lastUsed;

		if (!tmp.getHost().equals(online.getHost())
				|| !tmp.getUser().equals(online.getUser())
				|| !tmp.getDatabase().equals(online.getDatabase())) {

			noNew = false;

			for (int i = 0; i < ListRegs.size(); i++) {

				if (i != lastUsed) {

					ConnectionInfo element = (ConnectionInfo) ListRegs
							.elementAt(i);

					if (element.getHost().equals(online.getHost())
							&& element.getUser().equals(online.getUser())
							&& element.getDatabase().equals(
									online.getDatabase())) {

						noNew = true;
						pos = i;
						break;
					}
				}
			}
		}

		if (noNew) {
			new BuildConfigFile(ListRegs, pos, xlanguage);
		} else {
			new BuildConfigFile(ListRegs, online, xlanguage);
		}
	}

	/**
	 * METODO switchJMenus
	 */
	public void switchJMenus(boolean state) {

		dataBase.setEnabled(state);
		tables.setEnabled(state);
		query.setEnabled(state);
		admin.setEnabled(state);
	}

	/**
	 * METODO activeToolBar
	 */
	public void activeToolBar(boolean state) {

		disconnect.setEnabled(state);

		if (permissions[0].equals("true") && state) {

			newDB.setEnabled(true);
			dropDB.setEnabled(true);
			dataBase.setEnabled(true);
		} else {
			newDB.setEnabled(false);
			dropDB.setEnabled(false);
			dataBase.setEnabled(false);
		}

		newTable.setEnabled(state);
		dropTable.setEnabled(state);
		dumpTable.setEnabled(state);

		newTableItem.setEnabled(state);
		dropTableItem.setEnabled(state);
		dumpTableItem.setEnabled(state);
	}

	/**
	 * Metodo addTextLogMonitor Imprime mensajes en el Monitor de Eventos
	 */
	public void addTextLogMonitor(String msg) {

		LogWin.append(msg + "\n");
		int longiT = LogWin.getDocument().getLength();

		if (longiT > 0)
			LogWin.setCaretPosition(longiT - 1);
	}

	/**
	 * METODO delDBRegs Borra una base de datos
	 * 
	 * public void delDBReg(String deathDb){
	 *  }
	 * 
	 */

	/**
	 * METODO connectionLost Informa que la conexion se ha perdido
	 */
	public void connectionLost(String PSQLserver) {

		InterfaceOffLine();
		JOptionPane.showMessageDialog(XPg.this, Language.getWord("DOWSO")
				+ PSQLserver + Language.getWord("DOWSO2"), Language
				.getWord("ERROR!"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * METODO InterfaceOffLine
	 * 
	 */
	public void InterfaceOffLine() {

		if (networkLink)
			guard.goOut();

		HostTree();

		treeView = new JScrollPane(tree);
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));

		splitPpal.setLeftComponent(treeView);
		setTitle("XPg - PostgreSQL GUI");
		addTextLogMonitor(Language.getWord("DISSOF") + pgconn.getHostname()
				+ "\n");

		connected = false;
		switchJMenus(false);
		connect.setEnabled(true);
		connectItem.setEnabled(true);
		disconnectItem.setEnabled(false);
		activeToolBar(false);
		tabbedPane.setEnabled(false);
	}

	/** Maneja el evento de tecla presionada * */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * METODO keyPressed Maneja los eventos de teclas presionadas en el teclado
	 */
	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();
		String keySelected = KeyEvent.getKeyText(keyCode); // cadena que
															// describe la tecla
															// f�sica
															// presionada

		if (keySelected.equals("Delete")) { // si la tecla presionada es delete

			if (DBComponentType == 0) { // Presion� sobre la raiz del arbol de
										// conexi�n, el Servidor

				GenericQuestionDialog killcon = new GenericQuestionDialog(
						XPg.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDISC"),
						Language.getWord("MESGDISC") + DBComponentName + "?");

				boolean sure = killcon.getSelecction();

				if (sure) {

					InterfaceOffLine();
					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("DISSOF")
							+ pgconn.getHostname(), Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);
					pgconn.close();
				}

				return;
			}

			if (DBComponentType == 1) { // DB

				if (dbNames.size() == 1) {

					JOptionPane.showMessageDialog(XPg.this, Language
							.getWord("INVOP"), Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				GenericQuestionDialog killdb = new GenericQuestionDialog(
						XPg.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDELDB"),
						Language.getWord("MESGDELDB") + DBComponentName + "?");

				boolean sure = killdb.getSelecction();

				if (sure) {

					int pos = dbNames.indexOf(DBComponentName);
					PGConnection tempo = (PGConnection) vecConn.remove(pos);
					tempo.close();
					dbNames.remove(pos);
					// Eliminando BD

					String result = pgconn.SQL_Instruction("DROP DATABASE "
							+ DBComponentName);
					addTextLogMonitor(Language.getWord("EXEC")
							+ "DROP DATABASE " + DBComponentName + "\"");

					if (result.equals("OK")) {

						TreePath selPath = tree.getSelectionPath();
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
								.getLastPathComponent());
						DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
								.getParent();
						treeModel.removeNodeFromParent(currentNode);
						addTextLogMonitor(Language.getWord("RES") + result);
					} else {
						String tmp = result.substring(0, result.length() - 1);
						addTextLogMonitor(Language.getWord("RES") + tmp);
						JOptionPane.showMessageDialog(XPg.this, Language
								.getWord("ERRORPOS")
								+ tmp, Language.getWord("ERROR!"),
								JOptionPane.ERROR_MESSAGE);
					}

				}

				return;
			}

			if (DBComponentType == 2
					&& !DBComponentName
							.startsWith(Language.getWord("NOTABLES"))) { // Table

				GenericQuestionDialog killtb = new GenericQuestionDialog(
						XPg.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDELTB"),
						Language.getWord("MESGDELTB") + DBComponentName + "?");

				boolean sure = killtb.getSelecction();

				if (sure) {

					int poss = dbNames.indexOf(ActiveDataB);
					PGConnection konn = (PGConnection) vecConn.elementAt(poss);

					String result = konn.SQL_Instruction("DROP TABLE \""
							+ DBComponentName + "\"");
					String value = "";

					if (result.equals("OK")) {

						TreePath selPath = tree.getSelectionPath();
						TreePath lastPath = selPath.getParentPath();
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
								.getLastPathComponent());
						treeModel.removeNodeFromParent(currentNode);
						DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) (lastPath
								.getLastPathComponent());

						if (NodeDB.getChildCount() == 0) {

							DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
									Language.getWord("NOTABLES"));
							NodeDB.add(nLeaf);
						}

						structuresPanel.setNullTable();
						structuresPanel.setLabel("", "", "");
						recordsPanel.setLabel("", "", "");
						structuresPanel.activeToolBar(false);
						structuresPanel.activeIndexPanel(false);
						tabbedPane.setEnabledAt(0, false);
						tabbedPane.setEnabledAt(1, false);
						tabbedPane.setSelectedIndex(2);
						tree.setSelectionPath(lastPath);
					} else
						result = result.substring(0, result.length() - 1);

					addTextLogMonitor(Language.getWord("EXEC")
							+ "DROP TABLE \"" + DBComponentName + "\";\"");
					addTextLogMonitor(Language.getWord("RES") + result);
				}

				return;
			}
		}
	}

	/*
	 * METODO keyReleased Maneja el evento de tecla liberada.
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * METODO focusGained Maneja un foco para los eventos del teclado
	 */
	public void focusGained(FocusEvent e) {
		Component tmp = e.getComponent();
		tmp.addKeyListener(this);
	}

	/**
	 * METODO focusLost
	 */
	public void focusLost(FocusEvent e) {
		Component tmp = e.getComponent();
		tmp.removeKeyListener(this);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/*
	 * METODO mousePressed Maneja los eventos cuando se hace click con el mouse
	 */
	public void mousePressed(MouseEvent e) {

		treeView.requestFocus();

		if (tree.isEditable())
			tree.setEditable(false);

		// Eventos de un Click con Boton Derecho
		if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {

			TreePath selPath = tree.getClosestPathForLocation(e.getX(), e
					.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();

			if (node.isLeaf()
					&& !node.toString()
							.startsWith(Language.getWord("NOTABLES"))) {

				if (!popup.isVisible()) {

					popup.show(tree, e.getX(), e.getY());

					if (!DBComponentName.equals(node.toString())) {

						DBComponentName = node.toString();
						tree.setSelectionPath(selPath);
						activeFoldersTables(node);
					}
				}
			}

			if (!node.isRoot() && !node.isLeaf()) {

				if (!popupDB.isVisible()) {

					popupDB.show(tree, e.getX(), e.getY());

					if (!DBComponentName.equals(node.toString())) {

						DBComponentName = node.toString();
						tree.setSelectionPath(selPath);
						activeFoldersDB(selPath);
					}
				}

			}
		}

		// Eventos de un Click con Boton Izquierdo

		if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {

			TreePath selPath = tree.getClosestPathForLocation(e.getX(), e
					.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();
			DefaultMutableTreeNode dbnode;
			DBComponentName = node.toString();
			tree.setSelectionPath(selPath);

			// Si se selecciono una Tabla
			if (node.isLeaf()) {
				activeFoldersTables(node);

				int index = dbNames.indexOf(ActiveDataB);
				PGConnection connection = (PGConnection) vecConn
						.elementAt(index);

				queriesPanel.pgConn = connection;
				recordsPanel.setOrder();
			} else {
				// Si se selecciono una Base de Datos
				if (!node.isRoot()) {
					activeFoldersDB(selPath);
				} else {
					// Si se selecciono el Servidor de Base de Datos
					DBComponentType = 0;

					if (OldCompType != DBComponentType) {

						structuresPanel.setNullTable();
						structuresPanel.setLabel("", "", "");
						recordsPanel.setLabel("", "", "");
						structuresPanel.activeToolBar(false);
						structuresPanel.activeIndexPanel(false);
						queriesPanel.setNullPanel();
						queriesPanel.setTextLabel(Language.getWord("NODBSEL"));

						tabbedPane.setEnabledAt(0, false);
						tabbedPane.setEnabledAt(1, false);
						tabbedPane.setEnabledAt(2, false);
						ActiveDataB = "";
					}

					GenericQuestionDialog scanDb = new GenericQuestionDialog(
							XPg.this, Language.getWord("YES"), Language
									.getWord("NO"), Language.getWord("DBSCAN"),
							Language.getWord("DYWLOOK"));

					boolean sure = scanDb.getSelecction();

					if (!sure)
						return;

					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					query.setEnabled(false);

					// Consultando nombres de BD en el servidor
					Vector listDB = pgconn
							.TableQuery("SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' ORDER BY datname");
					addTextLogMonitor(Language.getWord("EXEC")
							+ " SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' ORDER BY datname;\"");

					Vector newsDB = new Vector();

					for (int p = 0; p < listDB.size(); p++) {

						Vector o = (Vector) listDB.elementAt(p);
						String db = (String) o.elementAt(0);

						if (!dbNames.contains(db))
							newsDB.addElement(db);
					}

					if (newsDB.size() > 0) {

						UpdateDBTree updateDBs = new UpdateDBTree(LogWin,
								pgconn, newsDB);
						Vector dbases = updateDBs.getDatabases();
						// Vector dbases = updateDBs.validDB;

						if (dbases.size() > 0) {

							Vector tmpConn = updateDBs.vecConn;

							for (int p = 0; p < dbases.size(); p++) {

								Object o = dbases.elementAt(p);
								String db = o.toString();

								PGConnection tmpDB = (PGConnection) tmpConn
										.elementAt(p);
								vecConn.addElement(tmpDB);
								dbNames.addElement(db);

								DefaultMutableTreeNode dbLeaf = new DefaultMutableTreeNode(
										db);
								DefaultMutableTreeNode noTables = new DefaultMutableTreeNode(
										Language.getWord("NOTABLES"));
								dbLeaf.add(noTables);

								DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
										.getRoot();
								treeModel.insertNodeInto(dbLeaf, parent, parent
										.getChildCount());
							} // fin for
						} // fin if dbases.size
					} // fin if newsDB.size

					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					if (!tree.isExpanded(selPath))
						tree.expandPath(selPath);
				}
			}
			OldCompType = DBComponentType;
		}
	}

	/**
	 * M�todo writeFile Sobre escribe el archivo de configuraci�n usado
	 * cuando el usuario quiere guardar un nuevo idioma
	 */
	public void writeFile(String idiomName) {

		try {
			ConfigFileReader overWrite = new ConfigFileReader(configPath, 2);
			Vector LoginRegisters = overWrite.CompleteList();

			PrintStream configFile = configFile = new PrintStream(
					new FileOutputStream(configPath));
			configFile.println("language=" + idiomName);

			for (int i = 0; i < LoginRegisters.size(); i++) {

				ConnectionInfo tmp = (ConnectionInfo) LoginRegisters
						.elementAt(i);
				configFile.println("server=" + tmp.getHost());
				configFile.println("database=" + tmp.getDatabase());
				configFile.println("username=" + tmp.getUser());
				configFile.println("port=" + tmp.getPort());
				configFile.println("ssl=" + tmp.requireSSL());
				configFile.println("last=" + tmp.getDBChoosed());
			}

			configFile.close();
		} catch (Exception ex) {
		}
	}

	/**
	 * M�todo NConnect Se encarga de realizar las operaciones de conexion
	 */
	public void NConnect() {

		LogWin.setText("");
		boolean fail = true;
		Vector Xtables = new Vector();
		boolean lookForOthers = false;

		while (fail) {
			ConnectionDialog connectionForm = new ConnectionDialog(LogWin,
					XPg.this);
			connectionForm.setLanguage(xlanguage);
			connectionForm.pack();
			connectionForm.setLocationRelativeTo(XPg.this);
			connectionForm.show();

			if (connectionForm.Connected()) {

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				lookForOthers = connectionForm.lookForOthers();
				networkLink = connectionForm.checkLink();
				online = connectionForm.getDataReg();
				pgconn = new PGConnection(online);

				if (!pgconn.Fail()) {

					fail = false;
					Xtables = pgconn
							.TableQuery("SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename");
					permissions = pgconn.getUserPerm(online.getUser());
					ReConfigFile(connectionForm.getDataReg(), connectionForm
							.getConfigRegisters(), connectionForm
							.getRegisterSelected());
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					ErrorDialog showError = new ErrorDialog(new JDialog(),
							pgconn.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(XPg.this);
					showError.show();
				}
			} else {
				return;
			}
		} // fin while

		connected = true;
		String strQuery = "SELECT datname FROM pg_database WHERE";

		if (!lookForOthers)
			strQuery += " datname='" + online.getDatabase() + "'";
		else
			strQuery += " datname != 'template1' AND datname != 'template0'";

		strQuery += " ORDER BY datname";

		Vector listDB = pgconn.TableQuery(strQuery);
		int numDbases = listDB.size();

		addTextLogMonitor(Language.getWord("LOOKDBS") + online.getHost() + "'");
		addTextLogMonitor(Language.getWord("EXEC") + " " + strQuery + ";\"");
		addTextLogMonitor(numDbases + " " + Language.getWord("DBON")
				+ online.getHost());

		if (numDbases > 0) {

			for (int i = 0; i < numDbases; i++) {

				Vector o = (Vector) listDB.elementAt(i);
				String dbname = (String) o.elementAt(0);
				addTextLogMonitor(Language.getWord("TRYCONN") + ": \"" + dbname
						+ "\"... ");
				ConnectionInfo tmp = new ConnectionInfo(online.getHost(),
						dbname, online.getUser(), online.getPassword(), online
								.getPort(), online.requireSSL());
				PGConnection proofConn = new PGConnection(tmp);

				if (!proofConn.Fail()) {

					addTextLogMonitor(Language.getWord("OKACCESS"));
					dbNames.addElement(dbname);
					vecConn.addElement(proofConn);
				} else {
					addTextLogMonitor(Language.getWord("NOACCESS"));
				}
			} // fin for
		} // fin if

		top = new DefaultMutableTreeNode(online.getHost());
		numDbases = dbNames.size();
		addTextLogMonitor(Language.getWord("REPORT")
				+ Language.getWord("USER ") + pgconn.getUsername()
				+ Language.getWord("VALID") + numDbases
				+ Language.getWord("NUMDB"));
		int index = -1;

		for (int m = 0; m < numDbases; m++) {

			Object o = dbNames.elementAt(m);
			String dbname = o.toString();
			addTextLogMonitor(Language.getWord("DB: ") + dbname);
			category1 = new DefaultMutableTreeNode(dbname);
			top.add(category1);

			if (dbname.equals(online.getDatabase())) {

				index = m;
				Vector OneTable = Xtables;
				int numTables = OneTable.size();

				if (numTables == 0) {
					globalLeaf = new DefaultMutableTreeNode(Language
							.getWord("NOTABLES"));
					category1.add(globalLeaf);
				} else {
					for (int i = 0; i < numTables; i++) {

						Vector t = (Vector) OneTable.elementAt(i);
						String tablename = (String) t.elementAt(0);
						globalLeaf = new DefaultMutableTreeNode(tablename);
						category1.add(globalLeaf);
					}
				}// fin else hay tablas
			} else {
				globalLeaf = new DefaultMutableTreeNode(Language
						.getWord("NOTABLES"));
				category1.add(globalLeaf);
			}
		}// fin ciclo for

		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(renderer);
		tree.expandRow(0);
		tree.expandRow(index + 1);
		tree.setSelectionRow(index + 1);

		treeView = new JScrollPane(tree);
		treeView.addFocusListener(this);
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));

		splitPpal.setLeftComponent(treeView);
		splitPpal.setDividerLocation(135);

		tree.addMouseListener(this);

		connect.setEnabled(false);
		connectItem.setEnabled(false);
		disconnectItem.setEnabled(true);
		activeToolBar(true);
		switchJMenus(true); // encender los botones requeridos cuando la
							// conexi�n es exitosa

		if (permissions[0].equals("false"))
			dataBase.setEnabled(false);

		if (permissions[1].equals("false"))
			admin.setEnabled(false);

		tabbedPane.setEnabled(true);
		tabbedPane.setEnabledAt(0, false);
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, true);
		tabbedPane.setSelectedIndex(2);

		queriesPanel.setButtons();

		DBComponentName = online.getDatabase();
		DBComponentType = 1;
		ActiveDataB = DBComponentName;
		int pox = dbNames.indexOf(ActiveDataB);
		queriesPanel.pgConn = (PGConnection) vecConn.elementAt(pox);
		queriesPanel.setLabel(ActiveDataB, queriesPanel.pgConn.getOwnerDB());
		OldCompType = 1;
		String sslEnabled = Language.getWord("DISABLE");
		if (online.requireSSL())
			sslEnabled = Language.getWord("ENABLE");

		setTitle(Language.getWord("UONLINE") + pgconn.getUsername());
		String mesg0 = Language.getWord("INFOSERVER") + pgconn.getHostname();
		String mesg1 = Language.getWord("VERSION") + pgconn.getVersion();
		String mesg2 = Language.getWord("WACCESS") + numDbases
				+ Language.getWord("NUMDB");
		String mesg3 = Language.getWord("CHKSSL") + ": " + sslEnabled;

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		JOptionPane.showMessageDialog(XPg.this, mesg0 + "\n" + mesg1 + "\n"
				+ mesg2 + "\n" + mesg3, Language.getWord("INFO"),
				JOptionPane.INFORMATION_MESSAGE);

		queriesPanel.queryX.requestFocus();

		String hostname = pgconn.getHostname();
		int port = pgconn.getPort();

		if (networkLink) {
			guard = new ConnectionWatcher(hostname, port, XPg.this);
			guard.start();
		}

		ChangeListener l = new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				int carpeta = tabbedPane.getSelectedIndex();
				int pox = dbNames.indexOf(ActiveDataB);
				PGConnection connection;

				if (pox != -1)
					connection = (PGConnection) vecConn.elementAt(pox);
				else
					connection = (PGConnection) vecConn.elementAt(0);

				currentTable = connection.getSpecStrucTable(DBComponentName);

				switch (carpeta) {

				case 0:
					ActivedTabbed = 0;
					indices = connection.getIndexTable(DBComponentName);
					addTextLogMonitor(Language.getWord("EXEC")
							+ connection.getSQL() + "\"");
					structuresPanel.activeToolBar(true);
					structuresPanel.activeIndexPanel(true);

					String result = connection.getOwner(DBComponentName);
					structuresPanel.setLabel(ActiveDataB, DBComponentName,
							result);
					structuresPanel.setTableStruct(currentTable);
					structuresPanel.setIndexTable(indices, connection);
					break;

				case 1:
					ActivedTabbed = 1;
					recordsPanel.setRecordFilter(DBComponentName, ActiveDataB);

					/*
					 * if (!evaluatedDB.contains(DBComponentName)) {
					 * currentTable.setSchema(connection.getSchemaName(DBComponentName));
					 * currentTable.setSchemaType(connection.gotUserSchema(DBComponentName));
					 * evaluatedDB.addElement(DBComponentName); }
					 */

					if (!recordsPanel.updateTable(connection, DBComponentName,
							currentTable)) {

						tabbedPane.setSelectedIndex(0);
						ActivedTabbed = 0;

						indices = connection.getIndexTable(DBComponentName);
						addTextLogMonitor(Language.getWord("EXEC")
								+ connection.getSQL() + "\"");
						structuresPanel.activeToolBar(true);
						structuresPanel.activeIndexPanel(true);
						String ownerName = connection.getOwner(DBComponentName);
						structuresPanel.setLabel(ActiveDataB, DBComponentName,
								ownerName);
						structuresPanel.setTableStruct(currentTable);
						structuresPanel.setIndexTable(indices, connection);
					}
					break;

				case 2:
					ActivedTabbed = 2;
					updateQueriesPanel(connection.getOwnerDB());
					break;
				}
			}
		};

		tabbedPane.addChangeListener(l);
	}

	/**
	 * METODO getTime Retorna la hora
	 */
	public String[] getTime() {

		Calendar today = Calendar.getInstance();
		String[] val = new String[5];
		int monthInt = today.get(Calendar.MONTH) + 1;
		int minuteInt = today.get(Calendar.MINUTE);
		String zero = "";
		String min = "";

		if (monthInt < 10)
			zero = "0";

		if (minuteInt < 10)
			min = "0";

		val[0] = "" + today.get(Calendar.DAY_OF_MONTH);
		val[1] = zero + monthInt;
		val[2] = "" + today.get(Calendar.YEAR);
		val[3] = "" + today.get(Calendar.HOUR_OF_DAY);
		val[4] = min + today.get(Calendar.MINUTE);

		return val;
	}

	/**
	 * METODO DateLogName Crea el nombre del archivo de logs segun la fecha
	 */
	public String DateLogName(String[] val) {

		String dformat = val[0] + "-" + val[1] + "-" + val[2] + "_" + val[3]
				+ "-" + val[4];
		return dformat;
	}

	public String DateClassic(String[] val) {

		String dformat = val[3] + ":" + val[4] + " " + val[0] + "/" + val[1]
				+ "/" + val[2];
		return dformat;
	}

	public void printFile(PrintStream xfile, Vector registers,
			Vector FieldNames, String Separator, TableHeader theader) {

		Vector types = new Vector();

		try {
			int TableWidth = FieldNames.size();

			for (int p = 0; p < TableWidth; p++) {

				String column = (String) FieldNames.elementAt(p);
				types.addElement(theader.getType(column));
				xfile.print(column);

				if (p < TableWidth - 1)
					xfile.print(Separator);
			}

			xfile.print("\n");

			for (int p = 0; p < registers.size(); p++) {

				Vector rData = (Vector) registers.elementAt(p);

				for (int i = 0; i < TableWidth; i++) {

					Object o = rData.elementAt(i);
					String Stype = (String) types.elementAt(i);
					String field = "null";

					if (o != null) {

						if (Stype.startsWith("varchar")
								|| Stype.startsWith("name")
								|| Stype.startsWith("text"))
							field = o.toString();

						if (Stype.startsWith("int")) {
							Integer ipr = (Integer) o;
							field = "" + ipr;
						}

						if (Stype.startsWith("float")
								|| Stype.startsWith("decimal")) {
							Integer ipr = (Integer) o;
							field = "" + ipr;
						}

						if (Stype.startsWith("bool")) {
							Boolean bx = (Boolean) o;
							field = "" + bx;
						}

					}

					xfile.print(field);

					if (i < TableWidth - 1)
						xfile.print(Separator);

				} // fin for

				xfile.print("\n");
			} // fin for

			xfile.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * METODO Count Retorna el numero de registros de una tabla
	 */
	public int Count(String TableN, PGConnection konn) {

		int val;
		String counting = "SELECT count(*) FROM " + TableN + ";";
		Vector result = konn.TableQuery(counting);
		Vector value = (Vector) result.elementAt(0);

		try {
			Integer entero = (Integer) value.elementAt(0);
			val = entero.intValue();
		} catch (Exception ex) {
			Long entero = (Long) value.elementAt(0);
			val = entero.intValue();
		}

		return val;
	}

	/**
	 * METODO closePGConnections Cierra todas las conexiones a un servidor
	 */
	public void closePGSockets() {

		/* ciclo for para cerrar todas la pg_konn activas */
		for (int p = 0; p < vecConn.size(); p++) {
			PGConnection tempo = (PGConnection) vecConn.remove(p);
			tempo.close();
		}

		pgconn.close();
		dbNames = new Vector();
		vecConn = new Vector();
	}

	/**
	 * METODO Disconnect Desactiva la conexion entre el cliente y el SMBD
	 */
	public void Disconnect() {

		closePGSockets();

		structuresPanel.activeToolBar(false);
		structuresPanel.activeIndexPanel(false);
		structuresPanel.setNullTable();

		recordsPanel.activeInterface(false);
		queriesPanel.setNullPanel();
		InterfaceOffLine();
		SaveLog();

		JOptionPane.showMessageDialog(XPg.this, Language.getWord("DISSOF")
				+ pgconn.getHostname(), Language.getWord("INFO"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * METODO main : SaveLog Guarda a un archivo la informacion contenida en el
	 * Monitor de Eventos
	 */
	public void SaveLog() {

		try {
			String LogName = DateLogName(getTime()) + ".log";
			String UHome = "logs" + System.getProperty("file.separator");

			if (OS.equals("Linux") || OS.equals("Solaris")
					|| OS.equals("FreeBSD"))

				UHome = System.getProperty("user.home")
						+ System.getProperty("file.separator") + ".xpg"
						+ System.getProperty("file.separator") + "logs"
						+ System.getProperty("file.separator");

			PrintStream fileLog = new PrintStream(new FileOutputStream(UHome
					+ LogName));
			fileLog.print("Begin: " + startDate + "\n" + "\n");
			fileLog.print(LogWin.getText());
			fileLog.print("End: " + DateClassic(getTime()));
			fileLog.close();
		} catch (Exception ex) {

			System.out.println("Error: " + ex);
			ex.printStackTrace();
		}
	}

	public void activeFoldersDB(TreePath selPath) {

		tabbedPane.setSelectedIndex(2);

		queriesPanel.setTextAreaEditable();
		if (!query.isEnabled())
			query.setEnabled(true);

		if (OldCompType == 0 || !queriesPanel.functions.isEnabled()) {
			tabbedPane.setEnabledAt(2, true);
			queriesPanel.queryX.setEditable(true);
			queriesPanel.functions.setEnabled(true);
			queriesPanel.loadQuery.setEnabled(true);
			queriesPanel.hqQuery.setEnabled(true);
		}

		ActiveDataB = DBComponentName;

		int index = dbNames.indexOf(ActiveDataB);
		PGConnection konn = (PGConnection) vecConn.elementAt(index);
		queriesPanel.pgConn = konn;

		queriesPanel.setLabel(ActiveDataB, konn.getOwnerDB());

		DBComponentType = 1;

		// Consultando nombres de tablas de una BD, sin incluir tablas del
		// sistema
		Vector RefreshTableList = konn
				.TableQuery("SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename  !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename");
		addTextLogMonitor(Language.getWord("EXEC")
				+ " SELECT tablename FROM pg_tables WHERE tablename  !~ '^pg_' AND tablename  !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename\"");

		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
				.getLastPathComponent());
		currentNode.removeAllChildren();

		if (RefreshTableList.size() != 0) {

			for (int i = 0; i < RefreshTableList.size(); i++) {

				Vector o = (Vector) RefreshTableList.elementAt(i);
				String nameT = (String) o.elementAt(0);
				DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(nameT);
				currentNode.add(nLeaf);
			}
		} else {
			DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(Language
					.getWord("NOTABLES"));
			currentNode.add(nLeaf);
		}

		treeModel.nodeStructureChanged(currentNode);

		if (!tree.isExpanded(selPath))
			tree.expandPath(selPath);
		else
			tree.collapsePath(selPath);

		if (OldCompType != 1) {

			structuresPanel.setNullTable();
			structuresPanel.setLabel("", "", "");
			recordsPanel.setLabel("", "", "");
			structuresPanel.activeToolBar(false);
			structuresPanel.activeIndexPanel(false);
			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(1, false);
		}
	}

	public void activeFoldersTables(DefaultMutableTreeNode node) {

		DefaultMutableTreeNode dbnode;
		DBComponentType = 2;
		dbnode = (DefaultMutableTreeNode) node.getParent();
		ActiveDataB = dbnode.toString();

		if (OldCompType == 0) {

			tabbedPane.setEnabledAt(2, true);
			queriesPanel.functions.setEnabled(true);
			queriesPanel.loadQuery.setEnabled(true);
		}

		int index = dbNames.indexOf(ActiveDataB);
		PGConnection connection = (PGConnection) vecConn.elementAt(index);

		if (!DBComponentName.startsWith(Language.getWord("NOTABLES"))) {

			queriesPanel.setTextAreaEditable();

			if (!query.isEnabled())
				query.setEnabled(true);

			currentTable = connection.getSpecStrucTable(DBComponentName);
			indices = connection.getIndexTable(DBComponentName);
			tabbedPane.setEnabledAt(0, true);
			tabbedPane.setEnabledAt(1, true);
			// Adicion temporal
			tabbedPane.setEnabledAt(2, true);
			structuresPanel.updateUI();

			if (ActivedTabbed == 0) {

				structuresPanel.activeToolBar(true);
				structuresPanel.activeIndexPanel(true);
				String result = connection.getOwner(DBComponentName);
				structuresPanel.setLabel(ActiveDataB, DBComponentName, result);
				structuresPanel.setTableStruct(currentTable);
				structuresPanel.setIndexTable(indices, connection);
			}

			if (ActivedTabbed == 1) {

				recordsPanel.setRecordFilter(DBComponentName, ActiveDataB);

				/*
				 * if (!evaluatedDB.contains(DBComponentName)) {
				 * currentTable.setSchema(connection.getSchemaName(DBComponentName));
				 * currentTable.setSchemaType(connection.gotUserSchema(DBComponentName));
				 * evaluatedDB.addElement(DBComponentName); }
				 */

				if (!recordsPanel.updateTable(connection, DBComponentName,
						currentTable)) {

					tabbedPane.setSelectedIndex(0);
					ActivedTabbed = 0;

					structuresPanel.activeToolBar(true);
					structuresPanel.activeIndexPanel(true);

					String result = connection.getOwner(DBComponentName);
					structuresPanel.setLabel(ActiveDataB, DBComponentName,
							result);
					structuresPanel.setTableStruct(currentTable);
					structuresPanel.setIndexTable(indices, connection);
				}
			}

			if (ActivedTabbed == 2) {
				updateQueriesPanel(connection.getOwnerDB());
			}

		} else {
			if (ActivedTabbed == 0) {
				tabbedPane.setSelectedIndex(2);
				ActivedTabbed = 2;
			}

			if (ActivedTabbed == 1) {
				tabbedPane.setSelectedIndex(2);
				ActivedTabbed = 2;
			}

			if (ActivedTabbed == 2) {
				queriesPanel.setLabel(ActiveDataB, connection.getOwnerDB());
			}

			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(1, false);
		}
	}

	public String createTableSQL(Table currentTable) {

		String sql = "CREATE TABLE " + currentTable.getName() + " (\n";
		// Nuevos datos de la tabla
		TableHeader headT = currentTable.getTableHeader();
		int numFields = headT.getNumFields();

		for (int k = 0; k < numFields; k++) {

			Object o = (String) headT.getNameFields().elementAt(k);
			String field_name = o.toString();
			TableFieldRecord tmp = (TableFieldRecord) headT.hashFields
					.get(field_name);
			sql += tmp.getName() + " ";

			String typeF = tmp.getType();

			if ("char".equals(tmp.getType()) || "varchar".equals(tmp.getType())) {

				int longStr = tmp.getOptions().getCharLong();

				if (longStr > 0)
					typeF = tmp.getType() + "("
							+ tmp.getOptions().getCharLong() + ")";
				else
					typeF = tmp.getType();
			}

			sql += typeF + " ";

			Boolean tmpbool = new Boolean(tmp.getOptions().isNullField());

			if (tmpbool.booleanValue())
				sql += "NOT NULL ";

			String defaultV = tmp.getOptions().getDefaultValue();

			if (defaultV.endsWith("::bool")) {

				if (defaultV.indexOf("t") != -1)
					defaultV = "true";
				else
					defaultV = "false";
			}

			if (defaultV.length() > 0)
				sql += " DEFAULT " + defaultV;

			if (k < numFields - 1)
				sql += ",\n";
		}

		sql += "\n);";

		return sql;
	}

	/**
	 * Metodo funcBasicStruct Organiza la definicion de las instrucciones SQL
	 * basicas
	 */

	public SQLFuncBasic[] funcBasicStruct() {

		SQLFuncBasic[] basicArray = new SQLFuncBasic[38];

		String[][] descriptFunc = {
				{
						"ALTER GROUP",
						"ALTER GROUP name ADD USER username [, ... ]<br>ALTER GROUP name DROP USER username [, ... ]" },
				{
						"ALTER TABLE",
						"ALTER TABLE [ ONLY ] table [ * ]<br>ADD [ COLUMN ] column type [ column_constraint [ ... ] ]<br><br>ALTER TABLE [ ONLY ] table [ * ]<br>ALTER [ COLUMN ] column { SET DEFAULT value | DROP DEFAULT }<br><br>ALTER TABLE [ ONLY ] table [ * ]<br>ALTER [ COLUMN ] column SET STATISTICS integer<br><br>ALTER TABLE [ ONLY ] table [ * ]<br>RENAME [ COLUMN ] column TO newcolumn<br><br>ALTER TABLE table<br>RENAME TO new_table<br><br>ALTER TABLE table<br>ADD table_constraint_definition<br><br>ALTER TABLE [ ONLY ] table<br>DROP CONSTRAINT constraint { RESTRICT | CASCADE }<br><br>ALTER TABLE table<br>OWNER TO new_owner" },
				{
						"ALTER USER",
						"ALTER USER username [ [ WITH ] option [ ... ] ]<br>"
								+ Language.getWord("WHERE")
								+ " <b>option</b> "
								+ Language.getWord("CAN")
								+ "<br>[ ENCRYPTED | UNENCRYPTED ] PASSWORD 'password'<br>| CREATEDB | NOCREATEDB<br>| CREATEUSER | NOCREATEUSER<br>| VALID UNTIL 'abstime'" },
				{
						"CREATE AGGREGATE",
						"CREATE AGGREGATE name ( BASETYPE = input_data_type,<br>SFUNC = sfunc, STYPE = state_type<br>[ , FINALFUNC = ffunc ]<br>[ , INITCOND = initial_condition ] )" },
				{
						"CONSTRAINT TRIGGER",
						"CREATE CONSTRAINT TRIGGER name<br>AFTER events ON<br>relation constraint attributes<br>FOR EACH ROW EXECUTE PROCEDURE func '(' args ')'" },
				{
						"CREATE DATABASE",
						"CREATE DATABASE name<br>[ WITH [ LOCATION = 'dbpath' ]<br>[ TEMPLATE = template ]<br>[ ENCODING = encoding ] ]" },
				{
						"CREATE FUNCTION",
						"CREATE [ OR REPLACE ] FUNCTION name ( [ argtype [, ...] ] )<br>RETURNS rettype<br>AS 'definition'<br>LANGUAGE langname<br>[ WITH ( attribute [, ...] ) ]<br><br>CREATE [ OR REPLACE ] FUNCTION name ( [ argtype [, ...] ] )<br>RETURNS rettype<br>AS 'obj_file', 'link_symbol'<br>LANGUAGE langname<br>[ WITH ( attribute [, ...] ) ]" },
				{
						"CREATE GROUP",
						"CREATE GROUP name [ [ WITH ] option [ ... ] ]<br>"
								+ Language.getWord("WHERE") + " <b>option</b> "
								+ Language.getWord("CAN")
								+ "<br>SYSID gid<br>| USER  username [, ...]" },
				{
						"CREATE INDEX",
						"CREATE [ UNIQUE ] INDEX index_name ON table<br>[ USING acc_method ] ( column [ ops_name ] [, ...] )<br>[ WHERE predicate ]<br><br>CREATE [ UNIQUE ] INDEX index_name ON table<br>[ USING acc_method ] ( func_name( column [, ... ]) [ ops_name ] )<br>[ WHERE predicate ]" },
				{ "CREATE LANGUAGE",
						"CREATE [ TRUSTED ] [ PROCEDURAL ] LANGUAGE langname<br>HANDLER call_handler" },
				{
						"CREATE OPERATOR",
						"CREATE OPERATOR name ( PROCEDURE = func_name<br>[, LEFTARG = lefttype<br>] [, RIGHTARG = righttype ]<br>[, COMMUTATOR = com_op ] [, NEGATOR = neg_op ]<br>[, RESTRICT = res_proc ] [, JOIN = join_proc ]<br>[, HASHES ] [, SORT1 = left_sort_op ] [, SORT2 = right_sort_op ] )" },
				{
						"CREATE RULE",
						"CREATE RULE name AS ON event<br>TO object [ WHERE condition ]<br>DO [ INSTEAD ] action<br>"
								+ Language.getWord("WHERE")
								+ " <b>action</b> "
								+ Language.getWord("CAN")
								+ "<br>NOTHING<br>|<br>query<br>|<br>( query ; query ... )<br>|<br>[ query ; query ... ]" },
				{
						"CREATE SEQUENCE",
						"CREATE [ TEMPORARY | TEMP ] SEQUENCE seqname [ INCREMENT increment ]<br>[ MINVALUE minvalue ] [ MAXVALUE maxvalue ]<br>[ START start ] [ CACHE cache ] [ CYCLE ]" },
				{
						"CREATE TABLE",
						"&nbsp;&nbsp;CREATE [ [ LOCAL ] { TEMPORARY | TEMP } ] TABLE table_name (<br>&nbsp;&nbsp;{ column_name data_type [ DEFAULT default_expr ] [ column_constraint [, ...<br>&nbsp;&nbsp;] ]<br>&nbsp;&nbsp;    | table_constraint }  [, ... ]<br>&nbsp;&nbsp;)<br>&nbsp;&nbsp;[ INHERITS ( parent_table [, ... ] ) ]<br>&nbsp;&nbsp;[ WITH OIDS | WITHOUT OIDS ]<br><br>"
								+ Language.getWord("WHERE")
								+ " <b>column_constraint</b> "
								+ Language.getWord("IS")
								+ "<br>&nbsp;&nbsp;[ CONSTRAINT constraint_name ]<br>&nbsp;&nbsp;{ NOT NULL | NULL | UNIQUE | PRIMARY KEY |<br>&nbsp;&nbsp;CHECK (expression) |<br>&nbsp;&nbsp;REFERENCES reftable [ ( refcolumn ) ] [ MATCH FULL | MATCH PARTIAL ]<br>&nbsp;&nbsp;[ ON DELETE action ] [ ON UPDATE action ] }<br>&nbsp;&nbsp;[ DEFERRABLE | NOT DEFERRABLE ] [ INITIALLY DEFERRED | INITIALLY IMMEDIATE ]<br><br>"
								+ Language.getWord("AND")
								+ " <b>table_constraint</b> "
								+ Language.getWord("IS")
								+ "<br>&nbsp;&nbsp;[ CONSTRAINT constraint_name ]<br>&nbsp;&nbsp;{ UNIQUE ( column_name [, ... ] ) |<br>&nbsp;&nbsp;PRIMARY KEY ( column_name [, ... ] ) |<br>&nbsp;&nbsp;CHECK ( expression ) |<br>&nbsp;&nbsp;FOREIGN KEY ( column_name [, ... ] ) REFERENCES reftable [ ( refcolumn [, ...<br>&nbsp;&nbsp;] ) ]<br>&nbsp;&nbsp;] ) ]<br>&nbsp;&nbsp;[ MATCH FULL | MATCH PARTIAL ] [ ON DELETE action ] [ ON UPDATE action ] }<br>&nbsp;&nbsp;[ DEFERRABLE | NOT DEFERRABLE ] [ INITIALLY DEFERRED | INITIALLY IMMEDIATE ]" },
				{
						"CREATE TABLE AS",
						"CREATE [ [ LOCAL ] { TEMPORARY | TEMP } ] TABLE table_name [ (column_name [, ...] ) ]<br>AS query" },
				{
						"CREATE TRIGGER",
						"CREATE TRIGGER name { BEFORE | AFTER } { event [OR ...] }<br>ON table FOR EACH { ROW | STATEMENT }<br>EXECUTE PROCEDURE func ( arguments )" },
				{
						"CREATE TYPE",
						"CREATE TYPE typename ( INPUT = input_function, OUTPUT = output_function<br>, INTERNALLENGTH = { internallength | VARIABLE }<br>[ , EXTERNALLENGTH = { externallength | VARIABLE } ]<br>[ , DEFAULT = default ]<br>[ , ELEMENT = element ] [ , DELIMITER = delimiter ]<br>[ , SEND = send_function ] [ , RECEIVE = receive_function ]<br>[ , PASSEDBYVALUE ]<br>[ , ALIGNMENT = alignment ]<br>[ , ALIGNMENT = alignment ]<br>[ , STORAGE = storage ]<br>)" },
				{
						"CREATE USER",
						"CREATE USER username [ [ WITH ] option [ ... ] ]<br>"
								+ Language.getWord("WHERE")
								+ " <b>option</b> "
								+ Language.getWord("CAN")
								+ "<br>SYSID uid<br>| [ ENCRYPTED | UNENCRYPTED ] PASSWORD 'password'<br>| CREATEDB | NOCREATEDB<br>| CREATEUSER | NOCREATEUSER<br>| IN GROUP groupname [, ...]<br>| VALID UNTIL 'abstime'" },
				{ "CREATE VIEW",
						"CREATE VIEW view [ ( column name list ) ] AS SELECT query" },
				{ "DELETE", "DELETE FROM [ ONLY ] table [ WHERE condition ]" },
				{ "DROP AGGREGATE", "DROP AGGREGATE name ( type )" },
				{ "DROP DATABASE", "DROP DATABASE name" },
				{ "DROP FUNCTION", "DROP FUNCTION name ( [ type [, ...] ] )" },
				{ "DROP GROUP", "DROP GROUP name" },
				{ "DROP INDEX", "DROP INDEX index_name [, ...]" },
				{ "DROP LANGUAGE", "DROP [ PROCEDURAL ] LANGUAGE name" },
				{ "DROP OPERATOR",
						"DROP OPERATOR id ( lefttype | NONE , righttype | NONE )" },
				{ "DROP RULE", "DROP RULE name [, ...]" },
				{ "DROP SEQUENCE", "DROP SEQUENCE name [, ...]" },
				{ "DROP TABLE", "DROP TABLE name [, ...]" },
				{ "DROP TRIGGER", "DROP TRIGGER name ON table" },
				{ "DROP TYPE", "DROP TYPE typename [, ...]" },
				{ "DROP USER", "DROP USER name" },
				{ "DROP VIEW", "DROP VIEW name [, ...]" },
				{
						"GRANT",
						"GRANT { { SELECT | INSERT | UPDATE | DELETE | RULE | REFERENCES | TRIGGER } [,...] | ALL [ PRIVILEGES ] }<br>ON [ TABLE ] objectname [, ...]<br>TO { username | GROUP groupname | PUBLIC } [, ...]" },
				{
						"REVOKE",
						"REVOKE { { SELECT | INSERT | UPDATE | DELETE | RULE | REFERENCES | TRIGGER } [,...] | ALL [ PRIVILEGES ] }<br>ON [ TABLE ] object [, ...]<br>FROM { username | GROUP groupname | PUBLIC } [, ...]" },
				{
						"SELECT",
						"SELECT [ ALL | DISTINCT [ ON ( expression [, ...] ) ] ]<br>* | expression [ AS output_name ] [, ...]<br>[ FROM from_item [, ...] ]<br>[ WHERE condition ]<br>[ GROUP BY expression [, ...] ]<br>[ HAVING condition [, ...] ]<br>[ { UNION | INTERSECT | EXCEPT } [ ALL ] select ]<br>[ ORDER BY expression [ ASC | DESC | USING operator ] [, ...] ]<br>[ FOR UPDATE [ OF tablename [, ...] ] ]<br>[ LIMIT { count | ALL } ]<br>[ OFFSET start ]<br><br>"
								+ Language.getWord("WHERE")
								+ " <b>from_item</b> "
								+ Language.getWord("CAN")
								+ "<br><br>[ ONLY ] table_name [ * ]<br>[ [ AS ] alias [ ( column_alias_list ) ] ]<br>|<br>( select )<br>[ AS ] alias [ ( column_alias_list ) ]<br>|<br>from_item [ NATURAL ] join_type from_item<br>[ ON join_condition | USING ( join_column_list ) ]" },
				{
						"UPDATE",
						"UPDATE [ ONLY ] table SET col = expression [, ...]<br>[ FROM fromlist ]<br>[ WHERE condition ]" } };

		for (int i = 0; i < 38; i++) {
			int k = i + 1;
			SQLFuncBasic func = new SQLFuncBasic(descriptFunc[i][0], Language
					.getWord("FDB" + k), descriptFunc[i][1]);
			basicArray[i] = func;
		}

		return basicArray;
	}

	/**
	 * Metodo funcDataStruct Organiza la definicion de las funciones
	 */
	public SQLFunctionDataStruc[] funcDataStruct() {

		SQLFunctionDataStruc[] funcList = new SQLFunctionDataStruc[100];
		int[] funcSets = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1,
				1, 1, 1, 1, 2, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 4, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 2, 2, 1, 3, 4, 1, 1, 1,
				1, 2, 1, 2, 1 };

		String[][] descriptFunc = {
				{ "COALESCE(list)", "non-NULL", "COALESCE(rle, c2 + 5, 0)" },
				{ "NULLIF(input,value)", "input or NULL", "NULLIF(c1, 'N/A')" },
				{ "CASE WHEN expr THEN expr [...] ELSE expr END", "expr",
						"CASE WHEN c1 = 1 THEN 'match'<br>ELSE 'no match' END" },
				{ "abs(float8)", "float8", "abs(-17.4)" },
				{ "degrees(float8)", "float8", "degrees(0.5)" },
				{ "exp(float8)", "float8", "exp(2.0)" },
				{ "ln(float8)", "float8", "ln(2.0)" },
				{ "log(float8)", "float8", "log(2.0)" },
				{ "pi()", "float8", "pi()" },
				{ "pow(float8,float8)", "float8", "pow(2.0, 16.0)" },
				{ "radians(float8)", "float8", "radians(45.0)" },
				{ "round(float8)", "float8", "round(42.4)" },
				{ "sqrt(float8)", "float8", "sqrt(2.0)" },
				{ "cbrt(float8)", "float8", "cbrt(27.0)" },
				{ "trunc(float8)", "float8", "trunc(42.4)" },
				{ "float(int)", "float8", "float(2)" },
				{ "float4(int)", "float4", "float4(2)" },
				{ "integer(float)", "int", "integer(2.0)" },
				{ "acos(float8)", "float8", "acos(10.0)" },
				{ "asin(float8)", "float8", "asin(10.0)" },
				{ "atan(float8)", "float8", "atan(10.0)" },
				{ "atan2(float8,float8)", "float8", "atan3(10.0,20.0)" },
				{ "cos(float8)", "float8", "cos(0.4)" },
				{ "cot(float8)", "float8", "cot(20.0)" },
				{ "sin(float8)", "float8", "cos(0.4)" },
				{ "tan(float8)", "float8", "tan(0.4)" },
				{ "char_length(string)", "int4", "char_length('jose')" },
				{ "character_length(string)", "int4",
						"character_length('jose')" },
				{ "lower(string)", "string", "lower('TOM')" },
				{ "octet_length(string)", "int4", "octet_length('jose')" },
				{ "position(string in string)", "int4",
						"position('o' in 'Tom')" },
				{ "substring(string [from int] [for int])", "string",
						"substring('Tom' from 2 for 2)" },
				{ "trim([leading|trailing|both] [string] from string)",
						"string", "trim(both 'x' from 'xTomx')" },
				{ "upper(text)", "text", "upper('tom')" },
				{ "char(text)", "char", "char('text string')" },
				{ "char(varchar)", "char", "char(varchar 'varchar string')" },
				{ "initcap(text)", "text", "initcap('thomas')" },
				{ "lpad(text,int,text)", "text", "lpad('hi',4,'??')" },
				{ "ltrim(text,text)", "text", "ltrim('xxxxtrim','x')" },
				{ "textpos(text,text)", "text", "position('high','ig')" },
				{ "rpad(text,int,text)", "text", "rpad('hi',4,'x')" },
				{ "rtrim(text,text)", "text", "rtrim('trimxxxx','x')" },
				{ "substr(text,int[,int])", "text", "substr('hi there',3,5)" },
				{ "text(char)", "text", "text('char string')" },
				{ "text(varchar)", "text", "text(varchar 'varchar string')" },
				{ "translate(text,from,to)", "text",
						"translate('12345', '1', 'a')" },
				{ "varchar(char)", "varchar", "varchar('char string')" },
				{ "varchar(text)", "varchar", "varchar('text string')" },
				{ "abstime(timestamp)", "abstime", "abstime(timestamp 'now')" },
				{ "age(timestamp)", "interval", "age(timestamp '1957-06-13')" },
				{ "age(timestamp,timestamp)", "interval",
						"age('now', timestamp '1957-06-13')" },
				{ "date_part(text,timestamp)", "float8",
						"date_part('dow',timestamp 'now')" },
				{ "date_part(text,interval)", "float8",
						"date_part('hour',interval '4 hrs 3 mins')" },
				{ "date_trunc(text,timestamp)", "timestamp",
						"date_trunc('month',abstime 'now')" },
				{ "interval(reltime)", "interval",
						"interval(reltime '4 hours')" },
				{ "isfinite(timestamp)", "bool", "isfinite(timestamp 'now')" },
				{ "isfinite(interval)", "bool", "isfinite(interval '4 hrs')" },
				{ "reltime(interval)", "reltime", "reltime(interval '4 hrs')" },
				{ "timestamp(date)", "timestamp", "timestamp(date 'today')" },
				{ "timestamp(date,time)", "timestamp",
						"timestamp(timestamp '1998-02-24',time '23:07');" },
				{ "to_char(timestamp,text)", "text",
						"to_char(timestamp '1998-02-24','DD');" },
				{ "to_char(timestamp, text)", "text",
						"to_char(timestamp 'now','HH12:MI:SS')" },
				{ "to_char(int, text)", "text", "to_char(125, '999')" },
				{ "to_char(float, text)", "text", "to_char(125.8, '999D9')" },
				{ "to_char(numeric, text)", "text",
						"to_char(numeric '-125.8', '999D99S')" },
				{ "to_date(text, text)", "date",
						"to_date('05 Dec 2000', 'DD Mon YYYY')" },
				{ "to_timestamp(text, text)", "date",
						"to_timestamp('05 Dec 2000', 'DD Mon YYYY')" },
				{ "to_number(text, text)", "numeric",
						"to_number('12,454.8-', '99G999D9S')" },
				{ "area(object)", "float8", "area(box '((0,0),(1,1))')" },
				{ "box(box,box)", "box",
						"box(box '((0,0),(1,1))',box '((0.5,0.5),(2,2))')" },
				{ "center(object)", "point", "center(box '((0,0),(1,2))')" },
				{ "diameter(circle)", "float8",
						"diameter(circle '((0,0),2.0)')" },
				{ "height(box)", "float8", "height(box '((0,0),(1,1))')" },
				{ "isclosed(path)", "bool",
						"isclosed(path '((0,0),(1,1),(2,0))')" },
				{ "isopen(path)", "bool", "isopen(path '[(0,0),(1,1),(2,0)]')" },
				{ "length(object)", "float8", "length(path '((-1,0),(1,0))')" },
				{ "pclose(path)", "path", "popen(path '[(0,0),(1,1),(2,0)]')" },
				{ "npoint(path)", "int4", "npoints(path '[(0,0),(1,1),(2,0)]')" },
				{ "popen(path)", "path", "popen(path '((0,0),(1,1),(2,0))')" },
				{ "radius(circle)", "float", "radius(circle '((0,0),2.0)')" },
				{ "width(box)", "float8", "width(box '((0,0),(1,1))')" },
				{ "box(circle)", "box", "box('((0,0),2.0)'::circle)" },
				{ "box(point,point)", "box",
						"box('(0,0)'::point,'(1,1)'::point)" },
				{ "box(polygon)", "box", "box('((0,0),(1,1),(2,0))'::polygon)" },
				{ "circle(box)", "box", "circle('((0,0),(1,1))'::box)" },
				{ "circle(point,float8)", "circle",
						"circle('(0,0)'::point,2.0)" },
				{ "lseg(box)", "lseg", "lseg('((-1,0),(1,0))'::box)" },
				{ "lseg(point,point)", "lseg",
						"lseg('(-1,0)'::point,'(1,0)'::point)" },
				{ "path(polygon)", "point",
						"path('((0,0),(1,1),(2,0))'::polygon)" },
				{ "point(circle)", "point", "point('((0,0),2.0)'::circle)" },
				{ "point(lseg,lseg)", "point",
						"point('((-1,0),(1,0))'::lseg, '((-2,-2),(2,2))'::lseg)" },
				{ "point(polygon)", "point",
						"point('((0,0),(1,1),(2,0))'::polygon)" },
				{ "polygon(box)", "polygon", "polygon('((0,0),(1,1))'::box)" },
				{ "polygon(circle)", "polygon",
						"polygon('((0,0),2.0)'::circle)" },
				{ "polygon(npts,circle)", "polygon",
						"polygon(12,'((0,0),2.0)'::circle)" },
				{ "polygon(path)", "polygon",
						"polygon('((0,0),(1,1),(2,0))'::path)" },
				{ "isoldpath(path)", "path",
						"isoldpath('(1,3,0,0,1,1,2,0)'::path)" },
				{ "revertpoly(polygon)", "polygon",
						"revertpoly('((0,0),(1,1),(2,0))'::polygon)" },
				{ "upgradepath(path)", "path",
						"upgradepath('(1,3,0,0,1,1,2,0)'::path)" },
				{ "upgradepoly(polygon)", "polygon",
						"upgradepoly('(0,1,2,0,1,0)'::polygon)" },
				{ "broadcast(cidr)", "text", "broadcast('192.168.1.5/24')" },
				{ "broadcast(inet)", "text", "broadcast('192.168.1.5/24')" },
				{ "host(inet)", "text", "host('192.168.1.5/24')" },
				{ "masklen(cidr)", "int4", "masklen('192.168.1.5/24')" },
				{ "masklen(inet)", "int4", "masklen('192.168.1.5/24')" },
				{ "netmask(inet)", "text", "netmask('192.168.1.5/24')" } };

		int counter = 0;
		int index = 0;
		SQLFunctionDataStruc func;

		for (int i = 0; i < funcSets.length; i++) {

			if (funcSets[i] == 1) {

				int word = index + 1;
				func = new SQLFunctionDataStruc(descriptFunc[index][0],
						descriptFunc[index][1], Language.getWord("FD" + word),
						descriptFunc[index][2]);
				index++;
			} else {

				func = new SQLFunctionDataStruc();

				for (int j = 0; j < funcSets[i]; j++) {

					int word = index + 1;
					func.addItem(descriptFunc[index][0],
							descriptFunc[index][1], Language.getWord("FD"
									+ word), descriptFunc[index][2]);
					index++;
				}
			}

			funcList[counter] = func;
			counter++;
		}

		return funcList;
	}

	public void updateQueriesPanel(String owner) {

		queriesPanel.setLabel(ActiveDataB, owner);

		queriesPanel.functions.setEnabled(true);
		queriesPanel.loadQuery.setEnabled(true);
		queriesPanel.hqQuery.setEnabled(true);
		queriesPanel.saveQuery.setEnabled(false);
		queriesPanel.runQuery.setEnabled(false);
		queriesPanel.queryX.requestFocus();

		String currString = queriesPanel.getStringQuery();

		if (currString.length() > 1) {

			queriesPanel.newQuery.setEnabled(true);

			if (currString.length() > 15) {
				queriesPanel.saveQuery.setEnabled(true);
				queriesPanel.runQuery.setEnabled(true);
			}
		} else {
			queriesPanel.newQuery.setEnabled(false);
		}
	}

	/**
	 * METODO isConnected Retorna verdadero si la conexion esta activa
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * METODO main : PRINCIPAL Crea un nuevo objeto XPg que inicializa la
	 * aplicaci�n
	 */
	public static void main(String arg[]) {

		final XPg program = new XPg();
		try {
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		/*Font f = new Font("SANS", Font.PLAIN, 12);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("Label.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Slider.font", f);
        UIManager.put("TitledBorder.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("InternalFrame.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TabbedPane.font", f);*/
		program.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Cierra la aplicacion cuando se hace clic en la X del Frame
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				GenericQuestionDialog killX = new GenericQuestionDialog(
						program, Language.getWord("OK"), Language
								.getWord("CANCEL"), Language
								.getWord("BOOLEXIT"), Language
								.getWord("QUESTEXIT"));

				boolean sure = killX.getSelecction();

				if (sure) {
					if (program.isConnected())
						program.closePGSockets();
					System.exit(0);
				} else {
					return;
				}
			}
		};

		program.addWindowListener(l);
		program.NConnect();

	}

}
