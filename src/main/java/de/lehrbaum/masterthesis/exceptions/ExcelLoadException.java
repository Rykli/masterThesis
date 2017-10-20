package de.lehrbaum.masterthesis.exceptions;

import javafx.geometry.Point2D;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExcelLoadException extends UserReadableException {
	public enum Reason{
		NUMBER_NOT_PARSABLE,
		NOT_FOUND,
		FILE_INACCESSIBLE,
		UNKOWN
	}

	private Reason reason;
	private Point2D address;
	private String extraInformation;

	public ExcelLoadException(@NotNull Reason reason, Point2D address) {
		this.reason = reason;
		this.address = address;
	}

	public ExcelLoadException(@NotNull Reason reason, Point2D address, @Nullable String extraInformation) {
		this.reason = reason;
		this.address = address;
		this.extraInformation = extraInformation;
	}

	public String convertForUser() {
		String result = "Da hat der Programmierer vergessen die Fehlermeldung korrekt zu erzeugen.";
		switch(reason) {
			case NUMBER_NOT_PARSABLE:
				result = String.format("Der Wert in Zeile %d Spalte %d konnte nicht in eine Zahl konvertiert werden.",
						(int)address.getY() + 1, (int)address.getX() + 1);
				if(extraInformation != null)
					result += " Der Wert ist " + extraInformation;
				break;
			case NOT_FOUND:
				result = String.format("Der Wert in Zeile %d Spalte %d konnte nicht gefunden werden.",
						(int)address.getY() + 1, (int)address.getX() + 1);
				break;
			case FILE_INACCESSIBLE:
				result = "Die Datei kann nicht geöffnet werden. Bitte stellen sie sicher, dass sie die Datei nicht in" +
						" Excel offen haben, da dies die Datei blockiert.";
				break;
			case UNKOWN:
				result = String.format("Der genaue Grund ist leider nicht bekannt. " +
								"Der Fehler trat in Zeile %d Spalte %d auf.",
						(int)address.getY() + 1, (int)address.getX() + 1);
		}
		return result;
	}
}
