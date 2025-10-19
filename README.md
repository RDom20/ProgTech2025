# Amőba Játék

Készítette: Ruska Dominik (FD69KA)

## Játék Alapok

A játék két játékos között zajlik: az egyik a felhasználó (User), a másik pedig az AI (Mesterséges Intelligencia).

### Játékmenet

Kezdés: A felhasználó kezd, és lerak egy piros X-et.

AI lépése: Ezután az AI véletlenszerűen lerak egy kék O-t.

Felhasználó lépése: A felhasználó ismét lerak egy X-et, és a játék folytatódik, amíg valaki eléri a győzelmet.

## Játékszabályok

AI védekezés: Ha a felhasználó két X-et helyez el, és azok összefüggnek (vagyis egy egyenes vonalon állnak), az AI észleli ezt, és megakadályozza, hogy a felhasználó 3 egymás melletti X-et helyezzen el.

Győzelem: A játék addig folytatódik, amíg valaki elér egy 4 egymás melletti jelet. Az első, aki ezt eléri, nyer.

## Hibák

AI logika: Az AI logikája nem a legjobb, de a játék célját megfelelően ellátja.

Játék mentése: A játék mentése működik, viszont a mentett állapot betöltése nem működik megfelelően; csak egy üres játéktáblát tölt be.
