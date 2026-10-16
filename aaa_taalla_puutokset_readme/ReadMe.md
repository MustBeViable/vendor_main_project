
# Vendor API – Jatkokehitysehdotukset

Repository: https://github.com/MustBeViable/vendor_main_project

Arvio perustuu main-haaran lähdekoodiin ja kurssin arviointikriteereihin. Sovellusta ei ole ajettu tätä arviointia varten.

## 1. Korkea prioriteetti – Toiminnallisuuden täydentäminen

### 1.1 Asiakkaiden CRUD täydelliseksi
- [X] Lisää `GET /customers` – kaikkien asiakkaiden hakeminen.
- [X] Lisää `GET /customers/{id}` – yksittäisen asiakkaan hakeminen.
- [X] Lisää `PUT /customers/{id}` – asiakastietojen päivittäminen.
- [X] Lisää `DELETE /customers/{id}` – asiakkaan poistaminen, jos viittaukset sen sallivat.
- [X] Lisää asiakaslistaukseen sivutus.

**Perustelu:** Asiakkailla on nyt vain luonti ja asiakkaan tilausten haku. Täydellinen CRUD laajentaa keskeisen resurssin API-toiminnallisuutta.

### 1.2 HTTP-virheiden käsittely
- [ ] Korjaa `GlobalExceptionHandler`, joka käsittelee kaikki poikkeukset yleisellä `Exception.class`-käsittelijällä.
- [ ] Varmista, että `ResponseStatusException` palauttaa oikean HTTP-koodin eikä muutu 500-virheeksi.
- [ ] Yhtenäistä virhevastausten JSON-rakenne.
- [ ] Testaa erityisesti virhekoodit 400, 404 ja 409.

**Perustelu:** Nykyinen yleinen virheenkäsittelijä voi muuttaa tarkoitukselliset asiakasvirheet 500 Internal Server Error -vastauksiksi.

### 1.3 Tuotteiden varastosaldon hallinta
- [ ] Muuta `ProductsService.update()` niin, ettei tavallinen tuotetietojen päivitys voi ohittaa varastosaldon hallintaa.
- [ ] Käytä varastosaldon muutoksiin ensisijaisesti olemassa olevia `stock_increase`- ja `stock_decrease`-proseduureja.
- [ ] Varmista, ettei negatiivista varastosaldoa voi tallentaa.
- [ ] Lisää testit varastosaldon muutoksiin.

**Perustelu:** Tuotteen PUT-päivitys muuttaa tällä hetkellä `stockQuantity`-kenttää suoraan, vaikka varastolle on omat proseduurinsa.

### 1.4 Tuotteiden hakutoimintojen parantaminen
- [ ] Mahdollista usean suodattimen käyttäminen samanaikaisesti: `categoryId`, `name` ja `maxStock`.
- [ ] Lisää `GET /products` -hakuun sivutus (`page`, `size`).
- [ ] Lisää selkeä lajittelu hinnan, nimen tai varastosaldon mukaan.
- [ ] Testaa, miten indeksit vaikuttavat suodatettuihin hakuihin.

**Perustelu:** Nykyinen toteutus käyttää vain ensimmäistä annettua suodatinta eikä sivuta tuotetuloksia.

### 1.5 Tilauksen peruuttaminen
- [ ] Lisää hallittu tilauksen peruutustoiminto.
- [ ] Palauta tilatut tuotteet varastoon peruutuksen yhteydessä.
- [ ] Suorita varaston palautus ja tilauksen tilamuutos samassa transaktiossa.
- [ ] Estä saman tilauksen peruuttaminen useaan kertaan.
- [ ] Testaa, että epäonnistunut peruutus ei muuta varastosaldoja osittain.

**Perustelu:** Nykyinen `OrdersService` estää `CANCELLED`-tilan asettamisen, koska peruutuksen varastokäsittely puuttuu.

