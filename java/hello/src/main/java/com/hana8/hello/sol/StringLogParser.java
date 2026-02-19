package com.hana8.hello.sol;

public class StringLogParser {

	public static void main(String[] args) {
		String log = """
			2024-02-05 09:15:23 ERROR UserService: Login failed for user admin
			2024-02-05 09:16:45 INFO PaymentService: Payment processed for order #1234
			2024-02-05 09:17:12 ERROR DatabaseService: Connection timeout
			2024-02-05 09:18:33 WARN UserService: Password retry limit reached for user john
			2024-02-05 09:19:01 ERROR UserService: Login failed for user admin
			2024-02-05 09:20:15 INFO OrderService: New order created #1235""";

		// System.out.println("log = " + log);
		int[] typeCnt = new int[3];
		String[] typeName = {"ERROR", "INFO", "WARN"};
		String[] prcLog = log.split("\n");

		StringBuilder sbAdmin = new StringBuilder();
		StringBuilder sbError = new StringBuilder();

		// log 분리 및 횟수 계산
		for (String l : prcLog) {
			String[] dt_tm_lvl_svc_msg = l.split(" ", 5);
			String lvl = dt_tm_lvl_svc_msg[2];
			String svc = dt_tm_lvl_svc_msg[3];
			String msg = dt_tm_lvl_svc_msg[4];

			switch (lvl) {
				case "ERROR" -> typeCnt[0]++;
				case "INFO" -> typeCnt[1]++;
				case "WARN" -> typeCnt[2]++;
			}
			if (msg.contains("admin")) {
				if (!sbAdmin.isEmpty()) {
					sbAdmin.append('\n');
				}
				sbAdmin.append(l);
			}
			if (lvl.equals("ERROR")) {
				if (!sbError.isEmpty()) {
					sbError.append('\n');
				}
				sbError.append(svc).append(' ').append(msg);
			}
		} //for end
		// mostCnt, mostName
		int mostCnt = 0;
		String mostName = "";
		for (int i = 0; i < typeCnt.length; i++) {
			if (typeCnt[i] > mostCnt) {
				mostCnt = typeCnt[i];
				mostName = typeName[i];
			}
		}
		// 1. 전체 로그 개수
		// 2. 각 로그 레벨(ERROR, INFO, WARN)별 개수
		// 3. 최다 등장한 서비스 이름과 횟수

		// 4. "admin" 사용자와 관련된 로그만 추출하여 출력
		// 5. ERROR 로그만 모아서 메시지 부분만 출력
		System.out.printf("1. 전체 로그 = %d%n", (typeCnt[0] + typeCnt[1] + typeCnt[2]));
		System.out.printf("2. ERROR: %d, INFO: %d, WARN: %d%n", typeCnt[0], typeCnt[1], typeCnt[2]);
		System.out.printf("3. 최다 등장 서비스: %s, %d회 등장%n", mostName, mostCnt);
		System.out.printf("4. admin 사용자 관련 로그만 출력 :%n%s%n", sbAdmin);
		System.out.printf("5. ERROR 로그만 모아서 메시지 부분 출력: %n%s", sbError);
	}
}
