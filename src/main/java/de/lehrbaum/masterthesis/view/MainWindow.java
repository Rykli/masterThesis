package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.Main;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.logging.Logger;

public class MainWindow extends VBox {
	private static final Logger logger = Logger.getLogger(MainWindow.class.getCanonicalName());

	private TabPane tabPane;
	public MainWindow () {
		super(10);
		getStylesheets().add("/main_window_style.css");
		setUpTabPane();
		setUpFeedbackButton();
	}

	private void setUpTabPane() {
		tabPane = new TabPane();
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		getChildren().add(tabPane);
		setUpTap1();
		setUpTap2();
	}

	private void setUpTap1() {
		NoDaysSymptomTestView view = new NoDaysSymptomTestView();
		Tab tab = new Tab("Symptomtest", view);//localize
		tab.setClosable(false);
		tabPane.getTabs().add(tab);
	}

	private void setUpTap2() {
		Tab tab = new Tab("Fragen test", new QuestionView());//localize
		tab.setClosable(false);
		tabPane.getTabs().add(tab);
	}

	private void setUpFeedbackButton() {
		Button b = new Button("Rückmeldung");//localize
		b.setMaxWidth(Double.POSITIVE_INFINITY);
		getChildren().add(b);
		b.setOnAction(event -> feedbackPressed());
	}

	private void feedbackPressed() {
		if(Main.loggerFile == null) {
			//this means the file logger could not be set up correctly.
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error Dialog");//localize
			alert.setHeaderText("Es gab ein problem mit der Protokoll Datei.");
			alert.setContentText("Es konnte keine Protokoll Datei erstellt werden. Bitte stellen sie sicher, dass" +
					" der Ordner in dem sich das Programm befindet nicht Schreibgeschützt ist.");
			alert.showAndWait();
			return;
		}

		//get user feedback and then log it.
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Rückmeldung speichern");//localize
		dialog.setHeaderText("Der eingegebene Text wird zusammen mit Informationen über den aktuellen Zustand des " +
				"Programms\nin der Datei " + Main.loggerFile + " abgespeichert.");
		dialog.setContentText("Bitte geben sie ihre Rückmeldung ein:");

		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			StringBuilder sb = new StringBuilder("[Feedback] ");
			Node current = tabPane.getSelectionModel().getSelectedItem().getContent();
			if (current instanceof LoggableViewState) {
				((LoggableViewState) current).appendViewState(sb);
			}
			sb.append("Message by user: ");
			sb.append(result.get());
			logger.info(sb.toString());
		}
	}

	interface LoggableViewState {
		void appendViewState(StringBuilder sb);
	}
}
