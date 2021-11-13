# 공지사항 관리 REST API 구현
## Stack
- SpringBoot v2.5.6, JDK 11.0.10, Hibernate ORM core 5.4.32 final 기반
- Spring MVC, HATEOAS - REST API 구현
- Spring Data JPA, QueryDSL - DB 접근
- Spring Security, Validation - 간단한 인증, 인가 구현 
- Apache Commons IO - 다중 파일 업로드 구현 
- Spring Data Redis - 세션 관리 
- H2 Database - 간편한 데이터베이스 
- Lombok, Mapstruct - 코드 간소화, Entity - DTO 간 mapping 작업 간편화 
- Thymeleaf, BootStrap - SSR 활용 간단한 화면 구현


## 패키지 구조
- util - 유용한 객체들 
  - GenericMapper - Mapstruct interface
  - CustomPage - Paging Object
  - Messages - Constants Object
- config - 설정 
  - Redis - Session
  - Async - Asynchronous
  - JPA - Auditing & QueryDSL
  - Security - Authentication & Authorization
- domain - 도메인, DB 접근 객체
  - Entity - Image, Notice, User Entity
  - Repository - Data JPA
- service - 핵심 로직
  - Service - Business Logic
- controller - Back, Front
  - Api - REST API
  - View - Client Controller, Sending HTTP request by WebClient


## 구현 화면
### 게시글 작성 페이지
#### 제목, 내용, 시작일시, 종료일시, 다중파일 업로드 기능
<img width="1466" alt="Screen Shot 2021-11-13 at 17 51 05" src="https://user-images.githubusercontent.com/76534087/141612542-c47f6447-7e2d-4c59-ab11-5a6ac9ddef27.png">
<hr>

### 페이징 가능한 전체 목록 페이지
#### Pageable interface 활용 size, page 정보 Query param 으로 넘겨 받음
<img width="1000" alt="Screen Shot 2021-11-13 at 17 39 17" src="https://user-images.githubusercontent.com/76534087/141612113-9ee24fe7-9204-493a-b7d8-dd0da23db75f.png">
<img width="1000" alt="Screen Shot 2021-11-13 at 17 46 18" src="https://user-images.githubusercontent.com/76534087/141612342-ea7ad22b-8f95-4783-a4cd-5a1f25d0a942.png">
<hr>

### 게시글 조회 정보를 담은 조회 페이지
#### 수정 페이지로 이동 가능하며 삭제 요청과 이전 페이지로 돌아가는 기능 포함
<img width="1691" alt="Screen Shot 2021-11-13 at 23 10 17" src="https://user-images.githubusercontent.com/76534087/141646976-df84918d-c914-45d9-bb4f-d95c2c4a6496.png">
<hr>

### 게시글 수정과 삭제 요청 가능한 수정 페이지
#### 이미지 추가와 내용 수정은 Form 으로 한번에 요청<br> Edit 버튼은 비동기 요청으로 async-await POST 요청으로 전송<br> Submit 버튼은 POST로 ViewController로 데이터를 담아 보낸 후 ApiController로 PATCH 전송하는 방식
<img width="1516" alt="Screen Shot 2021-11-13 at 23 04 56" src="https://user-images.githubusercontent.com/76534087/141646781-46adbbdb-7bab-4625-a85b-b18f6ff8e2e2.png">
<hr>

#### 이미지 삭제는 비동기 처리로 따로 요청
<img width="1439" alt="Screen Shot 2021-11-13 at 23 05 06" src="https://user-images.githubusercontent.com/76534087/141646785-77e98a39-bb5b-48f2-8a75-fec641d0d086.png">
<hr>

