package de.lehrbaum.masterthesis;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

public class TestMath {
	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
	}

	@Test
	public void testEntropy() {
		double[] lowEntrDistr = new double[20];
		Arrays.fill(lowEntrDistr, 0.01 / 19);
		lowEntrDistr[0] = 0.99;
		double lowEntr = MathUtils.entropy(lowEntrDistr);

		double[] highEntrDist = new double[20];
		Arrays.fill(highEntrDist, 1d / 20);
		double highEntr = MathUtils.entropy(highEntrDist);

		Assert.assertTrue("This is not how I understand entropy " + lowEntr + " " + highEntr,
				lowEntr < highEntr);
	}
}
