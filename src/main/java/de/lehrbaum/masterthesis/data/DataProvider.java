package de.lehrbaum.masterthesis.data;

import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;

import java.util.EnumSet;

public interface DataProvider {
	int getAmountSymptoms();

	String[] getSymptomNames();

	default String getSymptomName(int symptom) {
		return getSymptomNames()[symptom];
	}

	/**
	 * @return [disease][symptom]
	 */
	double[][] getSymptomProbabilities();

	default double[] getSymptomProbabilitiesGivenDisease(int disease) {
		return getSymptomProbabilities()[disease];
	}

	int getAmountDiseases();

	String[] getDiseaseNames();

	default String getDiseaseName(int disease) {
		return getDiseaseNames()[disease];
	}

	int getAmountAPrioriQuestions();

	String getAPrioriQuestionText(int aPrioriQuestion);

	EnumSet<Answer> getAPrioriAnswerPossibilities(int aPrioriQuestion);

	double[] getAPrioriProbabilities(Answer[] patientInformation);

	void resetInformation() throws ExcelLoadException;
}
