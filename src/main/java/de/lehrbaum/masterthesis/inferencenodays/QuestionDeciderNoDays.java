package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.MathUtils;

import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.MathUtils.bhattacharyyaDistance;
import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.symptoms;
import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.StepByStepInferenceNoDays;

/**
 * This class should decide what question to ask next. It estimates a gain for each question that could be asked. The
 * gain means how much extra information has been gained by asking the question. If not more gain is possible, it tries
 * to eliminate the main possibilities.
 */
public class QuestionDeciderNoDays {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionDeciderNoDays.class.getCanonicalName());
	/** Only used for test purposes. */
	double gainOfRecommendedQuestion = Double.MAX_VALUE;
	private QuestionGainEvaluator gainEvaluator;
	private StepByStepInferenceNoDays inferenceNoDays;

	QuestionDeciderNoDays(StepByStepInferenceNoDays inference, AlgorithmConfiguration configuration) {
		switch(configuration.getQuestionAlgorithm()) {
			case MINIMIZE_EXPECTED_ENTROPY:
				gainEvaluator = new MinimizeExpectedEntropy();
				break;
			case MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE:
				gainEvaluator = new MaximizeExpectedDistance();
				break;
		}
		this.inferenceNoDays = inference;
	}

	/**
	 * @return -1 means it is recommended to stop asking and just return the highest result
	 */
	public int recommendedSymptomToAsk() {
		int questionToAsk = - 1;
		double bestGain = - Double.MAX_VALUE;

		for(int symptom = 0; symptom < symptoms.length; symptom++) {
			if(inferenceNoDays.wasSymptomAnswered(symptom))
				continue;
			double gainBySymptom = gainEvaluator.estimateGainByAskingSymptom(inferenceNoDays, symptom);
			if(gainBySymptom > bestGain) {
				bestGain = gainBySymptom;
				questionToAsk = symptom;
			}
		}
		gainOfRecommendedQuestion = bestGain;

		//if the gain is to low consider stop asking
		logger.finer("Question decided: " + questionToAsk + " with expected gain: " + bestGain);

		return questionToAsk;
	}

	private interface QuestionGainEvaluator {
		double estimateGainByAskingSymptom(StepByStepInferenceNoDays inferenceNoDays, int symptom);
	}

	private static class MaximizeExpectedDistance implements QuestionGainEvaluator {

		@Override
		public double estimateGainByAskingSymptom(StepByStepInferenceNoDays inferenceNoDays, int
				symptom) {
			final double expectedGainIfPresent = getExpectedGain(inferenceNoDays, symptom, true);
			final double expectedGainIfAbsent = getExpectedGain(inferenceNoDays, symptom, false);
			return expectedGainIfAbsent + expectedGainIfPresent;
		}

		private static double getExpectedGain(StepByStepInferenceNoDays inferenceNoDays, int symptom,
											  boolean present) {
			final double distance = bhattacharyyaDistance(inferenceNoDays.getDiseaseProbabilities(),
					inferenceNoDays.simulateSymptomAnswered(symptom, present));
			final double gain = Math.abs(distance);
			return gain * inferenceNoDays.probabilityOfSymptom(symptom)[present ? 0 : 1];
		}
	}

	private static class MinimizeExpectedEntropy implements QuestionGainEvaluator {

		@Override
		public double estimateGainByAskingSymptom(StepByStepInferenceNoDays inferenceNoDays, int symptom) {
			final double expectedGainIfPresent = getExpectedGain(inferenceNoDays, symptom, true);
			final double expectedGainIfAbsent = getExpectedGain(inferenceNoDays, symptom, false);
			return expectedGainIfAbsent + expectedGainIfPresent;
		}

		private double getExpectedGain(StepByStepInferenceNoDays inferenceNoDays, int symptom, boolean present) {
			double entropy = MathUtils.entropy(inferenceNoDays.simulateSymptomAnswered(symptom, present));
			//lower entropy means higher gain since we want to minimize it. So its indirect proportional
			double gain = 1 / entropy;
			return inferenceNoDays.probabilityOfSymptom(symptom)[present ? 0 : 1] * gain;
		}
	}
}
