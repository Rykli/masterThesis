package de.lehrbaum.masterthesis.inferencenodays;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE;
import static de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE.UNKOWN;

/**
 * General inferencenodays for no days
 */
public abstract class AbstractInferenceNoDays implements InferenceNoDays{
	/**
	 * Contains probabilities at [disease][symptom].
	 */
	protected final double[][] probabilities;
	/**
	 * The a priori probabilities of the diseases.
	 */
	protected final double [] aPrioriProbabilities;
	/**
	 * The current probability for each disease.
	 */
	protected double[] currentProbabilities;

	@NotNull
	protected SYMPTOM_STATE[] symptomsAnswered;

	public AbstractInferenceNoDays(double[] aPrioriProbabilities, double[][] probabilities) {
		currentProbabilities = this.aPrioriProbabilities = aPrioriProbabilities;
		this.probabilities = probabilities;
		symptomsAnswered = new SYMPTOM_STATE[probabilities[0].length];
		Arrays.fill(symptomsAnswered, UNKOWN);
	}

	@Override
	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	public boolean wasSymptomAnswered(int symptom) {
		return symptomsAnswered[symptom] != UNKOWN;
	}

	protected int amountSymptoms() {
		return symptomsAnswered.length;
	}

	protected int amountDiseases() {
		return probabilities.length;
	}
}
