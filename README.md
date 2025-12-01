# Amőba játék — projekt összefoglaló
### Készítette: Ruska Dominik FD69KA

Ez a projekt egy teljes értékű, parancssoros Amőba játék Java 21 és Maven alapokon. A játék stabilan fut, a logika tesztelve van, a build mvn clean install parancs sikeresen lefut hibák nélkül, és a projekt tesztlefedettsége legalább 80% JaCoCo mérés szerint.

## Főbb funkciók és tulajdonságok
Teljes játéklogika: NxM méretű tábla támogatás (alapértelmezett 10×10), lépésellenőrzés, nyerésellenőrzés (4 egymás melletti jel), döntetlen felismerés.

- Játékosok: emberi játékos (X) és egyszerű AI (O). A játékos kezd, a játék parancssorból vezérelhető.

- Mentés és betöltés: játékállás mentése és visszatöltése (XML vagy JSON formátum opcionálisan), fájl alapú mentések kezelése a menüben.

- Adatbázis és High Score: játékosok neve és győzelmeinek száma H2 adatbázisban tárolva; parancssoros high score tábla megjelenítése.

- Színes kimenet: a tábla konzolos megjelenítése ANSI színekkel — X kék, O piros, üres mezők szürke/fehér.

- Konfigurálhatóság: tábla mérete és egyéb paraméterek könnyen állíthatók.

## AI és játékszabályok
- AI viselkedés: az AI egyszerű, megbízható védekező logikát és véletlenszerű lépéseket kombinál. Felismeri a közvetlen veszélyeket (pl. ha a játékos két összefüggő X-et hoz létre), és megpróbálja blokkolni a harmadik X lerakását. Nem professzionális stratégia, de a játék célját és a versenyképes viselkedést megfelelően szolgálja.

- Szabályok: a játék célja 4 egymás melletti jel elérése vízszintesen, függőlegesen vagy átlósan. A kezdő jel a tábla középső részére kerül, a további lépések szabályellenességét a program ellenőrzi.

## Minőségbiztosítás és tesztelés
- Egységtesztek: JUnit5 és Mockito használatával írt, stabil unit tesztek a játék logikájára (lépésellenőrzés, nyerésdetektálás, mentés/betöltés, DAO műveletek).

- Tesztlefedettség: JaCoCo mérés alapján a projekt legalább 80% lefedettséget ér el az üzleti logikát tartalmazó osztályokra. A ProcessBuilder alapú integrációs tesztek külön JVM-ben futnak; a lefedettség növeléséhez a kritikus logikát tesztelhető run(...) metódusokba szerveztük, így a JaCoCo ugyanabban a JVM-ben gyűjti az adatokat.

- Statikus ellenőrzés: Checkstyle beállítva a kódminőség fenntartására; a build a checkstyle szabályokkal együtt is lefut.

Logolás: SLF4J + Logback konfiguráció a futás közbeni diagnosztikához és hibakereséshez.

## Build és futtatás
- Fejlesztési környezet: Java 21, Maven.

- Futtatás fejlesztés alatt:

- Tesztek és riportok: mvn clean verify

- Gyors build és tesztek: mvn clean install

- Futtatható JAR: a maven-shade-plugin vagy assembly konfiguráció létrehozza a függőségeket tartalmazó futtatható JAR-t; a manifestben a Main-Class beállítva. A JAR futtatható: java -jar target/amoba-1.0-SNAPSHOT-jar-with-dependencies.jar.

## Tervezési minták és dokumentáció
- Tervezési minták dokumentum: a projekt gyökérkönyvtárában található tervezési minták.pdf, amely legalább három mintát részletez (példák, miért választottuk őket, hogyan alkalmaztuk a projektben).

- Kód és architektúra: a projektben alkalmazott minták és döntések röviden dokumentálva vannak a README‑ban és a PDF‑ben (pl. DAO minta a perzisztenciához, Strategy vagy Service minta az AI/ játéklogika elkülönítéséhez, Factory vagy Builder minták konfigurációs objektumokhoz).

## Miért megbízható ez a projekt
- Automatizált build és tesztek: a mvn clean install parancs sikeresen lefut, a tesztek stabilak és determinisztikusak.

- Tesztelhetőség: a játék logikája injektálható komponensekre bontva lett, így a kritikus ágak unit tesztjei ugyanabban a JVM‑ben futnak és JaCoCo által mérhetők.

- Biztonságos mentés és perzisztencia: mentések fájlba és adatbázisba történnek, a fájlok kezelése és a DAO réteg tesztelve van (H2 in‑memory tesztek).

- Karbantarthatóság: Checkstyle, logolás és jól strukturált kód segít a hosszú távú fenntartásban.

