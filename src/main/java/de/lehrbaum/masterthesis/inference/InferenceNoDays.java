package de.lehrbaum.masterthesis.inference;

/**
 * General inference for no days
 */
public abstract class InferenceNoDays {
	public enum SYMPTOM_STATE {
		UNKOWN,
		PRESENT,
		ABSENT
	}
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
	private double [][][] probabilitiesAssumingCache;

	private boolean [] symptomsAnswered;

	private int symptomCount;

	public InferenceNoDays(double[] aPrioriProbabilities, double[][] probabilities) {
		currentProbabilities = this.aPrioriProbabilities = aPrioriProbabilities;
		this.probabilities = probabilities;
		symptomCount = probabilities[0].length;
		probabilitiesAssumingCache =  new double[symptomCount][2][];
		symptomsAnswered = new boolean[symptomCount];
	}

	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	public abstract double[] calculateGivenAllAnswers(SYMPTOM_STATE[] symptomsAnswered);

	public double[] probabilityAssumingSymptom(int symptom, boolean has) {
		if(probabilitiesAssumingCache[symptom][has?1:0] == null)
			probabilitiesAssumingCache[symptom][has?1:0] = calculateGivenSymptom(symptom, has);
		return probabilitiesAssumingCache[symptom][has?1:0];
	}

	protected abstract double [] calculateGivenSymptom(int symptom, boolean has);

	public void symptomAnswered(int symptom, boolean has) {
		currentProbabilities = probabilityAssumingSymptom(symptom, has);
		//empty cache
		symptomsAnswered[symptom] = true;
		probabilitiesAssumingCache = new double[amountSymptoms()][2][];
	}

	public boolean wasSymptomAnswered(int symptom) {
		return symptomsAnswered[symptom];
	}

	public int amountSymptoms() {
		return symptomCount;
	}
}