### 1.6 Syötteiden validoinnin täydentäminen
- [ ] Lisää DTO-luokkiin Bean Validation -validointeja, esimerkiksi `@NotNull`, `@NotBlank` ja `@Min`.
- [ ] Estä negatiiviset tuotehinnat ja virheelliset varastomäärät myös tuotteen luomisessa ja päivittämisessä.
- [ ] Varmista asiakkaiden sähköpostiosoitteiden oikea formaatti.
- [ ] Palauta virheellisistä syötteistä johdonmukaisesti 400 Bad Request.

**Perustelu:** Osa rajapinnoista validoi syötteitä jo hyvin, mutta validointi ei ole kaikissa resursseissa yhtenäistä.

---

## 2. Korkea prioriteetti – Kurssin tietokantatekniikoiden osoittaminen

### 2.1 Laajempi transaktioesimerkki
- [ ] Toteuta toiminto, jolla luodaan tilaus ja useita tilausrivejä yhdellä API-kutsulla.
- [ ] Käytä `@Transactional`-annotaatiota koko operaatiolle.
- [ ] Testaa tilanne, jossa yhden tilausrivin lisääminen epäonnistuu.
- [ ] Varmista, että koko tilaus ja varastomuutokset peruuntuvat (ROLLBACK).
- [ ] Dokumentoi transaktion vaiheet README:hen.

**Perustelu:** Olemassa olevien yksittäisten transaktioiden lisäksi tämä osoittaa usean tietokantaoperaation atomisuuden.

### 2.2 Samanaikaisuuden hallinta
- [ ] Testaa kahden yhtäaikaisen tilauksen vaikutus saman tuotteen varastosaldoon.
- [ ] Varmista, ettei varastosaldo voi mennä negatiiviseksi kilpailutilanteessa.
- [ ] Dokumentoi triggerin käyttämän ehdollisen UPDATE-operaation rooli samanaikaisuuden hallinnassa.
- [ ] Lisää testi, jossa vain toinen kahdesta kilpailevasta tilauksesta voi onnistua, jos varastoa ei riitä molemmille.

**Perustelu:** Kurssilla käsiteltiin lukituksia ja samanaikaisuutta. Nykyisessä triggerissä on jo ehdollinen UPDATE, joten sen toiminnan osoittaminen olisi erityisen hyödyllistä.

### 2.3 Indeksien suorituskyvyn mittaaminen
- [ ] Suorita `EXPLAIN` kaikille neljälle projektin omalle indeksille.
- [ ] Kirjaa, käyttääkö kysely kyseistä indeksiä todellisuudessa.
- [ ] Vertaa kyselyjen suoritusaikoja indeksin kanssa ja ilman indeksiä.
- [ ] Kirjaa testidatan määrä ja mittaustulokset.
- [ ] Lisää lyhyt yhteenveto README:hen.

**Perustelu:** SQL-tiedostossa on jo EXPLAIN-esimerkkejä. Suorituskykymittaukset osoittavat, miksi indeksit on valittu.

### 2.4 Triggerien toiminnalliset testit
- [ ] Testaa `INSERT`: tilausrivin lisääminen vähentää varastoa.
- [ ] Testaa `UPDATE`: määrän muuttaminen päivittää varastosaldoa oikein.
- [ ] Testaa `DELETE`: tilausrivin poistaminen palauttaa varastoa.
- [ ] Testaa riittämättömän varastosaldon aiheuttama virhe ja rollback.
- [ ] Dokumentoi testitulokset.

**Perustelu:** Triggerit ovat jo olemassa. Niiden toiminnan osoittaminen on tärkeämpää kuin uusien triggerien luominen ilman käyttötarvetta.

### 2.5 Eventtien toiminnan todentaminen
- [ ] Testaa `check_old_orders`-eventin vaikutus yli seitsemän päivää vanhoihin NEW-tilauksiin.
- [ ] Testaa `update_daily_order_count`-eventin vaikutus tilastotauluun.
- [ ] Varmista, että `event_scheduler` on käytössä.
- [ ] Dokumentoi ennen/jälkeen-tilanteet erillisessä testitietokannassa.

**Perustelu:** Nykyinen tarkistusskripti tarkistaa eventtien rakenteen, mutta ei yksin todista niiden suorittamisen onnistumista.

