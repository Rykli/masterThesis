package de.lehrbaum.masterthesis.inference;

import de.lehrbaum.masterthesis.TestUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;

@SuppressWarnings("unused")//remains in case the test should be needed again.
public class SymptomProbabilityTests {
	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
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
