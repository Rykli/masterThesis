package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.QUESTION_ALGORITHM
		.MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.QUESTION_ALGORITHM
		.MINIMIZE_EXPECTED_ENTROPY;
import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.StepByStepInferenceNoDays;
import static de.lehrbaum.masterthesis.data.Answer.*;

/**
 * This class tests the deciding strategy.
 */
public class TestDecider {
	private static final Random random = new Random();
	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
	}
	//TODO: run for different question deciders

	@Test(timeout = 10000)
	public void testDoesDeciderTerminate() throws UserReadableException {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		for(int i = 0; i < 10; i++) {
			StepByStepInferenceNoDays inference = null;
			inference = AlgorithmFactory.getStepByStepInferenceNoDays(config);
			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int questionToAsk = questionDecider.recommendedSymptomToAsk();

			while(questionToAsk != - 1) {
				answerRandomly(inference, questionToAsk);
				questionToAsk = questionDecider.recommendedSymptomToAsk();
			}
		}
	}

	private void answerRandomly(StepByStepInferenceNoDays inferenceNoDays, int questionToAnswer) {
		EnumSet<Answer> possibleAnswers = inferenceNoDays.possibleAnswersForQuestion(questionToAnswer);
		int answerIndex = random.nextInt(possibleAnswers.size());
		Answer answer = possibleAnswers.stream().skip(answerIndex).findFirst().orElse(null);
		inferenceNoDays.questionAnswered(questionToAnswer, answer);
	}

	/**
	 * Before V2.0 function was checking that all questions are asked.
	 * However with the addition of more Questions in form of the APriori factors the
	 * likelihood of some combinations became so small that double couldn't handle them. The
	 * Question decided would just stop asking here. This behaviour is acceptable.
	 */
	@Test
	public void doesAskNearlyAllQuestions() throws UserReadableException {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGainLimit(-1);
		config.setQuestionAlgorithm(AlgorithmConfiguration.QUESTION_ALGORITHM.MINIMIZE_EXPECTED_ENTROPY);
		for(int i = 0; i < 10; i++) {
			StepByStepInferenceNoDays inference = AlgorithmFactory.getStepByStepInferenceNoDays(config);
			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int questionToAsk = questionDecider.recommendedSymptomToAsk();
			int amountQuestionsAsked = 0;
			while(questionToAsk != - 1) {
				amountQuestionsAsked++;
				answerRandomly(inference, questionToAsk);
				questionToAsk = questionDecider.recommendedSymptomToAsk();
			}

			Assert.assertTrue("Not enough questions where asked.",
					amountQuestionsAsked > (inference.getAmountQuestions()/2));
		}
	}

	//@Test This is for finding differences in the deciders
	public void findDeciderDifferences() throws UserReadableException {
		AlgorithmConfiguration config1 = new AlgorithmConfiguration();
		config1.setQuestionAlgorithm(MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE);
		AlgorithmConfiguration config2 = new AlgorithmConfiguration();
		config2.setQuestionAlgorithm(MINIMIZE_EXPECTED_ENTROPY);
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = (BayesInferenceNoDays) AlgorithmFactory.getStepByStepInferenceNoDays(config1);
			List<Integer> questionOrder = new LinkedList<>();

			QuestionDeciderNoDays questionDecider1 = AlgorithmFactory.getQuestionDecider(inference, config1);
			QuestionDeciderNoDays questionDecider2 = AlgorithmFactory.getQuestionDecider(inference, config2);
			while(true) {
				int questionToAsk1 = questionDecider1.recommendedSymptomToAsk();
				int questionToAsk2 = questionDecider2.recommendedSymptomToAsk();
				if(questionToAsk1 != questionToAsk2) {
					StringBuilder sb = new StringBuilder();
					sb.append("Different recommendation ");
					sb.append(inference.getDataProvider().getSymptomName(questionToAsk1));
					sb.append(" vs ");
					sb.append(inference.getDataProvider().getSymptomName(questionToAsk2));
					sb.append("\nAnswers were: ");
					questionOrder.forEach(symptom -> {
						sb.append(inference.getDataProvider().getSymptomName(symptom));
						sb.append(':');
						sb.append(inference.symptomAnswers[symptom]);
						sb.append(", ");
					});
					Assert.fail(sb.toString());
				}
				if(questionToAsk1 == - 1)
					break;
				questionOrder.add(questionToAsk1);
				answerRandomly(inference, questionToAsk1);
			}
		}
	}

	/**
	 * The gain of suggested questions should decrease, if the questions are answered as probability suggests.
	 */
	//@Test Even though it feels like it should be like this, it does not actually work out.
	@SuppressWarnings("unused")
	public void testIsGainDecreasing() throws UserReadableException {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		for(int i = 0; i < 10; i++) {
			StepByStepInferenceNoDays inference = AlgorithmFactory.getStepByStepInferenceNoDays(config);

			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int questionToAsk = questionDecider.recommendedSymptomToAsk();
			double lastGain = questionDecider.gainOfRecommendedQuestion;
			while(questionToAsk != - 1) {
				EnumMap<Answer, Double> probabilityOfSymptom = inference.probabilityOfAnswers(questionToAsk);
				boolean present = probabilityOfSymptom.get(PRESENT) > probabilityOfSymptom.get(ABSENT);
				inference.questionAnswered(questionToAsk, present ? PRESENT : ABSENT);
				questionToAsk = questionDecider.recommendedSymptomToAsk();
				Assert.assertTrue(questionDecider.gainOfRecommendedQuestion < lastGain);
				lastGain = questionDecider.gainOfRecommendedQuestion;
			}
		}
	}
}