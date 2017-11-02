package de.lehrbaum.masterthesis.inference.nodays;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import de.lehrbaum.masterthesis.inference.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inference.AlgorithmFactory;
import de.lehrbaum.masterthesis.inference.InferenceNoDays;
import org.junit.Test;

import static org.junit.Assert.*;
import static de.lehrbaum.masterthesis.TestUtils.*;

/**
 * General test for the bayes inference
 */
public class BayesTest {
	@Test(timeout = 20000)
	public void evaluateSpeed() throws UserReadableException {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		InferenceNoDays.StepByStepInferenceNoDays inferenceNoDays = AlgorithmFactory.getStepByStepInferenceNoDays(config);
		long sum = 0;
		for(int i = 0; i < 10; i++) {
			int questionToAnswer;
			do
				questionToAnswer = (int) (Math.random() * inferenceNoDays.getAmountQuestions());
			while(inferenceNoDays.wasQuestionAnswered(questionToAnswer));
			long start = System.nanoTime();
			answerRandomly(inferenceNoDays, questionToAnswer);
			sum += System.nanoTime() - start;
		}
		long average = sum/10;
		System.out.println("Average runtime in millis: " + average/1000000d);
	}

	@Test
	public void testWasQuestionAnswered() throws UserReadableException {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		InferenceNoDays.StepByStepInferenceNoDays inferenceNoDays = AlgorithmFactory.getStepByStepInferenceNoDays(config);
		int questionToAnswer = (int) (Math.random() * inferenceNoDays.getAmountQuestions());
		inferenceNoDays.questionAnswered(questionToAnswer, Answer.YES);
		assertTrue("Question not marked as answered even though answered",
				inferenceNoDays.wasQuestionAnswered(questionToAnswer));
	}

	/**
	 * This test figured out that the linear array processing is 10 times faster.
	 */
	@Test
	public void test2D() {
		double[][] probs = new double[20][50];
		for(int i = 0; i < probs.length; i++)
			for(int j = 0; j < probs[0].length; j++)
				probs[i][j] = Math.random();
		double[][] probs2 = new double[50][20];
		for(int i = 0; i < probs2.length; i++)
			for(int j = 0; j < probs2[0].length; j++)
				probs2[i][j] = Math.random();

		for(int s = 0; s < 2; s++) {
			long limit = 1 << probs.length;
			for(int hyp = 0; hyp < limit; hyp++) {
				double result = foo1(probs, s, hyp);
			}
		}

		long start = System.nanoTime();
		for(int s = 0; s < probs[0].length; s++) {
			long limit = 1 << probs.length;
			for(int hyp = 0; hyp < limit; hyp++) {
				double result = foo1(probs, s, hyp);
			}
		}
		long duration = System.nanoTime() - start;
		System.out.println("foo1 took: " + duration/1000000d);

		for(int s = 0; s < 2; s++) {
			long limit = 1 << probs[0].length;
			for(int hyp = 0; hyp < limit; hyp++) {
				double result = foo2(probs, s, hyp);
			}
		}

		start = System.nanoTime();
		for(int s = 0; s < probs.length; s++) {
			long limit = 1 << probs[0].length;
			for(int hyp = 0; hyp < limit; hyp++) {
				double result = foo2(probs, s, hyp);
			}
		}
		duration = System.nanoTime() - start;
		System.out.println("foo2 took: " + duration/1000000d);
	}

	static double foo1(double[][] probabilities, int symptom, long
			hypothesis) {
		double result = 1;
		for(int disease = 0; disease < probabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= 1 - probabilities[disease][symptom];
		}
		return result;
	}

	static double foo2(double[][] probabilities, int symptom, long
			hypothesis) {
		double result = 1;
		for(int disease = 0; disease < probabilities.length; disease++) {
			if((hypothesis >> disease & 1) == 1)
				result *= 1 - probabilities[symptom][disease];
		}
		return result;
	}
}
