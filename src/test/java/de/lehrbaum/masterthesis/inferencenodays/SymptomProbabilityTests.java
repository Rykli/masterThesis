package de.lehrbaum.masterthesis.inferencenodays;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Arrays;
import java.util.EnumSet;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.*;
import static de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays.VARIANTS
		.SYMPTOMS_CALCULATION_VARIANT_1;
import static de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays.VARIANTS
		.SYMPTOMS_CALCULATION_VARIANT_2;

public class SymptomProbabilityTests {

	//@Test Test result: Variant 2 is stupid. It calculates less probability the more symptoms there are.
	/**
	 * Tests whether the symptom calculation variants differ in the calculated order.
	 */
	public void testSymptomVariants() {
		// try until a difference is found or it has been tried 1000 times.
		for(int i = 0; i < 100; i++){
			SYMPTOM_STATE[] symptomStates = TestUtils.getRandomSymptomState(symptoms.length);
			CompleteInferenceNoDays inference1 = new BayesInferenceNoDays(probabilities,
					aPriorProbabilities, EnumSet.of(SYMPTOMS_CALCULATION_VARIANT_1));
			inference1.calculateGivenAllAnswers(symptomStates);
			CompleteInferenceNoDays inference2 = new BayesInferenceNoDays(probabilities,
					aPriorProbabilities, EnumSet.of(SYMPTOMS_CALCULATION_VARIANT_2));
			inference2.calculateGivenAllAnswers(symptomStates);

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

			if(Arrays.equals(symptoms1, symptoms2))
				continue;

			StringBuilder sb = new StringBuilder("Difference in symptoms order (try ");
			sb.append(i);
			sb.append(") :\nSymptoms:");
			sb.append(Arrays.toString(symptomStates));
			for(int j = 0; j < symptoms.length; j++)
				sb.append(String.format("\n%d\t%.2f\t%d\t%.2f", symptoms1[j].index, symptoms1[j].probability,
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
			return obj instanceof ProbabilityTuple && index == ((ProbabilityTuple) obj).index;
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
