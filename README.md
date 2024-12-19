# 동시성 제어 방식에 대한 분석 및 보고

## Step 1. 동시성 개념을 학습하고 적용해보기
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

### 멀티 쓰레드를 사용하는 이유 ?

- 자바에서 멀티 스레드를 사용하는 이유는 **CPU 사용율을 향상시켜 자원을 보다 효율적으로 사용할 수 있기 때문**이다. 정확히는 CPU 사용율을 높이기 위해 멀티스레딩을 사용하는 것이다.

### Race Condition 을 해결하자

- Race Condition 이란 멀티 쓰레드 환경에서 공유 자원에 대한 쓰기 동작을 진행할 때 순서나 여러 조건에 의해서 결과값에 영향을 주는 상태를 의미함
- 이는 프로그램의 잘못된 동작으로 이어지며 클라이언트에게 잘못된 결과값을 반환할 위험성이 존재함

### Race Condition 에 대한 소프트웨어적인 해결방안: Monitor

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

### 자바에서 제공해주는 synchronized

- 자바에서 제공해주는 **synchronized 키워드** 는 이러한 Monitor 기반의 동기화 메커니즘을 제공한다.
1. synchronized method 
- Method 단위에 synchronized를 적용하게 되면 인스턴스 단위 Monitor Lock 이 걸리게 된다.

### 생각해보기

- 자바의 synchronized 라는 편리한 기능으로  동시성 제어를 할 수 있음을 알았다.
  - 하지만 100건의 요청이 들어왔을 때 1건당 수행시간이 1초라고 할 때 마지막 100번째 요청은1분 40초 대기 후에 완료된다. ⇒ 도메인 적으로 성능이슈 가능성이 있어 보인다. ⇒ 해결 방법은?
  - Queue 를 사용하여 해당 문제를 해결할 수 있다. ( 멘토링 INFO )
    - ConcurrentLinkedQueue 를 활용
    - 현업에서는 서버가 여러개 존재 하므로 Queue 로 해결하기 보다는 인메모리 DB인 단일 Redis 서버를 사용
   

## Step 2. Queue 탐색하기

### BlockingQueue VS ConcurrentLinkedQueue

ConcurrentLinkedQueue
- non-blocking lock-free queues.

BlockingQueue
- Blocking & Lock Queue

LinkedBlockingQueue는
- 생성자의 인자에 큐의 용량 capacity 를 명시하여 사이즈를 지정할 수 있음

ConcurrentLinkedQueue는
- 큐의 사이즈를 지정할 수 없을 뿐만 아니라 size 메서드는 상수 시간에 호출되지 않아서 큐에 들어있는 원소의 개수를 파악하는 것이 어려움

ConcurrentLinkedQueue는 큐에 꺼낼 원소가 없다면 즉시 리턴하고 다른 일을 수행하러 간다.   
따라서, ConcurrentLinkedQueue는 생산자-소비자 producer-consumer 모델에서 소비자가 많고 생산자가 하나인 경우에 사용하면 좋다.

여러 개의 쓰레드에서 하나의 Queue 객체에 들어있는 데이터를 꺼내기 위해 queue.poll() 메서드를 호출할 경우, 동일한 실행 결과가 나타날 수 있다.   
예를 들어, Queue에 [1, 2, 3]과 같은 데이터가 들어있을 경우, 스레드 3개가 critical section에서 poll() 메서드를 호출하면 각 스레드들은 모두 1이라는 데이터를 가져오고 Queue에는 [2, 3]만 남게 된다.   
큐가 비어있을 경우 null을 리턴한다.

LinkedBlockingQueue 은 이름에서도 알 수 있듯이 각각의 블로킹 큐가 링크드 노드로 연결된 큐이다. 큐에서 꺼내갈 원소가 없을 경우 해당 쓰레드는 wait 상태에 들어간다.   
따라서, LinkedBlockingQueue는 생산자가 많고 하나의 소비자일 경우에 사용하면 좋다. 또한 이 글의 서두에서 언급한 것처럼, LinkedBlockingQueue은 큐의 폭발을 막기 위해 생성자에 큐의 사이즈를 명시할 수 있도록 설계되었다.   
LinkedBlockingQueue 내에 있는 데이터를 가져오기 retrieve 위해 poll()과 take() 메소드를 제공한다.   
이 두 메소드의 차이점은 큐가 비어있을 때, poll 메소드는 null을 리턴하거나 Timeout을 설정할 수 있는 반면에, take 메소드는 꺼낼 수 있는 원소가 있을 때까지 기다린다(waiting).

락(lock) 메커니즘은 Mutex(mutual-exclusion) 입니다. 기본적인 Mutex의 사용방법은 공유 자원에 접근하기 전에 락을 걸고(lock), 공유 자원의 사용을 마치면 락을 풀어주는(unlock) 것입니다

Thread 가 Critical Section에 접근할때 (또는 processer로 부터 resource를 할당받을때) 해당 Thread는 세마포어의 카운트를 감소시키고 수행이 종료된 후엔 세마포어의 카운트를 원래대로 증가시킨다. 다시말하면 세마포어는 일종의 신호등의 역할을 하는것이다.

세마포어는 한정된 수의 사용자만을 지원할 수 있는 공유 자원에 대한 접근을 통제하는데 유용하다. MFC에서는 지금까지 알아본 Critical Section, Mutex, Semaphore에 대한 클래스를 제공해 준다. 이 클래스들은 모두 CSyncObject에서 파생되었으며, 따라서 사용법도 비슷하다.

