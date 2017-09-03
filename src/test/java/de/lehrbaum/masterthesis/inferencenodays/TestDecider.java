package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.data.NoDaysDefaultData;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * This class tests the deciding strategy.
 */
public class TestDecider {
	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
	}
	//TODO: use parameterized tests with the AlgorithmConfigurations

	@Test(timeout = 10000)
	public void testDoesDeciderTerminate() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		//config.setQuestionAlgorithm(AlgorithmConfiguration.QUESTION_ALGORITHM.MINIMIZE_EXPECTED_ENTROPY);
		Random r = new Random();
		for(int i = 0; i < 10; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.aPriorProbabilities,
					NoDaysDefaultData.probabilities, config);

			QuestionDeciderNoDays questionDecider = AlgorithmFactory.getQuestionDecider(inference, config);
			int questionToAsk = questionDecider.recommendedSymptomToAsk();
			while(questionToAsk != - 1) {
				inference.symptomAnswered(questionToAsk, r.nextBoolean());
				questionToAsk = questionDecider.recommendedSymptomToAsk();
			}
		}
	}

	/**
	 * The gain of suggested questions should decrease, if the questions are answered as probability suggests.
	 */
	//@Test Even though it feels like it should be like this, it does not actually work out.
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
				inference.symptomAnswered(questionToAsk, present);
				questionToAsk = questionDecider.recommendedSymptomToAsk();
				Assert.assertTrue(questionDecider.gainOfRecommendedQuestion < lastGain);
				lastGain = questionDecider.gainOfRecommendedQuestion;
			}
		}
	}
}