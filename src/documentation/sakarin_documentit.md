
### 3.7 Tuotteet ja tuotekategoriat (Sakarihon)

Tämän osion dokumentaation täydentää tuotteiden ja tuotekategorioiden REST API -toteutuksesta vastannut projektin toinen jäsen.

Dokumentoi seuraavat endpointit:

| Metodi | Endpoint | Käyttötarkoitus |
|---|---|---|
| GET | `/products` | Hakee tuotteet, valinnainen `categoryId`-suodatus. |
| GET | `/products/{id}` | Hakee tuotteen ID:n perusteella. |
| POST | `/products` | Luo uuden tuotteen. |
| PUT | `/products/{id}` | Päivittää tuotteen tiedot. |
| PUT | `/products/{id}/stock?quantity=10` | Päivittää tuotteen varastosaldon. |
| DELETE | `/products/{id}` | Poistaa tuotteen. |
| POST | `/api/product-categories` | Luo uuden tuotekategorian. |

Täydennä dokumentaatioon:
- Endpointtien käyttötarkoitus ja mahdolliset parametrit.
- POST- ja PUT-pyyntöjen JSON-rungon rakenne.
- Palautettavan JSON-vastauksen keskeiset kentät.
- Mahdolliset HTTP-virhekoodit ja erityiset toimintarajoitukset.

Lisää myös lyhyet esimerkit tuotteen ja tuotekategorian luomisesta HTTP-pyynnöllä.

Dokumentoi vain toteutetut toiminnallisuudet ja varmista tiedot nykyisestä Java-koodista.
