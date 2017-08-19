package de.lehrbaum.masterthesis.inference;

public interface CompleteInferenceNoDays {
	double[] calculateGivenAllAnswers(SYMPTOM_STATE[] symptomsAnswered);

	enum SYMPTOM_STATE {
		UNKOWN,
		PRESENT,
		ABSENT
	}
}
