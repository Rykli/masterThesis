package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE;

import java.util.Random;

public class TestUtils {
	/**
	 * All possible symptoms states are currently 3^20, that are to many to test them all.
	 * So this method can generate random cases
	 * @return A random symptom state using {@link java.util.Random}.
	 */
	public static SYMPTOM_STATE [] getRandomSymptomState(int amountSymptoms) {
		SYMPTOM_STATE[] symptomStates = new SYMPTOM_STATE[amountSymptoms];
		Random r = new Random();
		for(int symptom = 0; symptom < amountSymptoms; symptom++) {
			switch (r.nextInt(3)) {
				case 0:
					symptomStates[symptom] = SYMPTOM_STATE.UNKOWN;
					break;
				case 1:
					symptomStates[symptom] = SYMPTOM_STATE.PRESENT;
					break;
				case 2:
					symptomStates[symptom] = SYMPTOM_STATE.ABSENT;
			}
		}
		return symptomStates;
	}
}
