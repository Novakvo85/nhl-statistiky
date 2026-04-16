# NHL Statistiky

Bakalářská práce na téma: Webová aplikace pro ukládání sportovních statistik

Živá ukázka: https://novakv-statistiky.cz

## Technologie
- Java 21, Spring Boot 3
- MySQL
- Thymeleaf, Bootstrap
- Docker

## Požadavky
- [Docker](https://www.docker.com/get-started) a Docker Compose

Jiné závislosti nejsou potřeba – Java ani MySQL není nutné instalovat, vše běží v Dockeru.

## Instalace Docker Desktop
1. Stáhněte instalátor na [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/)
2. Spusťte instalátor a postupujte podle pokynů
3. Po instalaci spusťte Docker Desktop a počkejte až se plně načte
4. Pro ověření instalace spusťte v příkazovém řádku příkaz:
docker --version

## Spuštění

1. Naklonujte repozitář:`git clone https://github.com/Novakvo85/nhl-statistiky.git`, nebo jej stáhněte jako `.zip` a rozbalte.

2. Vytvořte soubor `.env` v kořeni projektu a vložte do něj tyto proměnné (hodnoty lze změnit podle potřeby):
```
DB_NAME=statistics
DB_USER=springuser
DB_PASSWORD=heslo
DB_ROOT_PASSWORD=rootHeslo
```

3. Otevřete příkazový řádek (cmd) v kořenové složce projektu a sestavte a spusťte aplikaci pomocí příkazu:
`docker compose up -d --build`

4. Po dokončení inicializace otevřete aplikaci na adrese `http://localhost:8080`.

### Pro zastavení aplikace spusťte tento příkaz:
`docker compose down`

## Přihlášení admina
- Uživatelské jméno: `admin`
- Heslo: `admin`

V případě potřeby lze heslo změnit na adrese `http://localhost:8080/profile`.

