package de.lehrbaum.masterthesis.view;

import de.lehrbaum.masterthesis.exceptions.UserReadableException;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

class ViewUtils {
	public static class DoubleStringConverter extends StringConverter<Double> {

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
			try {
				d = new Double(string);
			} catch(NumberFormatException e) {
				try {
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

	public static void showErrorMessage(@NotNull UserReadableException e, @Nullable Scene owner) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Fehler aufgetreten");
		alert.setHeaderText("Ein Fehler ist aufgetreten. Falls sie ihn nicht beheben können senden sie bitte die log datei an mich.");
		alert.setContentText(e.convertForUser());
		if(owner != null) {
			alert.initOwner(owner.getWindow());
			alert.initModality(Modality.WINDOW_MODAL);
		}
		alert.showAndWait();
	}
}
