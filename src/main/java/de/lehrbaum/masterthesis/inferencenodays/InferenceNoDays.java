package de.lehrbaum.masterthesis.inferencenodays;

public interface InferenceNoDays {
	enum SYMPTOM_STATE {
		UNKOWN,
		PRESENT,
		ABSENT
	}

	double[] getDiseaseProbabilities();

	boolean wasSymptomAnswered(int symptom);

	/**
	 * @param symptom The index of the symptom asked for.
	 * @return At position 0 the probability of the symptom being answered as present, at position 1 the probability of
	 * the symptom being answered as absent. If the symptom was already answered, the return value is unspecified.
	 */
	double[] probabilityOfSymptom(int symptom);

	/**
	 * A complete inference is optimized to only once get information about symptoms. If you need to give information
	 * step by step try an Implementation of the {@link StepByStepInferenceNoDays}
	 */
	interface CompleteInferenceNoDays extends InferenceNoDays {
		void symptomsAnswered(SYMPTOM_STATE[] symptomsAnswered);
	}

	interface StepByStepInferenceNoDays extends InferenceNoDays {
		void symptomAnswered(int symptom, SYMPTOM_STATE state);

		double[] simulateSymptomAnswered(int symptom, boolean has);
	}
}
