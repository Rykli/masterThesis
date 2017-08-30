package de.lehrbaum.masterthesis.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

import static de.lehrbaum.masterthesis.data.NoDaysDefaultData.aPriorProbabilities;

/**
 * The question view consists of multiple changing views leading the user through the questioning process.
 */
class QuestionView extends BorderPane implements MainWindow.LoggableViewState {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QuestionView.class.getCanonicalName());

	private ObservableList<Double> aPrioriDiseaseProb;

	QuestionView() {
		Double[] boxedArray = DoubleStream.of(aPriorProbabilities).boxed().toArray(Double[]::new);
		aPrioriDiseaseProb = FXCollections.observableArrayList(boxedArray);
		setCenter(QuestionInitializationView.createQuestionInitializationView(aPrioriDiseaseProb));
	}

	@Override
	public void appendViewState(StringBuilder sb) {
		sb.append("Question view shown. \n");
		sb.append("A priori probabilities");
		sb.append(Arrays.toString(aPrioriDiseaseProb.toArray()));
		//TODO: implement
	}

}
