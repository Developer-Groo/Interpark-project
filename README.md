# 🤔 Interpark project

**인터파크(Interpark)와 유사한 콘서트 예매 및 인기 콘서트 검색 시스템**을 구현하는 백엔드 프로젝트입니다.  
주요 기능으로는 **콘서트 티켓 예매**, **인기 콘서트 검색**, **동시성 제어**, **캐시를 이용한 성능 최적화**가 포함됩니다.  
대량의 트래픽을 처리하는 환경을 고려하여 **Redis Lock을 활용한 동시성 제어**, **In-memory Cache & Redis Cache 적용** 등의 성능 개선 전략을 적용하였습니다.

## ☑️ Index
- [🏁 Team](#-Team)
- [📑 Commit Convention](#-Commit-Convention)
- [🛠 Technology](#-Technology)
- [🎯 Features](#-Features)
- [🔗 ERD](#-ERD)
- [🔥 Project Doc](#-Project-Doc)
- [🚨 Trouble Shooting](#-Trouble-Shooting)
- [🍰 Performance Comparison](#-Performance-Comparison)

<br>

## 🏁 Team
|**우현**|**혁규**|**진영**|**희현**|
|-------|-------|-------|-------|
|<img src="https://github.com/Developer-Nova/Sec19-Local-Data-Persistance_ByAngela/assets/123448121/17a2ba3b-a618-4ac8-93b9-0d0e02c19c78" width="110" height="110">|||<img src="https://github.com/user-attachments/assets/f7097a1a-52f7-4e77-9575-db7ac8ec5b0f" width="110" height="110">|
|[GitHub](https://github.com/Developer-Groo)|[GitHub](https://github.com/saintym)|[GitHub](https://github.com/dllll2)|[GitHub](https://github.com/HEEHYUN0221)|

<br>

## 📑 Commit Convention

**`feat`** : 새로운 기능 추가

**`fix`** : bug fix

**`docs`**  : 문서 수정

**`style`** : 세미콜론 같은 코드의 사소한 스타일 변화.

**`refactor`** : 변수명 수정같은 리팩터링

**`test`** : 테스트 코드 추가 & 수정

**`chore`** : 중요하지 않은 일

<br>

## 🛠 Technology
| **분야**        | **기술** |
|--------------|--------|
| **Backend** | Java 17, Spring Boot 3.x, JPA, QueryDSL |
| **DB** | MySQL 8.0, Redis |
| **Cache** | Spring Cache, Redis Cache |
| **Concurrency Control** | Redis Lock |
| **Testing** | JUnit5, MockMvc |
| **DevOps** | Docker |

<br>

## 🎯 Features
### **✅ 티켓 예매 기능**
- 콘서트 티켓을 **선착순으로 구매**할 수 있는 시스템 구현
- **Redis Lock을 이용한 동시성 제어** 적용 (동시에 많은 사용자가 구매 요청 시 데이터 정합성 유지)

### **✅ 인기 콘서트 검색 기능**
- **사용자 검색 패턴을 분석하여 인기 콘서트 조회**
- MySQL을 활용한 검색어 저장 및 인기 검색어 노출
- **Cache를 이용한 성능 최적화** (`@Cacheable` 적용)

### **✅ 성능 최적화 (Cache 적용)**
- **검색 API v1**: **In-memory Cache 적용 (`@Cacheable`)**
- **검색 API v2**: Redis 기반 **Remote Cache 적용**

### **✅ 동시성 제어**
- 동시 티켓 예매 시 **Race Condition 방지**
- **Redis Lock을 사용하여 동시성 이슈 해결**

<br>

## 🔗 ERD

~~~ mermaid
erDiagram
    USER {
        int id PK
        varchar name
        varchar email
        varchar password
        timestamp created_at
        timestamp updated_at
    }

    CONCERT {
        int id PK
        varchar name
        int total_amount
        int available_amount
        varchar singer_name
        timestamp open_at
        timestamp created_at
        timestamp updated_at
    }

    TICKET {
        int id PK
        int user_id FK
        int concert_id FK
        timestamp created_at
        timestamp updated_at
    }

    SEARCH_KEYWORD {
        int id PK
        varchar keyword
        int count
    }

    USER ||--o{ TICKET : "buys"
    CONCERT ||--o{ TICKET : "has"
~~~

<br>

## 🔥 Project Doc

- [Notion Link](https://teamsparta.notion.site/10-248ee9d389bb42b3bc957e69ea13d41b?pvs=25)


## 🚨 Trouble Shooting

### 1. Caching - 데이터 수정 시 캐시 삭제 불가능 문제

⚠️ cause : 검색어 기반으로 캐시 키를 생성하기 때문에 데이터가 변경되어도 해당 캐시를 삭제 할 수 없는 문제가 있었고 이는 데이터 정합성에 문제가 생깁니다.

![image](https://github.com/user-attachments/assets/64656330-7f48-4d86-87f3-06373e694926)

✅ solution 

1. TTL 을 짧게 설정 하여 지속적으로 데이터 정합성 유지

2. @Scheduled 사용하여 매일 정오에 캐시를 삭제 


### 2. 동시성 문제 - Transactional 범위 설정 문제 

⚠️ cause : 트랜잭션이 unlock 이후에 실행되면서 생긴 Dirty Read 문제가 발생했습니다.

![image](https://github.com/user-attachments/assets/e07e6fec-68a6-4ab7-b9ef-6a1758c8200d)


✅ solution : 트랜잭션을 분리해 unlock이 트랜잭션 완료 이후 동작하도록 설정해 해결했습니다.





## 🍰 Performance Comparison

### 1. In-Memory Cache VS Redis Cache 속도 비교

실제 성능 테스트 결과, In-Memory Cache가 Redis Cache 보다 읽기 속도가 더 빠르다는 것을 확인하였습니다. 

하지만 저희 프로젝트에서는 Redis Cache 방식을 채택하였습니다.

|In Memory Cache|Redis Cache|
|-------|-------|
|<img src="https://github.com/user-attachments/assets/0b088340-0856-49c8-8456-208d42ed5177" width="500" height="300">|<img src="https://github.com/user-attachments/assets/692889ed-04d3-4d23-aa14-240da4eae35e" width="500" height="300">|


Redis Cache 선택 이유는 다음과 같습니다.

1. 데이터 지속성 : 서버 재시작 시에도 데이터가 유지되어 안정적인 캐싱 환경을 제공

2. 유연성 : 다양한 데이터 구조를 지원하여 다양한 캐싱 시나리오에 활용 가능

3. 확장성 : Redis 는 분산 캐싱을 지원하여 애플리케이션 성장에 따라 쉽게 확장 가능

4. 정교한 캐시 정책 : 기본적으로 TTL 을 제공하며, 자동 만료 및 정교한 캐시 정책을 설정할 수 있어 데이터 최신성을 유지하기 용이




### 2. 동시성 문제 해결 Lock 방식 별 성능 테스트

해당 테스트는 티켓을 1000장 파는데 걸리는 시간을 비교한 테스트입니다.

Lock 중에서도 충돌에 강한 Lock 은 무엇이고 그중에서도 성능이 좋은 Lock 은 어떤 Lock 일까 고심하며 테스트를 진행했습니다.

성능이 압도적으로 좋았던 것은 비관적 Lock 이었습니다만, 다중서버로의 확장이 힘들다는 단점이 있어 현재 프로젝트의 확장 가능성을 고려해 Redisson Lock방식을 채택했습니다.

![테스트 확인](https://github.com/user-attachments/assets/5c83ff58-5d7b-4211-9afb-28b3c6d22c39)






