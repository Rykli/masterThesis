package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.*;
import static de.lehrbaum.masterthesis.inferencenodays.Bayes.BayesInferenceNoDays.VARIANTS.SYMPTOMS_CALCULATION_VARIANT_1;
import static de.lehrbaum.masterthesis.inferencenodays.CompleteInferenceNoDays.SYMPTOM_STATE;

class NoDaysSymptomTestView extends ScrollPane implements MainWindow.LoggableViewState{
	private static final Logger logger = Logger.getLogger(NoDaysSymptomTestView.class.getCanonicalName());

    private ToggleGroup[] symptomToggleGroups;
    private ToggleButton[][] symptomToggleButtons;
    private double[] diseaseProbabilities;
    private BarChart<String, Number> diseaseChart;

    private ObservableList<Double> aPrioriDiseaseProb;

    //region Update views

    private void updateValues() {
		diseaseChart.getData().clear();
		double [] aPrioriProbabilitiesArray = aPrioriDiseaseProb.stream().mapToDouble(Double::doubleValue).toArray();
		CompleteInferenceNoDays inference = new BayesInferenceNoDays(probabilities, aPrioriProbabilitiesArray,
				EnumSet.of(SYMPTOMS_CALCULATION_VARIANT_1));
		SYMPTOM_STATE[] symptomStates = getSymptomStates();
		inference.calculateGivenAllAnswers(symptomStates);
		diseaseProbabilities = inference.getDiseaseProbabilities();
		updateDiseaseChart();
		updateSymptomProbabilities(inference);
	}

	@NotNull
	private CompleteInferenceNoDays.SYMPTOM_STATE[] getSymptomStates() {
		SYMPTOM_STATE[] symptomStates = new SYMPTOM_STATE[symptoms.length];
		for(int i = 0; i < symptomStates.length; i++) {
			Toggle t = symptomToggleGroups[i].getSelectedToggle();
			if(t == null)
				symptomStates[i] = SYMPTOM_STATE.UNKOWN;
			else if(t instanceof AbsentToggleButton)
				symptomStates[i] = SYMPTOM_STATE.ABSENT;
			else if(t instanceof PresentToggleButton)
				symptomStates[i] = SYMPTOM_STATE.PRESENT;
			else
				System.out.println("Unexpected toggle button: " + t);
		}
		return symptomStates;
	}

	private void updateDiseaseChart() {
		for (int disease = 0; disease < diseases.length; disease++) {
			String diseaseName = diseases[disease];
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(diseaseName);
			series.getData().add(new XYChart.Data<>("", diseaseProbabilities[disease]));
			diseaseChart.getData().add(series);
		}
	}

	private void updateSymptomProbabilities(CompleteInferenceNoDays inference) {
		for(int symptom = 0; symptom < symptoms.length; symptom++) {
			double [] symptomProb = inference.probabilityOfSymptom(symptom);
			symptomToggleButtons[symptom][0].setText(String.format("%.2f", symptomProb[0]));
			symptomToggleButtons[symptom][1].setText(String.format("%.2f", symptomProb[1]));
		}
	}

	//endregion

    //region building the view
	NoDaysSymptomTestView() {
    	super();
    	Double [] boxedArray = DoubleStream.of(aPriorProbabilities).boxed().toArray(Double[]::new);
    	aPrioriDiseaseProb = FXCollections.observableArrayList(boxedArray);
		setUpUserInterface();
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
		symptomToggleGroups = new ToggleGroup[symptoms.length];
		symptomToggleButtons = new ToggleButton[symptoms.length][2];
		for(int i = 0; i < symptoms.length; i++) {
			Label l = new Label(symptoms[i]);
			ToggleGroup group = new ToggleGroup();
			symptomToggleGroups[i] = group;
			symptomToggleButtons[i][0] = new PresentToggleButton(group);
			symptomToggleButtons[i][1] = new AbsentToggleButton(group);
			gridPane.addRow(i + 1, l, symptomToggleButtons[i][0], symptomToggleButtons[i][1]);
			group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateValues());
		}
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.LEFT, true));
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.CENTER, true));
		gridPane.getColumnConstraints().add(new ColumnConstraints(USE_PREF_SIZE,
				USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.CENTER, true));
	}

	private void setUpRightSide(HBox root) {
		VBox rightSide = new VBox(5);
		root.getChildren().add(rightSide);
		HBox.setHgrow(rightSide, Priority.ALWAYS);
		diseaseChart = generateChart();
		rightSide.getChildren().add(diseaseChart);
		VBox.setVgrow(diseaseChart, Priority.ALWAYS);
		rightSide.getChildren().add(setUpPriorProbTable());
	}

	private TableView setUpPriorProbTable() {
    	List<Integer> items = IntStream.range(0, diseases.length).boxed().collect(Collectors.toList());
    	TableView<Integer> tableView = new TableView<>(FXCollections.observableList(items));

    	//The first column contains the names of the diseases
		TableColumn<Integer, String> nameColumn = new TableColumn<>("Krankheit");
		nameColumn.setPrefWidth(250);//I don't like fixed sizes, but otherwise it looks stupid...
		tableView.getColumns().add(nameColumn);
		nameColumn.setEditable(false);
		// the value of this column is the name of the disease
		nameColumn.setCellValueFactory(param -> new SimpleStringProperty(diseases[param.getValue()]));

		//This column contains the prior probability of the disease
		addValueColumn(tableView);
		tableView.setEditable(true);
		tableView.prefHeightProperty().bind(new ReadOnlyDoubleWrapper(24)
				.multiply(Bindings.size(tableView.getItems()).add(1.5)));
		return tableView;
	}

	private void addValueColumn(TableView<Integer> tableView) {
		TableColumn<Integer, Double> valueColumn = new TableColumn<>("A priori Wahrscheinlichkeit");
		tableView.getColumns().add(valueColumn);
		valueColumn.setEditable(true);
		valueColumn.setCellValueFactory(param -> Bindings.valueAt(aPrioriDiseaseProb, param.getValue()));
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		valueColumn.setOnEditCommit(event -> {
			if(event.getNewValue() != null) {
				aPrioriDiseaseProb.set(event.getRowValue(), event.getNewValue());
				updateValues();
			}
		});
	}

	private BarChart<String, Number> generateChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String,Number> bc =
				new BarChart<>(xAxis,yAxis);
		bc.setTitle("Disease probabilities");
		xAxis.setLabel("Disease");
		yAxis.setLabel("Probability");
		bc.setLegendSide(Side.BOTTOM);
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

	private class DoubleStringConverter extends StringConverter<Double> {

		@Override
		public String toString(Double d) {
			return String.format("%.2f", d);
		}

		@Override
		public Double fromString(String string) {
			if(string == null)
				return null;
			string = string.trim();
			if(string.isEmpty())
				return null;
			Double d;
			try{
				d = new Double(string);
			} catch (NumberFormatException e) {
				try{
					NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
					Number number = format.parse(string);
					d = number.doubleValue();
				} catch(ParseException e2) {
					return null;
				}
			}
			return d;
		}
	}

	//endregion

	@Override
	public void appendViewState(StringBuilder sb) {
		sb.append("[Symptom test view] Symptoms selected: ");
		sb.append(Arrays.toString(getSymptomStates()));
		sb.append("\nCalculated values: ");
		sb.append(Arrays.toString(diseaseProbabilities));
		sb.append('\n');
	}
}
