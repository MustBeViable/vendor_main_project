# Sakke [täs](src/documentation/sakarin_documentit.md) viel ohjeet miten lisäät omat dokumentoinnit ja minne.

# Vendor API – Tietokantaprojekti

Spring Boot 4.1.1-, JPA- ja MariaDB-pohjainen verkkokaupan tilausjärjestelmän REST API. API on toteutettu kauppa-alustan näkökulmasta. Se ei sisällä asiakkaan ostoja.

## 1. Tietokannan alustus

Tietokantana käytetään MariaDB:n `store_app`-tietokantaa.

**1. Lataa tietokantadumppi erikseen.**

Lataa `schema_populated_dump.sql` projektin tekijältä tai kurssimateriaalista ja sijoita se hakemistoon `database/`.

Dumppi ei sisälly GitHub-repositorioon, sillä sen koko ylittää GitHubin **100 Mt:n tiedostokokorajoituksen**.

**2. Tuo tietokanta.**

Varmista, että MariaDB on käynnissä. Suorita projektin juurihakemistosta:

```bash
bash src/sql/database-creation-scripts/01_import_database.sh
```

Skripti luo `store_app`-tietokannan tarvittaessa ja tuo dumpin, jos tietokanta on tyhjä. Se ei korvaa olemassa olevia tietoja.

**3. Suorita loput SQL-skriptit järjestyksessä.**

Kirjaudu MariaDB:hen projektin juurihakemistossa:

```bash
mariadb -u root -p
```

Suorita MariaDB-konsolissa:

```sql
SOURCE src/sql/database-creation-scripts/02_indexes.sql;
SOURCE src/sql/database-creation-scripts/03_views.sql;
SOURCE src/sql/database-creation-scripts/04_triggers.sql;
SOURCE src/sql/database-creation-scripts/05_procedures.sql;
SOURCE src/sql/database-creation-scripts/06_events.sql;
SOURCE src/sql/database-creation-scripts/07_temporal.sql;
SOURCE src/sql/database-creation-scripts/08_priviledges.sql;

SET GLOBAL event_scheduler = ON;

SOURCE src/sql/database-creation-scripts/09_verify.sql;
```

Viimeinen skripti tarkistaa tietokannan rakenteet ja ominaisuudet sekä näyttää onnistuneiden tarkistusten määrän.

## 2. Tietokannan toteutetut ominaisuudet

### Indeksit

| Indeksi | Tarkoitus |
|---|---|
| `idx_orders_status_order_date` | Nopeuttaa tilausten hakemista tilan ja päivämäärän perusteella. |
| `idx_orders_customer_order_date` | Nopeuttaa asiakkaan tilausten hakua aikajärjestyksessä. |
| `idx_products_name` | Tukee tuotteiden nimellä hakemista. |
| `idx_products_stock_quantity` | Tukee tuotteiden varastosaldoon perustuvia kyselyitä. |

### Näkymät

| Näkymä | Tarkoitus |
|---|---|
| `customer_orders` | Yhdistää asiakkaat, tilaukset ja tilausrivit raportointia varten. |
| `product_categories` | Näyttää tuotteiden kategoriat, hinnat ja varastosaldot. |
| `order_totals` | Laskee tilausten kokonaisarvon ja tuotemäärän. |

Kaikkia kolmea näkymää hyödynnetään REST API:n raportointi-endpointeissa.

### Triggerit ja proseduurit

Kolme triggeriä (`orderitems_stock_insert`, `orderitems_stock_update`, `orderitems_stock_delete`) ylläpitää varastosaldoa automaattisesti tilausrivien lisäyksen, muutoksen ja poiston yhteydessä.

Proseduurit `stock_increase` ja `stock_decrease` mahdollistavat tuotteiden varastosaldojen hallitun kasvattamisen ja vähentämisen tietokantatasolla.

### Ajastetut tapahtumat

- `check_old_orders`: merkitsee yli seitsemän päivää `NEW`-tilassa olleet tilaukset `CHECK_REQUIRED`-tilaan tunnin välein.
- `update_daily_order_count`: päivittää nykyisen ja edellisen päivän tilausmäärät tunnin välein.

`daily_order_count`-taulu alustetaan myös olemassa olevasta tilaushistoriasta.

### Temporaalisuus

`orders`- ja `products`-tauluissa käytetään MariaDB:n **SYSTEM VERSIONING** -ominaisuutta, joka tallentaa muutosten historian automaattisesti.

REST API hyödyntää `FOR SYSTEM_TIME ALL`- ja `FOR SYSTEM_TIME AS OF` -kyselyitä tilausten ja tuotteiden aikaisempien versioiden hakemisessa.

### Transaktiot ja tietoturva

JPA:n transaktioilla hallitaan tietokantaoperaatioita, ja MariaDB:n käyttöoikeudet rajaavat eri käyttäjien sallittuja toimintoja.

Tietokantakäyttäjät ovat `db_admin`, `vendor_app`, `employee` ja `report_viewer`. REST API käyttää HTTP Basic -tunnistautumista ja rooleja `ADMIN`, `EMPLOYEE` ja `REPORT_VIEWER`.

