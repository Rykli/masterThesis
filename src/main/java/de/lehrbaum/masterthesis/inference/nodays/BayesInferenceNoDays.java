package de.lehrbaum.masterthesis.inference.nodays;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.data.DataProviderNoDays;
import de.lehrbaum.masterthesis.inference.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inference.InferenceNoDays;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.inference.InferenceNoDays.StepByStepInferenceNoDays;
import static de.lehrbaum.masterthesis.data.Answer.*;

/**
 * This class contains an implementation of the bayes formula. It does not actually go step by step, but instead just
 * calculates with the complete inference every time. Less efficient but a variant to consider.
 */
public class BayesInferenceNoDays extends AbstractInferenceNoDays
		implements StepByStepInferenceNoDays, InferenceNoDays.CompleteInferenceNoDays {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BayesInferenceNoDays.class.getCanonicalName());

	/**
	 * The probability for each answer of all symptoms.
	 */
	private EnumMap<Answer, Double>[] currentSymptomProbs;

	private AlgorithmConfiguration configuration;

	public BayesInferenceNoDays(@NotNull DataProviderNoDays dataProvider,
								@NotNull AlgorithmConfiguration configuration) {
		super(dataProvider, configuration);
		this.configuration = configuration;
		updateSymptomProbabilities();
	}

	@Override
	public void questionAnswered(int question, Answer answer) {
		assert !wasQuestionAnswered(question);
		if(question < symptomAnswers.length)
			symptomAnswers[question] = answer;
		else
			aPrioriQuestionAnswers[question - symptomAnswers.length] = answer;
		if(answer != NOT_ANSWERED)
			symptomsAnswered(symptomAnswers);
	}

	@Override
	public EnumMap<Answer, Double> probabilityOfAnswers(int question) {
		if(isSymptom(question))
			return currentSymptomProbs[question];
		else {
			EnumMap<Answer, Double> result = new EnumMap<>(Answer.class);
			EnumSet<Answer> possibleAnswers = dataProvider.getAPrioriAnswerPossibilities(
					question - symptomAnswers.length);
			for(Answer a : possibleAnswers)
				result.put(a, 1d/possibleAnswers.size());
			return result;
		}
	}

	@Override
	public void symptomsAnswered(Answer[] symptomAnswers) {
		long start = System.nanoTime();
		currentProbabilities = configuration.normalizeIfNeeded(
				calculateGivenAllSymptomStates(symptomAnswers));
		long duration = System.nanoTime() - start;
		System.out.println("calculate given took in micros: " + duration/1000d);

		//mark the symptoms as answered that were answered
		this.symptomAnswers = symptomAnswers;
		start = System.nanoTime();
		updateSymptomProbabilities();
		duration = System.nanoTime() - start;
		System.out.println("Update symptoms took in micros: " + duration/1000d);
	}

	private double[] calculateGivenAllSymptomStates(Answer[] symptomInformation) {
		/*
		 * Want to calculate the probability that a disease d_i is present given a set of symptoms S.
		 * P(d_i | S) = nominator / denominator where
		 * nominator = sum (of all Combinations of diseases H where d_i is active) of P(S|H)*P(H)
		 * denominator = sum (of all Combinations of diseases H) of P(S|H)*P(H)
		 *
		 * The terms P(S|H) and P(H) will be explained in the subfunctions.
		 *
		 * Since the denominator already calculates all summands necessary for the nominators, they will be calculated
		 * as part of the denominator calculation.
		 */
		double[] diseaseNominators = new double[getAmountDiseases()];
		double denominator
				= calculateNominatorDenominator(symptomInformation, diseaseNominators);
		for(int disease = 0; disease < diseaseNominators.length; disease++) {
			diseaseNominators[disease] /= denominator;
		}
		return diseaseNominators;
	}

	@Override
	public double[] simulateQuestionAnswered(int question, Answer answer) {
		if(wasQuestionAnswered(question))
			throw new UnsupportedOperationException("Question was already answered.");
		double[] newProbabilities;
		if(isSymptom(question)) {
			//simulate it being answered
			symptomAnswers[question] = answer;
			newProbabilities = calculateGivenAllSymptomStates(symptomAnswers);
			//remove the answer again
			symptomAnswers[question] = NOT_ANSWERED;
		} else {
			//simulate it being answered
			aPrioriQuestionAnswers[question - symptomAnswers.length] = answer;
			newProbabilities = calculateGivenAllSymptomStates(symptomAnswers);
			//remove the answer again
			aPrioriQuestionAnswers[question - symptomAnswers.length] = NOT_ANSWERED;
		}
		return configuration.normalizeIfNeeded(newProbabilities);
	}

	@SuppressWarnings("unchecked")
	private void updateSymptomProbabilities() {
		currentSymptomProbs = new EnumMap[symptomAnswers.length];
		for(int symptom = 0; symptom < currentSymptomProbs.length; symptom++) {
			if(wasQuestionAnswered(symptom))
				continue;

			currentSymptomProbs[symptom] = new EnumMap<>(Answer.class);
			//important to use current probabilities here TODO
			double prob = 0;//calculateProbabilityOfSymptom(currentProbabilities, dataProvider.getSymptomProbabilities(), symptom);
			currentSymptomProbs[symptom].put(Answer.PRESENT, prob);
			currentSymptomProbs[symptom].put(Answer.ABSENT, 1 - prob);
		}
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
			//takes about 0.9 microseconds
			double hypothesisProbability = BayesNoDaysMath.calculatePrOfHypothesis(aPrioriProbabilities, hypothesis);
			//takes about 0.13 microseconds
			double probabilityOfSymptomGivenHypothesis =
					1 - BayesNoDaysMath.calculatePrOfNotSymptomGivenHypothesis(probabilities, symptom, hypothesis);
			sum += hypothesisProbability * probabilityOfSymptomGivenHypothesis;
		}
		return sum;
	}

	private double calculateNominatorDenominator(Answer[] symptomInformation,
												 double[] diseaseNominators) {
		/*
		 * S: Symptoms
		 * Here we need to calculate P(S|H)*P(H) of all Combinations of diseases H) (H stands for Hypothesis)
		 * To do that we will just increase a binary number and refer to the bits as whether a disease is present or
		 * not.
		 * I assume the number of diseases is smaller than 64 which is the amount of bits in a long. This is
		 * reasonable,
		 * since if it where larger, the runtime would be crazy.
		 */
		final long limit = 1 << getAmountDiseases();
		double denominator = 0;
		for(long hypothesis = 0; hypothesis < limit; hypothesis++) {
			double hypothesisProbability = BayesNoDaysMath.calculatePrOfHypothesis(getAPrioriProbabilities(), hypothesis);
			double probabilityOfSymptomsGivenHypothesis = 0; //TODO BayesNoDaysMath.calculatePrOfSymptomsGivenHypothesis(
					dataProvider.getSymptomProbabilities(), symptomInformation, hypothesis);
			double combinedProbability = hypothesisProbability * probabilityOfSymptomsGivenHypothesis;

			//add the combined probability to the denominator.
			denominator += combinedProbability;

			//also add the combined probability to the nominators of the active diseases.
			for(int disease = 0; disease < getAmountDiseases(); disease++) {
				if((hypothesis >> disease & 1) == 1) {
					//disease is active
					diseaseNominators[disease] += combinedProbability;
				}
			}
		}
		return denominator;
	}

	@Override
	public void recalculateProbabilities() {
		symptomsAnswered(symptomAnswers);
	}

	@Override
	public String toString() {
		return configuration.toString() +
				"\nnodays inference no days:\n" +
				super.toString();
	}
}
