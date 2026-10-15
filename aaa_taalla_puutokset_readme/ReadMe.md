Tietokantaprojektin puutteet

Tietokannan kaikki 50 tarkistusta onnistuvat, mutta kaikkia toteutettuja tietokantaominaisuuksia ei vielä hyödynnetä REST API:ssa.

1. Indeksit (2/4 mahdollisesti hyödynnetty) **SAKKE**

- [X] "idx_products_name" – API:ssa ei ole tuotteiden nimellä hakemista.
- [X] "idx_products_stock_quantity" – API:ssa ei ole varastosaldon perusteella hakemista.
- [X] "idx_orders_status_order_date" – Indeksin todellista käyttöä ei ole vahvistettu "EXPLAIN"-komennolla.
- [X] "idx_orders_customer_order_date" – Indeksin todellista käyttöä ei ole vahvistettu "EXPLAIN"-komennolla.
- [X] Tilauskyselyt järjestetään "id":n mukaan, vaikka indeksit sisältävät "order_date"-sarakkeen.

2. Näkymät (0/3 hyödynnetty)

- [X] "customer_orders" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.
- [X] "product_categories" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.
- [X] "order_totals" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.

3. Proseduurit (0/2 hyödynnetty) **SAKKE**

- [X] "stock_increase" – Proseduuria ei kutsuta Java-koodissa.
- [X] "stock_decrease" – Proseduuria ei kutsuta Java-koodissa.
- [X] "ProductsService.updateStock()" muuttaa varastosaldoa suoraan JPA:lla proseduurien sijaan.

4. Eventit (2/2 käytössä tietokannassa)

- [X] "daily_order_count" – Eventin päivittämiä tilastoja ei tarjota REST API:n kautta.
- [X] Eventtien onnistuneesta suorittamisesta ei ole erillistä toiminnallista testiä.

5. Temporaalisuus (2/2 käytössä tietokannassa)

- [X] "orders" – API:sta puuttuu tilausten muutoshistorian hakeminen.
- [X] "products" – API:sta puuttuu tuotteiden muutoshistorian hakeminen.
- [X] "FOR SYSTEM_TIME AS OF" / "FOR SYSTEM_TIME ALL" -historiakyselyitä ei hyödynnetä Java-koodissa.

6. Käyttöoikeudet

- [X] "employee" – Käyttäjälle ei ole toteutettu erillistä sovellusyhteyttä tai toimintoa.
- [X] "report_viewer" – Raportointikäyttäjälle ei ole toteutettu erillistä sovellusyhteyttä.
- [X] Eri tietokantakäyttäjien oikeuksien toiminnallinen testaus puuttuu.

Näiden käyttäjien ei kuitenkaan tarvitse olla REST API:n käytössä, jos tarkoituksena on osoittaa tietokantatason käyttöoikeuksien hallinta.

7. Triggerit (3/3 hyödynnetty)

Ei tunnistettuja integraatiopuutteita. Kaikkia kolmea triggeriä hyödynnetään tilausrivien lisäyksessä, päivityksessä ja poistossa.

---

Priorisoidut jatkokehitystehtävät

1. Korkea: Lisää API-endpointit kolmelle näkymälle.
2. Korkea: Integroi varastosaldon kasvatus- ja vähennysproseduurit sovellukseen.
3. Keskitaso: Lisää tuotteiden nimi- ja varastosaldohaku indeksejä hyödyntäviksi kyselyiksi.
4. Keskitaso: Varmista indeksien käyttö "EXPLAIN"-komennoilla.
5. Keskitaso: Lisää tuotteiden ja tilausten muutoshistorian hakeminen.
6. Matala: Lisää päivittäisten tilausmäärien raportointi REST API:in.
7. Matala: Lisää käyttöoikeuksien ja eventtien toiminnalliset testit.
