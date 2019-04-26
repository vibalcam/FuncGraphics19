package funcGraphics.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.data.xy.XYSeriesCollection;

import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.io.IOGrafica;
import funcGraphics.negocio.Grafica;

public class JVentanaGraficar extends JFrame {
	public static final String NOMBRE_APP = "FuncGraphics19";
	static final Color COLOR_BTN_DESACTIVADO = new Color(255, 124, 124);
	private static final int UNIT_INCREMENT_SCROLL_BAR = 10;

	private static final String MSG_GUARDADO = "Guardado";
	private static final String MSG_BTN_MOSTRAR = "Mostrar Todo";
	private static final String MSG_BTN_OCULTAR = "Ocultar Todo";
	public static final String MSG_AYUDA_ERROR = "No se pudo cargar la ayuda";
	private static final ImageIcon IMAGEN_AYUDA = new ImageIcon(JVentanaGraficar.class.getResource("/help.png"));

	private static String MSG_AYUDA;

	private boolean mostrarAllAct = true; // true al pulsar modo mostrar, false al pulsar modo ocultar
	private Grafica grafica;
	private GraficaPanel graficaPanel;
	private JTextFieldLimites txtLeftLimit;
	private JTextFieldLimites txtRightLimit;
	private JPanel pnInputs;
//	private JLabel lbFeedback;
	private JScrollPane scrInputs;
	private JButton btnAyuda;
	private ParallelGroup hGroup;
	private SequentialGroup vGroup;
	private JLabel lbGuardado;
	private JMenuBarGraficar mnbGraficar;

	public static void main(String[] args) {
		try {
			MSG_AYUDA = IOGrafica.getAyudaFromTxt(JVentanaGraficar.class.getResourceAsStream("/AyudaFunciones.txt"));
		} catch (IOException e) {
			MSG_AYUDA = MSG_AYUDA_ERROR;
			JOptionPane.showMessageDialog(null, "Error al cargar el fichero de ayuda", "Error Ayuda",
					JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
		}
		new JVentanaGraficar();
	}

	public JVentanaGraficar() {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
		this.setLayout(new BorderLayout());

		// grafica que contendrá las funciones
		grafica = new Grafica();

		// canvas donde estará la gráfica
		graficaPanel = new GraficaPanel(grafica);
		this.add(graficaPanel, BorderLayout.CENTER);
//		grafica.setPreferredSize(800,800);

		configSouth();
		configInputs();
		configNorth();
		configPropiedades();
		pnInputs.setMinimumSize(pnInputs.getPreferredSize());

		mnbGraficar = new JMenuBarGraficar(this);
		this.setJMenuBar(mnbGraficar);
		this.setGuardado(true); // no pregunta si deseas guardar si no has hecho ningún cambio
		this.setVisible(true);
	}

	Grafica getGrafica() {
		return grafica;
	}

	GraficaPanel getGraficaPanel() {
		return graficaPanel;
	}

//	JLabel getLbFeedback() {
//		return lbFeedback;
//	}

	JTextFieldLimites getRightTextField() {
		return txtRightLimit;
	}

	JTextFieldLimites getLeftTextField() {
		return txtLeftLimit;
	}

	void openGrafica(Grafica grafica) {
		this.grafica = grafica;
		graficaPanel.setGrafica(this.grafica);

		pnInputs.removeAll();
		XYSeriesCollection dataCollection = this.grafica.getDataCollection();
		Object temp;
		for (int k = 0; k < dataCollection.getSeriesCount(); k++) {
			temp = dataCollection.getSeriesKey(k);
			addInput(new JPanelFuncion(this, ((FuncionKey) temp).getFuncion()));
//			if(temp instanceof FuncionKey)
//				addInput(new JPanelFuncion(this,((FuncionKey) temp).getFuncion()));
		}
		txtLeftLimit.setValue(Double.valueOf(this.grafica.getLeftLimit()));
		txtRightLimit.setValue(Double.valueOf(this.grafica.getRightLimit()));
		this.setGuardado(true);
	}

	void setGuardado(boolean guardado) {
		grafica.setGuardado(guardado);
		if (guardado)
			lbGuardado.setText(MSG_GUARDADO);
		else
			lbGuardado.setText("");
	}

	private void configNorth() {
		JPanel pnNorth = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));

		// mensaje de error
//		lbFeedback = new JLabel();
//		lbFeedback.setFont(new Font(Font.SERIF,Font.BOLD,FONT_FEEDBACK));
//		pnNorth.add(lbFeedback);

		// input intervalo
		JPanel pnIntervalo = new JPanel(new FlowLayout());
		pnIntervalo.add(new JLabel("Intervalo: "));
		txtLeftLimit = new JTextFieldLimites(this, -1 * Grafica.DEFAULT_LIMIT, 5, true);
		txtRightLimit = new JTextFieldLimites(this, Grafica.DEFAULT_LIMIT, 5, false);

		pnIntervalo.add(txtLeftLimit);
		pnIntervalo.add(txtRightLimit);
		pnNorth.add(pnIntervalo);

