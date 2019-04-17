package funcGraphics.ui;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;

import funcGraphics.negocio.Grafica;

class JTextFieldLimites extends JFormattedTextField {
	private static final int MAX_FRACTION_DIGITS = 4;
//	private JVentanaGraficar ventana;
	
	JTextFieldLimites(JVentanaGraficar ventana, double value, int columnas, boolean lLimit) {
//		super(Double.valueOf(value));
		super();
		this.setFormatterFactory(new AbstractFormatterFactory() {
			private LimitesFormatter formatter = new LimitesFormatter();
			
			@Override
			public AbstractFormatter getFormatter(JFormattedTextField tf) {
				return formatter;
			}
		});
//		this.setFormatter(new LimitesFormatter());
		this.setValue(Double.valueOf(value));
//		this.ventana = ventana;
		this.setColumns(columnas);
		this.setFocusLostBehavior(COMMIT_OR_REVERT);
		
		// actualiza la gráfica cuando hay un cambio en el valor de los intervalos
		this.addPropertyChangeListener("value", event -> {
			Grafica grafica = ventana.getGrafica();
			if(lLimit) {
				grafica.setLeftLimit(getDoubleValue());
				ventana.getRightTextField().setValue(Double.valueOf(grafica.getRightLimit()));
			} else {
				grafica.setRightLimit(getDoubleValue());
				ventana.getLeftTextField().setValue(Double.valueOf(grafica.getLeftLimit()));
			}
			ventana.getGraficaPanel().repaint();
			System.out.println(getValue());
			System.out.println(this.getFormatter().getClass().getName());
			ventana.setGuardado(false);
		});
	}
	
	public double getDoubleValue() {
		return ((Number) super.getValue()).doubleValue();
	}
	
	class LimitesFormatter extends NumberFormatter {
		private DocumentFilter docFilter;
		
		LimitesFormatter() {
			super();
			configFormat();
			docFilter = new LimitesDocumentFilter();
			
			System.out.println("corre");
		}
		
		@Override
		protected DocumentFilter getDocumentFilter() {
			return docFilter;
		}
		
		private void configFormat() {
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
			this.setFormat(format);
		}
		
		private class LimitesDocumentFilter extends DocumentFilter {
			private Pattern pattern;
			
			private LimitesDocumentFilter() {
				super();
				Format format = LimitesFormatter.this.getFormat();
				char separator = format instanceof DecimalFormat ? ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator() : '.';
//				if(format instanceof DecimalFormat)
//					separator = ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator();
//				else
//					separator = '.';
				this.pattern = Pattern.compile("-?\\d*\\" + Character.toString(separator) + "?\\d*");
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if(pattern.matcher(string).matches())
					super.insertString(fb, offset, string, attr);
			}
			
			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
					throws BadLocationException {
				System.out.println(text);
				if(pattern.matcher(text).matches()) {
					super.replace(fb, offset, length, text, attrs);
					System.out.println("runs true");
				}
				else {
					System.out.println("runs false");
				}
			}
		}
	}
}
