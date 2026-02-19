package com.hana8.hello.trythis;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

	private static final int MIN_SCORE = 70;
	// "좋은 사람"이 앞에 오도록: 점수 ↓, 동점이면 이름 ↑
	private static final Comparator<Emp> BEST_FIRST =
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

		// 1) 공통 전제: 70 미만 제외
		List<Emp> eligible = emps.stream()
			.filter(e -> e.score() >= MIN_SCORE)
			.toList();

		// 2) 남은 사람들을 부서별로 출력 (부서 이름 순)
		System.out.println("== (1) 부서별 출력 (부서명 오름차순) ==");
		printByDeptAsc(eligible);

		// 3) 부서 별 최고 점수 1명만 남기기 (동점이면 이름 빠른 사람)
		System.out.println("\n== (2) 부서별 최고 1명 (부서명 오름차순) ==");
		Map<String, Emp> bestByDeptAsc = bestByDeptAsc(eligible);
		printBestAsc(bestByDeptAsc);

		// 4) 부서 이름 역순으로 출력 (3번 결과를 역순으로)
		System.out.println("\n== (3) 부서별 최고 1명 (부서명 내림차순) ==");
		printBestDesc(bestByDeptAsc);
	}

	// (2) 부서별 출력: 부서명 오름차순이 필요하므로 TreeMap 사용
	static void printByDeptAsc(List<Emp> eligible) {
		Map<String, List<Emp>> byDept = eligible.stream()
			.collect(Collectors.groupingBy(
				Emp::dept,
				TreeMap::new,
				Collectors.toList()
			));

		byDept.forEach((dept, list) -> {
			String joined = list.stream()
				.map(e -> e.name() + "(" + e.score() + ")")
				.collect(Collectors.joining(", "));
			System.out.println(dept + ": " + joined);
		});
	}

	// (3) 부서별 최고 1명: dept 키 충돌 시 BEST_FIRST 기준 "앞에 오는 사람"만 남김
	static Map<String, Emp> bestByDeptAsc(List<Emp> eligible) {
		return eligible.stream()
			.collect(Collectors.toMap(
				Emp::dept,
				Function.identity(),
				BinaryOperator.minBy(BEST_FIRST),
				TreeMap::new
			));
	}

	static void printBestAsc(Map<String, Emp> bestByDeptAsc) {
		bestByDeptAsc.forEach((dept, e) ->
			System.out.printf("%s: %s(%d)%n", dept, e.name(), e.score())
		);
	}

	static void printBestDesc(Map<String, Emp> bestByDeptAsc) {
		bestByDeptAsc.entrySet().stream()
			.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
			.forEach(entry -> {
				Emp e = entry.getValue();
				System.out.printf("%s: %s(%d)%n", entry.getKey(), e.name(), e.score());
			});
	}

	record Emp(String name, String dept, int score) {
	}
}
