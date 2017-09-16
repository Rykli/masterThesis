package de.lehrbaum.masterthesis;

import java.util.Random;
import java.util.logging.*;

import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.SYMPTOM_STATE;

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

	/**
	 * Since tests don't use the Main class the logging will not be initialized by default but has to be done extra.
	 */
	public static void initializeTestLogging() {
		Logger logger = Logger.getLogger(TestUtils.class.getPackage().getName());
		StreamHandler handler = new StreamHandler(System.out, new SimpleFormatter()) {
			@Override
			public synchronized void publish(final LogRecord record) {
				super.publish(record);
				flush();
			}
		};
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}
}
