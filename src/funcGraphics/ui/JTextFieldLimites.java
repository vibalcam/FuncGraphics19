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

	JTextFieldLimites(JVentanaGraficar ventana, double value, int columnas, boolean lLimit) {
		super();
		this.setFormatterFactory(new AbstractFormatterFactory() {
			private LimitesFormatter formatter = new LimitesFormatter();

			@Override
			public AbstractFormatter getFormatter(JFormattedTextField tf) {
				return formatter;
			}
		});
		this.setValue(Double.valueOf(value));
		this.setColumns(columnas);
		this.setFocusLostBehavior(COMMIT_OR_REVERT);

		// actualiza la gráfica cuando hay un cambio en el valor de los intervalos
		this.addPropertyChangeListener("value", event -> {
//			System.out.println(event.getOldValue() + "->" + event.getNewValue());
//			System.out.println(((Number)event.getNewValue()).doubleValue()==((Number)event.getOldValue()).doubleValue());
			if(((Number)event.getNewValue()).doubleValue()!=((Number)event.getOldValue()).doubleValue()) {
				Grafica grafica = ventana.getGrafica();
				if (lLimit) { // si es el límite izquierdo
//					System.out.println("pruebaL");
					grafica.setLeftLimit(getDoubleValue());
					// actualizar el otro límite si este ha cambiado
					ventana.getRightTextField().setValue(Double.valueOf(grafica.getRightLimit()));
				} else { // si es el límite derecho
//					System.out.println("pruebaR");
					grafica.setRightLimit(getDoubleValue());
					// actualizar el otro límite si este ha cambiado
					ventana.getLeftTextField().setValue(Double.valueOf(grafica.getLeftLimit()));
				}
				ventana.getGraficaPanel().notifyDataChange();
				ventana.setGuardado(false);
			}
		});
		
//		this.addPropertyChangeListener("value", event -> {
//			Grafica grafica = ventana.getGrafica();
//			double newValue;
//			JTextFieldLimites otroLimite;
//			if (lLimit) { // si es el límite izquierdo
//				System.out.println("pruebaL");
//				grafica.setLeftLimit(getDoubleValue());
//				// actualizar el otro límite si este ha cambiado
//				if((otroLimite = ventana.getRightTextField()).getDoubleValue()!=(newValue = grafica.getRightLimit()))
//					otroLimite.setValue(Double.valueOf(newValue));
//			} else { // si es el límite derecho
//				System.out.println("pruebaR");
//				grafica.setRightLimit(getDoubleValue());
//				// actualizar el otro límite si este ha cambiado
//				if((otroLimite = ventana.getLeftTextField()).getDoubleValue()!=(newValue = grafica.getLeftLimit()))
//					otroLimite.setValue(Double.valueOf(newValue));
//			}
//			ventana.getGraficaPanel().notifyDataChange();
//			ventana.setGuardado(false);
//		});
	}

	public double getDoubleValue() {
		return ((Number) this.getValue()).doubleValue();
	}

	class LimitesFormatter extends NumberFormatter {
		private DocumentFilter docFilter;

		LimitesFormatter() {
			super();
			configFormat();
			docFilter = new LimitesDocumentFilter();
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
				char separator = format instanceof DecimalFormat
						? ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator()
						: '.';
				this.pattern = Pattern.compile("-?\\d*\\" + Character.toString(separator) + "?\\d*");
			}

			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (pattern.matcher(string).matches())
					super.insertString(fb, offset, string, attr);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				if (pattern.matcher(text).matches())
					super.replace(fb, offset, length, text, attrs);
			}
		}
	}
}
