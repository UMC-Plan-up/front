<img width="256" height="256" alt="image" src="https://github.com/user-attachments/assets/6e677a82-372e-4492-9c33-2d3bb73623b3" />

# Plan Up Android
UMC Plan UP 프로젝트의 안드로이드 파트 리포지토리입니다.

<hr>

**📌 진행 사항 확인**

- **Notion**에서 자세한 진행사항 보러가기
-> https://www.notion.so/ad72c091dca1439d89658322c1904d2d?source=copy_link

  

## 🙋🏻‍♂️ 멤버 구성

|김한준|박선준|이미연|장용근|
|:---:|:---:|:---:|:---:|
|<img width="788" height="1205" alt="image" src="https://github.com/user-attachments/assets/30548295-24a9-43c7-8589-c6e5f809ac1c" />|<img width="828" height="1103" alt="image" src="https://github.com/user-attachments/assets/025182d1-3430-4caa-ba3f-1ddd5df25cc1" />|<img width="470" height="347" alt="image" src="https://github.com/user-attachments/assets/acf7ace7-8872-4ba5-af38-da5924525c57" />|<img width="469" height="652" alt="image" src="https://github.com/user-attachments/assets/0143afc4-30eb-48ef-b169-bb4185003d88" />|

## 🛠 기술 스택

| 구분 | 내용 |
|------|------|
| 언어 | Kotlin |
| UI 디자인 | XML 기반 |
| Android Studio 버전 | Narwhal (2025.1.1) |
| `targetSdk` | 35 (Android 15) |
| `minSdk` | 21 (Android 5.0) |
| 테스트 환경 | Android Emulator (API 21 이상) |

## 🧱 코드 컨벤션
### 📄 XML 네이밍 규칙 (`snake_case`)
- `drawable`  
  - 아이콘 → `ic_`  
  - 이미지 → `img_`  
  - 배경 → `bg_`  
  - 버튼 → `btn_`  
- `font`: `{폰트이름}_{스타일}` (예: `pretendard_bold`)  
- `string`: 명확하고 구체적으로 작성  
  - 예: `<string name="today_message">안녕하세요~! UMC에 오신 것을 환영합니다!</string>`
 
### 🧠 Kotlin 네이밍 규칙 (`lowerCamelCase`)
- 함수 이름
  - 초기화: `initView()`, `initClickListener()`
  - 갱신: `updateXXX()`, `removeXXX()`
  - LiveData 관찰: `setupXXX()`
  - 데이터 요청: `getXXX()`, `findXXX()`, `fetchXXX()`, `saveXXX()`
- 서버 API 요청 함수: `getUserList()`, `postMusic()`, `putProfile()`, `deleteUser()`
- 복수형은 뒤에 `s` 추가: `getBrands()`
- Enum: `find(rawValue: String): EnumType`
- 패키지명: 소문자 또는 카멜 케이스 (단, `_` 사용 지양)
  - 예: `package com.example.myProject`
 
### 📡 Lambda 함수 호출
```kotlin
foo()         // O
foo.invoke()  // X
bar?.invoke() // O
```

### 🧷 Listener 작성 규칙
- 단일 함수 인터페이스: fun interface OnXXXListener
- 다중 함수 인터페이스: interface XXXListener
- 네이밍
    - 이벤트 트리거 → `onClick()`, `onFocusChange()`
    - 이벤트 완료 후 알림 → `onScrollStateChanged()`, `onTextChanged()`


### 🎨 Formatting & Style
- When 문
  ```
  when (value) {
    0 -> return
    1, 2
    -> return
  }
  ```
- 긴 파라미터 함수 정의 시 파라미터 별로 개행
- 공식 스타일 가이드 참고
  - [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
  - [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)

## Git Branch 전략 (Gitflow)
|브랜치명|역할|
|main|릴리즈 버전 배포|
|develop|기능 통합 및 테스트|
|feat/"브랜치 명"|세부 기능 개발|
|release|릴리즈 용 브랜치|
|hotfix|긴급 패치용 브랜치|

## 이슈, 브랜치, PR 규칙
### ✅ 이슈 제목 예시
- [UI] 로그인 화면 UI 구성
- [Feat] 로그인 API 연동
- [Fix] 토큰 오류 해결

### 🌿 브랜치 명명 규칙
- "브랜치 유형/내용"
- ex) `feat/login-api-connect`

### 🔃 PR 제목 규칙
- "[PR 유형] PR 내용"
- ex) [Feat] 로그인 뷰-서버 클라이언트 API 연동"

### 👀 Merge 규칙
- 4명 중 최소 2명 이상 리뷰한 경우에만 Merge 가능
- Merge 메시지 예시: `[Merge] 로그인 뷰-서버 API 연동 -> develop`

### 📌 Commit 메시지 컨벤션
|태그|설멍|
|:---:|:---:|
|Feat|새로운 기능 추가|
|Mod|코드 수정|
|Add|푸가적인 파일 및 라이브러리 추가|
|Chore|버전, 타입, 변수명 변경 등|
|Delete|불필요한 파일, drawable, resource 삭제|
|UI|UI 작업|
|Fix|버그 수정|
|Hotfix|긴급 오류 수정|
|Merge|브랜치 병합|
|Move|코드/파일 디렉토리 이동|
|Rename|파일 이름 변경|
|Refactor|큰 규모의 코드 또는 파일 리팩토링|
|Docs|README, WIKI 페이지 작성 및 수정|
