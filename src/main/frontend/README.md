# TaehyunLibrary V1

태현님의 강의에 수업 자료로 활용하기 위해 만든 간단한 Single Page Web Application 입니다.

### Node Version

`node v16.15.0` - 2022년 초 기준 LTS Version

### 실행하기

`npm install`

`npm start` : 테스트 화면 구동하기

`npm run build` : 빌드하기

Chrome 및 Edge 브라우저에서 정상 구동 확인했습니다.

### Network Error 처리

* ERR_NETWORK : `` 단에서 난 Error로, 서버가 아예 켜지지 않은 경우입니다.
  * `서버에 연결이 불가능하거나, 네트워크 오류입니다.` 라는 Alert가 발생합니다.
* 404 Error: 서버에 특정 API에 대한 구현이 진행되지 않았을 때 발생하는 에러입니다.
  * `해당 URI에 대한 서버의 응답이 없습니다.` 라는 Alert가 발생합니다. 어떤 Request Method인지, 어떤 URI인지도 함께 표시됩니다.
* 500 Error: 클라이언트에서 알 수 없는 서버 내부 오류일 때 발생하는 에러입니다.
  * `서버 내부 오류입니다.` 라는 Alert가 발생합니다.
