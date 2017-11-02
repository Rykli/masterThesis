package de.lehrbaum.masterthesis.inference.nodays;

import de.lehrbaum.masterthesis.data.Answer;

public class BayesNoDaysMath {
	public static double calculatePrOfSymptomsGivenHypothesis(
			double[][] probabilities, Answer[] symptomInformation, long hypothesis) {
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
		}//TODO: flip loops, this is to inefficient
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
