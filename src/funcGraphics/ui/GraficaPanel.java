package funcGraphics.ui;

import java.awt.Graphics;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;

import funcGraphics.dominio.Funcion;
import funcGraphics.dominio.Funcion.FuncionKey;
import funcGraphics.negocio.Grafica;

class GraficaPanel extends ChartPanel {
	private Grafica grafica;

	GraficaPanel(Grafica grafica) {
		super(null);
		this.grafica = grafica;
		setChart(ChartFactory.createXYLineChart("","x","f(x)",grafica.getDataCollection()));
		
		getChart().getXYPlot().setNoDataMessage("NO HAY FUNCIONES AÑADIDAS");
	}
	
	void setGrafica(Grafica grafica) {
		this.grafica = grafica;
		setChart(ChartFactory.createXYLineChart("","x","f(x)",this.grafica.getDataCollection()));
		notifyDataChange();
	}
	
	void notifyDataChange() {
		XYItemRenderer renderer = getChart().getXYPlot().getRenderer();
		List series = grafica.getDataCollection().getSeries();
		XYSeries serie;
		for(int k=0; k<series.size(); k++) {
			serie = (XYSeries) series.get(k);
			if(serie.isEmpty())
				renderer.setSeriesVisible(k,false);
			else
				renderer.setSeriesVisible(k,((FuncionKey) serie.getKey()).getFuncion().isVisible());
		}
		this.repaint();
	}
	
//	@Override
//	public void paint(Graphics g) {
////		XYPlot plot = getChart().getXYPlot();
////		plot.setNoDataMessage("NO HAY FUNCIONES AÑADIDAS");
//		
////		XYItemRenderer renderer = plot.getRenderer();
////		List series = grafica.getDataCollection().getSeries();
////		XYSeries serie;
////		for(int k=0; k<series.size(); k++) {
////			serie = (XYSeries) series.get(k);
////			if(serie.isEmpty())
////				renderer.setSeriesVisible(k,false);
////			else
////				renderer.setSeriesVisible(k,((FuncionKey) serie.getKey()).getFuncion().isVisible());
////		}
//		
//		super.paint(g);
//	}
}
