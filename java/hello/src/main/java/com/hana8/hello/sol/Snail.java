package com.hana8.hello.sol;

public class Snail {
	public static void main(String[] args) {
		makeSnail(5);
		makeSnail(6);
		makeSnail(7);
	}

	public static int[] makeSnail(int rowSize) {
		int[] result = new int[rowSize * rowSize];
		for (int i = 0; i < rowSize; i++) {
			result[i] = i + 1;
			System.out.printf("%d", result[i]);
		}
		System.out.println();

		return result;
	}
}
