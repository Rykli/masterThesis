package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.data.Answer;
import de.lehrbaum.masterthesis.data.DataProvider;
import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmConfiguration;
import de.lehrbaum.masterthesis.inferencenodays.AlgorithmFactory;
import de.lehrbaum.masterthesis.inferencenodays.InferenceNoDays;
import javafx.geometry.HPos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.logging.Logger;

class NoDaysSymptomTestView extends ScrollPane implements MainWindow.LoggableViewState {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(NoDaysSymptomTestView.class.getCanonicalName());

	private ToggleGroup[] symptomToggleGroups;
	private ToggleButton[][] symptomToggleButtons;
	private double[] diseaseProbabilities;
	private BarChart<String, Number> diseaseChart;
	private DataProvider dataProvider;

	NoDaysSymptomTestView() {
		super();
		try {
			dataProvider = AlgorithmFactory.getDataProvider(new AlgorithmConfiguration());
		} catch(UserReadableException e) {
			ViewUtils.showErrorMessage(e, getScene());
		}
		setUpUserInterface();
	}

	@Override
	public void appendViewState(StringBuilder sb) {
		sb.append("[Symptom test view] Symptoms selected: ");
		sb.append(Arrays.toString(getSymptomStates()));
		sb.append("\nCalculated values: ");
		sb.append(Arrays.toString(diseaseProbabilities));
	}

	@Override
	public void reloadData() {
		updateValues();
	}

	@NotNull
	private Answer[] getSymptomStates() {
		Answer[] symptomStates = new Answer[symptomToggleGroups.length];
		for(int i = 0; i < symptomStates.length; i++) {
			Toggle t = symptomToggleGroups[i].getSelectedToggle();
			if(t == null)
				symptomStates[i] = Answer.NOT_ANSWERED;
			else if(t instanceof AbsentToggleButton)
				symptomStates[i] = Answer.ABSENT;
			else if(t instanceof PresentToggleButton)
				symptomStates[i] = Answer.PRESENT;
			else
				System.out.println("Unexpected toggle button: " + t);
		}
		return symptomStates;
	}

	private void updateValues() {
		InferenceNoDays.CompleteInferenceNoDays inference = null;
		try {
			inference = AlgorithmFactory.getCompleteInferenceNoDays(new AlgorithmConfiguration());
		} catch(UserReadableException e) {
			ViewUtils.showErrorMessage(e, getScene());
			return;
		}
		Answer[] symptomStates = getSymptomStates();
		inference.symptomsAnswered(symptomStates);
		diseaseProbabilities = inference.getDiseaseProbabilities();
		updateDiseaseChart();
		updateSymptomProbabilities(inference);
	}

	private void updateDiseaseChart() {
		diseaseChart.getData().clear();
		for(int disease = 0; disease < dataProvider.getAmountDiseases(); disease++) {
			String diseaseName = dataProvider.getDiseaseNames()[disease];
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(diseaseName);
			series.getData().add(new XYChart.Data<>("", diseaseProbabilities[disease]));
			diseaseChart.getData().add(series);
		}
	}

	private void updateSymptomProbabilities(InferenceNoDays.CompleteInferenceNoDays inference) {
		for(int symptom = 0; symptom < dataProvider.getAmountSymptoms(); symptom++) {
			if(inference.wasQuestionAnswered(symptom)) {
				symptomToggleButtons[symptom][0].setText("x");
				symptomToggleButtons[symptom][1].setText("x");
				continue;
			}
			EnumMap<Answer, Double> symptomProb = inference.probabilityOfAnswers(symptom);
			symptomToggleButtons[symptom][0].setText(String.format("%.2f", symptomProb.get(Answer.PRESENT)));
			symptomToggleButtons[symptom][1].setText(String.format("%.2f", symptomProb.get(Answer.ABSENT)));
		}
	}

	private void setUpUserInterface() {
		getStyleClass().add("padded-container");
		HBox root = new HBox(10);
		setContent(root);
		setUpLeftSide(root);
		setUpRightSide(root);
		//draw the first values
		updateValues();
	}

	private void setUpLeftSide(HBox root) {
		GridPane gridPane = new GridPane();
		gridPane.setVgap(5);
		gridPane.setHgap(7);
		root.getChildren().add(gridPane);
		gridPane.addRow(0, new Label("Symptom Name"),
				new Label("Vorhanden"), new Label("| Nicht Vorhanden"));
		symptomToggleGroups = new ToggleGroup[dataProvider.getAmountSymptoms()];
		symptomToggleButtons = new ToggleButton[dataProvider.getAmountSymptoms()][2];
		for(int symptom = 0; symptom < dataProvider.getAmountSymptoms(); symptom++) {
			Label l = new Label(dataProvider.getSymptomName(symptom));
			ToggleGroup group = new ToggleGroup();
			symptomToggleGroups[symptom] = group;
			symptomToggleButtons[symptom][0] = new PresentToggleButton(group);
			symptomToggleButtons[symptom][1] = new AbsentToggleButton(group);
			gridPane.addRow(symptom + 1, l, symptomToggleButtons[symptom][0], symptomToggleButtons[symptom][1]);
			group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateValues());
		}
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.LEFT, true));
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.CENTER, true));
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.CENTER, true));

		Button clearAll = new Button("Auswahl zurücksetzen");
		clearAll.setMaxWidth(Double.POSITIVE_INFINITY);
		clearAll.setOnAction(event -> {
			for(ToggleGroup group : symptomToggleGroups)
				group.selectToggle(null);
		});
		gridPane.addRow(dataProvider.getAmountSymptoms() + 1, clearAll);

	}

	private void setUpRightSide(HBox root) {
		VBox rightSide = new VBox(5);
		root.getChildren().add(rightSide);
		HBox.setHgrow(rightSide, Priority.ALWAYS);
		diseaseChart = generateChart();
		rightSide.getChildren().add(diseaseChart);
		VBox.setVgrow(diseaseChart, Priority.ALWAYS);
	}

	private BarChart<String, Number> generateChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> bc =
				new BarChart<>(xAxis, yAxis);
		bc.setTitle("Wahrscheinlichkeiten der Krankheiten");
		xAxis.setLabel("Krankheit");
		yAxis.setLabel("Wahrscheinlichkeit");
		bc.setAnimated(false);
		return bc;
	}

	private class BaseCustomToggleButton extends ToggleButton {
		BaseCustomToggleButton(ToggleGroup group) {
			setPrefWidth(60);
			setToggleGroup(group);
		}
	}

	private class PresentToggleButton extends BaseCustomToggleButton {
		PresentToggleButton(ToggleGroup group) {
			super(group);
			getStyleClass().add("present-button");
		}

	}

	private class AbsentToggleButton extends BaseCustomToggleButton {
		AbsentToggleButton(ToggleGroup group) {
			super(group);
			getStyleClass().add("absent-button");
		}
	}
}
