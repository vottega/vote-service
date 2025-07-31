# vote-service

투표(Vote) 도메인을 담당하는 마이크로서비스입니다. 투표 생성/참여/집계 등 투표와 관련된 핵심 기능을 제공합니다.

Redis로 방과 참가자의 상태를 캐싱하고 sse-server와 room-service에서 Kafka로 보낸 메시지를 이용해 상태를 업데이트 합니다.

---

## 🧰 기술 스택

- Language: Kotlin
- Framework : Spring MVC
- Database : MariaDB, Redis
- Message Queue : Kafka

---

## 📦 실행

### 로컬 실행
docker-compose up -d --build
