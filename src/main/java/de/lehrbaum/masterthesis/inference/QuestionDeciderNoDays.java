package de.lehrbaum.masterthesis.inference;

import de.lehrbaum.masterthesis.MathUtils;
import de.lehrbaum.masterthesis.data.Answer;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.MathUtils.bhattacharyyaDistance;
import static de.lehrbaum.masterthesis.inference.InferenceNoDays.StepByStepInferenceNoDays;

/**
 * This class should decide what question to ask next. It estimates a gain for each question that could be asked. The
 * gain means how much extra information has been gained by asking the question. If not more gain is possible, it tries
 * to eliminate the main possibilities.
 */
public class QuestionDeciderNoDays {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionDeciderNoDays.class.getCanonicalName());
	/** Only used for test purposes. Should be replaced with something fitting */
	public double gainOfRecommendedQuestion = Double.MAX_VALUE;
	private QuestionGainEvaluator gainEvaluator;
	private StepByStepInferenceNoDays inferenceNoDays;
	private double gainLimit;

	QuestionDeciderNoDays(StepByStepInferenceNoDays inference, AlgorithmConfiguration configuration, double
			gainLimit) {
		switch(configuration.getQuestionAlgorithm()) {
			case MINIMIZE_EXPECTED_ENTROPY:
				gainEvaluator = new MinimizeExpectedEntropy();
				break;
			case MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE:
				gainEvaluator = new MaximizeExpectedDistance();
				break;
		}
		this.inferenceNoDays = inference;
		this.gainLimit = gainLimit;
	}

	/**
	 * @return -1 means it is recommended to stop asking and conclude
	 */
	public int recommendedSymptomToAsk() {
		int questionToAsk = - 1;
		double bestGain = - Double.MAX_VALUE;

		for(int question = 0; question < inferenceNoDays.getAmountQuestions(); question++) {
			if(inferenceNoDays.wasQuestionAnswered(question))
				continue;
			double gainBySymptom = gainEvaluator.estimateGainByAskingSymptom(inferenceNoDays, question);
			if(gainBySymptom > bestGain) {
				bestGain = gainBySymptom;
				questionToAsk = question;
			}
		}
		gainOfRecommendedQuestion = bestGain;

		//if the gain is to low consider stop asking
		logger.finer("Question decided: " + questionToAsk + " with expected gain: " + bestGain);

		if(bestGain < gainLimit)
			questionToAsk = - 1;

		return questionToAsk;
	}

	private static abstract class QuestionGainEvaluator {
		double estimateGainByAskingSymptom(@NotNull StepByStepInferenceNoDays inferenceNoDays, int question) {
			double estimatedGain = 0;
			for(Answer a : inferenceNoDays.possibleAnswersForQuestion(question))
				estimatedGain += getExpectedGain(inferenceNoDays, question, a);
			return estimatedGain;
		}

		protected abstract double getExpectedGain(@NotNull StepByStepInferenceNoDays inferenceNoDays, int question, Answer answer);
	}

	private static class MaximizeExpectedDistance extends QuestionGainEvaluator {

		@Override
		protected double getExpectedGain(@NotNull StepByStepInferenceNoDays inferenceNoDays, int question, Answer answer) {
			final double distance = bhattacharyyaDistance(inferenceNoDays.getDiseaseProbabilities(),
					inferenceNoDays.simulateQuestionAnswered(question, answer));
			final double gain = Math.abs(distance);
			return gain * inferenceNoDays.probabilityOfAnswers(question).get(answer);
		}
	}

	private static class MinimizeExpectedEntropy extends QuestionGainEvaluator {

		@Override
		protected double getExpectedGain(@NotNull StepByStepInferenceNoDays inferenceNoDays, int question, Answer answer) {
			double entropy = MathUtils.entropy(inferenceNoDays.simulateQuestionAnswered(question, answer));
			//lower entropy means higher gain since we want to minimize it. So its indirect proportional
			double gain = 1 / entropy;
			return gain * inferenceNoDays.probabilityOfAnswers(question).get(answer);
		}
	}
}
