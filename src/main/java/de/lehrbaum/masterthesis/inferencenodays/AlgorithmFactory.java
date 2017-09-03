package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.StepByStepBayesInference;
import org.jetbrains.annotations.NotNull;

public class AlgorithmFactory {
	public static InferenceNoDays.StepByStepInferenceNoDays getStepByStepInferenceNoDays(
			@NotNull double[][] probabilities, @NotNull double[] aPrioriProbabilities, @NotNull AlgorithmConfiguration
			config) {
		switch(config.getBayesInferenceVariant()) {
			case BAYES_INFERENCE_VARIANT_1:
				return new BayesInferenceNoDays(aPrioriProbabilities, probabilities, config);
			case BAYES_INFERENCE_VARIANT_2:
				return new StepByStepBayesInference(aPrioriProbabilities, probabilities, config);
		}
		throw new IllegalArgumentException("Invalid configuration: " + config);
	}

	public static QuestionDeciderNoDays getQuestionDecider(@NotNull InferenceNoDays.StepByStepInferenceNoDays
																   inference,
														   @NotNull AlgorithmConfiguration configuration) {
		return new QuestionDeciderNoDays(inference, configuration);
	}
}
