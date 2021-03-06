package funcGraphics.ui;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;

import funcGraphics.negocio.Grafica;

class JTextFieldLimites extends JFormattedTextField {
	private static final int MAX_FRACTION_DIGITS = 4;
	private JVentanaGraficar ventana;

	JTextFieldLimites(JVentanaGraficar ventana, double value, int columnas, boolean lLimit) {
		super();
		this.ventana = ventana;
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

		// actualiza la gr�fica cuando hay un cambio en el valor de los intervalos
		this.addPropertyChangeListener("value", event -> {
			if(((Number)event.getNewValue()).doubleValue()!=((Number)event.getOldValue()).doubleValue()) {
				Grafica grafica = ventana.getGrafica();
				if (lLimit) { // si es el l�mite izquierdo
					grafica.setLeftLimit(getDoubleValue());
					// actualizar el otro l�mite si este ha cambiado
					ventana.getRightTextField().setValue(grafica.getRightLimit());
				} else { // si es el l�mite derecho
					grafica.setRightLimit(getDoubleValue());
					// actualizar el otro l�mite si este ha cambiado
					ventana.getLeftTextField().setValue(grafica.getLeftLimit());
				}
				ventana.getGraficaPanel().notifyDataChange();
				ventana.setGuardado(false);
			}
		});
	}
	
	@Override
	public void commitEdit() throws ParseException {
		ventana.getGraficaPanel().restoreAutoBounds();
		super.commitEdit();
	}

	public double getDoubleValue() {
		return ((Number) this.getValue()).doubleValue();
	}
	
	public void setValue(double value) {
		this.setValue(Double.valueOf(value));
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
