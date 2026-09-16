# Idea: vendor eli kaupan näkymä verkkokaupan hallintaan

## Sovittuja juttuja:

### Käyttöoikeidet/käyttäjät:
 - db_admin
   - kaikki SELECT, INSERT, UPDATE, DELETE, ALTER
 - employee
   - Select muihin paitsi as tietoihin
   - update, insert, delete orders tauluun
 - ISO (?) tietoturvavastaava
   - select, update ja insert tietoturvatauluihin


## TODO:
Tuoda omat:
 - indexoinnit
 - viewit
 - transaktiot
 - triggerit
 - eventit
 - temporaali ratkaisut
 - lukkoratkaisut
 - varmuuskopiointisuunnitelma