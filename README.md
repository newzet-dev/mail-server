
## 뉴스레터 관리 서비스 뉴젯의 메일, API 서버
### 뉴스레터만을 위한 독립된 환경을 제공하여, 뉴스레터를 자유롭게 구독하고, 소비하고 동기를 부여받는 기능을 제공합니다.

<br/>

![image](https://github.com/user-attachments/assets/dac5ee42-f575-4f1c-988c-01d1e457ebe5)


다운 링크:
[App Store](https://apps.apple.com/kr/app/%EB%89%B4%EC%A0%AF-%EB%89%B4%EC%8A%A4%EB%A0%88%ED%84%B0-%EC%9D%BD%EA%B8%B0%EB%A5%BC-%EC%A6%90%EA%B1%B0%EC%9A%B4-%EC%8A%B5%EA%B4%80%EC%9C%BC%EB%A1%9C/id6581484791) [Google Play Store](https://play.google.com/store/apps/details?id=com.team3to1.newzet)
<br/>



<br/><br/>

## 📙 프로젝트 개요
### 개발 배경

- **161명의 뉴스레터 소비자 대상 설문조사, 21명의 잠재고객 인터뷰, 노션 랜딩페이지를 통한 Fake Door Test 및 사전 예약**을 통해 뉴스레터 소비의 어려움을 파악하였습니다.
- '중요 메일과 뉴스레터가 섞여 메일함이 복잡해지는 문제','뉴스레터를 구독했지만 꾸준히 읽지 않아 부채감으로 남아있는 문제'를 해결하기 위해 **뉴스레터만을 위한 환경**을 제공하고, **동기부여 캘린더/추천/공유** 기능을 통해 뉴스레터 읽기를 즐거운 습관으로 만들어줍니다.

### 개발 인원

- 서버 개발자 1명, 프론트 개발자 1명, 풀스택 개발자 1명

### 성과

- 서비스 누적 사용자 2,600명 달성
- 구글 플레이스토어 뉴스/잡지 카테고리 35위
- 뉴스레터 평균 오픈율 대비 18% 높은 수치 기록
- 디스콰이엇 10월 4주차 인기 프로덕트 5위, 렛플 배너 소개, SW마에스트로 프로덕트 소개
- 뉴스레터 '너겟'과 일주일간 유료 광고 협업 진행

<br/>

## 아키텍처
**메일 수신 아키텍처**

![뉴젯_아키텍처](https://github.com/user-attachments/assets/cbd03e83-509f-400f-acf0-0809dbdfde02)
- 뉴스레터 수신/파싱 시 안정성이 높고 비용이 저렴한 AWS SQS, SES, S3, Lambda를 활용
- 캐시 히트율이 높고, Canary/Live 분산 환경에서 사용을 위해 Redis 캐시 도입
- 비용 절감을 위해 데이터베이스 및 API 서버는 Supabase를 통해 구축 (웹으로의 확장을 위해 Deno framework로 서버 로직 작성)

<br/>

**CI/CD 아키텍처**

![image](https://github.com/user-attachments/assets/e548979f-2288-4ebe-9fa8-af8b63b36c10)
- Github Project에 적합한 Github Actions 활용
- 운영중인 서비스의 안정적인 서버 배포를 위해 Canary 배포 전략 적용 및 모니터링 환경 구측
- Canary 배포를 직접 구축할 수 있고, 저비용으로 구축하기 위해 Docker Hub, EventBridge, Lambda, ALB를 활용해 Canary 배포 구현

<br/>

**모니터링**

![monitoring](https://github.com/user-attachments/assets/db8a7893-17da-42fe-a63a-6791477b2e7e)

<br/>



## 💻 수행 역할

### 팀 리더 역할

팀장 & 서버 개발자역할
 - 뉴스레터 수신 모듈 구축, 성능 최적화, API 개발 및 배포, CI 구축
 - 프로젝트 운영 관련 문서 작성, 외부 디자이너와의 협업, 멘토링 및 SW마에스트로 기획/중간/최종 발표

<br/>

## 버전관리
```
java:	17
spring: 6.2.1
springBoot: 3.4.1
lombok: 1.18.36
```

database
```
postgres: 42.7.4
redis: 7.0.8
redisson: 3.43.0
querydsl: japa:5.0.0
```

test
```
junit: 1.20.5
mockito: 5.11.0
testcontainer: 1.20.5
```

monitoring
```
prometheus: 1.14.2
```
