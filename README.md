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
- [N+1 문제 해결](https://velog.io/@taebong98/N1-%EB%AC%B8%EC%A0%9C-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85)
- [DataBase Replication 필요성과 적용과정](https://velog.io/@taebong98/MySQL-Replication)
- [낙관적 락을 활용한 동시성이슈 해결과정](https://velog.io/@taebong98/%EB%82%99%EA%B4%80%EC%A0%81-%EB%9D%BD%EC%9C%BC%EB%A1%9C-%EB%8F%99%EC%8B%9C%EC%84%B1%EC%9D%B4%EC%8A%88-%ED%95%B4%EA%B2%B0)
- [전략패턴을 활용한 중복코드 제거](https://velog.io/@taebong98/%EC%A0%84%EB%9E%B5%ED%8C%A8%ED%84%B4%EC%9C%BC%EB%A1%9C-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%A4%91%EB%B3%B5%EC%BD%94%EB%93%9C-%EC%A0%9C%EA%B1%B0%ED%95%98%EA%B8%B0)
- [분산서버 환경의 세션 스토리지 선택 고려사항](https://velog.io/@taebong98/%EB%8B%A4%EC%A4%91-%EC%84%9C%EB%B2%84-%ED%99%98%EA%B2%BD%EC%9D%98-%EC%84%B8%EC%85%98-%EC%8A%A4%ED%86%A0%EB%A6%AC%EC%A7%80-%EA%B3%A0%EB%A0%A4%EC%82%AC%ED%95%AD)
- FetchJoin, 1차캐시를 활용해 DB와의 통신 줄이기(In Progress)
- CompletableFuture를 활용해 결제로직 비동기 처리과정 (Todo)
- 데이터베이스 부하를 줄이기 위해 캐시서버 도입과정(Todo)

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


