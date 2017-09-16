package de.lehrbaum.masterthesis;

import org.jetbrains.annotations.NotNull;

import java.util.stream.DoubleStream;

import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.SYMPTOM_STATE;

/**
 * Contains general purpose mathematical functions.
 */
public class MathUtils {

	public static double[] normalize(double[] vector) {
		double sum = DoubleStream.of(vector).sum();
		return DoubleStream.of(vector).map(d -> d / sum).toArray();
	}

	public static double calculatePrOfSymptomsGivenHypothesis(
			double[][] probabilities, SYMPTOM_STATE[] symptomInformation, long hypothesis) {
		double result = 1;// the neutral element of multiplication is 1, so it should be the correct value.
		for(int symptom = 0; symptom < symptomInformation.length; symptom++) {
			if(symptomInformation[symptom] == null)
				continue;
			switch(symptomInformation[symptom]) {
				case ABSENT:
					result *= calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
					break;
				case PRESENT:
					result *= 1 - calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
					break;
				//ignore UNKOWN Symptoms
			}
		}
		return result;
	}

	/**
	 * Easier to calculate the negated probability than calculating positive probability. Use negative to get positive.
	 *
	 * @param probabilities [disease][symptom]
	 */
	public static double calculatePrOfNotSymptomGivenHypothesis(double[][] probabilities, int symptom, long
			hypothesis) {
		double result = 1;
		for(int disease = 0; disease < probabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= 1 - probabilities[disease][symptom];
		}
		return result;
	}

	public static double calculatePrOfHypothesis(double[] aPrioriProbabilities, long hypothesis) {
		double result = 1;
		for(int disease = 0; disease < aPrioriProbabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= aPrioriProbabilities[disease];
			else
				result *= 1 - aPrioriProbabilities[disease];
		}
		return result;
	}

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
