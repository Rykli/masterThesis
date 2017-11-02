package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.data.Answer;
import org.jetbrains.annotations.NotNull;

import java.util.stream.DoubleStream;

/**
 * Contains general purpose mathematical functions.
 */
public class MathUtils {

	/**
	 * Alters the source array.
	 * @return The array that was passed with sum of 1.
	 */
	public static double[] normalize(@NotNull double[] vector) {
		double sum = DoubleStream.of(vector).sum();
		for(int i = 0; i < vector.length; i++) {
			vector[i] = vector[i]/sum;
		}
		return vector;
	}

	//TODO: move bayes math no days near the bayes class.

	public static double bhattacharyyaDistance(double[] firstDistr, double[] secondDistr) {
		// The distance is D(p, q) = -ln(sum of all possible diseases i of (root of p(i)*q(i)))
		double sum = 0;
		for(int i = 0; i < firstDistr.length; i++) {
			sum += Math.sqrt(firstDistr[i] * secondDistr[i]);
		}
		return - Math.log(sum);
	}

	public static double entropy(@NotNull double[] distribution) {
		return - DoubleStream.of(distribution)
				.filter(value -> value != 0)//remove all 0 values, they don't work with log and are defined as 0
				.map(pr -> pr * Math.log(pr)).sum();
	}
}
