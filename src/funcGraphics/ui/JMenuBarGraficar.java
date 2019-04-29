package funcGraphics.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.data.xy.XYSeriesCollection;

import funcGraphics.dominio.Funcion;
import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.io.IOGrafica;

class JMenuBarGraficar extends JMenuBar {
	private static final String MSG_ABOUT = JVentanaGraficar.NOMBRE_APP
			+ ", realizado para Programación Orientada a Objetos (ICAI)\n" + "Autor: Vicente Balmaseda\n"
			+ "Fecha: 29/04/2019\n\n" + "Se ha hecho uso de las librerías open source JFreeChart y Opencsv.";
	private static final boolean DEFAULT_CHECKBOX_SELECTED = true;

	private File archivo;
	private JVentanaGraficar ventana;

	private JMenu mnArchivo;
	private JMenu mnGrafico;
	private JMenu mnAyuda;
	private JFileChooser fileChooser;

	public JMenuBarGraficar(JVentanaGraficar ventana) {
		super();
		this.ventana = ventana;

		// menus
		mnArchivo = new JMenu("Archivo");
		mnGrafico = new JMenu("Gráfico");
		mnAyuda = new JMenu("Ayuda");
		this.add(mnArchivo);
		this.add(mnGrafico);
		this.add(mnAyuda);

		this.menuArchivo();
		this.menuGrafico();

		// menu ayuda
		JMenuItem about = new JMenuItem("About " + JVentanaGraficar.NOMBRE_APP);
		mnAyuda.add(about);

		about.addActionListener(event -> JOptionPane.showMessageDialog(ventana, MSG_ABOUT,
				"About " + JVentanaGraficar.NOMBRE_APP, JOptionPane.PLAIN_MESSAGE));

		// File Chooser
		fileChooser = new JFileChooser() {
			public void approveSelection() {
				File destino = getSelectedFile();
				int type = this.getDialogType();

				if (type == JFileChooser.SAVE_DIALOG) {
					if (this.getFileSelectionMode() == JFileChooser.FILES_ONLY && destino.exists()) {
						String mensaje = destino.getName() + " ya existe.\n¿Desea reemplazarlo?";
						int retOptions = JOptionPane.showConfirmDialog(ventana, mensaje, "Confirmar Guardar Como",
								JOptionPane.OK_CANCEL_OPTION);
						if (retOptions == JOptionPane.OK_OPTION)
							super.approveSelection();
					} else
						super.approveSelection();
				} else
					super.approveSelection();
			}
		};

		fileChooser.setFileFilter(new FileNameExtensionFilter("FuncGraphics files", IOGrafica.EXTENSION));
	}

	private void menuArchivo() {
		// menu archivo
//		JMenuItem nuevo = new JMenuItem("Nuevo", 'n');
		JMenuItem miAbrir = new JMenuItem("Abrir", KeyEvent.VK_O);
		miAbrir.addActionListener(event -> openGrafica());
		miAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		JMenuItem miGuardar = new JMenuItem("Guardar", KeyEvent.VK_G);
		miGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		miGuardar.addActionListener(event -> saveGrafica());
		JMenuItem miGuardarComo = new JMenuItem("Guardar como...");
		miGuardarComo.addActionListener(event -> saveAsGrafica());
		JMenuItem miCerrar = new JMenuItem("Cerrar");
		miCerrar.addActionListener(event -> ventana.exit());

//		archivo.add(nuevo);
		mnArchivo.add(miAbrir);
		mnArchivo.addSeparator();
		mnArchivo.add(miGuardar);
		mnArchivo.add(miGuardarComo);
		mnArchivo.addSeparator();
		mnArchivo.add(miCerrar);

//		nuevo.addActionListener(event -> new JVentanaGraficar());	
	}

