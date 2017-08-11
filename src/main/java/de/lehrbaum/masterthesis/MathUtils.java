package de.lehrbaum.masterthesis;

import sun.security.krb5.Config;

import java.util.stream.DoubleStream;

/**
 * Contains general purpose mathematical functions.
 */
public class MathUtils {

	private static DistanceImpl distanceImpl = new BhattacharyyaDistance();

	public static double [] normalize(double [] vector) {
		double sum = DoubleStream.of(vector).sum();
		return DoubleStream.of(vector).map(d -> d/sum).toArray();
	}

	public static double distance(double [] firstDistribution, double[] secondDistribution) {
		assert firstDistribution.length == secondDistribution.length;
		return distanceImpl.distance(firstDistribution, secondDistribution);
	}

	private interface DistanceImpl {
		double distance(double[] firstDistr, double[] secondDistr);
	}

	private static class BhattacharyyaDistance implements DistanceImpl {
		@Override
		public double distance(double[] firstDistr, double[] secondDistr) {
			// The distance is D(p, q) = -ln(sum of all possible diseases i of (root of p(i)*q(i)))
			double sum = 0;
			for(int i = 0; i < firstDistr.length; i++) {
				sum += Math.sqrt(firstDistr[i]*secondDistr[i]);
			}
			return -Math.log(sum);
		}
	}
}
