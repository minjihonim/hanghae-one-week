# 동시성 제어 방식에 대한 분석 및 보고

- 동시성 제어는 여러 스레드가 동시에 실행될 때 데이터 무결성과 응답성을 보장하기 위한 다양한 기술과 기법
- 동시성 제어는 멀티스레드 프로그래밍이나 병렬 처리 환경에서 필수적인 요소

### 동시성 이슈란?

스레드는 cpu 작업의 한단위이다.

여기서 멀티스레드 방식은 멀티태스킹을 하는 방식 중, 한 코어에서 여러 스레드를 이용해서 `번갈아` 작업을 처리하는 방식이다.

멀티 스레드를 이용하면 공유하는 영역이 많아 프로세스방식보다 **context switcing(작업전환)** 오버헤드가 작아, 메모리 리소스가 상대적으로 적다는 장점이 있다.

하지만 **자원을 공유해서 단점도 존재한다.**

그게 바로, **동시성(concurrency) 이슈**이다.

여러 스레드가 동시에 하나의 자원을 공유하고 있기 때문에 같은 자원을 두고 **경쟁상태(raceCondition)** 같은 문제가 발생하는 것이다.

❗ 여기서 `동시성` 과 `병렬성`도 비교해보면 좋을 것이다.

!https://velog.velcdn.com/images/mooh2jj/post/ea921599-62ae-4553-b3ac-aa0f9f92c508/image.png

## 멀티 쓰레드를 사용하는 이유 ?

- 자바에서 멀티 스레드를 사용하는 이유는 **CPU 사용율을 향상시켜 자원을 보다 효율적으로 사용할 수 있기 때문**이다. 정확히는 CPU 사용율을 높이기 위해 멀티스레딩을 사용하는 것이다.

## Race Condition 을 해결하자

- Race Condition 이란 멀티 쓰레드 환경에서 공유 자원에 대한 쓰기 동작을 진행할 때 순서나 여러 조건에 의해서 결과값에 영향을 주는 상태를 의미함
- 이는 프로그램의 잘못된 동작으로 이어지며 클라이언트에게 잘못된 결과값을 반환할 위험성이 존재함

## Race Condition 에 대한 소프트웨어적인 해결방안: Monitor

- 프로세스 동기화에 대해서 굉장히 편리하고 효율적인 메커니즘을 제공해주는 ADT
    - Abstract Data Type (추상 데이터 타입)
    - **모니터(Monitor)**: 여러 스레드가 공유 자원을 안전하게 사용할 수 있도록 동기화를 지원하는 고급 추상화.
    - ADT는 프로세스 동기화에서 데이터를 처리하거나 연산을 수행할 때 사용되는 **추상화된 데이터 구조**를 의미하며, 동기화 메커니즘을 효율적으로 구현하는 데 도움을 줍니다
    
    https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbgkiPi%2FbtsBFE8K5Va%2FSDB85v0zKWAyiZOSEvxHz1%2Fimg.png
    
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

- 자바에서 제공해주는 **synchronized 키워드** 는 이러한 Monitor 기반의 동기화 메커니즘을 제공한다.
1. synchronized method 
- Method 단위에 synchronized를 적용하게 되면 인스턴스 단위 Monitor Lock 이 걸리게 된다.

## 생각해보기

- 자바의 synchronized 라는 편리한 기능으로  동시성 제어를 할 수 있음을 알았다.
    - 하지만 100건의 요청이 들어왔을 때 1건당 수행시간이 1초라고 할 때 마지막 100번째 요청은1분 40초 대기 후에 완료된다. ⇒ 도메인 적으로 성능이슈 가능성이 있어 보인다. ⇒ 해결 방법은?

## 문서 참조 자료

- https://sjiwon-dev.tistory.com/43
- [https://velog.io/@mooh2jj/멀티-스레드의-동시성-이슈](https://velog.io/@mooh2jj/%EB%A9%80%ED%8B%B0-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%9D%98-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88)
- chatGPT