	private void menuGrafico() {
		// menu grafico
		JMenuItem miPropiedades = new JMenuItem("Propiedades...", KeyEvent.VK_P);
		miPropiedades.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		JMenuItem miImprimir = new JMenuItem("Imprimir...", KeyEvent.VK_I);
		miImprimir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		JMenu mnExportar = new JMenu("Exportar...");
		mnExportar.setMnemonic(KeyEvent.VK_E);
		mnGrafico.add(miPropiedades);
		mnGrafico.add(miImprimir);
		mnGrafico.addSeparator();
		mnGrafico.add(mnExportar);

		// menu generar
		JMenuItem guardarPNG = new JMenuItem("PNG");
		JMenuItem generarCSV = new JMenuItem("CSV");
		mnExportar.add(guardarPNG);
		mnExportar.add(generarCSV);

		miPropiedades.addActionListener(event -> ventana.getGraficaPanel().doEditChartProperties());
		miImprimir.addActionListener(event -> ventana.getGraficaPanel().createChartPrintJob());
		guardarPNG.addActionListener(event -> {
			try {
				ventana.getGraficaPanel().doSaveAs();
			} catch (IOException e) {
				String mensaje = e.getMessage();
				if (mensaje == null || mensaje.isBlank())
					mensaje = "No se pudo completar con éxito la creación del PNG";
				JOptionPane.showMessageDialog(ventana, mensaje, "Error al guardar PNG", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
			}
		});
		generarCSV.addActionListener(event -> showGenerarCSV());
	}

	/**
	 * Guardado rápido de la gráfica. Si ya ha sido guardada, reemplazará el archivo
	 * existente. En caso contrario funcionará como el guardar como.
	 * @return true si se ha llevado a cabo el guardado con éxito
	 */
	boolean saveGrafica() {
		if (archivo != null && archivo.exists()) {
			return IOsaveGrafica(archivo);
		} else
			return saveAsGrafica();
	}

	/**
	 * Permite elegir al usuario donde guardar la grafica para su posterior
	 * recuperación.
	 * @return true si se ha llevado a cabo el guardado con éxito
	 */
	private boolean saveAsGrafica() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION)
			return IOsaveGrafica(fileChooser.getSelectedFile());
		else
			return false;
	}

	/**
	 * Guarda la gráfica en un archivo.
	 * @param destino File donde se va a guardar
	 * @return true si se ha llevado a cabo el guardado con éxito
	 */
	private boolean IOsaveGrafica(File destino) {
		try {
			IOGrafica.saveGrafica(ventana.getGrafica(), destino);
			archivo = destino;
			ventana.setGuardado(true);
			return true;
		} catch (IOException e) {
			String mensaje = e.getMessage();
			if (mensaje == null || mensaje.isBlank())
				mensaje = "No se pudo guardar con éxito la gráfica";
			JOptionPane.showMessageDialog(null, mensaje, "Error Guardar", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
			return false;
		}
	}

	private void openGrafica() {
		boolean abrir = true;
		if (!ventana.getGrafica().isGuardado()) {
			int opSel = JOptionPane.showConfirmDialog(ventana, "Hay cambios sin guardar.\n¿Desea guardar?",
					"Confirmar Guardar " + JVentanaGraficar.NOMBRE_APP, JOptionPane.YES_NO_OPTION);
			if (opSel == JOptionPane.YES_OPTION) {
				abrir = saveGrafica();
			}
		}

		if (abrir) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
				File fileTemp = fileChooser.getSelectedFile();
				try {
					ventana.openGrafica(IOGrafica.openGrafica(fileTemp));
					archivo = fileTemp;
				} catch (ClassNotFoundException | IOException e) {
					String mensaje = e.getMessage();
					if (mensaje == null || mensaje.isBlank())
						mensaje = "No se pudo abrir con éxito la gráfica desde el archivo " + fileTemp.toString();
					JOptionPane.showMessageDialog(null, mensaje, "Error Abrir", JOptionPane.ERROR_MESSAGE);
//					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Muestra diálogos para elegir que funciones se quieren guardar como archivos
	 * CSV y en que carpeta guardarlos.
	 */
	private void showGenerarCSV() {
		XYSeriesCollection dataCollection = ventana.getGrafica().getDataCollection();
		HashSet<Funcion> funciones = new HashSet<Funcion>();
		for (int k = 0; k < dataCollection.getSeriesCount(); k++)
			funciones.add(((FuncionKey) dataCollection.getSeriesKey(k)).getFuncion());

		if (funciones.isEmpty())
			JOptionPane.showMessageDialog(ventana, "No hay funciones añadidas", "Error Crear CSV",
					JOptionPane.ERROR_MESSAGE);
		else {
			JPanel pnOptions = new JPanel();
			BoxLayout lyOptions = new BoxLayout(pnOptions, BoxLayout.Y_AXIS);
			pnOptions.setLayout(lyOptions);
			JPanel pnTitle = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
			pnTitle.add(new JLabel("Elija las funciones que quiere exportar: "));
			JButton btnSelectAll = new JButton();
			pnTitle.add(btnSelectAll);
			pnOptions.add(pnTitle);

			JPanel pnFunciones = new JPanel(new GridLayout(10, (int) Math.ceil(funciones.size() / 10)));
			CheckBoxFuncion input;
			for (Funcion func : funciones) {
				input = new CheckBoxFuncion(func);
				pnFunciones.add(input);
			}
			pnOptions.add(pnFunciones);

			btnSelectAll.addActionListener(new ActionListener() {
				private boolean seleccionarTodos = DEFAULT_CHECKBOX_SELECTED;

				@Override
				public void actionPerformed(ActionEvent e) {
					for (Component comp : pnFunciones.getComponents())
						if (comp instanceof JCheckBox)
							((JCheckBox) comp).setSelected(seleccionarTodos);

					seleccionarTodos = !seleccionarTodos;
					if (seleccionarTodos)
						btnSelectAll.setText("Seleccionar Todos");
					else
						btnSelectAll.setText("Deseleccionar Todos");
				}
			});
			btnSelectAll.doClick(); // primera pulsación pone el boton en su valor por defecto

			int retOption = JOptionPane.showConfirmDialog(ventana, pnOptions, "Exportar Funciones",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (retOption == JOptionPane.OK_OPTION) {
				CheckBoxFuncion cbFuncion;
				for (Component comp : pnFunciones.getComponents()) {
					if (comp instanceof CheckBoxFuncion) {
						cbFuncion = (CheckBoxFuncion) comp;
						if (!cbFuncion.isSelected())
							funciones.remove(cbFuncion.getFuncion());
					}
				}

				if (!funciones.isEmpty()) {
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (fileChooser.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION) {
						try {
							if (!IOGrafica.doSaveAsCSV(funciones, fileChooser.getSelectedFile()))
								JOptionPane.showMessageDialog(ventana, "Algunos ficheros CSV no se pudieron crear",
										"Warning Crear CSV", JOptionPane.WARNING_MESSAGE);
						} catch (IOException ioE) {
							String mensaje = ioE.getMessage();
							if (mensaje == null || mensaje.isBlank())
								mensaje = "No se completo con éxito la creación de los archivos CSV";
							JOptionPane.showMessageDialog(ventana, mensaje, "Error Crear CSV",
									JOptionPane.ERROR_MESSAGE);
//							ioE.printStackTrace();
						}
					}
				} else
					JOptionPane.showMessageDialog(ventana, "No hay funciones añadidas", "Error Crear CSV",
							JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class CheckBoxFuncion extends JCheckBox {
		private Funcion funcion;

		private CheckBoxFuncion(Funcion funcion) {
			super(funcion.getExpresion());
			this.funcion = funcion;
		}

		private Funcion getFuncion() {
			return funcion;
		}
	}
}
