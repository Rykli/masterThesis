package de.lehrbaum.masterthesis;

public class StringContains {

	//fast method for string contains
	static String[][] cases = new String[][] {
			{"A", "A", "T"},
			{"AB", "A", "T"},
			{"BC", "A", "F"},
			{"AAAA", "A", "T"},
			{"CABABABD", "ABABD", "T"}
	};

	public static boolean doesStringContain(String s1, String s2) {
		assert s1.length() >= s2.length();
		int[] cycles = new int[s2.length()];
		String currentCycle = null;
		for(int i = 1; i < s2.length(); i++) {
			if(currentCycle != null) {
				String partOfCycleThatMustMatch = currentCycle.substring(0, i - currentCycle.length());
				String partOfSecondCycle = s2.substring(currentCycle.length(), i);
				if(partOfCycleThatMustMatch.equals(partOfSecondCycle)) {

				}
			}
			if(s2.charAt(i) == s2.charAt(0)) {
				//possible cycle
				currentCycle = s2.substring(0, i);
			}
		}
		return false;
	}

	public static void main(String[] args) {
		doesStringContain(null, "ABAB");
	}
}
