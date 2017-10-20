package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.data.Answer.*;

/**
 * A controller for the starting view. It is an inner class, because it is strongly connected to the {@link
 * QuestionView}. If the file ever is to long, this class is a good candidate to be moved.
 */
public class QuestionInitializationView implements Initializable {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionInitializationView.class.getCanonicalName());

	/** This enum should be moved to another class as soon as it get's used. */
	public enum Gender {
		MALE("Männlich", Answer.NO),
		FEMALE("Weiblich", Answer.YES),//localize
		OTHER("Unbekannt", Answer.NOT_ANSWERED);
		private String description;
		private Answer answer;

		Gender(String description, Answer answer) {
			this.description = description;
			this.answer = answer;
		}

		Answer getAnswer() {
			return answer;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	@FXML private TextField questionAbortGain;
	@FXML private Slider ageSlider;
	@FXML private ChoiceBox<Gender> genderBox;
	@FXML private TextArea initialProblemText;
	@FXML private ChoiceBox<AlgorithmConfiguration.QUESTION_ALGORITHM> questionAlgorithmChoice;

	private static final Answer[] ageStagesAnswers = new Answer[] {AGE_0_6, AGE_7_12, AGE_13_40, AGE_41_65, AGE_65_Inf};
	private static final String[] ageStages = new String[] {"0-6", "7-12", "13-40", "41-65", "65+"};
	private static final int AgeQuestionIndex = 21;
	private static final int GenderQuestionIndex = AgeQuestionIndex + 3;
	private QuestionInitializedListener listener;

	private QuestionInitializationView(@NotNull QuestionInitializedListener listener) {
		this.listener = listener;
	}

	//region set up UI
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AlgorithmConfiguration defaultConfig = new AlgorithmConfiguration();
		initLeftSide(defaultConfig);
		initRightSide(defaultConfig);
	}

	private void initLeftSide(AlgorithmConfiguration config) {
		genderBox.setItems(FXCollections.observableArrayList(Gender.values()));
		genderBox.getSelectionModel().select(Gender.OTHER);
		ageSlider.setMax(4);
		ageSlider.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double d) {
				return ageStages[d.intValue()];
			}

			@Override
			public Double fromString(String string) {
				return null;//not needed
			}
		});
	}

	private void initRightSide(AlgorithmConfiguration config) {
		questionAlgorithmChoice.setItems(
				FXCollections.observableArrayList(AlgorithmConfiguration.QUESTION_ALGORITHM.values()));
		questionAlgorithmChoice.getSelectionModel().select(config.getQuestionAlgorithm());
		questionAbortGain.setText(Double.toString(config.getGainLimit()));
	}

	//endregion

	@FXML
	private void startPressed() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setQuestionAlgorithm(questionAlgorithmChoice.getValue());
		double gainLimit;
		try {
			gainLimit = Double.parseDouble(questionAbortGain.getText());
		} catch(NumberFormatException e) {
			alertParseError(questionAbortGain.getText() + " ist nicht verwendbar");
			return;
		}
		config.setGainLimit(gainLimit);
		Map<Integer, Answer> initialValues = new HashMap<>();
		initialValues.put(AgeQuestionIndex, ageStagesAnswers[(int) ageSlider.getValue()]);
		initialValues.put(GenderQuestionIndex, genderBox.getValue().getAnswer());
		listener.startPressed(config, initialValues);
	}

	private void alertParseError(String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Konnte Zahlenwert nicht konvertieren.");
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public interface QuestionInitializedListener {
		void startPressed(@NotNull AlgorithmConfiguration configuration, Map<Integer, Answer> initialAnswers);
	}

	static Node createQuestionInitializationView(@NotNull QuestionInitializedListener listener) {
		try {
			URL resource = QuestionInitializationView.class.getResource("/question_initialization_view.fxml");
			FXMLLoader loader = new FXMLLoader(resource);
			loader.setControllerFactory(param -> new QuestionInitializationView(listener));
			loader.load();
			return loader.getRoot();
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Problem loading view", e);
			throw new RuntimeException(e);
		}
	}
}
