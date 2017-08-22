package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE;

import java.util.concurrent.ThreadLocalRandom;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.symptoms;

public class TestUtils {
	/**
	 * All possible symptoms states are 3^20, that are to many to test them all.
	 * @return A random symptom state using {@link java.util.Random}.
	 */
	public SYMPTOM_STATE [] getRandomSymptomState() {
		//damn math pow returns double...
		final long amountSymptomCombinations = (long) Math.pow(3, symptoms.length);
		long symptomCombinations = ThreadLocalRandom.current().nextLong(amountSymptomCombinations);
		return null;
	}
}
