# 동시성 제어 방식에 대한 분석 및 보고
- 동시성 제어는 여러 스레드가 동시에 실행될 때 데이터 무결성과 응답성을 보장하기 위한 다양한 기술과 기법
- 동시성 제어는 멀티스레드 프로그래밍이나 병렬 처리 환경에서 필수적인 요소

## Race Condition 을 해결하자
- Race Condition 이란 멀티 쓰레드 환경에서 공유 자원에 대한 쓰기 동작을 진행할 때 순서나 여러 조건에 의해서 결과값에 영향을 주는 상태를 의미함
- 이는 프로그램의 잘못된 동작으로 이어지며 클라이언트에게 잘못된 결과값을 반환할 위험성이 존재함

## Race Condition 에 대한 소프트웨어적인 해결방안: Monitor
- 프로세스 동기화에 대해서 굉장히 편리하고 효율적인 메커니즘을 제공해주는 ADT
![이미지파일URL “이미지이름”](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbgkiPi%2FbtsBFE8K5Va%2FSDB85v0zKWAyiZOSEvxHz1%2Fimg.png)

- Monitor 내부에는 다음과 같은 자원들이 존재한다.
  - 공유데이터
  - Monitor Lock
  - 1개 이상의 Condition Variable
 
- Monitor 내부의 공유 자원에 접근하기 위해서는 ADT 에서 제공해주는 API를 통해서만 접근이 가능하다.
- Monitor 내부에서는 오직 하나의 프로세스/쓰레드만이 Active 하다.

### Entry Queue & Waiting Queue
### Entry Queue
- Monitor 진입 시점에서 Mutual Exclusion을 보장하기 위한 Queue
  - Mutual Exclusion을 위한 Queue
- Entry Queue를 통해서 Monitor 내부에 동시에 여러 프로세스/쓰레드들이 진입하는 것을 막는다.

### Waiting Queue
- 내부에서 여러 로직 실행중에 Condition Variable의 wait()에 의해서 잠시 쉬러 들어가는 Queue
  - Conditional Synchronization을 위한 Queue
- Active 상태에서 로직을 실행하다가 Blocking되는 순간 들어가는 Queue
- Active Process/Thread가 signal()을 호출하면 Waiting Queue에 존재하는 여러 프로세스/쓰레드들이 Active를 위해서 다시 경쟁하게 된다.

## 자바에서 제공해주는 synchronized
- 자바에서 제공해주는 __synchronized 키워드__ 는 이러한 Monitor 기반의 동기화 메커니즘을 제공한다.
1. synchronized method </br>
- Method 단위에 synchronized를 적용하게 되면 <span style="color: tomato">인스턴스 단위 Monitor Lock</span> 이 걸리게 된다.
- 해당 방식을 사용하여 과제를 풀어 냄


## 문서 참조 자료
- https://sjiwon-dev.tistory.com/43  
- chatGPT
