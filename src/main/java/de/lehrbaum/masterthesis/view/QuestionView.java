package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.data.NoDaysDefaultData;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmFactory;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.QuestionDeciderNoDays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;
import java.util.stream.DoubleStream;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.aPriorProbabilities;
import static de.lehrbaum.masterthesis.view.QuestionAskingView.QuestionAnsweredListener;
import static de.lehrbaum.masterthesis.view.QuestionAskingView.createQuestionAskingView;

/**
 * The question view consists of multiple changing views leading the user through the questioning process.
 */
class QuestionView extends BorderPane implements MainWindow.LoggableViewState, QuestionInitializationView
		.QuestionInitializedListener, QuestionAnsweredListener {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionView.class.getCanonicalName());

	private ObservableList<Double> aPrioriDiseaseProbs;
	private InferenceNoDays.StepByStepInferenceNoDays inferenceNoDays;
	private QuestionDeciderNoDays questionDeciderNoDays;
	private AlgorithmConfiguration currentConfiguration;

	QuestionView() {
		Double[] boxedArray = DoubleStream.of(aPriorProbabilities).boxed().toArray(Double[]::new);
		aPrioriDiseaseProbs = FXCollections.observableArrayList(boxedArray);
		setCenter(QuestionInitializationView.createQuestionInitializationView(aPrioriDiseaseProbs, this));
	}

	@Override
	public void appendViewState(StringBuilder sb) {
		sb.append("Question view showing. \n");
		sb.append(inferenceNoDays);
		//TODO: implement
	}

	@Override
	public void startPressed(@NotNull AlgorithmConfiguration configuration) {
		double[] aPrioriDiseaseProbsArr = aPrioriDiseaseProbs.stream().mapToDouble(Double::doubleValue).toArray();
		inferenceNoDays = AlgorithmFactory.getStepByStepInferenceNoDays(NoDaysDefaultData.probabilities,
				aPrioriDiseaseProbsArr, configuration);
		questionDeciderNoDays = AlgorithmFactory.getQuestionDecider(inferenceNoDays, configuration);
		int symptomToAsk = questionDeciderNoDays.recommendedSymptomToAsk();
		assert symptomToAsk != - 1;//should never happen, but should be caught better later
		setCenter(createQuestionAskingView(symptomToAsk, inferenceNoDays, this));
	}

	@Override
	public void questionAnswered(int symptomIndex, InferenceNoDays.SYMPTOM_STATE answer) {
		inferenceNoDays.symptomAnswered(symptomIndex, answer);
		int symptomToAsk = questionDeciderNoDays.recommendedSymptomToAsk();
		if(symptomToAsk == - 1) {
			finish();
			return;
		}
		setCenter(createQuestionAskingView(symptomToAsk, inferenceNoDays, this));
	}

	private void finish() {
		double[] probs = inferenceNoDays.getDiseaseProbabilities();
		int mostLikely = 0;
		double probMostLikely = probs[0];
		for(int i = 1; i < probs.length; i++) {
			if(probs[i] > probMostLikely) {
				mostLikely = i;
				probMostLikely = probs[i];
			}
		}
		logger.info("Answering " + mostLikely + " based on:\n" + inferenceNoDays);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Fragen beendet");
		alert.setHeaderText(null);
		alert.setContentText("Sie haben wahrscheinlich " + NoDaysDefaultData.diseases[mostLikely] + ".");
		alert.showAndWait();

		setCenter(QuestionInitializationView.createQuestionInitializationView(aPrioriDiseaseProbs, this));
	}

	@Override
	public void navigateBackwards() {
		setCenter(QuestionInitializationView.createQuestionInitializationView(aPrioriDiseaseProbs, this));
	}
}
