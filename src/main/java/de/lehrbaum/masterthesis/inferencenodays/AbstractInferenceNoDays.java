package de.lehrbaum.masterthesis.inferencenodays;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * General inferencenodays for no days
 */
public abstract class AbstractInferenceNoDays implements InferenceNoDays {
	/**
	 * Contains probabilities at [disease][symptom].
	 */
	protected final double[][] probabilities;
	/**
	 * The a priori probabilities of the diseases.
	 */
	protected final double[] aPrioriProbabilities;
	/**
	 * The current probability for each disease.
	 */
	protected double[] currentProbabilities;

	protected SYMPTOM_STATE[] symptomsStates;

	@NotNull
	protected AlgorithmConfiguration configuration;

	public AbstractInferenceNoDays(double[] aPrioriProbabilities, double[][] probabilities, @NotNull
			AlgorithmConfiguration
			configuration) {
		currentProbabilities = this.aPrioriProbabilities = aPrioriProbabilities;
		this.probabilities = probabilities;
		this.configuration = configuration;
		symptomsStates = new SYMPTOM_STATE[probabilities[0].length];
	}

	@Override
	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	public boolean wasSymptomAnswered(int symptom) {
		return symptomsStates[symptom] != null;
	}

	protected int amountSymptoms() {
		return symptomsStates.length;
	}

	protected int amountDiseases() {
		return probabilities.length;
	}

	@Override
	public String toString() {
		return "The symptom states: " + Arrays.toString(symptomsStates) +
				"\nThe current probabilities: " + Arrays.toString(currentProbabilities);
	}
}
