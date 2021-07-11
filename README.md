# 사전과제 1 - 결제 시스템
## 개발 환경
### 기본 환경
- IDE: Eclipse
- OS: Window
- GIT
### Server
- Java11
- Spring Boot
- JPA
- H2
- Gradle
- Junit
### Build 환경 (Eclipse IDE)
1.Git Clone https://github.com/ichhwang/preProject.git

2.Gradle Project import
- Configure > Add  Gradle Nature

3.Refresh Gradle proejct
- Gradle > Refresh Gradle Project

4.Encoding
- Properties > Resource > Text file encoding > UTF-8

5.Build

6.Run Spring Boot
### 접속 Base URI: http://localhost:8080
### 도메인(ENTITY)
우리 회사는 감독원 규정을 준수하여 동의하지 않은 개인정보는 저장하지 않습니다.

카드정보는 카드사와 통신(PaymentDetail 조회)을 통해서 획득합니다.
- Payment(당사 결제 테이블)
```
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DETAIL_ID")
	private PaymentDetail detail;
```
- Cancel(당사 취소 테이블)
```
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENT_ID")
	private Payment payment;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DETAIL_ID")
	private PaymentDetail detail;
```
- PaymentDetail(카드사 테이블)
### API 기능명세
문제 유출 방지를 위해서 API는 호출/결과 예시로 간결하게 작성합니다.

#### 1.결제 API
```
- PUT localhost:8080/payment
```
- 예시)

호출
```
localhost:8080/payment?cardNo=123456781231&expiryDate=1221&cvc=456&installmentMonth=12&paymentAmount=11000
```

결과
```
{
    "id": "04020210710235212879",
    "contents": " 446PAYMENT   04020210710235212879123456781231        121221456     110000000001000                    Fs0HGIdXCRTJ4M0CalVe3TLEALmdsXTGSQIC5KwA6rU=                                                                                                                                                                                                                                                                                                               ",
    "rsltCd": 200,
    "rsltMsg": "OK"
}
```
#### 2.결제취소 API
```
- POST localhost:8080/cancel
```
- 예시)

호출
```
localhost:8080/cancel?id=53320210710235126161&cancelAmount=7000
```
결과
```
{
    "id": "04520210710235133066",
    "contents": " 446CANCEL    04520210710235133066123456781231        001221456      70000000000636                    Fs0HGIdXCRTJ4M0CalVe3TLEALmdsXTGSQIC5KwA6rU=                                                                                                                                                                                                                                                                                                               ",
    "rsltCd": 200,
    "rsltMsg": "OK"
}
```
#### 3.데이터 조회 API
```
- GET localhost:8080/info
```
- 예시)

호출
```
localhost:8080/info?id=53320210710235126161
```
결과
```
{
    "id": "53320210710235126161",
    "cardNo": "******78****",
    "expiryDate": 1221,
    "cvc": 456,
    "type": "PAYMENT",
    "amount": 11000,
    "vat": 1000,
    "rsltCd": 200,
    "rsltMsg": "OK"
}
```
#### API 호출 결과 코드와 메시지
```
- 200 OK 정상
- 901 The amount paid is not sufficient. 취소금액이 결제된 금액 이하입니다.
- 902 Missing required values. 필수입력 항목이 누락되었거나 잘못되었습니다.
```
### 테스트
#### 1.부분취소 TestCase1
- InadvanceProjectApplicationTests.testCase1
- 결과

```
	***** START testCase1 START *****
	***결재금액:11000 vat:1000
	***취소1 OK ,최종결재금액:9900 vat:900
	***취소2 OK ,최종결재금액:6600 vat:600
	***취소3 The amount paid is not sufficient ,최종결재금액:6600 vat:600
	***취소4 The amount paid is not sufficient ,최종결재금액:6600 vat:600
	***취소5 OK ,최종결재금액:0 vat:0
	***취소6 The amount paid is not sufficient ,최종결재금액:0 vat:0
	***** END testCase1 END *****`
```	
#### 2.부분취소 TestCase2
- InadvanceProjectApplicationTests.testCase2
- 결과

```
	***** START testCase2 START *****
	***결재금액:20000 vat:909
	***취소1 OK ,최종결재금액:10000 vat:909
	***취소2 The amount paid is not sufficient ,최종결재금액:10000 vat:909
	***취소3 OK ,최종결재금액:0 vat:0
	***** END testCase2 END *****
```	
#### 3.부분취소 TestCase3
- InadvanceProjectApplicationTests.testCase3
- 결과

```
	***** START testCase3 START *****
	***결재금액:20000 vat:1818
	***취소1 OK ,최종결재금액:10000 vat:818
	***취소2 The amount paid is not sufficient ,최종결재금액:10000 vat:818
	***취소3 OK ,최종결재금액:0 vat:0
	***** END testCase3 END *****
```
	
#### 4.Multi Thread 환경 대비 - 하나의 카드번호로 동시 결제할 수 없습니다.
- InadvanceProjectApplicationTests.testPaymentLock
- Version 컬럼 생성
```Java
@Column
@Version
private long version;
```
- 비관적 잠금 설정
```Java
@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
public Payment findById(long id);
```
- Thread 10개 생성하여 테스트
- 결과
```Java
Exception in thread "Thread-2" org.springframework.orm.jpa.JpaSystemException: Unable to commit against JDBC Connection; nested exception is org.hibernate.TransactionException: Unable to commit against JDBC Connection
Caused by: org.hibernate.TransactionException: Unable to commit against JDBC Connection
Caused by: org.h2.jdbc.JdbcSQLNonTransientConnectionException: Database is already closed (to disable automatic closing at VM shutdown, add ";DB_CLOSE_ON_EXIT=FALSE" to the db URL) [90121-200]
```
#### 5.Multi Thread 환경 대비 - 결제한 건에 대한 취소를 동시에 할 수 없습니다.
- InadvanceProjectApplicationTests.testCancelLock
- Version 컬럼 생성
```Java
@Column
@Version
private long version;
```
- 비관적 잠금 설정
```Java
@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
public Payment findById(long id);
```
- Thread 10개 생성하여 테스트
- 결과
```Java
Exception in thread "Thread-5" org.springframework.orm.ObjectOptimisticLockingFailureException: Object of class [com.insurance.payment.entity.Payment] with identifier [00920210711101419963]: optimistic locking failed; nested exception is org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [com.insurance.payment.entity.Payment#00920210711101419963]
Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [com.insurance.payment.entity.Payment#00920210711101419963]
```
