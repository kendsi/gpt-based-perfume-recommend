# GPT BASED PERFUME MAKER

## 프로젝트 개요
GPT BASED PERFUME MAKER는 체험형 조향 전시관 AC'SCENT에서 제작한 ChatGPT 기반 향수 추천 및 프로필 생성 백엔드 애플리케이션입니다.

Spring Boot 프레임워크를 사용하여 개발되었으며, Amazon AWS EC2 서버에 배포되었습니다. 본 애플리케이션은 사용자로부터 입력받은 데이터를 기반으로 맞춤형 향수를 추천하고, 향료 및 사용자 프로필을 생성합니다.

[서비스 페이지 링크](http://pixent.co.kr)

## 주요 기능
### 1. 맞춤형 향수 추천

사용자가 입력한 정보(향 선호도, 사진, 이름, 성별 등)를 기반으로 OpenAI의 ChatGPT를 활용해 데이터베이스에서 적합한 향수를 추천합니다.

### 2. 사용자 프로필 및 향료 생성

업로드된 이미지를 분석하여 사용자의 스타일, 분위기 등을 추출합니다.

ChatGPT를 통해 생성된 추천 결과를 조합해 새로운 향료를 생성합니다.

### 3. 결과 저장 및 관리

추천 결과 및 생성된 데이터를 MySQL 데이터베이스에 저장합니다.

업로드된 이미지는 AWS S3에 저장하여 효율적으로 관리합니다.

이 프로젝트는 사용자가 전해준 정보(향 선호도, 사진, 이름, 성별 등)를 기반으로 프롬프팅을 통해 Chat GPT로 데이터베이스에 있는 향수를 추천하고, 새로운 내용들(업로드된 이미지 분석, 추천된 향료, 프로필 등)을 생성합니다.

위 과정이 문제 없이 진행될 경우 해당 내용을 데이터베이스에 저장합니다. 이 떄, 이미지는 AWS S3로 저장하여 용이하게 관리합니다.

## 실행 절차

### 1. 페이지 접속 및 분석 시작

![image](https://github.com/user-attachments/assets/07f2bdd6-0e77-4eb6-aa2b-ada252310a6a)

서비스 페이지에 접속하여 분석하기 버튼을 클릭합니다.


### 2. 코드 입력

![image](https://github.com/user-attachments/assets/389d2ac9-7316-480f-9264-e7c025202745)

관리자에게 받은 6자리 코드를 입력합니다.


### 3. 분석 유형 선택

![image](https://github.com/user-attachments/assets/efa78da6-1487-459d-80e7-137b8028a6f6)

추천형 또는 커스텀형 중 하나를 선택합니다.


### 4. 향수 선호도 입력
   
![image](https://github.com/user-attachments/assets/14e6e255-593f-417e-8711-f4cf7823cf38)

선호하는 향과 비선호하는 향을 선택합니다.


### 5. 추가 정보 입력
   
![image](https://github.com/user-attachments/assets/b688d3fa-2bbb-49cf-90d3-f296053af88a)

![image](https://github.com/user-attachments/assets/71bf5953-258e-4588-a480-7ed8ab375317)

사용자 정보(사진, 이름, 성별 등)를 입력하고 로딩을 기다립니다.


### 6. 결과 확인

![image](https://github.com/user-attachments/assets/519595e3-1638-4053-80a5-6749f3c27a51) ![image](https://github.com/user-attachments/assets/48d1565c-24f1-4597-9903-acd1bae90013) ![image](https://github.com/user-attachments/assets/1e68724d-252d-4c42-bd64-f1f17596f5b5)

ChatGPT와 데이터베이스 분석을 거쳐 맞춤형 향수 추천 결과와 향료 정보를 확인합니다.


## 데이터 흐름

사용자가 페이지에 접속하여 데이터를 입력합니다.

데이터는 백엔드로 전송되어 ChatGPT API를 통해 분석됩니다.

분석 결과는 MySQL 데이터베이스에 저장되며, 이미지 데이터는 AWS S3에 업로드됩니다.

최종 추천 결과가 사용자에게 반환됩니다.


## 기술 스택
### 백엔드

Java 22

Spring Boot 3.3.5

Spring Data JPA: MySQL 연동 및 데이터 관리

OpenAI API: ChatGPT 기반 데이터 생성 및 추천

Lombok: 코드 간소화를 위한 애노테이션

### 데이터베이스 및 배포

MySQL: 사용자 및 추천 데이터를 저장

AWS EC2: 백엔드 애플리케이션 배포

AWS S3: 이미지 데이터 저장

### 기타
Springdoc OpenAPI UI: API 문서화를 통해 손쉬운 테스트 및 관리
