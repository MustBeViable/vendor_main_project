Tietokantaprojektin puutteet

Tietokannan kaikki 50 tarkistusta onnistuvat, mutta kaikkia toteutettuja tietokantaominaisuuksia ei vielä hyödynnetä REST API:ssa.

1. Indeksit (2/4 mahdollisesti hyödynnetty)

- [ ] "idx_products_name" – API:ssa ei ole tuotteiden nimellä hakemista.
- [ ] "idx_products_stock_quantity" – API:ssa ei ole varastosaldon perusteella hakemista.
- [ ] "idx_orders_status_order_date" – Indeksin todellista käyttöä ei ole vahvistettu "EXPLAIN"-komennolla.
- [ ] "idx_orders_customer_order_date" – Indeksin todellista käyttöä ei ole vahvistettu "EXPLAIN"-komennolla.
- [ ] Tilauskyselyt järjestetään "id":n mukaan, vaikka indeksit sisältävät "order_date"-sarakkeen.

2. Näkymät (0/3 hyödynnetty)

- [ ] "customer_orders" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.
- [ ] "product_categories" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.
- [ ] "order_totals" – Näkymää ei käytetä Java-repositoryssa tai REST API:ssa.

3. Proseduurit (0/2 hyödynnetty)

- [ ] "stock_increase" – Proseduuria ei kutsuta Java-koodissa.
- [ ] "stock_decrease" – Proseduuria ei kutsuta Java-koodissa.
- [ ] "ProductsService.updateStock()" muuttaa varastosaldoa suoraan JPA:lla proseduurien sijaan.

4. Eventit (2/2 käytössä tietokannassa)

- [ ] "daily_order_count" – Eventin päivittämiä tilastoja ei tarjota REST API:n kautta.
- [ ] Eventtien onnistuneesta suorittamisesta ei ole erillistä toiminnallista testiä.

5. Temporaalisuus (2/2 käytössä tietokannassa)

- [ ] "orders" – API:sta puuttuu tilausten muutoshistorian hakeminen.
- [ ] "products" – API:sta puuttuu tuotteiden muutoshistorian hakeminen.
- [ ] "FOR SYSTEM_TIME AS OF" / "FOR SYSTEM_TIME ALL" -historiakyselyitä ei hyödynnetä Java-koodissa.

6. Käyttöoikeudet

- [ ] "employee" – Käyttäjälle ei ole toteutettu erillistä sovellusyhteyttä tai toimintoa.
- [ ] "report_viewer" – Raportointikäyttäjälle ei ole toteutettu erillistä sovellusyhteyttä.
- [ ] Eri tietokantakäyttäjien oikeuksien toiminnallinen testaus puuttuu.

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