### 2.6 Temporaalitietokannan testaus
- [ ] Muuta tuotteen hintaa ja tarkista aiempi arvo historiasta.
- [ ] Muuta tilauksen statusta ja tarkista aiempi versio.
- [ ] Testaa `FOR SYSTEM_TIME ALL`.
- [ ] Testaa `FOR SYSTEM_TIME AS OF`.
- [ ] Dokumentoi esimerkki historiatiedon hyödyntämisestä.

**Perustelu:** Temporaalinen historia on jo toteutettu. Käyttöesimerkit osoittavat sen toiminnan käytännössä.

---

## 3. Keskikorkea prioriteetti – Tietoturva ja testaus

### 3.1 Salasanojen turvallinen käsittely
- [ ] Siirrä `application.yaml`-tiedoston salasanat ympäristömuuttujiin.
- [ ] Poista oikeat salasanat versionhallinnassa olevista SQL-skripteistä.
- [ ] Lisää esimerkkikonfiguraatio kehitysympäristöä varten.
- [ ] Vaihda mahdolliset käytössä olleet versionhallintaan tallennetut salasanat.
- [ ] Dokumentoi tarvittavat ympäristömuuttujat.

**Perustelu:** Nykyisessä repositoriossa on selkokielisiä tietokanta- ja API-tunnuksia.

### 3.2 Käyttöoikeuksien lisätestaus
- [ ] Säilytä nykyiset tietokantakäyttäjien integraatiotestit.
- [ ] Lisää HTTP-tason testit ADMIN-, EMPLOYEE- ja REPORT_VIEWER-rooleille.
- [ ] Varmista, ettei raportointikäyttäjä voi käyttää tietojen muokkausrajapintoja.
- [ ] Testaa vastaukset 401 Unauthorized ja 403 Forbidden.

**Perustelu:** Tietokantakäyttäjien oikeuksia testataan jo, mutta HTTP-roolien erottelu kannattaa osoittaa myös automaattisilla testeillä.

### 3.3 Automaattiset integraatiotestit
- [ ] Lisää tilausten CRUD-integraatiotestit.
- [ ] Lisää tuotteiden ja tilausrivien integraatiotestit.
- [ ] Testaa transaktioiden rollback ja varastotriggerit.
- [ ] Käytä erillistä testitietokantaa.
- [ ] Siirrä `ProductsCascadeTest.java` ja `ProductCategoriesTest.java` hakemistosta `src/main/java` hakemistoon `src/test/java`.

**Perustelu:** Projektissa on jo HTTP-testitiedostoja, mutta ne eivät korvaa automaattisia JUnit-integraatiotestejä.

### 3.4 Varmuuskopion palauttamisen testaus
- [ ] Tee tietokannasta oikea varmuuskopio.
- [ ] Palauta se erilliseen testitietokantaan.
- [ ] Varmista palautettujen taulujen ja tietojen eheys.
- [ ] Dokumentoi palautustestin lopputulos.

**Perustelu:** Varmuuskopiointisuunnitelma on jo olemassa. Käytännön palautustesti osoittaa, että suunnitelma toimii.

---

## 4. Keskikorkea prioriteetti – Dokumentaation viimeistely

### 4.1 README:n API-dokumentaatio
- [ ] Tarkista, että jokainen Java-controllerin endpoint löytyy README:stä.
- [ ] Lisää puuttuville endpoint-kuvauksille pyyntöjen JSON-esimerkit.
- [ ] Lisää keskeiset JSON-vastausesimerkit.
- [ ] Dokumentoi tärkeimmät virhetilanteet ja niiden HTTP-koodit.
- [ ] Dokumentoi yhdistetyt hakuehdot ja sivutus.
- [ ] Kuvaa tietokantatekniikoiden yhteys niitä käyttäviin API-toimintoihin.

**Perustelu:** Tehtävänanto painottaa dokumentaatiota, jonka perusteella toteutetun toiminnallisuuden täytyy olla ymmärrettävissä.

