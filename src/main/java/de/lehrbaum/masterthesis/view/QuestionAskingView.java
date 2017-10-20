package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.data.DataProvider;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestionAskingView implements Initializable {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionAskingView.class.getCanonicalName());

	@FXML private BarChart<String, Number> diseaseChart;
	@FXML private Label questionLabel;

	private int questionIndex;
	private InferenceNoDays inferenceNoDays;
	private QuestionAnsweredListener listener;

	private QuestionAskingView(int questionIndex, InferenceNoDays inferenceNoDays, QuestionAnsweredListener listener) {
		this.questionIndex = questionIndex;
		this.listener = listener;
		this.inferenceNoDays = inferenceNoDays;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//localize
		questionLabel.setText(inferenceNoDays.getTextGenerator().getQuestionText(questionIndex));

		double[] diseaseProbs = inferenceNoDays.getDiseaseProbabilities();
		for(int disease = 0; disease < diseaseProbs.length; disease++) {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(inferenceNoDays.getDiseaseName(disease));
			series.getData().add(new XYChart.Data<>("", diseaseProbs[disease]));
			diseaseChart.getData().add(series);
		}
	}

	@FXML
	private void yesPressed(ActionEvent actionEvent) {
		listener.questionAnswered(questionIndex, Answer.PRESENT);
	}

	@FXML
	private void noPressed(ActionEvent actionEvent) {
		listener.questionAnswered(questionIndex, Answer.ABSENT);
	}

	@FXML
	private void unknownPressed(ActionEvent actionEvent) {
		listener.questionAnswered(questionIndex, Answer.NOT_ANSWERED);
	}

	@FXML
	private void backPressed(ActionEvent actionEvent) {
		listener.navigateBackwards();
	}

	public interface QuestionAnsweredListener {
		void questionAnswered(int symptomIndex, Answer answer);

		void navigateBackwards();
	}

	static Node createQuestionAskingView(int symptomIndex, InferenceNoDays inferenceNoDays,
										 QuestionAnsweredListener listener) {
		try {
			URL resource = QuestionAskingView.class.getResource("/question_asking_view.fxml");
			FXMLLoader loader = new FXMLLoader(resource);
			loader.setControllerFactory(param -> new QuestionAskingView(symptomIndex, inferenceNoDays, listener));
			loader.load();
			return loader.getRoot();
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Problem loading view", e);
			throw new RuntimeException(e);
		}
	}
}
