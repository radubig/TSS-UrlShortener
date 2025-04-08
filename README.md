# Url Shortener

Un scurtător de URL-uri simplu care permite utilizatorilor să convertească link-uri lungi în unele mai scurte și mai ușor de gestionat. Serviciul suportă urmărirea accesărilor, date de expirare și gestionarea URL-urilor.

> Cod Echipa: B18

## Software folosit

- Backend: Java 21 + Spring Boot 3.4.0
- Build Tools: Gradle 8.12
- Database: MongoDB 6.0.20
- Testare si code coverage: JUnit 5.11.4 + JaCoCo latest
- IDE: IntelliJ IDEA

# Rulare/debug în IntelliJ
* Instalează Java 21
* Setează versiunea JDK a proiectului la 21 (`File > Project Structure > SDK`)
* Compilează codul
  * Se poate folosi și comanda `./gradlew build` pentru a rula testele automat.
* Creează o configurație de rulare în IntelliJ pentru o aplicație Jar
  * Adaugă în configurație calea către fișierul JAR din folderul de build:  
    `./build/libs/hello-0.0.1-SNAPSHOT.jar`
* Pornește containerul MongoDB folosind docker compose: `docker-compose up -d mongo`
* Rulează configurația de rulare din IntelliJ

# Rularea proiectului

* Poți accesa endpoint-urile API la:  
  [http://localhost:8080/](http://localhost:8080/)
* Poți accesa interfața de administrare MongoDB la:  
  [http://localhost:8090/](http://localhost:8090/)

# Rularea testelor

* Adaugă o configurație de test JUnit în IntelliJ, setată să testeze tot din pachetul `hello.test`
