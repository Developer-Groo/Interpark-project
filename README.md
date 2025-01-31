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
- [🔥 Trouble Shouting](#-Trouble-Shouting)

<br>

## 🏁 Team
|**우현**|**혁규**|**진영**|**희현**|
|-------|-------|-------|-------|
|<img src="https://github.com/Developer-Nova/Sec19-Local-Data-Persistance_ByAngela/assets/123448121/17a2ba3b-a618-4ac8-93b9-0d0e02c19c78" width="110" height="110">|
|[GitHub](https://github.com/Developer-Groo)|

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
| **Cache** | Caffeine Cache, Redis Cache |
| **Concurrency Control** | Redis Lock, MySQL Pessimistic Lock |
| **Testing** | JUnit5, Testcontainers, MockMvc |
| **DevOps** | Docker, GitHub Actions, AWS EC2 |

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
- **검색 API v2**: Redis 기반 **Remote Cache 적용 (선택 기능)**

### **✅ 동시성 제어**
- 동시 티켓 예매 시 **Race Condition 방지**
- **Redis Lock을 사용하여 동시성 이슈 해결**
- **MySQL 기반의 Pessimistic Lock 적용 (선택 기능)**

<br>

## 🔗 ERD

~~~ mermaid

~~~

<br>
<br>

## 🔥 Trouble Shouting
