# **Technical Constraints & Coding Conventions**

**Purpose:** Guidelines for AI to generate code that matches the project style.

## **1\. Backend Stack (Java/Spring Boot)**

- **Java Version:** Java 25 LTS (Required).
- **Framework:** Spring Boot 3.x.
- **Build Tool:** Gradle (Groovy DSL).
- **Lombok:** Heavily used for @Data, @Builder, @RequiredArgsConstructor.
- **Mapping:** Use MapStruct for Entity \<-\> DTO conversion. Do not write manual setters.
- **Testing:**
  - Unit Test: JUnit 5 \+ Mockito.
  - Assertion: AssertJ.

## **2\. Frontend Stack (Angular)**

- **Language:** TypeScript (Strict mode enabled).
- **Framework:** Angular 21 (Standalone Components preferred).
- **State Management:** RxJS (Observables).
- **Styling:** CSS
- **UI Library** taiga-ui

## **3\. Infrastructure & Cloud (AWS)**

- **Logging:** All logs must differ output format based on environment:
  - _Local:_ Console (Human readable).
  - _Production:_ JSON format (for CloudWatch/Splunk parsing).
- **Secrets:** Never hardcode credentials. Use environment variables or AWS Secrets Manager.

## **4\. Git & Version Control**

- **Branching:** Gitflow (feature/xxx, develop, main).
- **Commit Messages:** Conventional Commits (e.g., feat: add payment validation, fix: resolve jwt expiration).
