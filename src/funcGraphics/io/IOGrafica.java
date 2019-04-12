package funcGraphics.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.opencsv.CSVWriter;

import funcGraphics.dominio.Funcion;
import funcGraphics.negocio.Grafica;

public class IOGrafica {
	private static String NOMBRE_CARPETA = "FuncGraphicsCSV";
	private static String NOMBRE_ARCH_CSV = "Funcion%d.csv";
	
	/**
	 * Genera archivos CSV con los datos de las funciones del set
	 * @param funciones Set que contiene las funciones con los cuales se desea crear archivos CSV
	 * @param destino File donde se va a crear la carpeta que contendrá los CSV creados
	 * @throws IOException
	 */
	public static boolean doSaveAsCSV(Set<Funcion> funciones, File destino) throws IOException {
		File carpeta = new File(destino, NOMBRE_CARPETA);
		boolean ret = true;
		if(!carpeta.mkdir())	// mkdir devuelve false si la carpeta ya existía
			throw new IOException(carpeta.toString() + " ya existe");
		else {
			int k = 1;
			for(Funcion func:funciones) {
				try {
					doSaveAsCSV(func, new File(carpeta, String.format(NOMBRE_ARCH_CSV, k)));
				} catch(IOException e) {
					ret = false;
				}
				k++;
			}
		}
		return ret;
	}
	
	public static void doSaveAsCSV(Funcion funcion, File destino) throws IOException {
		// csv
		try(CSVWriter writer = new CSVWriter(new FileWriter(destino))) {
			writer.writeNext(new String[] {"Funcion:",funcion.getExpresion()},true);
			writer.writeNext(new String[] {"x","y"}, true);
			
			double[][] data = funcion.getData().toArray();	// array de 2 filas (x,y) y n columnas (n pares de puntos)
			String[] dataWrite = new String[2];
			for(int k=0; k<data[0].length; k++) {
				dataWrite[0] = Double.toString(data[0][k]);
				dataWrite[1] = Double.toString(data[1][k]);
				writer.writeNext(dataWrite,false);
			}
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static String getAyudaFromTxt(InputStream in) throws IOException {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String strTemp;
		while((strTemp = bfr.readLine())!=null) {
			str.append(strTemp);
			str.append("\n");
		}
		return str.toString();
	}
	
	public static void saveGrafica(Grafica grafica, File destino) throws IOException {
		System.out.println(destino.getPath());
		
		try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(destino))) {
			GraficaSerializable graficaOut = new GraficaSerializable(grafica);
			out.writeObject(graficaOut);
		} catch(IOException e) {
			throw e;
		}
	}

	public static Grafica openGrafica(File origen) throws IOException, ClassNotFoundException {
		Object graficaSerializable;
		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(origen))) {
			graficaSerializable = in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw e;
		}
		if(graficaSerializable instanceof GraficaSerializable)
			return ((GraficaSerializable) graficaSerializable).getGrafica();
		else
			throw new IOException("Error: valor leído no es correcto");
	}	
}
