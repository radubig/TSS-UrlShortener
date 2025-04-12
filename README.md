# Url Shortener

Un scurtător de URL-uri simplu care permite utilizatorilor să convertească link-uri lungi în unele mai scurte și mai ușor de gestionat. Serviciul suportă urmărirea accesărilor, date de expirare și gestionarea URL-urilor.

> Cod Echipa: B18

## Software folosit

- Backend: Java 21 + Spring Boot 3.4.0
- Build Tools: Gradle 8.12
- Database: MongoDB 6.0.20
- Testare si code coverage: JUnit 5.11.4 + JaCoCo latest
- IDE: IntelliJ IDEA

## Capturi de ecran
### Acoperirea testelor folosing Intellij
![Coverage 2025-04-08](https://github.com/user-attachments/assets/d6f7850a-9859-4685-8e9e-4c6ed08c3550)
### Rezultatele testelor, generate de Gradle
![Test Results](https://github.com/user-attachments/assets/fdd7d4be-070b-4e53-988e-bf34615b23b9)
### Acoperire generata de JaCoCo
![Jacoco Coverage](https://github.com/user-attachments/assets/6dd270e1-0238-429d-94b5-dd75f9c4c227)


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
