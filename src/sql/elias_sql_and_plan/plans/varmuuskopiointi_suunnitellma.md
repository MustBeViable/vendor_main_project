## Varmuuskopiointisuunnitelma

Koko tietokannan looginen osuus varmuuskopioidaan lämpimällä tavalla kerran viikossa siten, että SELECT-kyselyt toimivat varmuuskopioinnin aikana.

Joka päivä eli 24 tunnin välein tehdään inkrementaalinen varmuuskopio kyseisen päivän muutoksista.

Tietokannan molemmat loogiset varmuuskopiot tehdään eri fyysiselle levyasemalle, josta tiedot voidaan tarvittaessa palauttaa tietokannan rikkoutuessa.

Täysi varmuuskopiointi suoritetaan verkkokaupan hiljaisimpana hetkenä sunnuntaisin klo 23.59.

Inkrementaalinen varmuuskopiointi suoritetaan 24 tuntia edellisestä varmuuskopioinnista, oli edellinen varmuuskopio inkrementaalinen tai täysi. Varmuuskopiointi suoritetaan päivän hiljaisimpana hetkenä klo 23.59.

Jokaisen onnistuneen varmuuskopion, sekä täyden että inkrementaalisen, jälkeen varatietokanta päivitetään uusimpaan versioon.

Varatietokanta sijaitsee varapalvelimella eikä se ole normaalisti suoraan liitettynä verkkoon. Vikatilanteessa se liitetään verkkoon automaattisesti.

Jos automaatio epäonnistuu, päivystävä tietokantavastaava saa ilmoituksen ja suorittaa tarvittavat toimenpiteet manuaalisesti.

Varmuuskopioinnin suorittaa tarvittaessa manuaalisesti `db_admin`-oikeudet omaava tietokantavastaava kuluvan yön aikana.

Muuten varmuuskopiointi toimii automaattisesti ja verkkokaupan tilaukset tallennetaan palvelimen välimuistiin myöhempää päivitystä varten varmuuskopioinnin suorittamisen ajaksi.

### Vikatilanteet ja tietokannan palautus

Vikatilanteesta vastaa päivystävä tietokantavastaava.

Hän varmistaa, että automaattiset yhteydet ohjautuvat varatietokantaan, varatietokanta on ajan tasalla ja automaattiset palautustoimenpiteet toimivat.

Tarvittaessa tietokantavastaava suorittaa palautuksen ja varatietokantaan siirtymisen manuaalisesti.

Vikatilanteessa varatietokanta otetaan käyttöön ja mahdolliset ostot ohjataan sinne.

Päätietokanta palautetaan ensin edelliseen täyteen varmuuskopioon. Tämän jälkeen mahdolliset inkrementaaliset varmuuskopiot palautetaan aikajärjestyksessä.

Onnistuneen palautuksen jälkeen tietokannalle suoritetaan testit.

Kun testit on läpäisty, varatietokannan tiedot kopioidaan inkrementaalisesti päätietokantaan ja päätietokanta päivitetään ajan tasalle.

Tämän jälkeen liikenne ohjataan takaisin päätietokantaan ja varmistetaan, ettei varatietokanta jää suoraan verkkoon.

Vikatilanne logitetaan ja päivystävä tietokantavastaava laatii vikaraportin. Raportti käsitellään seuraavan työpäivän ensimmäisessä kokouksessa.