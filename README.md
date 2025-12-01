# Amőba játék — projekt összefoglaló
### Készítette: Ruska Dominik FD69KA

Ez a projekt egy teljes értékű, parancssoros Amőba játék Java 21 és Maven alapokon. A játék stabilan fut, a logika tesztelve van, a build `mvn clean install` parancs sikeresen lefut hibák nélkül, és a projekt tesztlefedettsége legalább 80% JaCoCo mérés szerint.

---

## Főbb funkciók és tulajdonságok
Teljes játéklogika: NxM méretű tábla támogatás (alapértelmezett 10×10), lépésellenőrzés, nyerésellenőrzés, döntetlen felismerés.

- **Játékosok**: emberi játékos (X) és egyszerű AI (O). A játékos kezd, a játék parancssorból vezérelhető.
- **Mentés és betöltés**: játékállás mentése és visszatöltése, fájl alapú mentések kezelése a menüben.
- **Adatbázis és High Score**: játékosok neve és győzelmeinek száma H2 adatbázisban tárolva; parancssoros high score tábla megjelenítése.
- **Színes kimenet**: a tábla konzolos megjelenítése ANSI színekkel — X kék, O piros, üres mezők szürke, top 3 játékos színes (arany, eüst, bronz).
- **Konfigurálhatóság**: tábla mérete és egyéb paraméterek könnyen állíthatók.

---

## AI és játékszabályok
- **AI viselkedés**: az AI egyszerű, megbízható védekező logikát és véletlenszerű lépéseket kombinál. Felismeri a közvetlen veszélyeket és megpróbálja blokkolni a veszélyes sorokat.
- **Szabályok**: a játék célja 5 egymás melletti jel elérése vízszintesen, függőlegesen vagy átlósan.

---

## Minőségbiztosítás és tesztelés
- **Egységtesztek**: JUnit5 és Mockito használatával írt, stabil unit tesztek a játék logikájára.
- **Tesztlefedettség**: JaCoCo mérés alapján a projekt legalább 80% lefedettséget ér el az üzleti logikát tartalmazó osztályokra.
- **Statikus ellenőrzés**: Checkstyle beállítva a kódminőség fenntartására.
- **Logolás**: SLF4J + Logback konfiguráció a futás közbeni diagnosztikához.

---

## Build és futtatás
- **Fejlesztési környezet**: Java 21, Maven.
- **Tesztek és riportok**:
```bash
mvn clean install
```
---
