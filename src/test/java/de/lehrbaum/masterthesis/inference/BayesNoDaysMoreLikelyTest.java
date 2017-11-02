package de.lehrbaum.masterthesis.inference;

import de.lehrbaum.masterthesis.TestUtils;
import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.lehrbaum.masterthesis.data.Answer.*;
import static org.junit.Assert.assertTrue;

/**
 * This class tests cases of symptoms where the answer should be obvious based on the data in
 * {@link de.lehrbaum.masterthesis.data.NoDaysDefaultData}. These cases are not generated by a expert on the matter.
 */
public class BayesNoDaysMoreLikelyTest {
	private AlgorithmConfiguration configuration;

	@BeforeClass
	public static void setClassUp() throws Exception {
		TestUtils.initializeTestLogging();
	}

	@Before
	public void setUp() throws Exception {
		configuration = new AlgorithmConfiguration();
	}

	@Test
	public void testCases() throws UserReadableException {
		for(ExampleCase exampleCase : exampleCases) {
			testIfCaseHoldsCompleteBayesInference(exampleCase);
		}
	}

	private void testIfCaseHoldsCompleteBayesInference(ExampleCase exCase) throws UserReadableException {

		InferenceNoDays.CompleteInferenceNoDays inference = AlgorithmFactory.getCompleteInferenceNoDays(configuration);
		inference.symptomsAnswered(exCase.symptomStates);
		double probLikelyDisease = inference.getDiseaseProbabilities()[exCase.likelyDisease];
		double probUnlikelyDisease = inference.getDiseaseProbabilities()[exCase.unlikelyDisease];
		String message = String.format("Probability for disease %d (%.2f) was not bigger than " +
				"probability for disease %d (%.2f)", exCase.likelyDisease, probLikelyDisease, exCase.unlikelyDisease,
				probUnlikelyDisease);
		assertTrue(message, probLikelyDisease > probUnlikelyDisease);
	}

	private ExampleCase[] exampleCases = new ExampleCase[] {
		new ExampleCase(0, 5, /*90 vs 10*/PRESENT, /*30 vs 90*/ABSENT, /*20 vs 50*/ABSENT,
				/*30 vs 10*/NOT_ANSWERED, /*90 vs 10*/PRESENT, /*0 vs 0*/ABSENT, /*10 vs 10*/ABSENT, /*10 vs 0*/ ABSENT,
				/*20 vs 10*/NOT_ANSWERED, /*20 vs 0*/NOT_ANSWERED, /* 60 vs 20*/PRESENT, /*70 vs 10*/PRESENT,
				/*70 vs 0*/ PRESENT, 	  /*50 vs 60*/NOT_ANSWERED, /*20 vs 40*/ABSENT, /*0 vs 20*/ABSENT, /*0 vs 0*/ABSENT,
				/*30 vs 20*/NOT_ANSWERED, /*10 vs 0*/NOT_ANSWERED, /*0 vs 0*/ABSENT, /*10 vs 10*/ABSENT )
	};

	/**
	 * This small class contains information about a set of symptoms where one disease should be clearely more likely
	 * than another.
	 */
	private class ExampleCase {
		Answer[] symptomStates;
		int likelyDisease;
		int unlikelyDisease;

		ExampleCase(int likelyDisease, int unlikelyDisease, Answer... symptomStates) {
			this.symptomStates = symptomStates;
			this.likelyDisease = likelyDisease;
			this.unlikelyDisease = unlikelyDisease;
		}
	}
}