[Readme 바로가기 📦](https://github.com/deceit-cat)<br>
[프론트엔드 Repo 바로가기 📦](https://github.com/deceit-cat/socket "deceit-cat/socket")

# Backend
## 1. 개발 환경
> IntelliJ IDEA</br>
> Spring Boot 2.7.17-SNAPSHOT </br>
> Java 11</br>
> Gradle </br>
## 2. AWS CI/CD 환경 구축 (~23.10.03)
`main` 브랜치에 소스코드를 `push`하면 **자동으로 EC2 인스턴스에 배포되는 워크플로우**
![image](https://github.com/deceit-cat/backend/assets/125736963/a1a049d5-f459-4921-b85b-9b858e0bfec6)
### 2.1. 백엔드 서버 배포 주소
> Elastic IP주소로 접속합니다.</br>
```
13.124.97.155:8080
```
### 2.2. API Docs 주소
> Swagger 3.0.0 버전을 적용합니다.</br>
> 라이브러리 : springdoc-openapi-ui

```
http://13.124.97.155:8080/api-docs
```
