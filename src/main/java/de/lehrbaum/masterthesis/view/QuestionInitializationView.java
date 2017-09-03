package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.diseases;

/**
 * A controller for the starting view. It is an inner class, because it is strongly connected to the {@link
 * QuestionView}. If the file ever is to long, this class is a good candidate to be moved.
 */
//needs to be public for fxml
public class QuestionInitializationView implements Initializable {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionInitializationView.class.getCanonicalName());

	@FXML private Slider ageSlider;
	@FXML private ChoiceBox<Gender> genderBox;
	@FXML private TextArea initialProblemText;
	@FXML private ChoiceBox<AlgorithmConfiguration.QUESTION_ALGORITHM> questionAlgorithmChoice;
	@FXML private TableView<Integer> aPrioriTable;
	@FXML private TableColumn<Integer, String> nameColumn;
	@FXML private TableColumn<Integer, Double> aPrioriColumn;
	private String[] ageStages = new String[] {"0-10", "10-20", "20-30", "30-40", "40-50",
			"50-60", "60-70", "70-80", "80-90", "90+"};

	private final ObservableList<Double> aPrioriDiseaseProb;
	private AlgorithmConfiguration configuration;

	private QuestionInitializationView(ObservableList<Double> aPrioriDiseaseProb) {
		this.aPrioriDiseaseProb = aPrioriDiseaseProb;
		configuration = new AlgorithmConfiguration();
	}

	//region set up UI

	static Node createQuestionInitializationView(ObservableList<Double> aPrioriDiseaseProb) {
		try {
			URL resource = QuestionInitializationView.class.getResource("/question_initializer_view.fxml");
			FXMLLoader loader = new FXMLLoader(resource);
			loader.setControllerFactory(param -> new QuestionInitializationView(aPrioriDiseaseProb));
			loader.load();
			return loader.getRoot();
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Problem loading view", e);
			throw new RuntimeException(e);
		}
	}

	@FXML
	private void startPressed() {
		//TODO: start asking questions
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setUpLeftViewPart();
		setUpRightViewPart();
	}

	//endregion

	private void setUpLeftViewPart() {
		genderBox.setItems(FXCollections.observableArrayList(Gender.values()));
		genderBox.getSelectionModel().select(Gender.OTHER);
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

	//These things will be moved to other classes as soon as they will be used:

	private void setUpRightViewPart() {
		questionAlgorithmChoice.setItems(
				FXCollections.observableArrayList(AlgorithmConfiguration.QUESTION_ALGORITHM.values()));
		questionAlgorithmChoice.getSelectionModel().select(configuration.getQuestionAlgorithm());

		List<Integer> items = IntStream.range(0, aPrioriDiseaseProb.size()).boxed().collect(Collectors.toList());
		aPrioriTable.setItems(FXCollections.observableList(items));
		aPrioriTable.prefHeightProperty().bind(new ReadOnlyDoubleWrapper(24)
				.multiply(Bindings.size(aPrioriTable.getItems()).add(1.5)));


		nameColumn.setCellValueFactory(param -> new SimpleStringProperty(diseases[param.getValue()]));

		aPrioriColumn.setCellValueFactory(param -> Bindings.valueAt(aPrioriDiseaseProb, param.getValue()));
		aPrioriColumn.setCellFactory(TextFieldTableCell.forTableColumn(new ViewUtils.DoubleStringConverter()));
		aPrioriColumn.setOnEditCommit(event -> {
			if(event.getNewValue() != null) {
				aPrioriDiseaseProb.set(event.getRowValue(), event.getNewValue());
			}
		});
	}

	public enum Gender {
		MALE("Männlich"),
		FEMALE("Weiblich"),//localize
		OTHER("Unbekannt");
		private String description;

		Gender(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public interface QuestionInitializedListener {
		void startPressed(AlgorithmConfiguration configuration);
	}
}