		// botones de añadir,mostrar/ocultar todo y eliminar todo
		JPanel pnBotones = new JPanel(new FlowLayout());
		JButton btnAnadir = new JButton("Nueva Funcion");
		pnBotones.add(btnAnadir);
		JButton btnMostrarAll = new JButton(MSG_BTN_MOSTRAR);
		pnBotones.add(btnMostrarAll);
		JButton btnReset = new JButton("Reset");
		pnBotones.add(btnReset);
		pnNorth.add(pnBotones);

		btnAnadir.addActionListener(event -> {
			addInput(new JPanelFuncion(this));
			setGuardado(false);
		});
		btnMostrarAll.addActionListener(event -> {
			for (int k = 0; k < pnInputs.getComponentCount(); k++) {
				Component comp = pnInputs.getComponent(k);
				if (comp instanceof JPanelFuncion)
					((JPanelFuncion) comp).mostrarFuncion(mostrarAllAct);
			}
			if (mostrarAllAct) { // cambiar a modo ocultar
				btnMostrarAll.setBackground(COLOR_BTN_DESACTIVADO);
				btnMostrarAll.setText(MSG_BTN_OCULTAR);
			} else { // cambiar a modo mostrar
				btnMostrarAll.setBackground(null);
				btnMostrarAll.setText(MSG_BTN_MOSTRAR);
			}
			mostrarAllAct = !mostrarAllAct;
			setGuardado(false);
		});
		btnReset.addActionListener(event -> {
			grafica.clear();
			pnInputs.removeAll();
			addInput(new JPanelFuncion(this));
			setGuardado(false);
		});

		addInput(new JPanelFuncion(this)); // creamos un input inicial

		// boton para desplegar ayuda de funciones
		btnAyuda = new JButton();
		btnAyuda.setIcon(IMAGEN_AYUDA);
		btnAyuda.setPreferredSize(new Dimension(30, 30));
		pnNorth.add(btnAyuda);
		btnAyuda.addActionListener(event -> JOptionPane.showMessageDialog(this, MSG_AYUDA, "Funciones Disponibles",
				JOptionPane.INFORMATION_MESSAGE));

		this.add(pnNorth, BorderLayout.NORTH);
	}

	private void configInputs() {
		// layout de los inputs
		pnInputs = new JPanel();
		scrInputs = new JScrollPane(pnInputs);
		scrInputs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrInputs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrInputs.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT_SCROLL_BAR);
		GroupLayout layout = new GroupLayout(pnInputs);
		pnInputs.setLayout(layout);
		hGroup = layout.createParallelGroup();
		layout.setHorizontalGroup(hGroup);
		vGroup = layout.createSequentialGroup();
		layout.setVerticalGroup(vGroup);
		this.add(scrInputs, BorderLayout.WEST);
	}

	private void configSouth() {
		// info app
		FlowLayout flInfo = new FlowLayout();
		flInfo.setHgap(120);
		JPanel pnInfo = new JPanel(flInfo);
		this.add(pnInfo, BorderLayout.SOUTH);
		pnInfo.add(new JLabel(NOMBRE_APP));
		pnInfo.add(new JLabel("Programación Orientada a Objetos(ICAI)"));
		pnInfo.add(new JLabel("por Vicente Balmaseda"));

		// estado guardado
		lbGuardado = new JLabel();
		pnInfo.add(lbGuardado);
	}

	private void configPropiedades() {
		// propiedades de la ventana
		this.setTitle(NOMBRE_APP);
		this.pack();
		this.setResizable(true);
		Dimension tamaño = this.getSize();
		this.setMinimumSize(tamaño);
		this.setPreferredSize(tamaño);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				boolean salir = true;
				if (!grafica.isGuardado()) {
					int opSel = JOptionPane.showConfirmDialog(JVentanaGraficar.this,
							"Hay cambios sin guardar.\n¿Desea guardar antes de salir?", "Confirmar Exit " + NOMBRE_APP,
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (opSel == JOptionPane.YES_OPTION)
						salir = mnbGraficar.saveGrafica();
					else if (opSel == JOptionPane.CANCEL_OPTION || opSel== JOptionPane.CLOSED_OPTION)
						salir = false;
				}
				if (salir)
					JVentanaGraficar.this.exit();
			}
		});
	}

	void exit() {
		this.dispose();
		System.exit(0);
	}

	/**
	 * Añade una nueva función de entrada y revalidad el layout
	 * 
	 * @param pnFuncion JPanelFuncion del nuevo input
	 */
	private void addInput(JPanelFuncion pnFuncion) {
		hGroup.addComponent(pnFuncion);
		vGroup.addComponent(pnFuncion, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
				GroupLayout.PREFERRED_SIZE);

		if (pnInputs.getComponentCount() <= 1) {
			this.revalidate();
			this.repaint();
		} else {
			pnInputs.revalidate();
			pnInputs.repaint();
		}
	}

	void eliminarInput(JPanel input) {
		pnInputs.remove(input);
		if (pnInputs.getComponentCount() == 0) {
			this.revalidate();
			this.repaint();
		} else {
			pnInputs.revalidate();
			pnInputs.repaint();
		}
	}
}