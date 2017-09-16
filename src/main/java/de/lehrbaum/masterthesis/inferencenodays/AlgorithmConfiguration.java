package de.lehrbaum.masterthesis.inferencenodays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * In the program there are multiple points where different algorithms can be used. This class specifies what algorithms
 * to use.
 */
public class AlgorithmConfiguration {
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
	@Nullable private BAYES_SYMPTOMS_CALCULATION_VARIANT bayesSymptomsCalculationVariant;
	@Nullable private QUESTION_ALGORITHM questionAlgorithm;
	@Nullable private BAYES_INFERENCE_VARIANT bayesInferenceVariant;
	private boolean normalize = true;
	private double gainLimit = 0.01;

	public AlgorithmConfiguration(@Nullable BAYES_SYMPTOMS_CALCULATION_VARIANT bayesSymptomsCalculationVariant,
								  @Nullable QUESTION_ALGORITHM questionAlgorithm,
								  @Nullable BAYES_INFERENCE_VARIANT bayesInferenceVariant, boolean normalize,
								  double gainLimit) {
		setBayesSymptomsCalculationVariant(bayesSymptomsCalculationVariant);
		setQuestionAlgorithm(questionAlgorithm);
		setBayesInferenceVariant(bayesInferenceVariant);
		setNormalize(normalize);
		setGainLimit(gainLimit);
	}

	public @NotNull BAYES_SYMPTOMS_CALCULATION_VARIANT getBayesSymptomsCalculationVariant() {
		if(bayesSymptomsCalculationVariant == null)
			return BAYES_SYMPTOMS_CALCULATION_VARIANT.defaultValue;
		return bayesSymptomsCalculationVariant;
	}

	public void setBayesSymptomsCalculationVariant(@Nullable BAYES_SYMPTOMS_CALCULATION_VARIANT
														   bayesSymptomsCalculationVariant) {
		if(bayesSymptomsCalculationVariant == null)
			this.bayesSymptomsCalculationVariant = BAYES_SYMPTOMS_CALCULATION_VARIANT.defaultValue;
		this.bayesSymptomsCalculationVariant = bayesSymptomsCalculationVariant;
	}

	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}

	public AlgorithmConfiguration() {

	}

	@Override
	public String toString() {
		return "Algorithm configuration. Bayes symptom variant: " + getBayesSymptomsCalculationVariant()
				+ ", bayes inference variant: " + getBayesInferenceVariant()
				+ ", question algorithm: " + getQuestionAlgorithm()
				+ ", normalize: " + getNormalize();
	}

	public double getGainLimit() {
		return gainLimit;
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

	public void setGainLimit(double gainLimit) {
		if(gainLimit < 0) {
			this.gainLimit = 0;
			return;
		}
		this.gainLimit = gainLimit;
	}

	public boolean getNormalize() {
		return normalize;
	}
}
