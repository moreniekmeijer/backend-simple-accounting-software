# Backend Simple Accounting Software

Een simpele backend voor een boekhoudapplicatie, gebouwd met Spring Boot en PostgreSQL. De applicatie ondersteunt Google Drive-integratie en OCR-functionaliteit.

## Vereisten

Zorg dat je het volgende hebt geïnstalleerd op je systeem:

- Java 17 of hoger
- Maven
- PostgreSQL
- pgAdmin (optioneel voor beheer)
- Google Cloud-project met Drive API en Service Account
- OCR API key (bijv. [ocr.space](https://ocr.space/))

## Installatie

### 1. Repository clonen

```bash
git clone https://github.com/moreniekmeijer/backend-simple-accounting-software.git
cd backend-simple-accounting-software
```

### 2. Database aanmaken

Maak een nieuwe PostgreSQL-database aan met de naam:

```
simple-accounting-software
```

Je kunt dit doen via pgAdmin of de terminal:

```sql
CREATE DATABASE simple_accounting_software;
```

### 3. `.env.properties` bestand aanmaken

Maak een bestand aan in de root van het project met de naam `.env.properties` en vul de volgende waarden in:

```properties
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

OCR_API_KEY=your_ocr_api_key

GOOGLE_DRIVE_ROOT_FOLDER=your_drive_folder_id
GOOGLE_SERVICE_ACCOUNT_KEYFILE=path/to/your-service-account.json
```

> 🔒 Zorg ervoor dat het service account toegang heeft tot de gewenste Google Drive folder.

### 4. Applicatie starten

Gebruik Maven om de applicatie te starten:

```bash
./mvnw spring-boot:run
```

De applicatie draait standaard op `http://localhost:8080`.

## Belangrijke configuratie

De applicatie laadt automatisch `.env.properties` als configuratiebestand:

```properties
spring.config.import=optional:file:.env.properties
```

## Database-initialisatie

- De database wordt bij het opstarten automatisch gestructureerd via `spring.jpa.hibernate.ddl-auto=create`.
- Gegevens uit `data.sql` worden automatisch ingeladen.

## Frontend integratie

Er is een bijbehorende frontend beschikbaar waarmee je direct kunt communiceren met deze backend. Deze is te vinden op:

👉 [frontend-simple-accounting-software](https://github.com/moreniekmeijer/frontend-simple-accounting-software)

Volg de instructies in de frontend-repository om deze lokaal op te zetten en te koppelen aan deze backend.

## Vragen of problemen?

Open een issue op de [GitHub repository](https://github.com/moreniekmeijer/backend-simple-accounting-software/issues) of neem contact op via de bekende weg.