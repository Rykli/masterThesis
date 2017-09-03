package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Arrays;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.*;
import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.BAYES_SYMPTOMS_CALCULATION_VARIANT
		.BAYES_SYMPTOMS_CALCULATION_VARIANT_1;
import static de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration.BAYES_SYMPTOMS_CALCULATION_VARIANT
		.BAYES_SYMPTOMS_CALCULATION_VARIANT_2;
import static de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays.SYMPTOM_STATE;

@SuppressWarnings("unused")//remains in case the test should be needed again.
public class SymptomProbabilityTests {
	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
	}

	/**
	 * Tests whether the symptom calculation variants differ in the calculated order.
	 */
	//@Test Test result: Variant 2 is stupid. It calculates less probability the more symptoms there are.
	public void testSymptomVariants() {
		AlgorithmConfiguration config1 = new AlgorithmConfiguration();
		config1.setBayesSymptomsCalculationVariant(BAYES_SYMPTOMS_CALCULATION_VARIANT_1);
		AlgorithmConfiguration config2 = new AlgorithmConfiguration();
		config2.setBayesSymptomsCalculationVariant(BAYES_SYMPTOMS_CALCULATION_VARIANT_2);
		// try until a difference is found or it has been tried 1000 times.
		for(int i = 0; i < 100; i++){
			SYMPTOM_STATE[] symptomStates = TestUtils.getRandomSymptomState(symptoms.length);
			InferenceNoDays.CompleteInferenceNoDays inference1 = new BayesInferenceNoDays(aPriorProbabilities,
					probabilities,

					config1);
			inference1.symptomsAnswered(symptomStates);
			InferenceNoDays.CompleteInferenceNoDays inference2 = new BayesInferenceNoDays(aPriorProbabilities,
					probabilities,
					config2);
			inference2.symptomsAnswered(symptomStates);

			/*
			 * This test is more interested in the order of the symptoms, not the actual probabilities
			*/
			ProbabilityTuple[] symptoms1 = new ProbabilityTuple[symptoms.length];
			ProbabilityTuple[] symptoms2 = new ProbabilityTuple[symptoms.length];
			for(int symptom = 0; symptom < symptoms.length; symptom++) {
				symptoms1[symptom] = new ProbabilityTuple(symptom, inference1.probabilityOfSymptom(symptom)[0]);
				symptoms2[symptom] = new ProbabilityTuple(symptom, inference2.probabilityOfSymptom(symptom)[0]);
			}

			Arrays.sort(symptoms1);
			Arrays.sort(symptoms2);

			//uses the overridden equals that only compares the index not the probability
			if(Arrays.equals(symptoms1, symptoms2))
				continue;

			StringBuilder sb = new StringBuilder("Difference in symptoms order (try ");
			sb.append(i);
			sb.append(") :\nSymptoms:");
			sb.append(Arrays.toString(symptomStates));
			for(int j = 0; j < symptoms.length; j++)
				sb.append(String.format("\n%d\t%e\t%d\t%e", symptoms1[j].index, symptoms1[j].probability,
						symptoms2[j].index, symptoms2[j].probability));

			Assert.fail(sb.toString());
			return;
		}
	}

	private class ProbabilityTuple implements Comparable<ProbabilityTuple> {
		int index;
		double probability;

		ProbabilityTuple(int index, double probability) {
			this.index = index;
			this.probability = probability;
		}

		@Override
		public boolean equals(Object obj) {
			//This violates the contract of equals a bit, but it is only used in this file so it is ok.
			if(obj instanceof ProbabilityTuple) {
				if(index == ((ProbabilityTuple) obj).index)
					return true;
			}
			return false;
		}

		@Override
		public int compareTo(@NotNull SymptomProbabilityTests.ProbabilityTuple o) {
			if(probability < o.probability)
				return -1;
			else if(probability > o.probability)
				return 1;
			else
				return index - o.index;
		}
	}
}
