# 글자 깨짐(한글 인코딩) 이슈 보고서

## 1) 개요
`java/hello/src/main/java/com/hana8/hello/sol/ScanScore.java` 실행 시 콘솔 출력의 한글이 `����` 형태로 깨지는 문제가 발생했다.

## 2) 원래 상태
- 실행 로그:
  - `:com.hana8.hello.sol.ScanScore.main()` 태스크로 실행됨(Gradle 실행)
  - 출력 문자열 한글 깨짐
- 확인 사항:
  - 소스 파일(`ScanScore.java`)의 한글 문자열 자체는 정상
  - 실행 중 `Charset.defaultCharset()`는 `UTF-8`로 출력된 사례가 있었음
- 해석:
  - 실행 시점 인코딩만으로는 설명되지 않고, 컴파일 산출물(`.class`) 생성 단계 인코딩 불일치 가능성 높음

## 3) 원인 분석
핵심 원인은 컴파일 인코딩 미고정이다.

- JVM 실행 인코딩(`-Dfile.encoding=UTF-8`)을 맞춰도,
- `compileJava`가 UTF-8이 아닌 인코딩으로 소스를 읽어 `.class`를 만들면,
- 문자열 상수가 이미 깨진 상태로 바이너리에 들어간다.
- 이후 실행이 UTF-8이어도 깨진 문자열이 그대로 출력된다.

추가 요인:
- Gradle 태스크 실행(`:...main()`) 경로를 사용하고 있었고,
- 이전에 잘못 생성된 클래스가 캐시/증분 빌드로 재사용될 수 있음(`UP-TO-DATE`).

## 4) 해결 방법
### 4.1 빌드 스크립트에 컴파일 인코딩 강제
`java/hello/build.gradle`에 추가:

```gradle
tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}
```

(필요 시 실행 인코딩도 함께 고정)

```gradle
tasks.withType(JavaExec).configureEach {
    jvmArgs '-Dfile.encoding=UTF-8'
}
```

### 4.2 기존 산출물/데몬 정리 후 재컴파일

```bash
./gradlew --stop
./gradlew clean compileJava
./gradlew com.hana8.hello.sol.ScanScore.main
```

### 4.3 IDE 보조 설정(권장)
- IntelliJ `File Encodings`: Global/Project/Properties 모두 UTF-8
- Run Configuration VM options: `-Dfile.encoding=UTF-8` (보조 용도)

## 5) 검증 기준
정상 기준:
- `ScanScore.main()` 실행 시 한글이 정상 출력
- `Charset.defaultCharset()` 출력이 `UTF-8`
- `clean` 후 재실행해도 재발하지 않음

## 6) 원래 설정으로 되돌리는 방법(롤백)
문제 해결 설정을 원복하려면:

1. `build.gradle`에서 아래 블록 삭제(또는 주석 처리)

```gradle
tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}
tasks.withType(JavaExec).configureEach {
    jvmArgs '-Dfile.encoding=UTF-8'
}
```

2. (추가했다면) `gradle.properties`의 인코딩 관련 항목 삭제

```properties
org.gradle.jvmargs=-Dfile.encoding=UTF-8
systemProp.file.encoding=UTF-8
```

3. 산출물 정리

```bash
./gradlew --stop
./gradlew clean
```

주의:
- 롤백 시 동일 이슈가 다시 발생할 수 있다.

## 7) 결론
이번 이슈는 "실행 인코딩"보다 "컴파일 인코딩"이 핵심이었다. 재발 방지를 위해서는 IDE 개인 설정에 의존하지 말고, `build.gradle`에서 `JavaCompile` 인코딩을 UTF-8로 강제하는 것이 가장 효과적이다.
