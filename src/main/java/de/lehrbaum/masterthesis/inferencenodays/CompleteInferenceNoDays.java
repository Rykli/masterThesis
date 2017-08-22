package de.lehrbaum.masterthesis.inferencenodays;

public interface CompleteInferenceNoDays extends InferenceNoDays {
	enum SYMPTOM_STATE {
		UNKOWN,
		PRESENT,
		ABSENT
	}

	void calculateGivenAllAnswers(SYMPTOM_STATE[] symptomsAnswered);


}
