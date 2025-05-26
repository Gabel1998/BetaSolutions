# Alpha Solutions – Projektkalkulationsværktøj
*KEA Datamatiker – Eksamensprojekt, Forår 2025*

Dette system er udviklet som en del af eksamensprojektet på KEA 2. semester. Formålet er at understøtte Alpha Solutions’ behov for bedre overblik og styring af projekter, medarbejdere, tid og ressourcer.

🔗 **Live site**: [Åbn applikationen](betasolutions-afepfyanhcddcpau.westeurope-01.azurewebsites.net)  
🔗 **GitHub repository**: [Se kildekode](https://github.com/gabel1998/betasolutions)  
🔗 **Eksamensrapport**: [Læs rapporten (PDF)](https://github.com/gabel1998/betasolutions/tree/master/docs/Rapport.pdf)

---

## ⚡ TL;DR – Kom hurtigt i gang

1. **Klon projektet**

   ```bash
   git clone https://github.com/gabel1998/betasolutions.git
   cd betasolutions
   ```

2. **Opsæt MySQL-database**

    - Opret database: `betasolutions`
    - Kør SQL-scripts fra `src/main/resources/database/`

3. **Tilføj `application.properties` i `src/main/resources/`**

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/alphasolutions
   spring.datasource.username=dinBruger
   spring.datasource.password=ditPassword
   spring.profiles.active=dev
   ```

4. **Kør applikationen**

    - Åbn i IntelliJ IDEA (Ultimate anbefales)
    - Start `AlphaSolutionsApplication.java`

5. **Login testbruger**

    - Brugernavn: `ajen`
    - Adgangskode: `Password123!`

---

## 🎯 Problemstilling og Formål

Alpha Solutions har brug for et internt værktøj til at:

- Nedbryde projekter i opgaver og ressourcer
- Estimere tidsforbrug og medarbejderbelastning
- Beregne CO₂-forbrug (ESG)
- Øge overblikket og lette projektledelse

---

## 🧱 Teknologier og Arkitektur

| Teknologi       | Version     | Funktion                  |
|-----------------|-------------|---------------------------|
| Java            | 17          | Backend                   |
| Spring Boot     | 3.2         | Webramme og DI            |
| JDBC Template   | –           | Database access           |
| MySQL           | 8.x         | Primær database           |
| H2              | In-memory   | Testdatabase              |
| Thymeleaf       | –           | HTML templating           |
| GitHub Actions  | YAML        | CI/CD pipeline            |
| Azure           | App Services| Hosting                   |
| Qodana          | JetBrains   | Statisk kodeanalyse       |

### 📂 Projektstruktur

```text
src/
├── controller/      // HTTP-routing
├── service/         // Forretningslogik
├── repository/      // Data access via JDBC
├── rowmapper/       // ResultSet → Objekt
├── model/           // Domæneklasser
├── config/          // Datasource-opsætning
├── templates/       // HTML-sider (Thymeleaf)
└── static/          // CSS og evt. billeder
```

---

## 🚀 Deployment og test

| Miljø      | URL                                             |
|------------|--------------------------------------------------|
| Azure Prod | https://<indsæt-azure-link>.azurewebsites.net   |
| Login      | `ajen` / `Password123!` *(testkonto)*           |

### CI/CD

- GitHub Actions: build → test → deploy
- Qodana anvendt til statisk kodeanalyse
- JaCoCo anvendt til måling af testdækning

---

## 📦 Funktionalitet

- CRUD på projekter, opgaver og medarbejdere
- Visualisering af arbejdsbelastning og tid
- Gantt-inspireret visning (statisk)
- Beregning af CO₂ baseret på ressourceforbrug
- Login med sessionshåndtering

---

## 🧪 Test og kvalitetssikring

- Unit- og integrationstests med JUnit + H2
- Testdata via SQL-scripts
- Dækningsgrad monitoreret via JaCoCo
- Qodana integreret i pipeline for code quality

---

## 📄 Dokumentation og Bilag

Alt dokumentation findes i `/docs`-mappen:

- **UML Klassediagram** – `docs/klassediagram.png`
- **Package Diagram** – `docs/package-diagram.png`
- **ER-diagram** – `docs/erdiagram.png`
- **Burndown charts** – `docs/burndown-sprint1.png` m.fl.
- **Eksamensrapport (PDF)** – `docs/Rapport.pdf`

---

## 👥 Udviklingsteam

- [👩‍💻 Alexander Örn Birgisson](https://github.com/bixson)
- [👨‍💻 Rasmus Mellerkaer](https://github.com/Mellerkaer)
- [👩‍💻 Sofie Nakskov](https://github.com/sofi8917)
- [👨‍💻 Andreas Gabel](https://github.com/Gabel1998)

> Projektgruppe – KEA, Datamatikeruddannelsen, Forår 2025  
> Udviklet med fokus på modularitet, overblik og bæredygtig arkitektur.

---

## 📚 Licens

Dette projekt er udarbejdet som en del af KEA’s undervisning og må ikke anvendes kommercielt uden tilladelse fra udvikleren og KEA.
