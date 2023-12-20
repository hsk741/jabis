# 삼쩜삼 사전과제 - 사용자의 환급액 계산 기능 구현하기
## 목차
- [Swagger주소](#Swagger주소)
- [개발 환경 및 프레임워크](#개발-환경-및-프레임워크)
- [테스트 방법 및 절차](#테스트-방법-및-절차)
- [설계](#설계)

---
## Swagger주소
- http://localhost:8080/swagger-ui.html

## 개발 환경 및 프레임워크
- 개발 환경
  - IDE: IntelliJ IDEA
  - Java17
  - Spring Boot 3.2.0
  - Spring Data JPA
  - JUnit5
  - H2 2.1.210
  - Gradle
  - Tomcat

## 테스트 방법 및 절차
  - profile : test
  - 회원정보, 스크랩, 결정세액 및 퇴직연금세액공제금액조회의 경우 회원가입 및 로그인을 했다는 가정하에 테스트 데이터를 DB에 insert한 후 테스트 수행
  - 기능별 테스트 케이스
    - 회원가입
      - 회원가입요청_사용자아이디_파라미터검증 : 요청 정보 중 사용자 ID가 null이거나 empty인 경우 validation 오류
      - 회원가입요청_주민등록번호_파라미터검증 : 요청 정보 중 주민등록번호가 정해진 정규표현식 규칙에 어긋난 데이터값이 전송될 때 validation
      - 회원가입_불가능 : 회원가입 가능한 사용자(scraping이 가능한 사용자)이외의 이름과 주민등록번호가 요청되었을 때 회원가입 불가 테스트
      - 회원가입_기가입자 : 기존 가입자가 다시 회원가입을 요청한 경우 이미 가입됨을 응답하는 테스트
      - 회원가입 : 회원가입 가능한 사용자(scraping이 가능한 사용자) 모두에 대한 회원가입 테스트
    - 로그인
      - 로그인_사용자ID_공백검증 : 요청 정보 중 사용자 ID가 null이거나 empty인 경우 validation 오류
      - 로그인_암호_공백검증 : 요청 정보 중 암호가 null이거나 empty인 경우 validation 오류
      - 로그인_미존재_사용자ID_검증 : 요청한 사용자ID가 존재하지 않는 경우 NotFoundUserException 응답
      - 로그인_암호_파라미터검증 : 요청한 사용자ID에 대한 암호가 불일치 하는 경우 NotMatchedPasswordException 응답
      - 로그인
        - 요청 사용자에 대해 존재여부를 확인하여 존재시 30분 유효기간을 가지는 JWT토큰을 생성하여 응답
    - Authorization헤더에 Bear토큰으로 JWT
      - Authorization헤더_Bearer_토큰_미존재
        - jwt토큰으로 인가받고 접근해야 하는 기능(회원정보, 스크랩, 결정세액과 퇴직연세액공제금액 조회 등)들에 토큰없이 접속시 InvalidBearerTokenException 응답
      - Authorization헤더_Bearer_토큰_만료
        - jwt토큰이 만료된 경우 ExpiredTokenException 응답
    - 회원정보 조회
      - 요청시 http헤더에 JWT토큰을 담아 보내고 서버에서 JWT토큰으로부터 사용자 ID를 추출
      - 추출한 사용자 ID에 대한 사용자 정보를 응답 
    - 스크래핑_결정세액과퇴직연세액공제금액조회
      - 요청시 http헤더에 JWT토큰을 담아 보내고 서버에서 JWT토큰으로부터 사용자 ID를 추출
      - 사용자 ID에 대한 주민등록번호를 Scraping서버에 request로 전송하고 응답결과를 사용자에게 전송 
    - 스크래핑정보미존재
      - 요청시 http헤더에 JWT토큰을 담아 보내고 서버에서 JWT토큰으로부터 사용자 ID를 추출
      - 추출한 사용자 ID에 대한 scraping정보가 존재하지 않을 경우 NotFoundScrapingDataException발생
    - 스크래핑서버_통신에러
      - 요청시 http헤더에 JWT토큰을 담아 보내고 서버에서 JWT토큰으로부터 사용자 ID를 추출
      - 추출한 사용자 ID에 대한 주민등록번호를 Scraping서버에 request로 전송하고 응답을 받는 중 에러 발생시 ScrapFailureException을 발생시키도록 가정 후 응답 확인
    - 스크래핑 mock서버통신_확인
      - scraping서버와 통신이 안되는 경우를 대비하여 scraping mock server를 구동하고 request, response를 미리 정의하여 테스트 가능한 형태로 구성  
- 온라인 테스트
  - profile : local 
  - Swagger를 통한 테스트 : 본래 OpenAPI를 위한 명세를 목적으로 사용하나 여기서는 테스트를 용이하게 하기 위한 목적으로 구성
    - http://localhost:8080/swagger-ui.html 접속
    - 테스트 하고자 하는 기능에 대해  [Try it out]버튼을 누르고 아래 [Request Body]탭에 요청데이터를 지정
    - 아래 [Execute]버튼을 눌러 실행
    - 아래 [Responses] - [Server Response]항목에 나타나는 Code, Details값으로 확인
    - 회원가입 및 로그인 테스트 후 응답에 표시된 token값(tokenValue)을 Swagger맨 상단에 [Authorize]나 기능별 URL오른쪽에 표시되는 열쇠아이콘 눌렀을 때 표시되는 화면의 [Value]에 입력하여 [Authorize]한다.
      - 위 과정을 통해 부여된 토큰을 Authorize를 통해 등록하여야 회원정보, 스크랩, 결정세액 및 퇴직연금세액공제금액 조회시 Bearer토큰형태로 http header에 추가되어 요청한다.
![Bearer-input](https://user-images.githubusercontent.com/85001/91781994-d722e980-ec36-11ea-805e-88e7afca26d8.png)
![Bearer-result](https://user-images.githubusercontent.com/85001/91782055-046f9780-ec37-11ea-99fe-03112ab82720.png)
    - scrap서버는 사전과제문서에 기술된 실scrap서버 사용

## 구현방법
- 회원가입
  - 어플리케이션 구동시 회원가입 가능한 사용자(enable-user.csv)를 로드하여 회원가입가능 사용자 테이블에 저장한다.
    - 회원가입가능 사용자 테이블은 실제 외부 scraping서버를 통해 scrap가능한 사용자만을 저장 
  - 사용자 요청에 대한 request body를 UserProfileDto로 mapping
  - UserProfileDto의 이름(name)과 주민등록번호(regNo)가 회원가입가능 사용자 테이블에 존재하는지 여부를 확인하여 미존재 하는 경우 NotEnabledUserException발생
  - 주민등록번호(regNo)로 사용자 존재여부를 확인(DB조회) 존재할 경우 AlreadyExistedUserException 발생
  - 암호는 단방향 해시 BCrypt, 주민등록번호는 AES256방식으로 암호화하여 DB저장
  - 생성된 UserProfile엔티티를 UserProfileResponse로 변환하여 응답
- 로그인
  - 사용자 요청의 request body를 UserIdPasswordToken으로 mapping
  - ID체크 : 사용자ID(userId)로 사용자 존재여부를 확인(DB조회) 미존재할 경우 NotFoundUserException 발생
  - 패스워드 체크 : UserIdPasswordToken와 password와 ID체크시 조회한 사용자 정보의 암호화된 password값을 검증
    - 검증 성공 : JWT토큰을 생성하여 응답
      * 토큰 생성 및 검증 참고
    - 검증 실패 : NotMatchedPasswordException발생
- 가입회원정보 조회
  - 사용자ID(userId)로 사용자 정보를 DB에 조회하며 미존재할 경우 NotFoundUserException 발생
  - 위에서 조회한 UserProfile엔티티를 UserProfileDto로 변환하여 응답(주민등록번호는 decrypt하여 값 설정)
- 스크랩
  - 사용자ID(userId)로 사용자 정보를 DB에 조회하며 미존재할 경우 NotFoundUserException 발생
  - 사용자명과 주민등록번호를 가지는 ScrapRequest객체를 생성하여 외부 scrap서버에 사용자의 환급금 정보를 요청
  - 요청 성공한 경우 ScrapResponse객체로 mapping하여 반환
  - 요청 처리 중 예외가 발생한 경우나 요청에 대한 응답을 수신하였으나 ScrapResponse의 status값이 success가 아닌 경우 ScrapFailureException발생
  - 요청에 대한 예상치 못한 예외가 발생한 경우 예외메시지를 RuntimeException에 담아 예외 발생
  - userId기준 기존 스크랩정보(급여정보, 소득공제정보, 산출세액 등) 정보가 존재하면 모두 삭제
    - 여러 번 scraping요청에도 응답데이터가 이전 응답데이터와 구분할 수 있는 항목이 존재하지 않아 이전 scraping을 삭제하고 새로 scraping데이터를 insert 
  - mapping된 ScrapResponse객체에서 스크랩정보(급여정보, 소득공제정보, 산출세액 등)를 DB에 저장
  - scrap한 정보 중 jsonList정보를 응답
- 결정세액 및 퇴직연금세액공제금액 계산 및 조회
  - 사용자ID(userId)로 사용자 정보를 DB에 조회하며 미존재할 경우 NotFoundUserException 발생
  - 사용자ID(userId)로 기 스크랩정보(급여정보, 소득공제정보, 산출세액 등)를 조회하며 미존재할 경우 NotFoundScrapingDataException발생
  - RefundPolicy에 스크랩한 정보 중(산출세액, 총급여, 퇴직연금, 보험료, 의료비, 보험료, 기부금)을 전달하여 결정세액 및 퇴직연금세액공제금액 조회
  - ScrapRefundResponse에 사용자명, 결정세액 및 퇴직연금세액공제금액을 설정하여 응답 
- 토큰 생성 및 검증
  - 토큰은 회원가입 후 로그인 성공시마다 JWT토큰, 발행일자, 만료일자를 포함한 OAuth2Token구현객체(DefaultOAuth2Token)로 제공
  - JWT토큰 생성, 파싱 및 검증은 OAuth2TokenConverter구현체인 JwtOAuth2TokenConverter에서 수행 
    - JWT토큰 생성
      - JwtOAuth2TokenConverter의 encode메소드를 통해 수행
      - 토큰 발급자(iss): application.yml파일의 spring.application.name
      - 토큰 요청자(sub), 토큰 수신자(aud): 토큰 요청/수신자 ID
      - 토큰 발행일자(iat): 로그인 처리중 JWT토큰 생성시점
      - 토큰 만료일자(exp): 토큰 발행일자 기준 30분 이후 시점
        - application.yml의 jwt.access-expiration-time에 초단위로 설정
      - 토큰 활성화시점(nbf): 로그인 처리중 JWT토큰 생성시점
      - 토큰 헤더 kid : spring.application.name
    - JWT토큰 검증
      - 가입회원정보, 스크랩정보 조회, 결정세액 및 퇴직연금세액공제금액 조회 기능에서 수행
        - 해당 기능 요청시 Authorization헤더에 로그인시 JWT토큰을 Bearer type으로 전송
      - BearerTokenRequestInterceptor에서 검증 수행
        - Bearer토큰 존재 여부, Authorization헤더 문자열 형식 검증 수행
        - JwtOAuth2TokenConverter의 decode메소드를 통해 JWT토큰 parsing 및 검증 수행
  - JWT토큰 생성 및 검증 방식
    - 비대칭키(Asymmetric) 방식으로 생성 및 검증 
      - 로그인 서버에서 JWT토큰 header, payload생성 후 private key로 sign하여 생성
      - API 서버에서 JWT토큰 검증시 public key로 Bearer타입으로 전송된 JWT토큰 검증(JWT토큰 위변조 여부 판단)
      * 토큰 생성 및 검증이 동일 어플리케이션에서 수행됨에 따라 KeyPair(private key, public key)를 임의로 생성하여 application.yml의 jwt.private-key, jwt.public-key에 값 지정
        * 토큰을 생성하는 어플리케이션 관점에서 public key에 접근할 수 있는 URL(JWKㄴ)을 제공하며 토큰 검증시 JWK URL로 public key를 가져와 토큰 검증 수행
          - JWT kid값(spring.application.name값) 존재 여부 및 kid에 mapping된 public key를 가지고 서명 및 토큰 검증 수행
            - JWK url : http://localhost:8080/.well-known/jwks.json => resource폴더에 jwks.json 
            - JwtConfiguration의 jwtProcessor bean 참고

## 설계
- Entity
  - 회원
    - EnableUserProfile
      - 미가입 상태인 모든 사용자가 가입 가능한 것이 아니고, scraping가능한 사용자 계정에 한해서만 가입이 가능
      - scraping가능한 사용자 계정 정보로서 해당 테이블에 존재하는 사용자 계정에 대해서만 회원 가입이 가능
      - 구현
        - resources폴더에 enable-user.csv파일에 scraping가능한 사용자의 이름 및 주민등록번호가 기록
        - 어플리케이션 구동시 csv파일을 읽어서 해당 테이블에 insert
    - UserProfile
      - 가입된 온라인 환급서비스 사용자 계정 정보
  - Scrap
    - Salary : 급여
    - Deduction : 공제 및 산출세액
    - scraping서버로부터 응답메시지 분석해 본 결과
      - 같은 사용자에 대해서 여러번 호출하여도 scraping정보 중 변경되는 항목을 파악하기 어려움
        - 같은 사용자에 대해서 전체 scraping정보는 단건으로 가정하여 호출시마다 기존 scraping정보들을 모두 제거 후 새로 응답받은 데이터를 최신화
      - 데이터 구조는 하나의 소득공제에 대해서 실질적으로 급여정보는 단건만 수신되고 있으나 배열로 정의되어 있는 것으로 보아 다건으로 판단하여 Deduction(공제, 산출세액)와 Salary(급여)와의 관계를 1:N으로 판단
        - Salarm : Deduction => N:1 양방향 관계로 mapping

- Authentication
  - 구현과제에서 필요한 인증관련 구현방식은 stateless한 JWT토큰방식 채택
    - stateless(no cookie, no session)를 만족하기 위해 self-contained(토큰자체 정보내장) JWT토큰도입
      - 이에 따라 JWT검증도 인증서버가 아닌 client app이나 resource서버가 수행하는 것으로 가정하여 별도의 토큰 관련 테이블, 부수적인 보안 기능 관련 테이블 배제
      - refresh토큰 배제
      - JWK rotate key 배제
      - 이외 등등
    - 위에 서술한 토큰 생성 및 검증을 구현하기 위해 JWT, JWS, JWK(S) 표준 specification 중 환급서비스에 도입가능한 부분만 세부적으로 적용
      - OIDC의 id_token배제 등 