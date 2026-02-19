package com.hana8.hello.sol;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PerformApp {

	private static final int MIN_SCORE = 70;
	// 높은 점수 우선, 동점이면 이름 빠른 순
	private static final Comparator<Emp> BY_SCORE_DESC_THEN_NAME_ASC =
		Comparator.comparingInt(Emp::score).reversed()
			.thenComparing(Emp::name);

	public static void main(String[] args) {
		List<Emp> emps = List.of(
			new Emp("Hong", "Sales", 85),
			new Emp("Kim", "Sales", 95),
			new Emp("Choi", "HR", 55),
			new Emp("Nam", "HR", 75),
			new Emp("Lee", "IT", 82),
			new Emp("Park", "IT", 92),
			new Emp("Ahn", "Sales", 95)
		);

		bestByDept(emps).entrySet().stream()
			.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
			.forEach(e -> System.out.printf(
				"%s: %s(%d)%n",
				e.getKey(),
				e.getValue().name(),
				e.getValue().score()
			));
	}

	static Map<String, Emp> bestByDept(List<Emp> emps) {
		return emps.stream()
			.filter(e -> e.score() >= MIN_SCORE)
			.collect(Collectors.toMap(
				Emp::dept,
				Function.identity(),
				BinaryOperator.minBy(BY_SCORE_DESC_THEN_NAME_ASC)
			));
	}

	record Emp(String name, String dept, int score) {
	}
}
