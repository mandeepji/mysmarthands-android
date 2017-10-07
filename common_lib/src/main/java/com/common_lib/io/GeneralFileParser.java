package com.common_lib.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GeneralFileParser {

	public static final String NEWLINE_REGEX = "\\r?\\n";

	public static BufferedReader openFileReader(String path) {

		BufferedReader br = null;
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(path);
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return br;
	}

	public static BufferedWriter openFileWriter(String path) {

		BufferedWriter bw = null;
		try {
			FileOutputStream fstream = new FileOutputStream(path);
			DataOutputStream in = new DataOutputStream(fstream);
			bw = new BufferedWriter(new OutputStreamWriter(in));
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bw;
	}

	public static String fileToString(String file) {

		String result = null;
		DataInputStream in = null;

		try {
			File f = new File(file);
			byte[] buffer = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			in.readFully(buffer);
			result = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	public static void stringToFile(String path, String string) {

		// System.out.println(string);
		try {
			createPath(path);
			File newTextFile = new File(path);
			FileWriter fw = new FileWriter(newTextFile);
			fw.write(string);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stringToFile(String path, String string,
			boolean overwrite) {

		// System.out.println(path);
		File potential = new File(path);
		if (!overwrite) {
			String directory = fileDirectory(path);
			String fileName = fileNameWithoutExtensionFromPath(path);
			String extension = fileExtensionFromPath(path);
			int appendage = 1;
			while (potential.exists()) {
				potential = new File(directory + fileName + (++appendage) + "."
						+ extension);
			}
		}
		// System.out.println(potential.getPath());
		stringToFile(potential.getPath(), string);
	}

	public static String[] readLine(BufferedReader br, String seperator) {

		String[] ret = null;

		try {
			String line = br.readLine();
			ret = line.split(seperator);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static int[] convertLine_int(String[] line) {

		int[] vals = new int[line.length];
		int i = 0;
		for (String s : line)
			vals[i++] = Integer.valueOf(s);

		return vals;
	}

	public static double[] convertLine_double(String[] line) {

		double[] vals = new double[line.length];
		int i = 0;
		for (String s : line)
			vals[i++] = Double.valueOf(s);

		return vals;
	}

	public static void shuffle(List<?> list) {

		Collections.shuffle(list);
	}

	// ------------------------------------------------------------------------+
	// Directory, File names, and Extension Extraction
	public static String fileNameFromPath(String path) {

		String[] foldersAndFile = path.split("/");
		return foldersAndFile[foldersAndFile.length - 1];
	}

	public static String fileNameWithoutExtensionFromPath(String path) {

		String[] foldersAndFile = path.split("/");
		String[] nameAndExtension = foldersAndFile[foldersAndFile.length - 1]
				.split("\\.");

		return nameAndExtension[0];
	}

	public static String fileExtensionFromPath(String path) {

		String[] foldersAndFile = path.split("/");
		String[] nameAndExtension = foldersAndFile[foldersAndFile.length - 1]
				.split("\\.");
		return nameAndExtension[nameAndExtension.length - 1];
	}

	public static String fileDirectory(String path) {

		String[] foldersAndFile = path.split("/");
		String ret = path;
		return ret.replace(foldersAndFile[foldersAndFile.length - 1], "");
	}

	public static String[] fileDirectories(String path) {

		return path.split("/");
	}

	public static String tabString(int tabs) {

		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < tabs; ++i) {
			ret.append("  ");
		}

		return ret.toString();
	}

	public static String replaceExtension(String path, String newExt) {

		return path.replace(fileExtensionFromPath(path), newExt);
	}

	public static File[] folderList(String folderPath){
		
		File folder = new File(folderPath);
		return folder.listFiles();
	}
	
	public static boolean createPath(String path){
		
		File newTextFile = new File(path);
		if (newTextFile.getParentFile() != null) {
			return newTextFile.getParentFile().mkdirs();
		}
		return false;
	}
	
	public static boolean deletePath(String path){
		
		return false;
	}
	
	public static void deleteDir(File path) {
        
		if (path.isDirectory()){
            for (File child : path.listFiles()){
            	deleteDir(child);
            }
		}
		path.delete();
    }
	
	// ------------------------------------------------------------------------+
	// Strings
	public static String removeWhiteSpace(String str) {

		return str.replaceAll("\\s", "");
	}

	public static String strip(String str) {

		return str.substring(1, str.length() - 1);
	}

	public static URL stringToURL(String urlStr){
		
		URL url = null;
		try {
		  url = new URL(urlStr);
		} catch (MalformedURLException e) {
		  
		}
		
		return url;
	}
	
	// ------------------------------------------------------------------------+
	// Maps
	public static Map<String, String> stringToMap(String mapStr) {

		String[] lines = mapStr.split(NEWLINE_REGEX);
		Map<String, String> map = new HashMap<String, String>(lines.length);

		String[] entry;
		for (String line : lines) {
			if (line.length() < 2 || line.startsWith("//")) {
				continue;
			}
			entry = line.split(":");
			map.put(entry[0], entry[1]);
		}
		return map;
	}

	public static String mapToString(Map<String, String> map) {

		StringBuilder str = new StringBuilder();

		for (Entry<String, String> entry : map.entrySet()) {
			str.append(entry.getKey()).append(":").append(entry.getValue())
					.append("\n");
		}

		return str.toString();
	}

	public static Map<String, String> fileToMap(String path) {

		String fileContent = fileToString(path);
		// System.out.println(fileContent);
		return stringToMap(fileContent);
	}

	public static void mapToFile(String path, Map<String, String> map) {

		stringToFile(path, mapToString(map));
	}

	public static List<Map<String, String>> fileToMapList(String path,
			String seperator) {

		String[] mapStrings = fileToString(path).split(seperator);

		List<Map<String, String>> ret = new ArrayList<Map<String, String>>(
				mapStrings.length);

		for (String mapStr : mapStrings) {
			// System.out.println(mapStr);
			ret.add(stringToMap(mapStr));
		}

		return ret;
	}

	// ------------------------------------------------------------------------+
	// Lists
	public static String primitiveListToString(List<? extends Object> list) {

		StringBuilder str = new StringBuilder();

		str.append("{");

		for (Object p : list) {
			str.append(p.toString()).append(",");
		}
		if (!list.isEmpty()) {
			// replace
			str.deleteCharAt(str.length() - 1);
			str.append("}");
		}

		return str.toString();
	}

	public static List<Integer> stringToList_Integer(String strList) {

		String[] items = getListStringArray(strList);

		List<Integer> list = new ArrayList<Integer>();
		for (String i : items) {
			list.add(Integer.valueOf(i));
		}

		return list;
	}

	public static List<Double> stringToList_Double(String strList) {

		String[] items = getListStringArray(strList);

		List<Double> list = new ArrayList<Double>();
		for (String i : items) {
			list.add(Double.valueOf(i));
		}

		return list;
	}

	public static List<Boolean> stringToList_Boolean(String strList) {

		String[] items = getListStringArray(strList);

		List<Boolean> list = new ArrayList<Boolean>();
		for (String i : items) {
			list.add(Boolean.valueOf(i));
		}

		return list;
	}

	public static List<String> stringToList_String(String strList) {

		String[] items = getListStringArray(strList);

		List<String> list = new ArrayList<String>();
		for (String i : items) {
			list.add(i);
		}

		return list;
	}

	public static String[] getListStringArray(String strList) {

		if (strList.charAt(0) == '{')
			strList = strList.substring(1);
		if (strList.charAt(strList.length() - 1) == '}')
			strList = strList.substring(0, strList.length() - 1);

		if (strList.contains("{")) {
			return strList.split(",\\{");
		} else {
			return strList.split(",");
		}
	}

	public static List<double[]> fileToList_double(String path) {

		String fileStr = fileToString(path);
		String[] lines = fileStr.split(NEWLINE_REGEX);
		ArrayList<double[]> ret = new ArrayList<double[]>(lines.length);

		for (String ln : lines)
			ret.add(convertLine_double(ln.split(",")));

		return ret;
	}

	public static List<double[]> stringToList_doubleArray(String str) {

		String fileStr = str;
		String[] lines = fileStr.split(NEWLINE_REGEX);
		ArrayList<double[]> ret = new ArrayList<double[]>(lines.length);

		for (String ln : lines)
			ret.add(convertLine_double(ln.split(",")));

		return ret;
	}

	public static String list_doubleToString(List<double[]> list) {

		StringBuilder sb = new StringBuilder();
		for (double[] da : list) {
			for (double val : da) {
				sb.append(val + ",");
			}
			sb.deleteCharAt(sb.length() - 1).append("\n");
		}

		return sb.toString();
	}

	public static String listToString(List<? extends Object> list) {

		StringBuilder sb = new StringBuilder("{");
		for (Object object : list) {
			sb.append(object.toString() + ",");
		}
		sb.deleteCharAt(sb.length() - 1).append("}");

		return sb.toString();
	}

	public static String listToCSVFileString(List<? extends CSVFormatable> list) {

		StringBuilder sb = new StringBuilder();
		for (CSVFormatable object : list) {
			sb.append(object.toCSVString() + "\n");
			// System.out.println(object.toString());
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	public static <E> List<List<E>> transpose(List<List<E>> data) {

		List<List<E>> ret = new ArrayList<List<E>>();
		int rowIndex;
		for (List<E> row : data) {
			rowIndex = 0;
			for (E e : row) {
				if (ret.size() <= rowIndex) {
					ret.add(new ArrayList<E>());
				}
				ret.get(rowIndex).add(e);
				++rowIndex;
			}
		}
		return ret;
	}

	public static <E> List<E> combine(List<List<E>> data) {

		List<E> ret = new ArrayList<E>();
		for (List<E> l : data) {
			ret.addAll(l);
		}
		return ret;
	}

	// ------------------------------------------------------------------------+
	// Arrays
	public static double[][] StringTo2DArray_Double(String str) {

		String[] rows = str.split(NEWLINE_REGEX);
		int rowCount = rows.length;
		int colCount = rows[0].split(",").length;
		double[][] ret = new double[rowCount][colCount];

		String[] rowVals;
		for (int x = 0; x < rowCount; ++x) {
			rowVals = rows[x].split(",");
			for (int y = 0; y < rowVals.length; ++y) {
				ret[x][y] = Double.valueOf(rowVals[y]);
			}
		}

		return ret;
	}

	public static List<double[]> transpose_ListOfArrays(List<double[]> data) {

		List<double[]> ret = new ArrayList<double[]>();
		// assumes non-ragged array
		int rowCount = data.size();
		int rowIndex, colIndex = 0;
		for (double[] row : data) {
			rowIndex = 0;
			for (double e : row) {
				if (ret.size() <= rowIndex) {
					ret.add(new double[rowCount]);
				}
				ret.get(rowIndex)[colIndex] = e;
				++rowIndex;
			}
			++colIndex;
		}
		return ret;
	}

	public static double[] combine_ListOfArrays(List<double[]> data) {

		int size = 0;
		for (double[] ds : data) {
			size += ds.length;
		}
		double[] ret = new double[size];
		int x = 0, y = 0;
		for (double[] ds : data) {
			for (double d : ds) {
				ret[x + (y++)] = d;
			}
			++x;
		}
		return ret;
	}

	// ------------------------------------------------------------------------+
	// Object Serialization
	public static void serializableToFile(String path, Serializable obj) {

		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object fileToSerializable(String path){

		Object ret = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			ret = in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// ------------------------------------------------------------------------+
	// Expressions
	public static List<Double> parseRange(String rangeExpression) {

		String[] rangeAndStep = rangeExpression.split(",");
		String[] startAndEnd = rangeAndStep[0].split("-");

		List<Double> ret = new ArrayList<Double>();
		double step = Double.valueOf(rangeAndStep[1]);
		double end = Double.valueOf(startAndEnd[1]);
		double current = Double.valueOf(startAndEnd[0]);
		ret.add(current);

		while (true) {
			current = current + step;
			// System.out.println(current);
			// System.out.println(step);
			if (current > end) {
				break;
			}
			ret.add((double) Math.round(current * 100000) / 100000); // 5
																		// decimal
																		// prescision
		}

		return ret;
	}

	public static List<String> parseRange_String(String rangeExpression) {

		// System.out.println("in: "+rangeExpression);

		List<Double> dValues = parseRange(rangeExpression);
		// System.out.println("doubles: "+dValues);
		List<String> sValues = new ArrayList<String>(dValues.size());

		for (double val : dValues) {
			sValues.add(String.valueOf(val));
		}
		// System.out.println("out: "+sValues);
		// System.out.println();

		return sValues;
	}

	// ------------------------------------------------------------------------+
	// conversions
	public static void convertToCSV(String path, String seperator) {

		String contents = fileToString(path);
		String csvStr = contents.replaceAll(seperator, ",");
		stringToFile(replaceExtension(path, "csv"), csvStr);
	}

	// ------------------------------------------------------------------------+

	// public static void main(String[] args) {
	//
	// GeneralFileParser.convertToCSV("test data/clustering/sSet/s4.txt","    ");
	//
	// }
}
