package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays;

import java.util.stream.DoubleStream;

/**
 * Contains general purpose mathematical functions.
 */
public class MathUtils {

	private static DistanceImpl distanceImpl = new BhattacharyyaDistance();

	public static double [] normalize(double [] vector) {
		double sum = DoubleStream.of(vector).sum();
		return DoubleStream.of(vector).map(d -> d/sum).toArray();
	}

	public static double distance(double [] firstDistribution, double[] secondDistribution) {
		assert firstDistribution.length == secondDistribution.length;
		return distanceImpl.distance(firstDistribution, secondDistribution);
	}

	private interface DistanceImpl {
		double distance(double[] firstDistr, double[] secondDistr);
	}

	private static class BhattacharyyaDistance implements DistanceImpl {
		@Override
		public double distance(double[] firstDistr, double[] secondDistr) {
			// The distance is D(p, q) = -ln(sum of all possible diseases i of (root of p(i)*q(i)))
			double sum = 0;
			for(int i = 0; i < firstDistr.length; i++) {
				sum += Math.sqrt(firstDistr[i]*secondDistr[i]);
			}
			return -Math.log(sum);
		}
	}

	public static double calculatePrOfSymptomsGivenHypothesis(double[][] probabilities, CompleteInferenceNoDays.SYMPTOM_STATE[]
			symptomInformation, long hypothesis) {
		double result = 1;// the neutral element of multiplication is 1, so it should be the correct value.
		for(int symptom = 0; symptom < symptomInformation.length; symptom++) {
			switch (symptomInformation[symptom]) {
				case UNKOWN:
					continue;//ignore unknown symptoms
				case ABSENT:
					result *= calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
					break;
				case PRESENT:
					result *= 1 - calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
					break;
			}
		}
		return result;
	}

	/**
	 * Easier to calculate the negated probability than calculating positive probability. Use negative to get positive.
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
}
