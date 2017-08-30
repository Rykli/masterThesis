package de.lehrbaum.masterthesis.inferencenodays;

public interface CompleteInferenceNoDays extends InferenceNoDays {
	void calculateGivenAllAnswers(SYMPTOM_STATE[] symptomsAnswered);

	enum SYMPTOM_STATE {
		UNKOWN,
		PRESENT,
		ABSENT
	}


}
