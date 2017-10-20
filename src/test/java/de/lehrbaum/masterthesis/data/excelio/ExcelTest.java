package de.lehrbaum.masterthesis.data.excelio;

import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ExcelTest {

	@Test
	public void basicExcelTest() throws ExcelLoadException {
		ExcelReader reader = new ExcelReader();
		reader.test();
	}

	@Test
	public void testAmountFactors() throws ExcelLoadException {
		ExcelReader reader = new ExcelReader();
		Map<String, double[]> map = null;
		map = reader.getFactors();
		assertEquals("The amount of factors read was wrong", 8, map.size());
	}

	@Test
	public void testAPriori() throws ExcelLoadException {
		ExcelReader reader = new ExcelReader();
		double[][] result = null;
		result = reader.getAPrioriBasedOnAge();
		assertEquals("The amount of age steps read was wrong", 5, result.length);
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j< result[i].length; j++) {
				assertTrue("Value at " + i + " " + j + " is not valid.", Double.isFinite(result[i][j]));
			}
		}
	}
}
