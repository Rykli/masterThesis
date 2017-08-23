package de.lehrbaum.masterthesis.inferencenodays;

public interface StepByStepInferenceNoDays extends InferenceNoDays {
	void symptomAnswered(int symptom, boolean has);
}
