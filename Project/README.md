# ▶️ Project 실행 방법

### 1️⃣ H2 데이터베이스 서버 실행
- 먼저 H2 데이터베이스 서버를 실행합니다.

### 2️⃣ 프로젝트 폴더 이동
- Project 폴더로 이동합니다.

### 3️⃣ Spring Boot 애플리케이션 실행
- `src/main/java/HelloSpringApplication.java` 파일을 실행하여 Spring Boot 애플리케이션을 시작합니다.

---

# ▶️ Spring Boot 실행 방법

### 1️⃣ 기존 빌드 파일 삭제
- Remove-Item -Recurse -Force .\build

### 2️⃣ 빌드
- .\gradlew build

### 3️⃣ 실행
- java -jar build\libs\hello-spring-0.0.1-SNAPSHOT.jar

---

# ⚠️ 주의할 점

### 1️⃣ 빌드 폴더 중복 오류
- 프로젝트 실행 시 Gradle이 자동으로 빌드를 생성하므로, `build` 폴더가 중복되어 오류가 발생할 수 있습니다.
- 이 경우, `build` 폴더를 **수동으로 삭제**한 후, 다시 실행하면 됩니다.

### 2️⃣ `.vscode` 폴더 중복 생성
- 프로젝트 폴더로 이동하지 않고 `Spring-Study` 디렉토리에서 실행할 경우, `.vscode` 폴더가 생성됩니다.
- 이 폴더는 프로젝트 폴더 내에 이미 정의되어 있어, **중복된 의미 없는 폴더**가 발생할 수 있습니다.
- 따라서, 반드시 **프로젝트 폴더로 이동한 후 실행**해야 합니다.

---
