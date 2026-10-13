#!/usr/bin/env bash

set -euo pipefail

# 01_import_dump.sh
# Luo store_app-tietokannan tarvittaessa.
# Tuo alkuperäisen SQL-dumpin vain tyhjään tietokantaan.

DB_NAME="store_app"

# Skriptin sijainti:
# src/sql/database-creation-scripts/
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Dumpin sijainti:
# database/schema_populated_dump.sql
DUMP_PATH="${SCRIPT_DIR}/../../../database/schema_populated_dump.sql"

# Tarkistetaan dump-tiedoston olemassaolo.
if [[ ! -f "$DUMP_PATH" ]]; then
    echo "VIRHE: SQL-dumppia ei löytynyt:"
    echo "$DUMP_PATH"
    exit 1
fi

# Tarkistetaan MariaDB-asiakasohjelma.
if ! command -v mariadb >/dev/null 2>&1; then
    echo "VIRHE: mariadb-komentoa ei löytynyt."
    exit 1
fi

echo "Tarkistetaan tietokanta: $DB_NAME"

# Luodaan tietokanta tarvittaessa.
# Samalla tarkistetaan, sisältääkö se jo tauluja.
TABLE_COUNT=$(
    mariadb -h 127.0.0.1 -P 3306 -u root -p \
        --batch --skip-column-names -e "
        CREATE DATABASE IF NOT EXISTS store_app
            CHARACTER SET utf8mb4
            COLLATE utf8mb4_unicode_ci;

        SELECT COUNT(*)
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = 'store_app'
          AND TABLE_TYPE IN ('BASE TABLE', 'SYSTEM VERSIONED');
        "
)

# Jos tauluja on jo olemassa, scripti ei tuo dumppia uudelleen.
if [[ "$TABLE_COUNT" -gt 0 ]]; then
    echo "Tietokanta sisältää jo $TABLE_COUNT taulua."
    echo "Dumpin tuonti ohitetaan tietojen suojaamiseksi."
    exit 0
fi

echo "Tietokanta on tyhjä. Aloitetaan populointi..."
echo "Tiedosto: $DUMP_PATH"

# Tuodaan alkuperäiset taulut ja kaikki tietueet.
mariadb -h 127.0.0.1 -P 3306 -u root -p \
    "$DB_NAME" < "$DUMP_PATH"

echo "Tietokannan luonti ja populointi valmis."
