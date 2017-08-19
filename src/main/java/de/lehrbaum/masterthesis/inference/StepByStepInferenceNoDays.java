package de.lehrbaum.masterthesis.inference;

public interface StepByStepInferenceNoDays {
	double[] getDiseaseProbabilities();

	void symptomAnswered(int symptom, boolean has);

	boolean wasSymptomAnswered(int symptom);

	int amountSymptoms();
}