Asetukset löytyvät `application.yaml`-tiedostosta. Nykyiset plaintext-tunnukset on tarkoitettu paikalliseen kehitys- ja testikäyttöön.

## 3. REST API:n käyttö

Käynnistä Spring Boot -sovellus:

```bash
./mvnw spring-boot:run
```

API toimii osoitteessa `http://localhost:8081`.

HTTP-pyynnöt voidaan suorittaa esimerkiksi IntelliJ IDEA:n `.http`-tiedostoista.

| Metodi | Käyttö | Tyypillinen vastaus |
|---|---|---|
| `GET` | Tietojen hakeminen | `200 OK` + JSON |
| `POST` | Tietueen luominen | `201 Created` + JSON |
| `PUT` | Tietueen päivittäminen | `200 OK` + JSON |
| `PATCH` | Yksittäisen kentän muuttaminen | `200 OK` + JSON |
| `DELETE` | Tietueen poistaminen | `204 No Content` |

POST-, PUT- ja PATCH-pyynnöissä lähetetään tarvittavat tiedot JSON-muodossa. Sivutetuissa hauissa käytetään parametreja `page` ja `size`.

Esimerkki:

```http
GET http://localhost:8081/orders?page=0&size=20
Authorization: Basic admin <API-salasana>
```

### 3.1 Asiakkaat ja tilaukset

| Endpoint | Käyttötarkoitus |
|---|---|
| `POST /customers` | Luo asiakkaan. |
| `GET /customers/{id}/orders` | Hakee asiakkaan tilaukset, myös statuksella suodattaen. |
| `GET /orders` | Hakee tilaukset sivutettuna; suodatus `customerId` ja `status`. |
| `GET /orders/{id}` | Hakee yksittäisen tilauksen. |
| `GET /orders/{id}/items` | Hakee tilauksen tilausrivit. |
| `POST /orders` | Luo tilauksen. |
| `PUT /orders/{id}` | Päivittää tilauksen. |
| `PATCH /orders/{id}/status` | Muuttaa tilauksen statuksen. |
| `DELETE /orders/{id}` | Poistaa tilauksen. |

Tilaukset palautetaan `OrdersDTO`-muodossa (mm. `id`, `customerId`, `orderDate`, `deliveryDate`, `shippingAddressId`, `status`). Sivutetut haut palauttavat `Page`-rakenteen.

### 3.2 Tilausrivit

| Endpoint | Käyttötarkoitus |
|---|---|
| `GET /orderitems` | Hakee tilausrivit sivutettuna. |
| `GET /orderitems/{orderId}/{productId}` | Hakee yksittäisen tilausrivin. |
| `POST /orderitems` | Lisää tuotteen tilaukseen. |
| `PUT /orderitems/{orderId}/{productId}` | Päivittää määrän tai hinnan. |
| `DELETE /orderitems/{orderId}/{productId}` | Poistaa tilausrivin. |

JSON sisältää `orderId`, `productId`, `quantity` ja `unitPrice`. Tilausrivien muutokset käynnistävät tietokannan varastotriggerit.

### 3.3 Toimittajat ja osoitteet

Seuraavilla resursseilla on kaikilla `GET`-, `GET /{id}`-, `POST`-, `PUT /{id}`- ja `DELETE /{id}`-endpointit:

| Resurssi | Käyttötarkoitus ja JSON-vastaus |
|---|---|
| `/suppliers` | Toimittajien CRUD; `id`, `name`, `contactName`, `phone`, `email`. Sivutus sekä `name`-suodatus. |
| `/supplieraddresses` | Toimittajien osoitteiden CRUD; `id`, `supplierId`, `streetAddress`, `postalCode`, `city`, `country`. Suodatus `supplierId`-parametrilla. |
| `/customeraddresses` | Asiakkaiden osoitteiden CRUD; `id`, `customerId` ja osoitekentät. Sivutus ja `customerId`-suodatus. |

Käytössä olevien toimittajien ja tilausten toimitusosoitteiden poistamista rajoitetaan tietojen eheyden säilyttämiseksi.

### 3.4 SQL-näkymien raportointi

| GET-endpoint | Käyttötarkoitus ja vastaus |
|---|---|
| `/reports/customer-orders?customerId=1` | Asiakkaan tilausrivit, tuotetiedot ja rivisummat. |
| `/reports/order-totals?customerId=1` | Tilausten kokonaismäärät ja rahasummat. |
| `/reports/product-categories?categoryId=1` | Kategorian tuotteet, hinnat ja varastosaldot. |
| `/reports/daily-order-count?from=2025-01-01&to=2025-12-31` | Päivittäiset tilausmäärät (`date`, `orderCount`). |

Raportit palautetaan sivutettuina DTO-tietueina.

### 3.5 Erillisten tietokantakäyttäjien raportit

| GET-endpoint | Käyttötarkoitus |
|---|---|
| `/reports/employee/customer-orders?customerId=1` | Lukee asiakastilausraportin `employee`-tietokantayhteydellä. |
| `/reports/report-viewer/daily-order-count?from=2025-01-01&to=2025-12-31` | Lukee tilastot `report_viewer`-yhteydellä. |

