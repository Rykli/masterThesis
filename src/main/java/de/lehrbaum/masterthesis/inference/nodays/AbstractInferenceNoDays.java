package de.lehrbaum.masterthesis.inference.nodays;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.data.DataProviderNoDays;
import de.lehrbaum.masterthesis.inference.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inference.InferenceNoDays;
import de.lehrbaum.masterthesis.inference.TextGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * General inference for no days.
 * Questions are index from symptom 0 to symptom n and then continue with aPrioriQuestion 0 to aPrioriQuestion m
 * So there are n + m questions.
 */
public abstract class AbstractInferenceNoDays implements InferenceNoDays {

	protected final DataProviderNoDays dataProvider;
	/**
	 * The current probability for each disease.
	 */
	protected double[] currentProbabilities;

	protected Answer[] symptomAnswers;

	protected Answer[] aPrioriQuestionAnswers;

	@NotNull
	protected AlgorithmConfiguration configuration;

	public AbstractInferenceNoDays(@NotNull DataProviderNoDays dataProvider, @NotNull AlgorithmConfiguration configuration) {
		this.dataProvider = dataProvider;
		this.configuration = configuration;
		symptomAnswers = new Answer[dataProvider.getAmountSymptoms()];
		Arrays.fill(symptomAnswers, Answer.NOT_ANSWERED);
		aPrioriQuestionAnswers = new Answer[dataProvider.getAmountAPrioriQuestions()];
		Arrays.fill(aPrioriQuestionAnswers, Answer.NOT_ANSWERED);
		currentProbabilities = dataProvider.getAPrioriProbabilities(aPrioriQuestionAnswers);
	}

	protected double[] getAPrioriProbabilities() {
		return dataProvider.getAPrioriProbabilities(aPrioriQuestionAnswers);
	}

	@Override
	public double[] getDiseaseProbabilities() {
		return currentProbabilities;
	}

	public boolean wasQuestionAnswered(int question) {
		if(isSymptom(question))
			return symptomAnswers[question] != Answer.NOT_ANSWERED;
		else
			return aPrioriQuestionAnswers[question - symptomAnswers.length] != Answer.NOT_ANSWERED;
	}

	@Override
	public int getAmountQuestions() {
		return aPrioriQuestionAnswers.length + symptomAnswers.length;
	}

	@Override
	public EnumSet<Answer> possibleAnswersForQuestion(int question) {
		if(isSymptom(question))
			return EnumSet.of(Answer.ABSENT, Answer.PRESENT);
		else
			return dataProvider.getAPrioriAnswerPossibilities(question - symptomAnswers.length);
	}

	protected int getAmountDiseases() {
		return currentProbabilities.length;
	}

	protected DataProviderNoDays getDataProvider() {
		return dataProvider;
	}

	@Override
	public TextGenerator getTextGenerator() {
		return new TextGenerator() {//Localize
			@Override
			public String getQuestionText(int question) {
				if(isSymptom(question)) {
					return "Haben sie " + dataProvider.getSymptomName(question) + "?";
				} else {
					return "Sind sie " + dataProvider.getAPrioriQuestionText(question - symptomAnswers.length) + "?";
				}
			}

			@Override
			public String getFinalText() {
				int mostLikely = 0;
				double probMostLikely = currentProbabilities[0];
				for(int i = 1; i < currentProbabilities.length; i++) {
					if(currentProbabilities[i] > probMostLikely) {
						mostLikely = i;
						probMostLikely = currentProbabilities[i];
					}
				}
				return "Sie haben wahrscheinlich " + dataProvider.getDiseaseName(mostLikely) + ".";
			}
		};
	}

	@Override
	public String getDiseaseName(int disease) {
		return dataProvider.getDiseaseName(disease);
	}

	protected boolean isSymptom(int question) {
		return question < symptomAnswers.length;
	}

	@Override
	public String toString() {
		return "The symptom states: " + Arrays.toString(symptomAnswers) +
				"\nThe aPriori question state: " + Arrays.toString(aPrioriQuestionAnswers) +
				"\nThe current probabilities: " + Arrays.toString(currentProbabilities);
	}
}