### 회원 가입 페이지
#### 아이디 `@Pattern(regexp = "^[\\w]{4,8}$")` 정규식에 따라 특수 문자 제외 문자 4-8자 입력 가능
#### 비밀번호 `@Pattern(regexp = "^[\\S]{4,8}$")` 정규식에 따라 공백 제외 모든 문자 4-8자 입력 가능
<img width="669" alt="Screen Shot 2021-11-13 at 17 53 15" src="https://user-images.githubusercontent.com/76534087/141612609-4da154d1-fe7d-4f2a-b0b5-e8183284e193.png">
<hr>

### 로그인 페이지
#### 회원가입 페이지와 검증 동일
<img width="796" alt="Screen Shot 2021-11-13 at 17 53 26" src="https://user-images.githubusercontent.com/76534087/141612616-89d437af-2d4b-485b-afc2-5083cf421dfd.png">
<hr>


## 문제 해결 전략
### Detail Page
게시글 하나의 조회를 위해서 게시글 번호를 PathVariable로 받아 게시글 정보와 이미지 정보를 따로 요청<br>
리팩토링 진행 시 Redis Cache를 이용해 게시글 단건이나 이미지 정보를 캐싱하면 대용량 트래픽 처리 가능

<img width="566" alt="Screen Shot 2021-11-13 at 19 09 36" src="https://user-images.githubusercontent.com/76534087/141614732-6e0520c6-26ec-4811-ace9-55770ebc444d.png">
<hr>

### List Page
QueryDSL을 활용해 게시글을 역순으로 보여주기 위해 orderBy id desc 조건을 건 페이징 처리를 진행<br>

<img width="884" alt="Screen Shot 2021-11-13 at 19 18 23" src="https://user-images.githubusercontent.com/76534087/141614970-3d60fb99-10d4-4c2a-ad7d-27a0c822f225.png">
<hr>

@QueryProjection 애너테이션을 사용해 컴파일 시 Q 파일이 생성 후 직접 DTO 조회 가능<br>
Notice Entity에는 User 객체를 필드 변수로 가지고 있고 <br>
NoticeResponse에서는 user를 직접 반환하지 못 하도록 UserResponse 객체를 필드 변수로 가지고 있음<br>
user <-> userResponse 간 타입이 달라 변환이 되지 않기 때문에 QueryProjection에 사용되는 생성자에<br>
String 타입의 username을 받고 이를 이용해 생성자 내부에서 UserResponse 객체를 생성하도록 만들어 문제를 해결

<img width="911" alt="Screen Shot 2021-11-13 at 19 22 16" src="https://user-images.githubusercontent.com/76534087/141615051-20ce0c67-6cda-4e58-8e33-37bceec68a5a.png">
<hr>

### Soft Delete
게시글 삭제 요청이 오더라도 추후 게시글 복구 기능까지 예정에 둔다면 Notice Entity에 Status 상태 변수를 두고<br>
이 상태를 바꿈으로 Soft delete하고 배치 처리나 스케줄링을 통해 일정 기간이 지난 후에 DB 삭제를 진행<br>
상태값이 INACTIVE 경우 게시글이 조회되지 않도록 게시글 조회 쿼리에 Status = ACTIVE 조건을 걸어서 조회

### Multi File Upload
다중파일 업로드에 대해 자세히 알지 못해 정보를 찾아보고 튜토리얼 참고 후, 본 프로젝트에 맞게 리팩토링 진행<br>
이미지 처리를 담당하는 ImageProcessor 컴포넌트 생성 후 핵심 로직의 parse()로 이미지 처리 진행<br>
이미지 파일의 이름 중복을 막기 위해 `System.nanoTime()` 문자열로 변환해 저장<br>
이미지의 일자별 관리를 위해 업로드 날짜에 해당하는 `DateTimeFormatter.ofPattern("yyyyMMdd");` 패턴으로 폴더 생성

<img width="945" alt="Screen Shot 2021-11-13 at 18 27 47" src="https://user-images.githubusercontent.com/76534087/141613686-25a8afa0-8365-4dcd-8d1b-1e6652a0f5ab.png">
<hr>

