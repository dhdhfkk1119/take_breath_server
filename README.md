<a href="https://club-project-one.vercel.app/" target="_blank">
// 홍보 이미지 넣기
</a>

<br/>
<br/>

# 📝 Back Server 소개 


## 💻 0. Getting Started 프로그램 시작하는 법 
- server 깃 코드 다운 받으신 후 프로그램 실행 (IntelliJ , VScode , Eclipse) 등등
- 실행하면 Server에서 등록된 Gemini기본키 주석 처리 하셔야 할겁니다


## 📖 1. Project Overview (프로젝트 개요)
- 프로젝트 이름: 한숨(Take_Breath) 
- 프로젝트 설명: 익명 기반 커뮤니티와 전문 상담 연계를 통해 직장 내 따돌림 괴롭힘에 대한 심리적 회복과 안전 , 자신의 경험을 공유를 지원 신뢰 기반 디지털 앱


## 🛠️ 2. 개발 환경 
- **언어** : JAVA
- **프레임워크** : SpringBoot
- **JDK** : Java 21 
- **Build Tool** : Gradle (groovy)
- **DBMS** : MySQL(Workbench)
- **ORM** : JPA
- **개발기간** : 2025.10.22 ~ 2025.11.11


## 🔑 3. Key Features (주요 기능)
- **회원 가입**
  - 아이디 형식 회원 가입 및 이메일 회원 가입
  - 약관 동의 (체크)
  - 중복 체크 및 이메일 인증(Google SMTP 인증방식)
    
- **로그인**
  -  소셜 로그인 (google - firebase 사용) (네이버 로그인 사용)
  -  일반 로그인 및 이메일 로그인
    
- **채팅 기능**
  -  1:1 채팅 기능 
  -  WebSocket 사용
  -  STOMP 사용

- **기록실 등록**
  - 기록실 작성하기 (이미지,녹음파일 첨부 가능)
  - 기록실 수정 삭제
  - 기록실 다운로드
    - 자신이 작성한 날짜에 다운로드 누르면 pdf 형식으로 로컬에 다운로드 됨 
      
- **게시물 등록**
  - 게시물 등록 삭제 수정 (커뮤니티)
  - 게시물 신고 하기 (신고 승인 결과 따라 정지 유무 결정)
    - 신고 횟수에 따른 게시물 등록 하지 못하는 시간이 늘어남(정지 기능)
   
- **결제(포트원)**
  - 포트원을 사용해서 결제 하기(해당 유저의 포인트 증가) 
  - 환불 하기
    - 포인트 히스토리 내역을 만들어서 결제했던 건에 대해서 제일 최신을 기준으로 포인트 사용하지 않은것만 환불 가능 

- **웹 서버 어드민 페이지**
  - 커뮤니티 신고 목록 검사하기 (신고 승인)
  - 상담사 승인
  - 시청각 자료 관리 (Youtube Data API 사용)
  - 통계 (회원가입,로그인,매출 등등 대시보드)

## ♻️ 4. Tasks & Responsibilities (작업 및 역할 분담)
|  |  |  |
|-----------------|-----------------|-----------------|
| 조정우    |  [<img src="https://avatars.githubusercontent.com/u/140272714?v=4" alt="조정우" width="100">](https://github.com/dhdhfkk1119) | <ul><li>전체적인 프로젝트 계획,배포 및 관리</li><li>[SpringBoot] 공통 Util , AOP 예외 처리, 구글 소셜 로그인 및 필요한 코드 리펙토링 코드편집 </li><li>[Flutter] 전체적인 UI 구성 및 상태관리 회원가입,소셜로그인,커뮤니티</li></ul>     |
| 유류진   |  [<img src="https://avatars.githubusercontent.com/u/208729786?v=4" alt="유류진" width="100">](https://github.com/yooryujin)| <ul><li>Figma 레이아웃</li><li>유저 거래 리뷰 및 평점</li><li>flutter 게시물 등록 수정 삭제</li></ul> |
| 양성빈   |  [<img src="https://avatars.githubusercontent.com/u/197378605?v=4" alt="양성빈" width="100">](https://github.com/ysb5397)    |<ul><li>Flutter GPS 기능 </li><li>Toss 결제 하기 기능</li><li>상품 등록 Ai 이미지 인식 기능</li></ul>  |
| 안성엽    |  [<img src="https://avatars.githubusercontent.com/u/183344667?v=4&size=64" alt="안성엽" width="100">](https://github.com/seongyob99)    | <ul><li>회원가입 및 로그인 기능</li><li>관리자 기능 및 관리자 페이지</li></ul>    |
| 윤준빈    |  [<img src="https://avatars.githubusercontent.com/u/208729868?v=4" alt="윤준빈" width="100">](https://github.com/junbean)    | <ul><li>웹소켓 채팅 기능 구현</li><li>기록실 백엔드 구현</li></ul>    |

<br/>
<br/>

## 기능들에 대한 영상 및 이미지는 -> 플러터 프론트 에서 관리하고있습니다
👀 [눈으로 보고 싶다 -> 프론트 주소](https://github.com/dhdhfkk1119/take_breath_front.git)

