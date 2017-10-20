package de.lehrbaum.masterthesis.data;

import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static de.lehrbaum.masterthesis.data.Answer.NOT_ANSWERED;
import static de.lehrbaum.masterthesis.data.Answer.YES;
import static org.junit.Assert.*;

public class TestDataProvider {
	@Test
	public void doesAPrioriIncrease() throws ExcelLoadException {
		DataProvider dataProvider = DataProviderImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, Answer.NOT_ANSWERED);
		double probOtitis1 = dataProvider.getAPrioriProbabilities(answers)[0];
		answers[3] = YES;//female
		double probOtitis2 = dataProvider.getAPrioriProbabilities(answers)[0];
		assertTrue("Probability does not increase.", probOtitis2 > probOtitis1);
	}

	@Test
	public void isAmountDiseasesConsistent() throws ExcelLoadException {
		DataProvider dataProvider = DataProviderImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, Answer.NOT_ANSWERED);
		double[] aPriorProbs = dataProvider.getAPrioriProbabilities(answers);
		assertEquals("Amount of diseases differs", dataProvider.getAmountDiseases(), aPriorProbs.length);
	}

	@Test
	public void nothingSelectedGivesBaseProbability() throws ExcelLoadException {
		DataProviderImplementation dataProvider = DataProviderImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, NOT_ANSWERED);
		double[] baseCase = dataProvider.getBaseProbabilities();
		double[] calculatedCase = dataProvider.getAPrioriProbabilities(answers);
		for(int i = 0; i < baseCase.length; i++) {
			assertEquals("The values differ", baseCase[i], calculatedCase[i], 0.0000001);
		}
	}
}
