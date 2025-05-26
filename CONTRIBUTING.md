# 🤝 Contributing Guide

Hej og velkommen til!  
Hvis du ønsker at bidrage til udviklingen af projektkalkulationsværktøjet for Alpha Solutions, så læs venligst nedenstående retningslinjer.

Dette projekt er oprindeligt udviklet som eksamensopgave på KEA’s Datamatikeruddannelse (forår 2025), men strukturen er klargjort til videreudvikling og evt. produktionsbrug.

---

## 🧱 Projektstruktur
```text
src/
├── controller/      // Web endpoints og routing
├── service/         // Forretningslogik
├── repository/      // Data access (JDBC)
├── rowmapper/       // ResultSet → Objekt
├── model/           // Domæneklasser
├── config/          // Datasources og app config
├── templates/       // HTML (Thymeleaf)
└── static/          // CSS og evt. billeder
```

Vi følger **MVC-principperne**, og alt kode er skrevet i Java 17 med Spring Boot 3.2.

---

## 🗃 Branch-konventioner

- `main`: Produktionsklar kode. Intet merges direkte hertil.
- `develop`: Samlingsgren for ny funktionalitet.
- `feature/<navn>`: Nye features udvikles her.
- `bugfix/<navn>`: Bruges til fejlrettelser.

> Navngiv dine branches klart, f.eks.:  
> `feature/employee-overview`, `bugfix/co2-calc-error`

---

## 🧪 Test og QA

- Alle pull requests skal indeholde tests, hvor det er muligt.
- Brug JUnit og H2 til integrationstests.
- Dækningsgrad måles med JaCoCo.
- Koden køres gennem Qodana ved hver PR via GitHub Actions.

---

## 💡 Kodekonventioner

- Følg Java’s standard style + Spring Boot best practices.
- Brug `@Service`, `@Repository`, `@Controller` korrekt.
- Brug dependency injection via konstruktører – ikke field injection.
- Navngiv HTML-filer logisk: `edit-project.html`, `login.html`, osv.
- SQL-filer placeres i `src/main/resources/sql/`

---

## ✏️ Commit- og PR-beskeder

**Commits:**

**PR’er:**
- Beskriv hvad du har ændret og hvorfor
- Henvis gerne til issue eller user story

---

## 👤 Adgang og ansvar

> Dette repo vedligeholdes af Andreas Gabel (@Gabel1998)  
> Kun godkendte brugere kan merge til `develop` og `main`.

Hvis du er i tvivl — opret en issue først og beskriv, hvad du ønsker at bidrage med. Vi svarer hurtigt.

---

## 📜 Licens og brug

Projektet er omfattet af KEA’s regelsæt og må ikke anvendes kommercielt uden tilladelse. Bidrag må gerne anvendes til undervisning, showcase og faglig udvikling.

