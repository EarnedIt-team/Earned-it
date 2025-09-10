# Earned-it 🎯

> **수익 기반 퍼즐 수집 게임 플랫폼**

Earned-it은 사용자의 수익 정보를 기반으로 한 퍼즐 수집 게임입니다. 일일 출석 체크를 통해 다양한 아이템 조각을 수집하고, 퍼즐을 완성하여 성취감을 느낄 수 있는 웹 애플리케이션입니다.

## 🚀 주요 기능

### 🔐 인증 시스템
- **이메일 회원가입/로그인**: 기본 이메일 인증 시스템
- **소셜 로그인**: 카카오, 애플 로그인 지원
- **JWT 토큰**: Access Token & Refresh Token 기반 인증
- **이메일 인증**: 회원가입 시 이메일 검증

### 🎮 게임 시스템
- **일일 출석 체크**: 매일 출석하여 랜덤 아이템 조각 획득
- **퍼즐 수집**: 다양한 테마의 퍼즐 조각 수집
- **랭킹 시스템**: 사용자별 점수 및 순위 관리
- **보상 시스템**: 레어도별 차등 점수 지급

### 💰 수익 관리
- **월급 정보 관리**: 사용자별 수익 정보 저장/조회
- **위시리스트**: 원하는 상품 등록 및 관리
- **구매 완료 처리**: 위시리스트 상품 구매 완료 표시

### 👤 사용자 관리
- **프로필 관리**: 닉네임, 프로필 이미지 변경
- **공개/비공개 설정**: 프로필 공개 여부 설정
- **다크모드**: UI 테마 변경 지원
- **신고 시스템**: 부적절한 사용자 신고 기능

### 🛠️ 관리자 기능
- **아이템 관리**: 퍼즐 조각 아이템 CRUD
- **퍼즐 슬롯 관리**: 퍼즐 구성 요소 관리
- **대시보드**: 관리자 전용 관리 페이지

## 🏗️ 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security**: JWT 기반 인증
- **Spring Data JPA**: 데이터 접근 계층
- **QueryDSL**: 동적 쿼리 처리
- **MapStruct**: DTO 매핑

### Database
- **PostgreSQL**: 메인 데이터베이스
- **Redis**: 캐싱 및 세션 관리

### Infrastructure
- **Docker & Docker Compose**: 컨테이너화
- **AWS S3**: 파일 저장소
- **Gmail SMTP**: 이메일 전송

### Documentation
- **Swagger UI**: API 문서화
- **Thymeleaf**: 관리자 페이지 템플릿

## 📁 프로젝트 구조

```
src/main/java/_team/earnedit/
├── config/          # 설정 클래스
├── controller/      # REST API 컨트롤러
├── dto/            # 데이터 전송 객체
├── entity/         # JPA 엔티티
├── global/         # 전역 설정 및 예외 처리
├── mapper/         # MapStruct 매퍼
├── repository/     # 데이터 접근 계층
└── service/        # 비즈니스 로직
```

## 🚀 실행 방법

### 1. 환경 변수 설정
`.env` 파일을 생성하고 다음 환경 변수를 설정하세요:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=earnedit
DB_USERNAME=root
DB_PASSWORD=earnedit99

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=12341234

# JWT
JWT_SECRET=your-jwt-secret
JWT_REFRESH_SECRET=your-jwt-refresh-secret
JWT_ACCESS_EXPIRE_TIME=3600000
JWT_REFRESH_EXPIRE_TIME=604800000

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
EMAIL_VERIFICATION_URL=http://localhost:8080

# AWS S3
S3_ACCESS_KEY=your-s3-access-key
S3_SECRET_ACCESS_KEY=your-s3-secret-key
S3_BUCKET_NAME=your-bucket-name
```

### 2. Docker로 실행
```bash
# 개발 환경 실행
./start-dev.sh

# 또는 직접 실행
docker compose up -d --build
```

### 3. 로컬 실행
```bash
# 빌드
./gradlew bootJar

# 실행
java -jar build/libs/earnedit-0.0.1-SNAPSHOT.jar
```

## 📚 API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🎯 주요 API 엔드포인트

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `POST /api/auth/signin/kakao` - 카카오 로그인
- `POST /api/auth/signin/apple` - 애플 로그인

### 퍼즐 및 랭킹
- `GET /api/mainpage` - 메인 페이지 정보
- `GET /api/puzzle` - 퍼즐 정보 조회
- `POST /api/daily-check/reward` - 일일 출석 보상
- `GET /api/rank` - 랭킹 조회

### 사용자
- `GET /api/profile/salary` - 수익 정보 조회
- `POST /api/profile/salary` - 수익 정보 저장
- `GET /api/wish` - 위시리스트 조회
- `POST /api/wish` - 위시리스트 추가

## 🗄️ 데이터베이스 스키마

### 주요 엔티티
- **User**: 사용자 정보
- **Item**: 퍼즐 조각 아이템
- **Piece**: 사용자가 수집한 조각
- **PuzzleSlot**: 퍼즐 슬롯 구성
- **Wish**: 위시리스트
- **Salary**: 수익 정보
- **Star**: 즐겨찾기

## 🔧 개발 환경

- **JDK**: 17
- **Gradle**: 8.x
- **Docker**: 20.x
- **PostgreSQL**: 15.x
- **Redis**: 7.2

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**Earned-it** - 수익으로 만드는 나만의 퍼즐 컬렉션 🧩
