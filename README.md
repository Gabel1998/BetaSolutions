# BetaSolutions - Eksamensprojekt F2025

![Build and Test Status - Develop](https://github.com/Gabel1998/BetaSolutions/actions/workflows/ci.yml/badge.svg?branch=develop)

## Om projektet
Dette projekt er udviklet som en del af Eksamensprojektet på 2. semester (Datamatiker, KEA).

BetaSolutions er et projektkalkulationsværktøj, som:
- Hjælper med at nedbryde projekter i delprojekter, opgaver og deadlines.
- Giver overblik over tidsforbrug på opgaver og projekter.
- Understøtter planlægning af arbejdstid, så projekter kan afsluttes til tiden.

Projektet anvender:
- **Java 21**
- **Spring Boot 3.4.5**
- **Maven** til build og dependency management
- **MySQL** som database
- **Thymeleaf** til frontend rendering

## Build og test
Denne repository anvender **GitHub Actions** til automatiseret build og test.

**Build Workflow:**
- Triggeres ved push og pull request til `develop` branch.
- Kompilerer Java-koden.
- Kører alle unit tests.
- Pakker applikationen.

> Hvis badgen øverst på siden er grøn ✅, betyder det, at projektets seneste build og tests på `develop` branch er succesfulde.

## Versionskrav
- IntelliJ IDEA 2024.1 eller nyere (anbefalet)
- Java JDK 21
- Maven 3.9+
- MySQL 8.0+

## Links
- [Link til repository på GitHub](https://github.com/Gabel1998/BetaSolutions)
- [Link til GitHub Actions (Workflows)](https://github.com/Gabel1998/BetaSolutions/actions)
