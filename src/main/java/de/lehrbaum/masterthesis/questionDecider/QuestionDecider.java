package de.lehrbaum.masterthesis.questionDecider;

import de.lehrbaum.masterthesis.inference.AbstractStepByStepInferenceNoDays;

/**
 * This class should decide what question to ask next.
 * It estimates a gain for each question that could be asked. The gain means how much extra information
 * has been gained by asking the question. If not more gain is possible, it tries to eliminate the main
 * possibilities.
 */
public class QuestionDecider {
	private int questionToAsk;

	//private double GAIN_THRESHOLD = 0.1;
	public QuestionDecider(AbstractStepByStepInferenceNoDays inference) {
		/*if(inference.amountSymptoms() < 1)
			throw new IllegalArgumentException("No symptoms");
		questionToAsk = -1;
		double gainByQuestion = -Double.MAX_VALUE;
		//[symptom][yes/no answer][disease] contains the probabilities assuming a given symptom
		for(int symptom = 0; symptom < inference.amountSymptoms(); symptom++) {
			if(inference.wasSymptomAnswered(symptom))
				continue;
			double gainIfNo = Math.abs(MathUtils.distance(inference.getDiseaseProbabilities(),
					inference.probabilityAssumingSymptom(symptom, false)));
			//int finalSymptom = symptom;
			//double propOfNo = IntStream.range(0, inference.probabilities.length).
			//		mapToDouble(i -> inference.getDiseaseProbabilities()[i] * inference.probabilities[i][finalSymptom])
			//		.sum();
			double gainIfYes = Math.abs(MathUtils.distance(inference.getDiseaseProbabilities(),
					inference.probabilityAssumingSymptom(symptom, true)));
			double completeGain = gainIfNo + gainIfYes;
			if(completeGain > gainByQuestion) {
				gainByQuestion = completeGain;
				questionToAsk = symptom;
			}
		}

		//if the gain is to low try to figure one disease out
		//if(gainByQuestion < )*/
	}

	/**
	 *
	 * @return -1 means it is recommended to stop asking and just return the highest result
	 */
	public int recommendedSymptomToAsk() {
		return questionToAsk;
	}
}
