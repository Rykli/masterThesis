package de.lehrbaum.masterthesis.inferencenodays;

public interface InferenceNoDays {
	double[] getDiseaseProbabilities();

	boolean wasSymptomAnswered(int symptom);

	/**
	 *
	 * @param symptom The index of the symptom asked for.
	 * @return At position 0 the probability of the symptom being answered as present, at position 1 the probability of the
	 * 			symptom being answered as absent.
	 * 			If the symptom was already answered, the return value is unspecified.
	 */
	double [] probabilityOfSymptom(int symptom);
}
