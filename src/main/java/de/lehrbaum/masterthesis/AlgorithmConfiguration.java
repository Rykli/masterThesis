package de.lehrbaum.masterthesis;

/**
 * In the program there are multiple points where different algorithms can be used. This class specifies what algorithms
 * to use.
 */
public class AlgorithmConfiguration {
	public enum BAYES_VARIANTS {
		SYMPTOMS_CALCULATION_VARIANT_1,
		/**
		 * This variant is stupid it calculates lower numbers the more symptoms there are.
		 */
		SYMPTOMS_CALCULATION_VARIANT_2;
		public static final BAYES_VARIANTS defaultValue = SYMPTOMS_CALCULATION_VARIANT_1;
	}

	public enum QUESTION_ALGORITHM {
		MINIMIZE_EXPECTED_ENTROPY,
		MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
		public static final QUESTION_ALGORITHM defaultValue = MAXIMIZE_EXPECTED_PROBABILITY_DIFFERENCE;
	}
}
