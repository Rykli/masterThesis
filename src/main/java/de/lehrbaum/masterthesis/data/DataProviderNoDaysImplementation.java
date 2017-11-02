package de.lehrbaum.masterthesis.data;

import de.lehrbaum.masterthesis.exceptions.ExcelLoadException;
import de.lehrbaum.masterthesis.data.excelio.ExcelReader;

import java.util.EnumSet;
import java.util.Map;

import static de.lehrbaum.masterthesis.data.Answer.*;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.*;

public class DataProviderNoDaysImplementation implements DataProviderNoDays {

	private static DataProviderNoDaysImplementation instance;

	public static DataProviderNoDaysImplementation getInstance() throws ExcelLoadException {
	    if (instance == null)
	        instance = new DataProviderNoDaysImplementation();
	    return instance;
	}

	private String[] aPrioriFactorNames;
	/**[factor][disease]*/
	private double[][] aPrioriFactorMultipliers;
	/**[age][disease]*/
	private double[][] aPrioriProbabilities;

	private DataProviderNoDaysImplementation() throws ExcelLoadException {
		resetInformation();
	}

	@Override
	public int getAmountSymptoms() {
		return symptoms.length;
	}

	@Override
	public String[] getSymptomNames() {
		return symptoms;
	}

	@Override
	public double[][] getSymptomProbabilities() {
		return probabilities;
	}

	@Override
	public int getAmountDiseases() {
		return diseases.length;
	}

	@Override
	public String[] getDiseaseNames() {
		return diseases;
	}

	@Override
	public int getAmountAPrioriQuestions() {
		return aPrioriFactorNames.length + 1;//age is another factor
	}

	@Override
	public String getAPrioriQuestionText(int aPrioriQuestion) {
		if(aPrioriQuestion == 0)
			return "Alter";
		return aPrioriFactorNames[aPrioriQuestion - 1];
	}

	@Override
	public EnumSet<Answer> getAPrioriAnswerPossibilities(int aPrioriQuestion) {
		if(aPrioriQuestion == 0)
			return EnumSet.of(AGE_0_6, AGE_7_12, AGE_13_40, AGE_41_65, AGE_65_Inf);
		return EnumSet.of(NO, YES);
	}

	@Override
	public double[] getAPrioriProbabilities(Answer[] patientInformation) {
		int ageIndex = getAgeIndex(patientInformation[0]);
		double[] aPrioriProbs = aPrioriProbabilities[ageIndex].clone();
		for(int factor = 0; factor < aPrioriFactorMultipliers.length; factor++) {
			if(patientInformation[factor + 1] == YES) {
				for(int disease = 0; disease < aPrioriProbs.length; disease++)
					aPrioriProbs[disease] *= aPrioriFactorMultipliers[factor][disease];
			}
		}
		//have to add normal disease option
		double [] result = new double[aPrioriProbs.length + 1];
		System.arraycopy(aPrioriProbs, 0, result, 0, aPrioriProbs.length);
		result[aPrioriProbs.length] = 0.01;
		return result;
	}

	private int getAgeIndex(Answer age) {
		int ageIndex;
		switch(age) {
			case AGE_0_6:
				ageIndex = 0;
				break;
			case AGE_7_12:
				ageIndex = 1;
				break;
			case AGE_41_65:
				ageIndex = 3;
				break;
			case AGE_65_Inf:
				ageIndex = 4;
				break;
			case AGE_13_40:
			default:
				ageIndex = 2;
		}
		return ageIndex;
	}

	@Override
	public void resetInformation() throws ExcelLoadException {
		ExcelReader excelReader = new ExcelReader();
		readAPrioriFactors(excelReader);
		aPrioriProbabilities = excelReader.getAPrioriBasedOnAge();
		excelReader.close();
	}

	private void readAPrioriFactors(ExcelReader excelReader) throws ExcelLoadException {
		Map<String, double[]> aPrioriFactors = excelReader.getFactors();
		aPrioriFactorNames = new String[aPrioriFactors.size()];
		aPrioriFactorMultipliers = new double[aPrioriFactors.size()][];
		int index = 0;
		for(Map.Entry<String, double[]> entry : aPrioriFactors.entrySet()) {
			aPrioriFactorNames[index] = entry.getKey();
			aPrioriFactorMultipliers[index] = entry.getValue();
			index++;
		}
	}

	double[] getBaseProbabilities() {
		return aPrioriProbabilities[getAgeIndex(NOT_ANSWERED)];
	}
}
