package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.data.NoDaysDefaultData;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.symptoms;
import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.QUESTION_ALGORITHM
		.MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.QUESTION_ALGORITHM
		.MINIMIZE_EXPECTED_ENTROPY;
import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.SYMPTOM_STATE.*;

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
	public void testDoesDeciderTerminate() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		//config.setQuestionAlgorithm(AlgorithmConfiguration.QUESTION_ALGORITHM.MINIMIZE_EXPECTED_ENTROPY);
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.aPriorProbabilities,
					NoDaysDefaultData.probabilities, config);
			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int symptomToAsk = questionDecider.recommendedSymptomToAsk();

			while(symptomToAsk != - 1) {
				inference.symptomAnswered(symptomToAsk, getRandomAnswer());
				symptomToAsk = questionDecider.recommendedSymptomToAsk();
			}
		}
	}

	private InferenceNoDays.SYMPTOM_STATE getRandomAnswer() {
		int answer = random.nextInt(3);
		switch(answer) {
			case 0:
				return ABSENT;
			case 1:
				return PRESENT;
			default:
				return UNKOWN;
		}
	}

	@Test
	public void doesAskAllQuestions() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGainLimit(0);
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.aPriorProbabilities,
					NoDaysDefaultData.probabilities, config);
			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int symptomToAsk = questionDecider.recommendedSymptomToAsk();
			int amountQuestionsAsked = 0;
			while(symptomToAsk != - 1) {
				amountQuestionsAsked++;
				inference.symptomAnswered(symptomToAsk, getRandomAnswer());
				symptomToAsk = questionDecider.recommendedSymptomToAsk();
			}

			Assert.assertEquals("Not all questions where asked.", 21, amountQuestionsAsked);
		}
	}

	//@Test This is for finding differences in the deciders
	public void findDeciderDifferences() {
		AlgorithmConfiguration config1 = new AlgorithmConfiguration();
		config1.setQuestionAlgorithm(MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE);
		AlgorithmConfiguration config2 = new AlgorithmConfiguration();
		config2.setQuestionAlgorithm(MINIMIZE_EXPECTED_ENTROPY);
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.aPriorProbabilities,
					NoDaysDefaultData.probabilities, config1);
			List<Integer> questionOrder = new LinkedList<>();

			QuestionDeciderNoDays questionDecider1 = AlgorithmFactory.getQuestionDecider(inference, config1);
			QuestionDeciderNoDays questionDecider2 = AlgorithmFactory.getQuestionDecider(inference, config2);
			while(true) {
				int symptomToAsk1 = questionDecider1.recommendedSymptomToAsk();
				int symptomToAsk2 = questionDecider2.recommendedSymptomToAsk();
				if(symptomToAsk1 != symptomToAsk2) {
					StringBuilder sb = new StringBuilder();
					sb.append("Different recommendation ");
					sb.append(symptoms[symptomToAsk1]);
					sb.append(" vs ");
					sb.append(symptoms[symptomToAsk2]);
					sb.append("\nAnswers were: ");
					questionOrder.forEach(symptom -> {
						sb.append(symptoms[symptom]);
						sb.append(':');
						sb.append(inference.symptomsStates[symptom]);
						sb.append(", ");
					});
					Assert.fail(sb.toString());
				}
				if(symptomToAsk1 == - 1)
					break;
				questionOrder.add(symptomToAsk1);
				inference.symptomAnswered(symptomToAsk1, getRandomAnswer());
			}
		}
	}

	/**
	 * The gain of suggested questions should decrease, if the questions are answered as probability suggests.
	 */
	//@Test Even though it feels like it should be like this, it does not actually work out.
	@SuppressWarnings("unused")
	public void testIsGainDecreasing() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.aPriorProbabilities,
					NoDaysDefaultData.probabilities, config);

			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int questionToAsk = questionDecider.recommendedSymptomToAsk();
			double lastGain = questionDecider.gainOfRecommendedQuestion;
			while(questionToAsk != - 1) {
				double[] probabilityOfSymptom = inference.probabilityOfSymptom(questionToAsk);
				boolean present = probabilityOfSymptom[0] > probabilityOfSymptom[1];
				inference.symptomAnswered(questionToAsk, present ? PRESENT : ABSENT);
				questionToAsk = questionDecider.recommendedSymptomToAsk();
				Assert.assertTrue(questionDecider.gainOfRecommendedQuestion < lastGain);
				lastGain = questionDecider.gainOfRecommendedQuestion;
			}
		}
	}
}