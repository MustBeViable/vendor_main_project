## Tietokantamuutosten suunnitelma

Työryhmä laatii muutossuunnitelman, jossa kaikki työryhmän jäsenet arvioivat muutoksen järkevyyden.

Muutossuunnitelma annetaan lisäksi työryhmän ulkopuoliselle henkilölle tarkistettavaksi ja luettavaksi.

Kun kaikki osapuolet ovat hyväksyneet muutossuunnitelman, luodaan kaksi tietokantaa:

- testitietokanta
- vertailutietokanta, joka vastaa senhetkisen varmuuskopion sisältöä

Tietokantaan ajetaan muutosskripti ja tietokannan eheys tarkistetaan eheystesteillä.

Kun testit on läpäisty, tarkistetaan lisäksi, etteivät tietokannan sisältö tai kyselyt ole rikkoutuneet.

Kun työryhmä on varmistanut, ettei muutos riko tietokannan logiikkaa, tietoliikenne siirretään varatietokantaan.

Tämän jälkeen päätietokannasta tehdään varmuuskopio, joka säilytetään eri palvelimella kuin päätietokanta.

Tietokantapäivitykset suoritetaan ennalta ilmoitettuna ajankohtana päiväsaikaan kyseisen päivän hiljaisimpana hetkenä.

Kun päivitys on onnistunut, liikenne siirretään takaisin päivitettyyn päätietokantaan.

Tämän jälkeen varatietokanta kytketään pois aktiivisesta käytöstä ja siihen ajetaan samat muutokset kuin päätietokantaan.

### Vikatilanteet

Jos tietokantamuutos epäonnistuu, työryhmä logittaa epäonnistumisen.

Päätietokantaan palautetaan päivitetyt tiedot varatietokannasta ja liikenne ohjataan takaisin toimivaan tietokantaan.