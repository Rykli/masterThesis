package de.lehrbaum.masterthesis.inferencenodays;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.SYMPTOM_STATE.UNKOWN;

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

	@NotNull
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
		Arrays.fill(symptomsStates, UNKOWN);
	}

	@Override
	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	public boolean wasSymptomAnswered(int symptom) {
		return symptomsStates[symptom] != UNKOWN;
	}

	protected int amountSymptoms() {
		return symptomsStates.length;
	}

	protected int amountDiseases() {
		return probabilities.length;
	}
}
