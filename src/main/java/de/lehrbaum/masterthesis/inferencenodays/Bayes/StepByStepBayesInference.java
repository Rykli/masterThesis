package de.lehrbaum.masterthesis.inferencenodays.Bayes;

import de.lehrbaum.masterthesis.inferencenodays.AbstractInferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import org.jetbrains.annotations.NotNull;

public class StepByStepBayesInference extends AbstractInferenceNoDays implements InferenceNoDays
		.StepByStepInferenceNoDays {
	public StepByStepBayesInference(double[] aPrioriProbabilities, double[][] probabilities,
									@NotNull AlgorithmConfiguration configuration) {
		super(aPrioriProbabilities, probabilities, configuration);
	}

	@Override
	public double[] probabilityOfSymptom(int symptom) {
		return new double[0];
	}

	@Override
	public void symptomAnswered(int symptom, SYMPTOM_STATE state) {

	}

	@Override
	public double[] simulateSymptomAnswered(int symptom, boolean has) {
		return new double[0];
	}
}
