package com.hana8.hello.sol;

public class ScanScore {
	public static void main(String[] args) {
		System.out.println(java.nio.charset.Charset.defaultCharset());
		int[] score = {71, 96, 92};
		String[] names = {"Hong", "Kim", "Lee"};

		int totScore = 0;
		int bestScore = 0;
		String bestMember = "";

		int i = 0;
		for (i = 0; i < 3; i++) {
			totScore += score[i];
			if (score[i] > bestScore) {
				bestScore = score[i];
				bestMember = names[i];
			}
		}
		double avgScore = (double)totScore / i;
		System.out.printf("총점은 %d점, 평균은 %f점, 최고 득점자는 %s이며 학점은 %c 입니다.\n", totScore, avgScore, bestMember,
			grading(bestScore));

	}

	private static char grading(int score) {
		return switch (score / 10) {
			case 9, 10 -> 'A';
			case 8 -> 'B';
			case 7 -> 'C';
			default -> 'D';
		};
	}
}
