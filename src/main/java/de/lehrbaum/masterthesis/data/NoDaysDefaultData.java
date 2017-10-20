package de.lehrbaum.masterthesis.data;

import de.lehrbaum.masterthesis.MathUtils;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

abstract class NoDaysDefaultData {
	public static final String[] symptoms = new String[] {
			"Ohrschmerz", "Hörminderung", "Ohrdruck", "Ohrausfluss", "Tragus/Ohrhebeschmerz", "Fieber",
			"Schmerzen bei Kauen", "Odynophagie/Ohrenschmerz beim Schlucken", "Druckschmerz Kiefergelenk",
			"Schmerzhafte Knoten periauriulär", "Ziehen am Ohr", "Stechender starker Schmerz", "Brennender Schmerz",
			"Jucken im Ohr", "Rauschen im Ohr", "Wattegefühl im Ohr", "Pfeifen im Ohr", "Schmatzendes Ohrgeräusch",
			"Blubbern im Ohr", "Drehschwindel wie im Karussell", "Schwankendes/unsicheres Gefühl"};//localize

	public static final String[] diseases = new String[] {
			"Otitis externa", "AOM", "CMD", "TVS/PE", "Hörsturz",//localize
			"Cerumen obturans", "Zoster oticus", "GG-Verletzung", "COMM", "Gesund"};

	public static final double[][] probabilities = Stream.of(new double[][] {
			//			  0    1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20
			new double[] {90, 30, 20, 30, 90, 0, 10, 10, 20, 20, 60, 70, 70, 50, 20, 0, 0, 30, 10, 0, 10},
			new double[] {100, 90, 60, 20, 10, 50, 10, 10, 10, 10, 20, 90, 30, 10, 80, 10, 20, 10, 30, 10, 10},
			new double[] {60, 10, 20, 0, 20, 0, 80, 10, 90, 10, 70, 60, 20, 10, 10, 0, 50, 0, 0, 0, 0},
			new double[] {30, 80, 80, 0, 0, 0, 10, 20, 0, 0, 10, 20, 10, 0, 80, 10, 10, 20, 70, 0, 0},
			new double[] {10, 90, 40, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 40, 80, 80, 0, 0, 10, 20},
			new double[] {10, 90, 50, 10, 10, 0, 10, 0, 10, 0, 20, 10, 0, 60, 40, 20, 0, 20, 0, 0, 10},
			new double[] {90, 70, 10, 10, 20, 10, 10, 10, 10, 50, 20, 70, 50, 0, 10, 50, 40, 0, 0, 20, 20},
			new double[] {100, 0, 0, 30, 30, 0, 20, 20, 20, 0, 20, 80, 50, 10, 0, 0, 0, 10, 0, 0, 0},
			new double[] {10, 70, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 60, 10, 10, 10, 10, 10, 60},
			new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	}).map(array -> DoubleStream.of(array).map(d -> d / 100).toArray()).toArray(double[][]::new);

	public static final double[] aPrioriProbabilitiesOld = MathUtils.normalize(new double[] {1, 1, 1, 1, 1, 1, 1, 1, 1,
			0.1});
}