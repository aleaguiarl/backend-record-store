<h1 align="center" id="title">Alexandre Aguiar - Backend Project</h1>

<p id="description">This project's objective is to create an API simulating a record store integrated with the Spotify Web API. It comprises two services responsible for managing album search album sale user-related operations and wallet management.</p>

<h2>Dependencys:</h2>

* JDK 17+
* Docker Desktop
* Default ports available:
  * 8081:8081
  * 8082:8082
  * 5432:5432
  * 5672:5672
  * 15672:15672
  
<h2>🛠️ Installation Steps:</h2>

<p>1. Clone the repository to a folder of your choice</p>

```
git clone https://github.com/bc-fullstack-04/alexandre-aguiar-backend
```
<p>2. Go inside the project folder</p>

```
 cd .\alexandre-aguiar-backend\
```
<p>3. Go to app-user-api</p>

```
cd .\app-user-api\
```
<p>4. Run</p>

```
mvn clean install
```
<p>5. Go back</p>

```
cd ..
```
<p>6. Go to app-integration-api</p>

```
cd .\app-integration-api\
```
<p>7. Run</p>

```
mvn clean install
```
<p>8. Go back</p>

```
cd ..
```
<p>9. Build the containers</p>

```
docker-compose -f docker-compose.yml build
```
<p>10. Run the containers</p>

```
docker-compose -f docker-compose.yml up -d
```
  
<h2>💻 Built with</h2>

Technologies used in the project:

*   Java
*   Spring Boot
*   PostgreSQL
*   Docker
*   JUnit
