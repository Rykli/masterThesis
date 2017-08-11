package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.inference.Bayes.BayesInferenceNoDays;
import de.lehrbaum.masterthesis.inference.InferenceNoDays;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.*;

public class NoDaysView extends Application {
	private static final Logger logger = Logger.getLogger(NoDaysView.class.getCanonicalName());

    private ToggleGroup[] symptomToggles;
    private double[] diseaseProbabilities;
    private BarChart<String, Number> diseaseChart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(buildView());
        primaryStage.show();
    }

    //region Update views

    private void updateValues() {
		diseaseChart.getData().clear();
		InferenceNoDays inference = new BayesInferenceNoDays(probabilities, aPriorProbabilities);
		InferenceNoDays.SYMPTOM_STATE[] symptomStates = getSymptomStates();
		diseaseProbabilities = inference.calculateGivenAllAnswers(symptomStates);
    	updateDiseaseChart();
	}

	@NotNull
	private InferenceNoDays.SYMPTOM_STATE[] getSymptomStates() {
		InferenceNoDays.SYMPTOM_STATE[] symptomStates = new InferenceNoDays.SYMPTOM_STATE[symptoms.length];
		for(int i = 0; i < symptomStates.length; i++) {
			Toggle t = symptomToggles[i].getSelectedToggle();
			if(t instanceof UnkownToggleButton)
				symptomStates[i] = InferenceNoDays.SYMPTOM_STATE.UNKOWN;
			else if(t instanceof PresentToggleButton)
				symptomStates[i] = InferenceNoDays.SYMPTOM_STATE.PRESENT;
			else if(t instanceof AbsentToggleButton)
				symptomStates[i] = InferenceNoDays.SYMPTOM_STATE.ABSENT;
			else
				System.out.println("Unexpected toggle button: " + t);
		}
		return symptomStates;
	}

	private void updateDiseaseChart() {
		for (int i = 0; i < diseases.length; i++) {
			String disease = diseases[i];
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(disease);
			series.getData().add(new XYChart.Data<>("", diseaseProbabilities[i]));
			diseaseChart.getData().add(series);
		}
	}


	//endregion

    //region building the view

    private Scene buildView() {
    	HBox root = new HBox();
    	root.getStylesheets().add("/no_days_view_style.css");
        VBox leftSide = new VBox(4);
        root.getChildren().add(leftSide);
        symptomToggles = new ToggleGroup[symptoms.length];
        for(int i = 0; i < symptoms.length; i++) {
			HBox line = createSymptomLine(i);
			leftSide.getChildren().add(line);
        }
        VBox rightSide = new VBox(5);
        root.getChildren().add(rightSide);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
		buildRightSide(rightSide);
        updateValues();
        return new Scene(root, 1100, 650);
    }

	private void buildRightSide(VBox rightSide) {
		diseaseChart = generateChart();
		rightSide.getChildren().add(diseaseChart);
		VBox.setVgrow(diseaseChart, Priority.ALWAYS);
		Button feedback = new Button("Rückmeldung");
		feedback.setMaxWidth(Double.POSITIVE_INFINITY);
		rightSide.getChildren().add(feedback);
		feedback.setOnAction(event -> feedbackPressed());
	}

	@NotNull
	private HBox createSymptomLine(int i) {
		HBox line = new HBox(10);
		//add the label
		Label label = new Label(symptoms[i]);
		label.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(label, Priority.ALWAYS);
		line.getChildren().add(label);
		//add the combo options
		symptomToggles[i] = setUpToggleButtons(line.getChildren());
		return line;
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

    private ToggleGroup setUpToggleButtons(List<Node> children) {
		ToggleGroup group = new ToggleGroup();
		ToggleButton unkown = new UnkownToggleButton(group);
		children.add(unkown);
		children.add(new PresentToggleButton(group));
		children.add(new AbsentToggleButton(group));
		group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue == null) {
				group.selectToggle(unkown);
				return;
			}
			updateValues();
		});
		return group;
	}

	private class UnkownToggleButton extends ToggleButton {
		UnkownToggleButton(ToggleGroup group) {
			super("Unbekannt");
			setSelected(true);
			setToggleGroup(group);
			getStyleClass().add("unknown-button");
		}
	}

	private class PresentToggleButton extends ToggleButton {
		PresentToggleButton(ToggleGroup group) {
			super("Vorhanden");
			setToggleGroup(group);
			getStyleClass().add("present-button");
		}

    }

    private class AbsentToggleButton extends ToggleButton {
		AbsentToggleButton(ToggleGroup group) {
			super("Nicht vorhanden");
			setToggleGroup(group);
			getStyleClass().add("absent-button");
		}
	}

	//endregion

	private void feedbackPressed() {
    	if(loggerFile == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Es gab ein problem mit der Protokoll Datei.");
			alert.setContentText("Es konnte keine Protokoll Datei erstellt werden. Bitte stellen sie sicher, dass" +
					" der Ordner in dem sich das Programm befindet nicht Schreibgeschützt ist.");
			alert.showAndWait();
			return;
		}
		//get user feedback and then log it.
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Rückmeldung speichern");
		dialog.setHeaderText("Der eingegebene Text wird zusammen mit der aktuellen Auswahl der Symptome und den berechneten" +
				" Krankheitswahrscheinlichkeiten\nin der Datei " + loggerFile + " abgespeichert.");
		dialog.setContentText("Bitte geben sie ihre Rückmeldung ein:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(s -> logger.info("Symptoms: " + Arrays.toString(getSymptomStates())
				+ "\nDiseases: " + Arrays.toString(diseaseProbabilities)
				+ "\nFeedback: " + s));
	}

	public static void launch() {
    	setUpFileLogger();
    	launch((String[])null);
	}

	private static File loggerFile;

	private static void setUpFileLogger() {
		URL url = NoDaysView.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			File folder = new File(url.toURI()).getParentFile();

			File logFile = new File(folder, "protokol.log");
			String logfilePath = logFile.getAbsolutePath().replace(File.separatorChar, '/');
			logger.fine(logfilePath);
			Logger mainLogger = Logger.getLogger("de.lehrbaum.masterthesis");
			FileHandler fileHandler = new FileHandler(logfilePath, true);
			fileHandler.setLevel(Level.INFO);
			mainLogger.addHandler(fileHandler);
			loggerFile = logFile;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Problem setting up logger.", e);
		}catch (URISyntaxException e) {
			logger.severe("Problem with the program folder: " + url);
		}
	}
}
