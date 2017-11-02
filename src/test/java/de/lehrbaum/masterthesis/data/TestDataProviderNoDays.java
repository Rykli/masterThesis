package de.lehrbaum.masterthesis.data;

import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static de.lehrbaum.masterthesis.data.Answer.NOT_ANSWERED;
import static de.lehrbaum.masterthesis.data.Answer.YES;
import static org.junit.Assert.*;

public class TestDataProviderNoDays {
	@Test
	public void doesAPrioriIncrease() throws ExcelLoadException {
		DataProviderNoDays dataProvider = DataProviderNoDaysImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, Answer.NOT_ANSWERED);
		double probOtitis1 = dataProvider.getAPrioriProbabilities(answers)[0];
		answers[3] = YES;//female
		double probOtitis2 = dataProvider.getAPrioriProbabilities(answers)[0];
		assertTrue("Probability does not increase.", probOtitis2 > probOtitis1);
	}

	@Test
	public void isAmountDiseasesConsistent() throws ExcelLoadException {
		DataProviderNoDays dataProvider = DataProviderNoDaysImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, Answer.NOT_ANSWERED);
		double[] aPriorProbs = dataProvider.getAPrioriProbabilities(answers);
		assertEquals("Amount of diseases differs", dataProvider.getAmountDiseases(), aPriorProbs.length);
	}

	@Test
	public void nothingSelectedGivesBaseProbability() throws ExcelLoadException {
		DataProviderNoDaysImplementation dataProvider = DataProviderNoDaysImplementation.getInstance();
		Answer[] answers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(answers, NOT_ANSWERED);
		double[] baseCase = dataProvider.getBaseProbabilities();
		double[] calculatedCase = dataProvider.getAPrioriProbabilities(answers);
		for(int i = 0; i < baseCase.length; i++) {
			assertEquals("The values differ", baseCase[i], calculatedCase[i], 0.0000001);
		}
	}

	@Test
	public void diseseaseNames() throws ExcelLoadException {
		DataProviderImplementation dataProvider = DataProviderImplementation.getInstance();
		String[] diseases = dataProvider.getDiseaseNames();
		assertEquals("The amount of diseases is inconsistent", diseases.length, dataProvider.getAmountDiseases());
		assertEquals("The amount of diseases is wrong" , 9, diseases.length);
		String[] expected = new String []{"Otitis externa","AOM","CMD","TVS/PE","Hörsturz","Cerumen obturans","Zoster oticus","GG-Verletzung","COMM"};
		assertTrue("The actual diseases are : " + Arrays.toString(diseases), Arrays.equals(diseases, expected));
	}

	@Test
	public void diseaseSymptomProbabilities() throws ExcelLoadException {
		DataProviderImplementation dataProvider = DataProviderImplementation.getInstance();
		double[][][] symptomProbs = dataProvider.getSymptomProbabilities();
		assertEquals("Amount of diseseases inconsistent", dataProvider.getAmountDiseases(), symptomProbs.length);
		assertEquals("Amount of symptoms inconsistent", dataProvider.getAmountSymptoms(), symptomProbs[0][0].length);
	}
}
