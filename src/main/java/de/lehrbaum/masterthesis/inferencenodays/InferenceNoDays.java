package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.data.DataProvider;

import java.util.EnumMap;
import java.util.EnumSet;

public interface InferenceNoDays {
	double[] getDiseaseProbabilities();

	boolean wasQuestionAnswered(int question);

	/**
	 * @param question The index of the question.
	 * @return The probabilities of the answers for this question.
	 */
	EnumMap<Answer, Double> probabilityOfAnswers(int question);

	int getAmountQuestions();

	EnumSet<Answer> possibleAnswersForQuestion(int question);

	//TODO should move to different class, not part of inference.
	TextGenerator getTextGenerator();

	String getDiseaseName(int disease);

	/**
	 * Use this to recalculate after excel has changed
	 */
	void recalculateProbabilities();

	/**
	 * A complete inference is optimized to only once get information about symptoms. If you need to give information
	 * step by step try an Implementation of the {@link StepByStepInferenceNoDays}
	 */
	interface CompleteInferenceNoDays extends InferenceNoDays {
		void symptomsAnswered(Answer[] symptomsAnswered);
	}

	interface StepByStepInferenceNoDays extends InferenceNoDays {
		void questionAnswered(int symptom, Answer state);

		double[] simulateQuestionAnswered(int symptom, Answer answer);
	}
}
