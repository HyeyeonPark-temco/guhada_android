# Guhada Android 앱 개발

## 개발 환경
- Android Studio
- Java 1.8

## 프로젝트
### 개요
- 배포 타겟(Deployment Target) : 5.0 이상 (Lollipop, 21)
- 단말 방향(Orientation) : Only Portrait
- 버전 관리 :  #.#.# (Major.Minor.Build)

### 구조
- common : Util, Etc..
- view : activity, fragment, adapter, custom
- data : viewmodel, server, model

## Git 사용
### 구조
1. dev : 개발 버전
2. master : 마이너 버전
3. release : 배포 버전
### 작성
- Commit Comment에는 시간을 입력하지 않으며 작업 또는 수정한 내용을 명확하게 적는다.

## Coding 가이드
### 명명법
코드의 통일성을 갖추기 위해 아래와 같이 명명할 것을 권고한다.

1. Value, Function
- Camel Case
```
void examFuncName() {
    int userName;
    String userPwd;
}
```

2. Class, Interface, Enum
- Pascal Case
```
public enum ExamEnum {
    ONE,
    TWO
}
```
```
class ExamActivity extends Activity {
}
```

3. Constant
- GNU Naming Convention + UnderBar
```
final String TEST_MAIN = "main"
```

4. XML [링크](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)
```
activity_<WHERE>
fragment_<WHERE>
item_list_<WHERE>
item_<WHERE>
layout_<WHERE> // incclude
header_<WHERE> // toolbar
view_<WHERE>_<DESCRIPTION> // custom view
```

### 주석
모든 클래스, 함수, 변수, enum 등 주석을 추가하는 것을 원칙으로 한다
1. 클래스, 변수
```
// 예시 클래스
class examClass {

    // 예시 변수
    String mValue;

    // Init
    void init() {
      ...
    }

    // 카운트
    int getCount() {
      ...
    }
}
```
