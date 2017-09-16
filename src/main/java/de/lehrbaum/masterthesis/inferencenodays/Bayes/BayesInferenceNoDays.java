package de.lehrbaum.masterthesis.inferencenodays.Bayes;

import de.lehrbaum.masterthesis.MathUtils;
import de.lehrbaum.masterthesis.inferencenodays.AbstractInferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.MathUtils.*;
import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.StepByStepInferenceNoDays;

/**
 * This class contains an implementation of the bayes formula. It does not actually go step by step, but instead just
 * calculates with the complete inference every time. Less efficient but a variant to consider.
 */
public class BayesInferenceNoDays extends AbstractInferenceNoDays
		implements StepByStepInferenceNoDays, InferenceNoDays.CompleteInferenceNoDays {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BayesInferenceNoDays.class.getCanonicalName());

	/**
	 * The probability for each symptom.
	 */
	private double[][] symptomProbabilities;

	private AlgorithmConfiguration configuration;

	public BayesInferenceNoDays(double[] aPrioriProbabilities, double[][] probabilities,
								@NotNull AlgorithmConfiguration configuration) {
		super(aPrioriProbabilities, probabilities, configuration);
		this.configuration = configuration;
		updateSymptomProbabilities(symptomsStates);
	}

	@Override
	public void symptomAnswered(int symptom, SYMPTOM_STATE state) {
		assert ! wasSymptomAnswered(symptom);
		symptomsStates[symptom] = state;
		if(state != SYMPTOM_STATE.UNKOWN)
			symptomsAnswered(symptomsStates);
	}

	private static double calculateProbabilityOfSymptom(double[] aPrioriProbabilities, double[][] probabilities, int
			symptom) {
		final long limit = 1 << aPrioriProbabilities.length;
		/*
		 * The probability of a symptom is the sum over all possible hypothesis
		 * of the probabilities for that symptom given a hypothesis.
		 * Formally: sum (from 0 to all hypothesis) pr[symptom|hypothesis]*pr[hypothesis]
		 */
		double sum = 0;
		for(long hypothesis = 0; hypothesis < limit; hypothesis++) {
			double hypothesisProbability = calculatePrOfHypothesis(aPrioriProbabilities, hypothesis);
			double probabilityOfSymptomGivenHypothesis =
					1 - calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
			sum += hypothesisProbability * probabilityOfSymptomGivenHypothesis;
		}
		return sum;
	}

	private double calculatePrOfSymptoms(double[] aPrioriProbabilities, double[][] probabilities,
										 SYMPTOM_STATE[] symptomInformation) {
		final long limit = 1 << aPrioriProbabilities.length;
		/*
		 * The probability of multiple symptoms is the sum over all possible hypothesis
		 * of the probabilities for those symptoms given a hypothesis.
		 * Formally: sum (from 0 to all hypothesis) pr[symptoms|hypothesis]*pr[hypothesis]
		 */
		double sum = 0;
		for(long hypothesis = 0; hypothesis < limit; hypothesis++) {
			double hypothesisProbability = calculatePrOfHypothesis(aPrioriProbabilities, hypothesis);
			double probabilityOfSymptomsGivenHypothesis =
					calculatePrOfSymptomsGivenHypothesis(probabilities, symptomInformation, hypothesis);
			sum += hypothesisProbability * probabilityOfSymptomsGivenHypothesis;
		}
		return sum;
	}

	@Override
	public double[] probabilityOfSymptom(int symptom) {
		return symptomProbabilities[symptom];
	}

	@Override
	public void symptomsAnswered(SYMPTOM_STATE[] symptomInformation) {
		if(configuration.getNormalize())
			currentProbabilities = MathUtils.normalize(calculateGivenAllSymptomStates(symptomInformation));
		else
			currentProbabilities = calculateGivenAllSymptomStates(symptomInformation);

		//mark the symptoms as answered that are not unknown
		this.symptomsStates = symptomInformation;

		updateSymptomProbabilities(symptomInformation);
	}

	private double[] calculateGivenAllSymptomStates(SYMPTOM_STATE[] symptomInformation) {
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
		double[] diseaseNominators = new double[aPrioriProbabilities.length];
		double denominator
				= calculateNominatorDenominator(symptomInformation, diseaseNominators);
		for(int disease = 0; disease < diseaseNominators.length; disease++)
			diseaseNominators[disease] /= denominator;
		return diseaseNominators;
	}

	@Override
	public double[] simulateSymptomAnswered(int symptom, boolean has) {
		assert symptomsStates[symptom] == null;
		symptomsStates[symptom] = has ? SYMPTOM_STATE.PRESENT : SYMPTOM_STATE.ABSENT;
		double[] newProbabilities = calculateGivenAllSymptomStates(symptomsStates);
		symptomsStates[symptom] = null;
		if(configuration.getNormalize())
			return MathUtils.normalize(newProbabilities);
		else
			return newProbabilities;
	}

	@Override
	public String toString() {
		return configuration.toString() +
				"\nBayes inference no days:\n" +
				super.toString();
	}

	private void updateSymptomProbabilities(SYMPTOM_STATE[] symptomsAnswered) {
		symptomProbabilities = new double[amountSymptoms()][2];
		for(int symptom = 0; symptom < amountSymptoms(); symptom++) {
			if(symptomsStates[symptom] == SYMPTOM_STATE.PRESENT
					|| symptomsStates[symptom] == SYMPTOM_STATE.ABSENT)
				continue;

			double prob = 0;
			switch(configuration.getBayesSymptomsCalculationVariant()) {
				case BAYES_SYMPTOMS_CALCULATION_VARIANT_1:
					//important to use the current probabilities here
					prob = calculateProbabilityOfSymptom(currentProbabilities, probabilities, symptom);
					break;
				case BAYES_SYMPTOMS_CALCULATION_VARIANT_2:
					SYMPTOM_STATE[] symptomInformation = symptomsAnswered.clone();
					symptomInformation[symptom] = SYMPTOM_STATE.PRESENT;
					prob = calculatePrOfSymptoms(aPrioriProbabilities, probabilities, symptomInformation);
					break;
			}
			symptomProbabilities[symptom][0] = prob;
			symptomProbabilities[symptom][1] = 1 - prob;
		}
	}

	private double calculateNominatorDenominator(CompleteInferenceNoDays.SYMPTOM_STATE[] symptomInformation,
												 double[] diseaseNominators) {
		/*
		 * Here we need to calculate P(F|H)*P(H) of all Combinations of diseases H) (H stands for Hypothesis)
		 * To do that we will just increase a binary number and refer to the bits as whether a disease is present or
		 * not.
		 * I assume the number of diseases is smaller than 64 which is the amount of bits in a long. This is
		 * reasonable,
		 * since if it where larger, the runtime would be crazy.
		 */
		final long limit = 1 << amountDiseases();
		double denominator = 0;
		for(long hypothesis = 0; hypothesis < limit; hypothesis++) {
			double hypothesisProbability = calculatePrOfHypothesis(aPrioriProbabilities, hypothesis);
			double probabilityOfSymptomsGivenHypothesis =
					calculatePrOfSymptomsGivenHypothesis(probabilities, symptomInformation, hypothesis);
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
}
