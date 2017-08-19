package de.lehrbaum.masterthesis.inference;

/**
 * General inference for no days
 */
public abstract class AbstractStepByStepInferenceNoDays implements StepByStepInferenceNoDays {
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

	private boolean [] symptomsAnswered;

	private int symptomCount;

	public AbstractStepByStepInferenceNoDays(double[] aPrioriProbabilities, double[][] probabilities) {
		currentProbabilities = this.aPrioriProbabilities = aPrioriProbabilities;
		this.probabilities = probabilities;
		symptomCount = probabilities[0].length;
		symptomsAnswered = new boolean[symptomCount];
	}

	@Override
	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	protected abstract double [] calculateGivenSymptom(int symptom, boolean has);

	@Override
	public void symptomAnswered(int symptom, boolean has) {
		currentProbabilities = calculateGivenSymptom(symptom, has);
		//empty cache
		symptomsAnswered[symptom] = true;
	}

	@Override
	public boolean wasSymptomAnswered(int symptom) {
		return symptomsAnswered[symptom];
	}

	@Override
	public int amountSymptoms() {
		return symptomCount;
	}
}
