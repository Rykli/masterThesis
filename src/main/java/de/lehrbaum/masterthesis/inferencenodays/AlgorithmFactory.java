package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.data.DataProvider;
import de.lehrbaum.masterthesis.data.DataProviderImplementation;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import org.jetbrains.annotations.NotNull;

import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.*;

public abstract class AlgorithmFactory {
	public static @NotNull StepByStepInferenceNoDays getStepByStepInferenceNoDays(
			@NotNull AlgorithmConfiguration config) throws UserReadableException {
		switch(config.getInferenceVariant()) {
			case INFERENCE_BAYES_UPDATING:
				//This variant is not yet implemented
			case INFERENCE_BAYES_FORMULA:
				return new BayesInferenceNoDays(getDataProvider(config), config);
		}
		throw new IllegalArgumentException("Invalid configuration: " + config);
	}

	public static CompleteInferenceNoDays getCompleteInferenceNoDays(
			@NotNull AlgorithmConfiguration config) throws UserReadableException {
		return new BayesInferenceNoDays(getDataProvider(config), config);
	}

	public static QuestionDeciderNoDays getQuestionDecider(@NotNull InferenceNoDays.StepByStepInferenceNoDays
																   inference,
														   @NotNull AlgorithmConfiguration configuration) {
		return new QuestionDeciderNoDays(inference, configuration, configuration.getGainLimit());
	}

	public static DataProvider getDataProvider(@NotNull AlgorithmConfiguration config) throws UserReadableException {
		return DataProviderImplementation.getInstance();
	}
}