세마포어(Semaphore) : 공유된 자원의 데이터를 여러 프로세스가 접근하는 것을 막는 것

뮤텍스(Mutex) : 공유된 자원의 데이터를 여러 쓰레드가 접근하는 것을 막는 것

** 뮤텍스란(Mutex)? **

“Mutual Exclusion 으로 상호배제라고도 한다. Critical Section을 가진 쓰레드들의 Runnig Time이 서로 겹치지 않게 각각 단독으로 실행되게 하는 기술입니다. 다중 프로세스들의 공유 리소스에 대한 접근을 조율하기 위해 locking과 unlocking을 사용합니다.

즉, 쉽게 말하면 뮤텍스 객체를 두 쓰레드가 동시에 사용할 수 없다는 의미입니다.

** 세마포어란?(Semaphore) **

” 세마포어는 리소스의 상태를 나타내는 간단한 카운터로 생각할 수 있습니다. 일반적으로 비교적 긴 시간을 확보하는 리소스에 대해 이용하게 되며,  
유닉스 시스템의 프로그래밍에서 세마포어는 운영체제의 리소스를 경쟁적으로 사용하는 다중 프로세스에서 행동을 조정하거나 또는 동기화 시키는 기술입니다.

세마포어는 운영체제 또는 커널의 한 지정된 저장장치 내 값으로서, 각 프로세스는 이를 확인하고 변경할 수 있습니다. 확인되는 세마포어의 값에 따라,   
그 프로세스가 즉시 자원을 사용할 수 있거나, 또는 이미 다른 프로세스에 의해 사용 중이라는 사실을 알게 되면 재시도하기 전에 일정 시간을 기다려야만 합니다.   
세마포어는 이진수 (0 또는 1)를 사용하거나, 또는 추가적인 값을 가질 수도 있습니다.   
세마포어를 사용하는 프로세스는 그 값을 확인하고, 자원을 사용하는 동안에는 그 값을 변경함으로써 다른 세마포어 사용자들이 기다리도록 해야합니다.

( 차이점들!! )

1) Semaphore는 Mutex가 될 수 있지만 Mutex는 Semaphore가 될 수 없습니다. (Mutex 는 상태가 0, 1 두 개 뿐인 binary Semaphore) 2) Semaphore는 소유할 수 없는 반면, Mutex는 소유가 가능하며 소유주가 이에 대한 책임을 집니다. (Mutex 의 경우 상태가 두개 뿐인 lock 이므로 lock 을 ‘가질’ 수 있습니다.) 3) Mutex의 경우 Mutex를 소유하고 있는 쓰레드가 이 Mutex를 해제할 수 있습니다. 하지만 Semaphore의 경우 이러한 Semaphore를 소유하지 않는 쓰레드가 Semaphore를 해제할 수 있습니다. 4) Semaphore는 시스템 범위에 걸쳐있고 파일시스템상의 파일 형태로 존재합니다. 반면 Mutex는 프로세스 범위를 가지며 프로세스가 종료될 때 자동으로 Clean up된다.

### 생각해보기
- 현재 내가 동시성 제어를 구현함에 있어 사용한 Queue 는 ConcurrentLinkedQueue 이다.
  - 포인트 사용 / 소비 처리를 큐를 각각 구현 함
  - 스케쥴 형태로 작동되도록 구현하였기 때문에 나의 Queue 구현체에서는 LinkedBlockingQueue 와 ConcurrentLinkedQueue 의 선택에 대해 크게 고려할 필요는 없다고 보여집니다.
 
## Step 3. 동시성 제어 통합 테스트 개발을 하기 위해 학습한 내용
- 멀티 쓰레드 테스트를 위한 ExecutorService 

## Step 4. 동시성 제어 개발 과정 정리
- 처음 동시성 제어 부분에 대한 개발은 단순 synchronized 매서드를 적용함
  - 하지만 대량의 작업 요청에 대해 지연시간이 길어지므로 서비스 제공 관점으로 봤을 때 효율성이 떨어짐
- 멘토링을 통해 큐를 적용해야 함을 알고 ConcurrentLinkedQueue 을 적용 함.
  - 큐를 작업할 매서드에 스케쥴링(@Scheduled) 어노테이션을 적용하여 1초마다 수행되도록 구현
  - 1초마다 큐를 처리하는 매서드가 수행되기 때문에 **논리적인 작업 상태(예: 작업 중인지 여부)** 를 확인하는 플래그의 필요성이 있어 보임
   - ConcurrentLinkedQueue는 작업 상태 추적이나 작업 락을 제공하지 않음.
- 다양한 케이스를 설정하여 통합테스트 진행
 
### 생각해보기
- 그럼 Queue 에 데이터가 쌓인 상태에서 서버의 오류가 발생하여 해당 Queue 의 작업이 이루어지지 않았을 경우 및 Queue 의 데어터가 초기화됐을 때는 어떻게 할 것인가 ?
  - 인 메모리 DB Redis 를 사용하도록 생각해보기

## 문서 참조 자료
- https://sjiwon-dev.tistory.com/43
- [https://velog.io/@mooh2jj/멀티-스레드의-동시성-이슈](https://velog.io/@mooh2jj/%EB%A9%80%ED%8B%B0-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%9D%98-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88)
- https://jjaesang.github.io/java/2019/07/22/java-blockingqueue-vs-concurrentLinkedQueue.html
- https://chatgpt.com/
- https://simyeju.tistory.com/119
