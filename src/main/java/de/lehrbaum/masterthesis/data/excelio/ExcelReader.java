package de.lehrbaum.masterthesis.data.excelio;

import de.lehrbaum.masterthesis.Main;
import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;
import javafx.geometry.Point2D;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.exceptions.ExcelLoadException.Reason.*;

public class ExcelReader {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExcelReader.class.getCanonicalName());

	private static final String EXCEL_FILE_NAME = "Daten.xlsx";
	private static final String RESOURCE_EXCEL_FILE_NAME = EXCEL_FILE_NAME;

	private Workbook workbook;

	public ExcelReader() throws ExcelLoadException {
		File folder = Main.getDirectoryForFiles();
		File excelFile = new File(folder, EXCEL_FILE_NAME);
		if(!excelFile.exists()) {
			InputStream fileStream = ExcelReader.class.getResourceAsStream("/" + RESOURCE_EXCEL_FILE_NAME);
			try {
				FileUtils.copyInputStreamToFile(fileStream, excelFile);
				if(!excelFile.exists())
					throw new IOException("The file is not there even though it should be.");
			} catch(IOException e) {
				logger.log(Level.SEVERE, "Could not create the excel File.", e);
			}
		}
		try {
			workbook = WorkbookFactory.create(excelFile);
		} catch(InvalidFormatException | IOException e) {
			logger.log(Level.SEVERE, "Could not open Excel Workbook", e);
			throw new ExcelLoadException(FILE_INACCESSIBLE, null);
		}
	}

	private static final String APrioriSheet = "Prävalenzen";

	private static final NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

	public Map<String, double[]> getFactors() throws ExcelLoadException {
		Sheet sheet = workbook.getSheet(APrioriSheet);
		int amountFactors = sheet.getLastRowNum() - 5;
		Map<String, double[]> result = new HashMap<>();
		parseRows(sheet, result);
		logger.fine("Read " + amountFactors + " factors named " + Arrays.toString(result.keySet().toArray()));
		return result;
	}

	private void parseRows(Sheet sheet, Map<String, double[]> result) throws ExcelLoadException {
		for(int rowIndex = 6; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			final int amountDiseases = 9;
			double[] multipliers = new double[amountDiseases];
			for(int diseaseIndex = 0; diseaseIndex < amountDiseases; diseaseIndex++) {
				String multiplier = null;
				try {
					multiplier = row.getCell(diseaseIndex + 1).getStringCellValue();
					multiplier = multiplier.substring(1);//remove the * at the front
					multipliers[diseaseIndex] = format.parse(multiplier).doubleValue();
				} catch(ParseException | IndexOutOfBoundsException e) {
					Point2D position = new Point2D(diseaseIndex + 1, rowIndex);
					logger.log(Level.SEVERE, "Could not parse the multiplier at Position " + position, e);
					throw new ExcelLoadException(NUMBER_NOT_PARSABLE, position, multiplier);
				} catch(NullPointerException e) {
					Point2D position = new Point2D(diseaseIndex + 1, rowIndex);
					logger.log(Level.SEVERE, "Something is missing. Maybe a cell " + position, e);
					throw new ExcelLoadException(NOT_FOUND, position);
				}
			}
			String factorName = row.getCell(0).getStringCellValue();
			result.put(factorName, multipliers);
		}
	}

	/**
	 * @return [age][disease]
	 * @throws ExcelLoadException If a load error happens.
	 */
	public double[][] getAPrioriBasedOnAge() throws ExcelLoadException {
		Sheet sheet = workbook.getSheet(APrioriSheet);
		int amountAgeSteps = 5;
		final int amountDiseases = 9;
		double[][] result = new double[amountAgeSteps][amountDiseases];
		for(int ageStep = 0; ageStep < amountAgeSteps; ageStep++) {
			Row row = sheet.getRow(ageStep + 1);
			for(int diseaseIndex = 0; diseaseIndex < amountDiseases; diseaseIndex++) {
				try {
					result[ageStep][diseaseIndex] = row.getCell(diseaseIndex + 1).getNumericCellValue()/100;//percent
				} catch(IllegalStateException | NumberFormatException e) {
					Point2D position = new Point2D(diseaseIndex + 1, ageStep + 1);
					logger.log(Level.SEVERE, "Could not parse the multiplier at Position " + position, e);
					throw new ExcelLoadException(NUMBER_NOT_PARSABLE, position);
				}
			}
		}
		return result;
	}

	public void close() {
		try {
			workbook.close();
		} catch(IOException e) {
			logger.log(Level.WARNING, "Problem closing the workbook.", e);
		}
	}

	void test() {
		Sheet sheet = workbook.getSheet(APrioriSheet);
		Row r = sheet.getRow(0);
		System.out.println(r.getCell(0));
	}
}
