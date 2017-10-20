package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmFactory;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.QuestionDeciderNoDays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

import static de.lehrbaum.masterthesis.view.QuestionAskingView.QuestionAnsweredListener;
import static de.lehrbaum.masterthesis.view.QuestionAskingView.createQuestionAskingView;

/**
 * The question view consists of multiple changing views leading the user through the questioning process.
 */
class QuestionView extends BorderPane implements MainWindow.LoggableViewState, QuestionInitializationView
		.QuestionInitializedListener, QuestionAnsweredListener {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionView.class.getCanonicalName());

	private InferenceNoDays.StepByStepInferenceNoDays inferenceNoDays;
	private QuestionDeciderNoDays questionDeciderNoDays;
	private AlgorithmConfiguration currentConfiguration;

	QuestionView() {
		setCenter(QuestionInitializationView.createQuestionInitializationView(this));
	}

	@Override
	public void appendViewState(StringBuilder sb) {
		sb.append("Question view showing. \n");
		sb.append(inferenceNoDays);
	}

	@Override
	public void reloadData() {
		if(inferenceNoDays == null)
			return;//still in initialize view
		inferenceNoDays.recalculateProbabilities();
		int symptomToAsk = questionDeciderNoDays.recommendedSymptomToAsk();
		if(symptomToAsk == - 1) {
			finish();
			return;
		}
		setCenter(createQuestionAskingView(symptomToAsk, inferenceNoDays, this));
	}

	@Override
	public void startPressed(@NotNull AlgorithmConfiguration configuration, Map<Integer, Answer> initialValues) {
		try {
			inferenceNoDays = AlgorithmFactory.getStepByStepInferenceNoDays(configuration);
		} catch(UserReadableException e) {
			ViewUtils.showErrorMessage(e, getScene());
		}
		for(Map.Entry<Integer, Answer> e : initialValues.entrySet()) {
			inferenceNoDays.questionAnswered(e.getKey(), e.getValue());
		}
		questionDeciderNoDays = AlgorithmFactory.getQuestionDecider(inferenceNoDays, configuration);
		int symptomToAsk = questionDeciderNoDays.recommendedSymptomToAsk();
		assert symptomToAsk != - 1;//should never happen, but should be caught better later
		setCenter(createQuestionAskingView(symptomToAsk, inferenceNoDays, this));
	}

	@Override
	public void questionAnswered(int symptomIndex, Answer answer) {
		inferenceNoDays.questionAnswered(symptomIndex, answer);
		int symptomToAsk = questionDeciderNoDays.recommendedSymptomToAsk();
		if(symptomToAsk == - 1) {
			finish();
			return;
		}
		setCenter(createQuestionAskingView(symptomToAsk, inferenceNoDays, this));
	}

	private void finish() {
		logger.info("Answering " + inferenceNoDays.getTextGenerator().getFinalText()
				+ " based on:\n" + inferenceNoDays);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Fragen beendet");
		alert.setHeaderText(null);
		alert.setContentText(inferenceNoDays.getTextGenerator().getFinalText());
		alert.showAndWait();

		setCenter(QuestionInitializationView.createQuestionInitializationView(this));
		inferenceNoDays = null;
	}

	@Override
	public void navigateBackwards() {
		setCenter(QuestionInitializationView.createQuestionInitializationView(this));
	}
}