### 4.2 Alustusohjeiden yhtenäistäminen
- [ ] Yhtenäistä `README.md`- ja `database/readme.md`-tiedostojen tietokannan alustusohjeet.
- [ ] Poista vanhat ristiriitaiset ohjeet tietokannan poistamisesta ennen alustusta.
- [ ] Varmista, että SQL-skriptit voidaan suorittaa dokumentoidussa järjestyksessä.
- [ ] Testaa projektin pystytys puhtaaseen ympäristöön.

**Perustelu:** Pää-README kuvaa nykyisen alustusskriptin toimintaa, mutta `database/readme.md` sisältää myös vanhempia manuaalisia ohjeita.

### 4.3 Vanhentuneen puutelistan päivittäminen
- [ ] Päivitä `aaa_taalla_puutokset_readme/ReadMe.md`.
- [ ] Poista jo korjatut puutteet tai merkitse ne toteutetuiksi ja tarkistetuiksi.
- [ ] Lisää jäljellä olevat kehityskohteet.
- [ ] Varmista, ettei puutelista ole ristiriidassa pää-README:n kanssa.

**Perustelu:** Vanhassa puutelistassa on vielä kohtia, jotka on jo toteutettu nykyisessä main-haarassa.

---

## 5. Lisäominaisuudet – Jos aikaa jää

### 5.1 Varastoraportointi
- [ ] Lisää raportti vähäisen varastosaldon tuotteista.
- [ ] Lisää toimittajakohtainen varastoraportti.
- [ ] Lisää tuotteiden yhteenlasketun varastoarvon laskenta.
- [ ] Toteuta raportti SQL-näkymällä ja tarjoa sille GET-endpoint.

### 5.2 Tilausten tilastointi
- [ ] Lisää tilausten kokonaismyynti päivittäin tai kuukausittain.
- [ ] Lisää myydyimpien tuotteiden raportti.
- [ ] Lisää asiakkaan ostohistorian yhteenveto.
- [ ] Hyödynnä JOIN-, GROUP BY- ja aggregaattikyselyitä.

### 5.3 API-dokumentaation automatisointi
- [ ] Lisää OpenAPI/Swagger-dokumentaatio.
- [ ] Kuvaa endpointtien parametrit ja vastausmallit.
- [ ] Mahdollista API:n kokeileminen selainkäyttöliittymästä.

### 5.4 Kehitysympäristön automatisointi
- [ ] Lisää Docker Compose Spring Bootille ja MariaDB:lle.
- [ ] Lisää erillinen testitietokanta.
- [ ] Lisää GitHub Actions -työnkulku automaattisten testien suorittamiseen.

---

## 6. Suositeltu toteutusjärjestys

Jos aikaa on vähän, keskittyisin seuraaviin:

1. **HTTP-virheiden käsittely** – korjaa mahdolliset virheelliset vastaukset.
2. **Asiakkaiden CRUD** – täydentää keskeisen resurssin toiminnallisuuden.
3. **Varastosaldon hallinta** – estää tietojen eheyden rikkoutumisen.
4. **Transaktio- ja samanaikaisuustestit** – osoittavat kurssilla opittuja tekniikoita.
5. **Indeksien EXPLAIN- ja suorituskykymittaukset** – perustelevat tehdyt ratkaisut.
6. **Tietoturvan parantaminen** – poistaa kovakoodatut tunnukset.
7. **README:n viimeistely** – tekee toteutetun työn näkyväksi arvioijalle.

### Yhteenveto

Projektissa on jo suurin osa vaadituista tietokantaominaisuuksista.

Parhaat seuraavat kehitysaskeleet ovat:
- Nykyisten toiminnallisuuksien korjaaminen ja täydentäminen.
- Transaktioiden, triggerien, indeksien ja samanaikaisuuden toiminnan testaaminen.
- Tietokantaratkaisujen hyötyjen osoittaminen käytännössä.
- Kaikkien toteutusten selkeä dokumentointi.

Uusien ominaisuuksien määrän sijaan kannattaa painottaa toimivia, testattuja ja perusteltuja ratkaisuja.
