package de.lehrbaum.masterthesis.inference.Bayes;

import de.lehrbaum.masterthesis.inference.AbstractStepByStepInferenceNoDays;
import de.lehrbaum.masterthesis.inference.CompleteInferenceNoDays;

import java.util.logging.Logger;

/**
 * This class contains
 */
public class BayesInferenceNoDays extends AbstractStepByStepInferenceNoDays implements CompleteInferenceNoDays {
	private static final Logger logger = Logger.getLogger(BayesInferenceNoDays.class.getCanonicalName());

	/**
	 * The probability for each symptom.
	 */
	private double[] symptomProbabilities;

	public BayesInferenceNoDays(double[][] probabilities, double [] aPrioriProbabilities) {
		super(aPrioriProbabilities, probabilities);
		/*logger.finer("Calculating symptom probabilites");
		symptomProbabilities = calculateSymptomProbabilities();
		logger.finer("Calculated symptom probabilities " + Arrays.toString(symptomProbabilities));*/
	}

	@Override
	public double[] calculateGivenAllAnswers(SYMPTOM_STATE[] symptomsAnswered) {
		/*
		 * Want to calculate the probability that a disease d_i is present given a set of symptoms S.
		 * P(d_i | S) = nominator / denominator where
		 * nominator = sum (of all Combinations of diseases H where d_i is active) of P(F|H)*P(H)
		 * denominator = sum (of all Combinations of diseases H) of P(F|H)*P(H)
		 *
		 * The terms P(F|H) and P(H) will be explained in the subfunctions.
		 *
		 * Since the denominator already calculates all summands necessary for the nominators, they will be calculated
		 * as part of the denominator calculation.
		 */
		double [] diseaseNominators = new double[aPrioriProbabilities.length];
		double denominator = calculateNominatorDenominator(symptomsAnswered, diseaseNominators);
		for(int disease = 0; disease < diseaseNominators.length; disease++)
			diseaseNominators[disease] /= denominator;
		return diseaseNominators;
	}

	@Override
	protected double[] calculateGivenSymptom(int symptom, boolean has) {
		return new double[0];//TODO: implement
	}

	//region Mathematics helper functions.

	private double calculateNominatorDenominator(SYMPTOM_STATE[] symptomInformation, double[] diseaseNominators) {
		/*
		 * Here we need to calculate P(F|H)*P(H) of all Combinations of diseases H) (H stands for Hypothesis)
		 * To do that we will just increase a binary number and refer to the bits as whether a disease is present or not.
		 * I assume the number of diseases is smaller than 64 which is the amount of bits in a long. This is reasonable,
		 * since if it where larger, the runtime would be crazy.
		 */
		final long limit = 1 << aPrioriProbabilities.length;
		double denominator = 0;
		for(long hypothesis = 0; hypothesis < limit; hypothesis++) {
			double hypothesisProbability = probabilityOfHypothesis(hypothesis);
			double probabilityOfSymptomsGivenHypothesis = probabilityOfSymptomsGivenHypothesis(symptomInformation, hypothesis);
			double combinedProbability = hypothesisProbability * probabilityOfSymptomsGivenHypothesis;

			//add the combined probability to the denominator.
			denominator += combinedProbability;

			//also add the combined probability to the nominators of the active diseases.
			for(int disease = 0; disease < aPrioriProbabilities.length; disease++) {
				if((hypothesis >> disease & 1) == 1) {
					//disease is active
					diseaseNominators[disease] += combinedProbability;
				}
			}
		}
		return denominator;
	}

	private double probabilityOfSymptomsGivenHypothesis(SYMPTOM_STATE[] symptomInformation, long hypothesis) {
		double result = 1;// the neutral element of multiplication is 1, so it should be the correct value.
		for(int symptom = 0; symptom < symptomInformation.length; symptom++) {
			switch (symptomInformation[symptom]) {
				case UNKOWN:
					continue;//ignore unknown symptoms
				case ABSENT:
					result *= probabilityOfNotSymptomGivenHypothesis(symptom, hypothesis);
					break;
				case PRESENT:
					result *= 1 - probabilityOfNotSymptomGivenHypothesis(symptom, hypothesis);
					break;
			}
		}
		return result;
	}

	/**
	 * Easier to calculate the negated probability.
	 */
	private double probabilityOfNotSymptomGivenHypothesis(int symptom, long hypothesis) {
		double result = 1;
		for(int disease = 0; disease < aPrioriProbabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= 1 - probabilities[disease][symptom];
		}
		return result;
	}

	private double probabilityOfHypothesis(long hypothesis) {
		double result = 1;
		for(int disease = 0; disease < aPrioriProbabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= aPrioriProbabilities[disease];
			else
				result *= 1 - aPrioriProbabilities[disease];
		}
		return result;
	}

	//endregion
}
