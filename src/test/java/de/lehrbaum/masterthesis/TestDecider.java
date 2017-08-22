package de.lehrbaum.masterthesis;

import de.lehrbaum.masterthesis.data.NoDaysDefaultData;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.questionDecider.QuestionDecider;
import org.junit.Test;

import java.util.Random;

/**
 * This class tests the deciding strategy.
 */
public class TestDecider {

	@Test(timeout=1000)
	public void testDoesDeciderTerminate() {
		for(int i = 0; i < 100; i++) {
			BayesInferenceNoDays inference = new BayesInferenceNoDays(NoDaysDefaultData.probabilities,
					NoDaysDefaultData.aPriorProbabilities, BayesInferenceNoDays.VARIANTS.defSet);
			int questionToAsk = new QuestionDecider(inference).recommendedSymptomToAsk();
			while (questionToAsk != - 1) {
				inference.symptomAnswered(questionToAsk, (new Random()).nextBoolean());
				questionToAsk = new QuestionDecider(inference).recommendedSymptomToAsk();
			}
		}
	}


}
