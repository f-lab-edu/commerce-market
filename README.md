# commerce-market
마켓플레이스 형태의 커머스 프로젝트입니다.
서버 관련 학습에 집중하기 위해 클라이언트는 카카오 오븐을 이용해 프로토타입으로 제작했습니다.

자세한 구현 내용은 Pull Requests에서 확인해주시면 감사하겠습니다.

# 프로젝트 구조
![프로젝트구조](https://github.com/f-lab-edu/commerce-market/assets/96982575/80adb01d-dadb-4fa8-b0ac-d80b7d563b1f)

# 사용 기술 및 개발환경
Java11, Spring Boot 2.x, Gradle, JPA, Redis, MySQL, OAuth 2.0, Docker, Jenkins, Naver Cloud Platform

# 프로젝트 주요 관심사

- 관심사를 분리하여 코드의 유지보수성을 향상시키기 위해 노력했습니다.
- 대용량 트래픽의 상황을 가정하여 서버 성능을 개선하기 위해 노력했습니다.
- 성공하는 테스트 뿐만 아니라, 실패 테스트를 작성하여 테스트 커버리지를 향상시켰습니다.
- 특정 기술을 도입하는데 명확한 이유와 근거를 기반으로 하였습니다.
- 기술에 적용된 다양한 디자인 패턴을 학습하여 객체지향 원리를 이해하기 위해 노력했습니다.

# 기술적 이슈와 해결과정
- Junit 활용한 단위 테스트 작성 및 테스트 커버리지를 향상시키기 위해 노력
- Git Issue를 활용한 Task 분리
- 문제 해결과정을 WIKI와 블로그로 정리
- 코드리뷰를 통한 커뮤니케이션(치열하게 토론 후 결과에 승복)
- 유스케이스 다이어그램, 시퀀스 다이어그램 활용하여 비즈니스 로직 설명 경험
- Jenkins 배포 파이프라인 구축
- Nginx 서버를 활용한 로드밸런싱 구축
- JMH 벤치마킹 툴을 활용한 성능 측정
- N+1 문제 해결
- 데이터베이스 Master-Slave 구조 활용
- 데이터베이스 역정규화로 조인 연산 최소화
- 데이터베이스 인덱스 설정으로 응답속도 개선
- 낙관적락과 사용자 인터렉션을 통해 동시성 제어
- 전략패턴을 활용한 코드 추상화
- 분산 환경에서 세션, 캐시 데이터 공유 방법 고민 (NoSQL 활용)
- FetchJoin, 1차캐시를 활용해 DB와의 통신 줄이기
- CompletableFuture 활용한 결제 요청 비동기 처리, 보상트랜잭션으로 예외 처리
- 데이터베이스 부하를 줄이기 위해 캐시서버 도입과정
- SpringSecurity와 OAuth를 활용한 보안 기능 구현
- AOP를 통한 횡단관심사 분리
- Git Rebase를 활용하여 커밋트리 가독성 향상

# 브랜치 전략

브랜치 전략은 Git-flow를 사용했습니다. Git-flow에는 항상 유지되는 메인 브랜치(`main`, `develop`)와 일정 기간 동안 유지되는 보조 브랜치(`feat`, `release`, `hotfix`)로 총 5가지 종류의 브랜치가 있습니다. 

해당 프로젝트는 서비스 운영을 목적의 목적이 아니었기 때문에 `develop`, `release`, `hotfix와` 같이 불필요한 브랜치를 사용하지 않고 `feat` 브랜치를 직접 `main` 브랜치로 병합하는 방식을 사용했습니다.

또한 병합 과정에서 rebase를 사용하여 커밋 트리를 단순화시켰습니다. 자세한 내용은 [wiki](https://github.com/f-lab-edu/commerce-market/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90-Git-flow-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0#2-main-%EB%B3%80%EA%B2%BD%EC%82%AC%ED%95%AD%EC%9D%84-feature%EB%A1%9C-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0---rebase)를 참고해주시면 감사하겠습니다.

# DB ERD
![ERD](https://github.com/f-lab-edu/commerce-market/assets/96982575/98f9d8f1-9b2b-43b8-ae0f-900e9a6e9879)
[ERD 설계 과정](https://github.com/f-lab-edu/commerce-market/wiki/ERD)

# 유스케이스 다이어그램
<img width="815" alt="유스케이스다이어그램" src="https://github.com/f-lab-edu/commerce-market/assets/96982575/0b4bac12-337c-47ca-892e-8d81bd263133">


[유스케이스 설명](https://github.com/f-lab-edu/commerce-market/wiki/%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%9C%A0%EC%8A%A4%EC%BC%80%EC%9D%B4%EC%8A%A4)

# OAuth 인가 시퀀스 다이어그램
![인가 시퀀스다이어그램](https://github.com/f-lab-edu/commerce-market/assets/96982575/de3d0d53-4e10-4b31-9fcb-7fe64efc4fc9)

자세한 내용은 [wiki](https://github.com/f-lab-edu/commerce-market/wiki/OAuth-2.0-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%8F%99%EC%9E%91%EA%B3%BC%EC%A0%95)를 확인해 주시면 감사하겠습니다.
 
# 화면 프로토타입
![화면프로토콜 drawio](https://github.com/f-lab-edu/commerce-market/assets/96982575/68e0ec04-612d-4431-b4ed-ddb7f7446282)


