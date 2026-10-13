# Tuo populoitu sql tähän kansioon nimellä: schema_populated_dump.sql

Github rajoittaa ylin 100mt tiedostojen koon. Joten tuo se ladattu tiedosto tuolla nimellä tähän kansioon, 
jotta databasen luoniscripti toimii.

Ensiksi pitää dropata vanha db, jos on samalla nimellä (nimi=store_app).

Mene rootilla mariadb ja aja:
```sql
CREATE DATABASE store_app
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### **HUOM!** seuraavat askeleet root käyttäjällä

poistu mariadb ja aja bash (linux/mac):

```bash
cd ~/Downloads/vendor_main

mariadb -h 127.0.0.1 -P 3306 -u root -p store_app \
  < database/schema_populated_dump.sql
```

windows (powershellillä):

```powershell
cd "$HOME\Downloads\vendor_main"

cmd /c "mariadb -h 127.0.0.1 -P 3306 -u root -p store_app < database\schema_populated_dump.sql"
```
