package de.lehrbaum.masterthesis.inferencenodays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * In the program there are multiple points where different algorithms can be used. This class specifies what algorithms
 * to use.
 */
public class AlgorithmConfiguration {
	@Nullable private BAYES_SYMPTOMS_CALCULATION_VARIANT bayesSymptomsCalculationVariant;
	@Nullable private QUESTION_ALGORITHM questionAlgorithm;
	@Nullable private BAYES_INFERENCE_VARIANT bayesInferenceVariant;
	private boolean normalize = true;

	public @NotNull BAYES_SYMPTOMS_CALCULATION_VARIANT getBayesSymptomsCalculationVariant() {
		if(bayesSymptomsCalculationVariant == null)
			return BAYES_SYMPTOMS_CALCULATION_VARIANT.defaultValue;
		return bayesSymptomsCalculationVariant;
	}

	public void setBayesSymptomsCalculationVariant(@Nullable BAYES_SYMPTOMS_CALCULATION_VARIANT
														   bayesSymptomsCalculationVariant) {
		this.bayesSymptomsCalculationVariant = bayesSymptomsCalculationVariant;
	}

	public @NotNull QUESTION_ALGORITHM getQuestionAlgorithm() {
		if(questionAlgorithm == null)
			return QUESTION_ALGORITHM.defaultValue;
		return questionAlgorithm;
	}

	public void setQuestionAlgorithm(@Nullable QUESTION_ALGORITHM questionAlgorithm) {
		this.questionAlgorithm = questionAlgorithm;
	}

	public BAYES_INFERENCE_VARIANT getBayesInferenceVariant() {
		if(bayesInferenceVariant == null)
			return BAYES_INFERENCE_VARIANT.defaultValue;
		return bayesInferenceVariant;
	}

	public void setBayesInferenceVariant(@Nullable BAYES_INFERENCE_VARIANT bayesInferenceVariant) {
		this.bayesInferenceVariant = bayesInferenceVariant;
	}

	public boolean getNormalize() {
		return normalize;
	}

	public enum BAYES_SYMPTOMS_CALCULATION_VARIANT {
		BAYES_SYMPTOMS_CALCULATION_VARIANT_1,
		/**
		 * This variant is stupid it calculates lower numbers the more symptoms there are.
		 */
		BAYES_SYMPTOMS_CALCULATION_VARIANT_2;
		static final BAYES_SYMPTOMS_CALCULATION_VARIANT defaultValue = BAYES_SYMPTOMS_CALCULATION_VARIANT_1;
	}

	public enum QUESTION_ALGORITHM {
		MINIMIZE_EXPECTED_ENTROPY,
		MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
		static final QUESTION_ALGORITHM defaultValue = MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
	}

	public enum BAYES_INFERENCE_VARIANT {
		BAYES_INFERENCE_VARIANT_1,
		BAYES_INFERENCE_VARIANT_2;
		static final BAYES_INFERENCE_VARIANT defaultValue = BAYES_INFERENCE_VARIANT_1;
	}
}
