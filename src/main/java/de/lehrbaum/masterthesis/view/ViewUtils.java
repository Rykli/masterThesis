package de.lehrbaum.masterthesis.view;

import javafx.util.StringConverter;

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
}
