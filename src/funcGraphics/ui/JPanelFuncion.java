package funcGraphics.ui;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import funcGraphics.dominio.Funcion;
import funcGraphics.negocio.Grafica;

class JPanelFuncion extends JPanel { //implements KeyListener {
//	private static final Color COLOR_BTN_DESACTIVADO = new Color(255,124,124);
	private static final String MSG_BTN_OCULTAR = "Ocultar";
	private static final String MSG_BTN_MOSTRAR = "Mostrar";
	private static final String MSG_CALCULANDO = "Calculando...";
	private static final String MSG_FUNCION_ERROR = "Funcion no valida";
	
	private static final ImageIcon IMAGEN_OK = new ImageIcon(JPanelFuncion.class.getResource("/ok.png"));
	private static final ImageIcon IMAGEN_ERROR = new ImageIcon(JPanelFuncion.class.getResource("/error.png"));
	private static String VARIABLE = "x";
	private static boolean DEFAULT_MOSTRADO = true;	// por defecto, se muestra la funci�n al crearse
	
	private JLabel imagen;
	private JTextField txtFunc;
//	private JTextField txtVariable;
	private JButton btnActivar;
	private JButton btnEliminar;
	private JVentanaGraficar ventana;
	private Funcion funcion;
	
	/**
	 * Construye un nuevo panel con las entradas para las funciones
	 * @param ventana JVentanaGraficar que lo contiene
	 */
	public JPanelFuncion(JVentanaGraficar ventana) {
		super(new FlowLayout());
//		this.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				txtFunc.requestFocus();
//			}
//		});
		
		this.ventana = ventana;
		funcion = new Funcion(ventana.getGrafica(), VARIABLE, VARIABLE, DEFAULT_MOSTRADO);
		try {
			funcion.updateData();
		} catch (ScriptException e) {
			e.printStackTrace();	// no deber�a suceder
		}
		ventana.getGrafica().add(funcion);
		configInputs();
		mostrarFuncion(true);
	}
	
	public JPanelFuncion(JVentanaGraficar ventana, Funcion funcion) {
		super(new FlowLayout());
//		this.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				txtFunc.requestFocus();
//			}
//		});
		
		this.ventana = ventana;
		this.funcion = funcion;
		ventana.getGrafica().add(this.funcion);	// en el caso de ya estar a�adida, no hace nada
		configInputs();
		txtFunc.setText(funcion.getExpresion());
		try {
			this.funcion.updateData();
			updateFuncionInfo(null);
		} catch (ScriptException e) {
			e.printStackTrace();
			updateFuncionInfo(e);
		}
		mostrarFuncion(this.funcion.isVisible());
	}
	
	private void configInputs() {
		//a�adimos los componentes
		imagen = new JLabel(IMAGEN_OK);	// al principio funcion estar� definida a 0
		add(imagen);
		add(new JLabel("Funci�n: "));
		txtFunc = new JTextField(VARIABLE, 10);
		add(txtFunc);
//		add(new JLabel("Variable: "));
//		txtVariable = new JTextField(3);
//		add(txtVariable);
		btnActivar = new JButton();
		add(btnActivar);
		btnEliminar = new JButton("Eliminar");
		add(btnEliminar);
		
		// funcionalidad de los JTextField y JButton
		txtFunc.addKeyListener(new KeyAdapter() {
			boolean update = false;
			
			@Override
			public void keyTyped(KeyEvent e) {
				update = true;
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(update) {
					update = false;
					updateFuncion();
				}
			}
		});
		btnActivar.addActionListener(event -> mostrarFuncion(!funcion.isVisible()));
		btnEliminar.addActionListener(event -> {
			ventana.getGrafica().remove(funcion);
//			ventana.getGraficaPanel().repaint();
			ventana.getGraficaPanel().notifyDataChange();
			ventana.eliminarInput(this);
		});
		
		// focus se mueve al siguiente input
//		txtFunc.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				ventana.pnInputs.requestFocus();
//				ventana.pnInputs.transferFocus();
//			};
//		});
	}

	void mostrarFuncion(boolean mostrar) {
		if(mostrar) {
			btnActivar.setBackground(null);
			btnActivar.setText(MSG_BTN_OCULTAR);
		} else {
			btnActivar.setBackground(JVentanaGraficar.COLOR_BTN_DESACTIVADO);
			btnActivar.setText(MSG_BTN_MOSTRAR);
		}
		funcion.setVisible(mostrar);
//		ventana.getGraficaPanel().repaint();
		ventana.getGraficaPanel().notifyDataChange();
	}
	
	/**
	 * Actualiza la funci�n con los valores actuales de los JTextField y muestra los mensajes correspondientes
	 */
	private void updateFuncion() {
//		JLabel lbFeedback = ventana.getLbFeedback();
//		lbFeedback.setForeground(JVentanaGraficar.COLOR_FB_DEFAULT);
//		lbFeedback.setText(MSG_CALCULANDO);
		
		try {
			funcion.setFuncion(txtFunc.getText(), VARIABLE);
//			imagen.setIcon(IMAGEN_OK);
//			lbFeedback.setText("");
//			imagen.setToolTipText(null);
			updateFuncionInfo(null);
		} catch(ScriptException e) {
//			imagen.setIcon(IMAGEN_ERROR);
//			lbFeedback.setForeground(JVentanaGraficar.COLOR_FB_ERROR);
//			lbFeedback.setText(MSG_FUNCION_ERROR);
//			imagen.setToolTipText(e.getMessage());
//			e.printStackTrace();
			updateFuncionInfo(e);
		}
		
//		imagen.repaint();
//		ventana.getGrafica().repaint();
//		ventana.getGraficaPanel().repaint();
		ventana.getGraficaPanel().notifyDataChange();
//		lbFeedback.setText("");
	}
	
	private void updateFuncionInfo(Exception e) {
		if(e==null) {
			imagen.setIcon(IMAGEN_OK);
			imagen.setToolTipText(null);
		} else {
			imagen.setIcon(IMAGEN_ERROR);
			imagen.setToolTipText(e.getMessage());
		}
		imagen.repaint();
	}
}