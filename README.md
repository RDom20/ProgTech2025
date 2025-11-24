# Amőba Játék

Készítette: Ruska Dominik (FD69KA)

## Játék Alapok

A játék két játékos között zajlik: az egyik a felhasználó (User), a másik pedig az AI (Mesterséges Intelligencia).

### Játékmenet

Kezdés: A felhasználó kezd, és lerak egy piros X-et.

AI lépése: Ezután az AI véletlenszerűen lerak egy kék O-t.

Felhasználó lépése: A felhasználó ismét lerak egy X-et, és a játék folytatódik, amíg valaki eléri a győzelmet.

A győzelmeket egy adatbázisban menti és a program végén ki is írja a legjobb eredményeket (felhasználónév és nyerések száma).

## Játékszabályok

AI védekezés: Ha a felhasználó két X-et helyez el, és azok összefüggnek (vagyis egy egyenes vonalon állnak), az AI észleli ezt, és megakadályozza, hogy a felhasználó 3 egymás melletti X-et helyezzen el.

Győzelem: A játék addig folytatódik, amíg valaki elér egy 5 egymás melletti jelet. Az első, aki ezt eléri, nyer.