Ensimmäinen vaatii HTTP-roolin `EMPLOYEE` tai `ADMIN`, jälkimmäinen `REPORT_VIEWER` tai `ADMIN`. Muut endpointit edellyttävät `ADMIN`-roolia.

### 3.6 Temporaalinen historia

| GET-endpoint | Käyttötarkoitus |
|---|---|
| `/orders/{id}/history` | Palauttaa kaikki tilauksen versiot. |
| `/products/{id}/history` | Palauttaa kaikki tuotteen versiot. |

Molemmat hyväksyvät valinnaisen `asOf`-parametrin, esimerkiksi `?asOf=2026-10-15T12:00:00`.

Vastauksena palautetaan JSON-taulukko, jossa ovat tietueen kentät sekä `versionStart` ja `versionEnd`.

### 3.7 Tuotteet ja tuotekategoriat

#### Endpointit

| Metodi | Endpoint | Käyttötarkoitus |
|--------|----------|-----------------|
| GET | `/products` | Hakee tuotteet. Suodattimet: `?categoryId=`, `?name=`, `?maxStock=` |
| GET | `/products/{id}` | Hakee tuotteen ID:n perusteella. |
| POST | `/products` | Luo uuden tuotteen. |
| PUT | `/products/{id}` | Päivittää tuotteen tiedot. |
| DELETE | `/products/{id}` | Poistaa tuotteen. |
| POST | `/products/{id}/stock/increase?amount=10` | Kasvattaa varastosaldoa. |
| POST | `/products/{id}/stock/decrease?amount=5` | Vähentää varastosaldoa. |
| GET | `/api/product-categories` | Hakee kaikki tuotekategoriat. |
| GET | `/api/product-categories/{id}` | Hakee kategorian ID:n perusteella. |
| POST | `/api/product-categories` | Luo uuden tuotekategorian. |
| PUT | `/api/product-categories/{id}` | Päivittää tuotekategorian. |
| DELETE | `/api/product-categories/{id}` | Poistaa tuotekategorian. |

#### GET /products — suodattimet

Kaikki suodattimet ovat valinnaisia.

| Parametri | Esimerkki | Mitä tekee |
|-----------|-----------|------------|
| `categoryId` | `?categoryId=2` | Hakee kategorian tuotteet |
| `name` | `?name=Sw` | Hakee tuotteet, joiden nimi alkaa "Sw" |
| `maxStock` | `?maxStock=10` | Hakee tuotteet, joissa varasto on enintään 10 |

#### Pyyntö- ja vastausesimerkki

**POST /products**

Pyyntö:
```json
{
  "name": "Testituote",
  "description": "Kuvaus",
  "price": 9.99,
  "stockQuantity": 10,
  "categoryId": 1,
  "supplierId": 1
}
```

Vastaus 201 Created: sama rakenne + `id`.

#### HTTP-virhekoodit

| Koodi | Tilanne |
|-------|---------|
| 200 | Päivitys onnistui |
| 201 | Tuote tai kategoria luotu |
| 204 | Tuote tai kategoria poistettu |
| 400 | Virheellinen määrä tai puuttuva nimi |
| 404 | Tuotetta tai kategoriaa ei löydy |
| 409 | Riittämätön varasto tai kategoriaa ei voi poistaa |

Virheet palautetaan `GlobalExceptionHandler`-luokasta:

```json
{ "error": "Insufficient stock" }
```

#### Toimintarajoitukset

- Varastosaldo ei voi mennä negatiiviseksi.
- Kategoriaa ei voi poistaa, jos siihen on liitetty tuotteita.
- Varaston muutokset tehdään proseduurien `stock_increase` ja `stock_decrease` kautta.

Nämä palauttavat 409 Conflict.

## 4. Testaus

### **HUOM!** aja nämä testit vain testitietokantaan
HTTP-testit löytyvät hakemistosta `src/test/http_test/`. Niillä testataan onnistuneet operaatiot, virheelliset syötteet, käyttöoikeudet ja tietokannan toimintojen vaikutukset.

Tietokantarakenteiden tarkistamiseen käytetään `09_verify.sql`-skriptiä. Kirjoittavat HTTP-testit tulee suorittaa erillisessä testitietokannassa.


## 5. Tietokannan ylläpitosuunnitelmat

Tietokannan ylläpitoa varten on laadittu kaksi suunnitelmaa, jotka sijaitsevat hakemistossa `src/documentation/plans/`.

- **[Tietokantamuutossuunnitelma](src/documentation/plans/tietokantamuutos_suunnitelma.md)** – Kuvaa tietokannan rakenteellisten muutosten suunnittelun, toteutuksen ja testauksen sekä mahdollisiin ongelmiin varautumisen.

- **[Varmuuskopiointisuunnitelma](src/documentation/plans/varmuuskopiointi_suunnitellma.md)** – Kuvaa tietokannan varmuuskopioinnin, tietojen palauttamisen ja tietojen säilymisen varmistamisen.

Suunnitelmien tarkoituksena on varmistaa tietokannan eheys, turvallinen ylläpito ja palautettavuus muutosten tai ongelmatilanteiden yhteydessä.
