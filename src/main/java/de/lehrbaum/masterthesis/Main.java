package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.data.NoDaysDefaultData;
import de.lehrbaum.masterthesis.inference.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.inference.InferenceNoDays;
import de.lehrbaum.masterthesis.view.NoDaysView;

import java.util.Arrays;
import java.util.logging.*;

public class Main {
	private static final Logger logger = Logger.getLogger("de.lehrbaum.masterthesis");

	public static void main(String[] args) {
		initializeLogging();
		//testStuff();
		NoDaysView.launch();
	}

	private static void testStuff() {

		InferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.probabilities, NoDaysDefaultData.aPriorProbabilities);
		inference.symptomAnswered(0, true);
		inference.symptomAnswered(1, true);
		inference.symptomAnswered(2, false);
		inference.symptomAnswered(11, true);
		inference.symptomAnswered(14, true);
		logger.info(Arrays.toString(inference.getDiseaseProbabilities()));
		inference.symptomAnswered(14, true);
		logger.info(Arrays.toString(inference.getDiseaseProbabilities()));

		inference = new BayesInferenceNoDays(NoDaysDefaultData.probabilities, NoDaysDefaultData.aPriorProbabilities);
		inference.symptomAnswered(0, true);
		inference.symptomAnswered(1, true);
		inference.symptomAnswered(2, false);
		inference.symptomAnswered(14, true);
		inference.symptomAnswered(11, true);
		logger.info(Arrays.toString(inference.getDiseaseProbabilities()));
	}

	private static void initializeLogging() {
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
