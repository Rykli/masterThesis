package de.lehrbaum.masterthesis.inference;

import de.lehrbaum.masterthesis.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * In the program there are multiple points where different algorithms can be used. This class specifies what algorithms
 * to use.
 */
public class AlgorithmConfiguration {

	public enum QUESTION_ALGORITHM {
		MINIMIZE_EXPECTED_ENTROPY,
		MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
		static final QUESTION_ALGORITHM defaultValue = MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
	}

	public enum BAYES_INFERENCE_VARIANT {
		INFERENCE_BAYES_FORMULA,
		INFERENCE_BAYES_UPDATING;
		static final BAYES_INFERENCE_VARIANT defaultValue = INFERENCE_BAYES_FORMULA;
	}
	@Nullable private QUESTION_ALGORITHM questionAlgorithm;
	@Nullable private BAYES_INFERENCE_VARIANT bayesInferenceVariant;
	private boolean normalize = true;
	private double gainLimit = 0.01;

	public AlgorithmConfiguration(@Nullable QUESTION_ALGORITHM questionAlgorithm,
								  @Nullable BAYES_INFERENCE_VARIANT bayesInferenceVariant, boolean normalize,
								  double gainLimit) {
		setQuestionAlgorithm(questionAlgorithm);
		setBayesInferenceVariant(bayesInferenceVariant);
		setNormalize(normalize);
		setGainLimit(gainLimit);
	}

	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}

	public AlgorithmConfiguration() {

	}

	@Override
	public String toString() {
		return "Algorithm configuration. nodays inference variant: " + getInferenceVariant()
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

	public BAYES_INFERENCE_VARIANT getInferenceVariant() {
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

	/**
	 * Normalizes the array if needed, otherwise just returns it.
	 */
	public double[] normalizeIfNeeded(@NotNull double[] array) {
		if(normalize)
			MathUtils.normalize(array);
		return array;
	}
}