### Paging
대용량 트래픽을 고려하여 전체 조회가 아닌 Page 정보를 활용한 목록 조회<br>
NoticeViewController에서 NoticeApiController로 목록 정보 요청한 후<br>
Response로 담겨오는 페이지 정보를 CustomPage에 담아 View로 내려줌

<img width="590" alt="Screen Shot 2021-11-13 at 17 58 18" src="https://user-images.githubusercontent.com/76534087/141612776-e1cbb42b-e2d1-452e-8e09-59184e808222.png">
<hr>

화면단에서는 다른 페이징들과 다르게 약 3 - 4줄로 간단하게 구현 가능 

<img width="826" alt="Screen Shot 2021-11-13 at 17 57 56" src="https://user-images.githubusercontent.com/76534087/141612769-aeb47fb2-d189-4591-97ca-4c6fe7ef87ab.png">
<hr>

### Test
32개의 테스트 코드

- Domain 테스트
- @DataJpaTest, Slicing Repository 테스트
- @SpringBootTest, Service 테스트
- @AutoConfigureMockMVc, @WebMvcTest Controller 테스트

<img width="1429" alt="Screen Shot 2021-11-13 at 18 06 05" src="https://user-images.githubusercontent.com/76534087/141612968-96b43de9-6254-4090-ad9f-ce7aa45d34ea.png">
<hr>

## Trouble Shooting
### Notice & Image
설계 초기에 Notice Entity에 이미지 파일 정보를 직접 가지고 있게 만드려 했는데 진행할수록 책임을 분리하는게 낫겠다 생각이 들어 Image Entity 생성 후 이미지 관련 정보를 옮김<br> 
게시글과 이미지는 하나의 게시글에 여러 이미지가 있을 수 있으므로 1:N 관계로 만듬<br>
NoticeId를 외래키로 가지고 있는 Image가 연관 관계의 주인이 됨<br>
게시글을 조회할 때 무조건적으로 이미지를 조회할 필요가 없다 생각해 Lazy Loading을 활용하려 했고<br> 
이미지가 필요한 경우 조회를 위한 쿼리에 fetch join을 걸었는데 No value present 에러 발생<br> 
아래 코드에 주석으로 배운 점을 기록

<img width="1177" alt="Screen Shot 2021-11-13 at 18 57 54" src="https://user-images.githubusercontent.com/76534087/141614451-3156ab66-8715-4a3f-bf27-dfbe7fd3bf89.png">
<hr>

### WebClient
REST API 활용을 위해 프론트단이 필요했는데 Vue.js는 아직 학습 중이라 대신 WebFlux WebClient 도입<br>
WebClient도 처음 사용해봐서 JSON 변환 작업을 하드코딩으로 하였음 프론트단 프레임워크의 필요성이 크다는 걸 깨달았음 

<img width="954" alt="Screen Shot 2021-11-13 at 18 13 17" src="https://user-images.githubusercontent.com/76534087/141613178-6bf893ed-fc73-4fe9-b600-ca373bb65852.png">
<hr>

WebClient에서의 multipart/form-data 전송 방법도 배웠고 현재 학습 중인 것들이 정리가 되면 WebFlux 도 학습할 예정

<img width="1143" alt="Screen Shot 2021-11-13 at 18 13 47" src="https://user-images.githubusercontent.com/76534087/141613197-9d9b08f9-17e7-466e-9442-fd0cf1e28933.png">
<hr>

### 수정 로직
multipart/form-data 데이터 사용에 익숙하지 않아 조금 헤맸지만 다양한 방법을 찾아보고 시도해 결국 v4 버전의 POST, PATCH 요청을 모두 처리할 수 있는 REST API endpoint 완성
<img width="1027" alt="Screen Shot 2021-11-13 at 18 10 29" src="https://user-images.githubusercontent.com/76534087/141613099-d7fc076b-3fe5-4324-beb9-f1469c751cef.png">
