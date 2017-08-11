package de.lehrbaum.masterthesis.inference.Bayes;

import de.lehrbaum.masterthesis.inference.InferenceNoDays;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This class contains
 */
//TODO need to rewrite most of the calculations
public class BayesInferenceNoDays extends InferenceNoDays {
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

	//region calculate general symptom probabilities
	private double[] calculateSymptomProbabilities() {
		//go parallel to use multiple cores
		return IntStream.range(0, probabilities[0].length).parallel().
				mapToDouble(this::calculateSymptomProbability).toArray();
	}

	private double calculateSymptomProbability(int symptom) {
		// A symptoms probability calculates as
		// sum over all combinations of diseases in Pr[symptom|diseases]*Pr[diseases]
		// since this is exponential we limit ourselves to combination of 4 active diseases,
		// this reduces the runtime

		double result = 0;
		boolean[] activeDiseases = new boolean[probabilities.length];
		// This sum goes over all possibly combinations of 4 active diseases, checked by Sebastian
		for(int i = 0; i < probabilities.length; i++) {
			activeDiseases[i] = true;
			for(int j = i + 1; j < probabilities.length; j++) {
				activeDiseases[j] = true;
				for(int k = j + 1; k < probabilities.length; k++) {
					activeDiseases[k] = true;
					for(int l = k + 1; l < probabilities.length; l++) {
						activeDiseases[l] = true;
						// A unique combination of four active diseases.
						result += calculateProbForDiseases(symptom, activeDiseases);
						activeDiseases[l] = false;
					}
					// If only 3 diseases are active
					result += calculateProbForDiseases(symptom, activeDiseases);
					activeDiseases[k] = false;
				}
				// If only 2 diseases are active
				result += calculateProbForDiseases(symptom, activeDiseases);
				activeDiseases[j] = false;
			}
			// If only one disease is active
			result += calculateProbForDiseases(symptom, activeDiseases);
			activeDiseases[i] = false;
		}
		return result;
	}

	private double calculateProbForDiseases(int symptom, boolean[] diseases) {
		// The probability for a symptom given a specific diseases combination is
		// (the sum of the probability of one of the diseases to cause the symptom)
		// times the probability of that disease combination

		// The probability that multiple diseases cause a symptom is difficult to calculate, but the probability
		// that multiple diseases do not cause a symptom is easier calculated, so I calculate that.

		double probNotSympt = 1; //probability of these diseases not causing the symptom
		double probDisease = 1; // probability of this disease combination
		for(int m = 0; m < probabilities.length; m++) {
			if(diseases[m]) {
				probNotSympt *= 1 - probabilities[m][symptom];
				probDisease *= currentProbabilities[m];
			} else
				probDisease *= 1 - currentProbabilities[m];
		}
		return (1 - probNotSympt) * probDisease;
	}

	//endregion

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
	 * Easier to calulate the negated probability.
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

	@Override
	protected double[] calculateGivenSymptom(int symptom, boolean has) {
		double [] diseaseProbabilities = currentProbabilities.clone();
		for(int i = 0; i < currentProbabilities.length; i++) {
			if(has)
				diseaseProbabilities[i] = (probabilities[i][symptom] * diseaseProbabilities[i])
						/ symptomProbabilities[symptom];
			else
				diseaseProbabilities[i] = ( (1 - probabilities[i][symptom]) * diseaseProbabilities[i])
						/ (1 - symptomProbabilities[symptom]);
		}
		return diseaseProbabilities;
	}

	@Override
	public void symptomAnswered(int symptom, boolean has) {
		super.symptomAnswered(symptom, has);
		// The probability of a symptom changes if the probability of a disease changes
		symptomProbabilities = calculateSymptomProbabilities();
	}
}
