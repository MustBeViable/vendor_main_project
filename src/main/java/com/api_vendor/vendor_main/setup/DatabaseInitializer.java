
package com.api_vendor.vendor_main.setup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

public final class DatabaseInitializer {

    private static final String DB_NAME = "store_app";

    private static final List<String> SCRIPTS = List.of(
            "02_indexes.sql",
            "03_views.sql",
            "04_triggers.sql",
            "05_procedures.sql",
            "06_events.sql",
            "07_temporal.sql",
            "08_privileges.sql"
    );

    private DatabaseInitializer() {}

    public static void initialize() {

        // Alustus on käytössä vain, kun se sallitaan erikseen.
        if (!Boolean.parseBoolean(env("DB_SETUP_ENABLED", "false"))) {
            return;
        }

        if (!DB_NAME.equals(env("DB_NAME", DB_NAME))) {
            throw new IllegalStateException(
                    "DB_NAME must be store_app: scripts use store_app."
            );
        }

        String user = env("DB_SETUP_USER", "root");
        String password = System.getenv("DB_SETUP_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Set DB_SETUP_PASSWORD for database setup."
            );
        }

        if (Set.of(
                "db_admin", "vendor_app", "employee", "report_viewer"
        ).contains(user)) {
            throw new IllegalStateException(
                    "Use an independent privileged setup user, not " + user
            );
        }

        // Projektin juurikansio.
        Path root = Path.of(
                env("DB_PROJECT_ROOT", System.getProperty("user.dir"))
        ).toAbsolutePath().normalize();

        Path sqlDir = root.resolve(
                "src/sql/database-creation-scripts"
        );

        Path dump = root.resolve(
                "database/schema_populated_dump.sql"
        );

        // Tarkistetaan SQL-tiedostojen olemassaolo.
        for (String script : SCRIPTS) {
            requireFile(sqlDir.resolve(script));
        }

        Path verify = sqlDir.resolve("09_verify.sql");
        requireFile(verify);

        // Korvataan salasanojen paikkamerkit ympäristömuuttujista.
        String privilegesSql = preparePrivileges(
                sqlDir.resolve("08_privileges.sql")
        );


        Client client = new Client(user, password);

// Tarkistetaan, että store_app-tietokanta on jo olemassa.
        System.out.println("[DB] Checking existing database...");

        int tableCount = Integer.parseInt(client.execute(
                "SELECT COUNT(*) FROM information_schema.TABLES " +
                        "WHERE TABLE_SCHEMA='store_app' AND TABLE_NAME IN " +
                        "('contacts','customeraddresses','customers','orderitems'," +
                        "'orders','productcategories','products'," +
                        "'supplieraddresses','suppliers') " +
                        "AND TABLE_TYPE IN ('BASE TABLE','SYSTEM VERSIONED')",
                false
        ).trim());

        if (tableCount != 9) {
            throw new IllegalStateException(
                    "Database store_app is missing or incomplete. " +
                            "Found " + tableCount + "/9 required tables. " +
                            "Import the database dump before starting the application."
            );
        }

        System.out.println(
                "[DB] Existing database verified. Starting SQL setup..."
        );

        client.execute(
                "CREATE TABLE IF NOT EXISTS setup_applied_scripts (" +
                        "filename VARCHAR(128) PRIMARY KEY, " +
                        "checksum CHAR(64) NOT NULL, " +
                        "applied_at TIMESTAMP NOT NULL " +
                        "DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB",
                true
        );

        // 4. Suoritetaan SQL-skriptit järjestyksessä.
        for (String filename : SCRIPTS) {

            Path path = sqlDir.resolve(filename);

            String checksum = sha256(path);

            String saved = client.execute(
                    "SELECT checksum FROM setup_applied_scripts " +
                            "WHERE filename='" + filename + "'",
                    true
            ).trim();

            // Jo onnistuneesti ajettua skriptiä ei ajeta uudelleen.
            if (saved.equals(checksum)) {
                System.out.println(
                        "[DB] Already applied: " + filename
                );
                continue;
            }

            // Estetään vanhan migraation muuttaminen jälkikäteen.
            if (!saved.isEmpty()) {
                throw new IllegalStateException(
                        filename +
                                " has changed since the last execution. " +
                                "Create a new migration instead."
                );
            }

            System.out.println("[DB] Running: " + filename);

            // Oikeusskriptin salasanat annetaan muistista.
            if ("08_privileges.sql".equals(filename)) {
                client.executeText(privilegesSql);
            } else {
                client.executeFile(path);
            }

            // Merkitään skripti suoritetuksi vasta onnistumisen jälkeen.
            client.execute(
                    "INSERT INTO setup_applied_scripts " +
                            "(filename, checksum) VALUES ('" +
                            filename + "','" + checksum + "')",
                    true
            );
        }

        // 5. Suoritetaan tietokannan tarkistus joka käynnistyksellä.
        System.out.println("[DB] Verifying database...");

        String verification = client.executeFile(verify);

        System.out.println(verification);

        if (verification.contains("TARKISTA PUUTTEET")
                || !verification.contains("KAIKKI OK")) {

            throw new IllegalStateException(
                    "09_verify.sql did not confirm database integrity."
            );
        }

        System.out.println(
                "[DB] Database initialization completed."
        );
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    // Täydennetään 08_privileges.sql:n salasanat ympäristömuuttujista.
    private static String preparePrivileges(Path path) {
        try {
            String sql = Files.readString(path);

            String[][] credentials = {
                    {
                            "VAIHDA_DB_ADMIN_SALASANA",
                            "DB_ADMIN_ACCOUNT_PASSWORD"
                    },
                    {
                            "VAIHDA_VENDOR_APP_SALASANA",
                            "DB_APP_PASSWORD"
                    },
                    {
                            "VAIHDA_EMPLOYEE_SALASANA",
                            "DB_EMPLOYEE_PASSWORD"
                    },
                    {
                            "VAIHDA_REPORT_VIEWER_SALASANA",
                            "DB_REPORT_PASSWORD"
                    }
            };

            for (String[] pair : credentials) {

                if (!sql.contains(pair[0])) {
                    continue;
                }

                String secret = System.getenv(pair[1]);

                // Rajataan merkit, jotta salasana voidaan
                // sijoittaa turvallisesti SQL-merkkijonoon.
                if (secret == null ||
                        !secret.matches(
                                "[A-Za-z0-9!@#$%^&*._+=?\\-]{12,}"
                        )) {

                    throw new IllegalStateException(
                            "Set " + pair[1] +
                                    " (12+ characters; allowed: letters, " +
                                    "numbers and !@#$%^&*._+=?-)"
                    );
                }

                sql = sql.replace(pair[0], secret);
            }

            if (sql.contains("VAIHDA_")) {
                throw new IllegalStateException(
                        "Unreplaced credentials in 08_privileges.sql"
                );
            }

            return sql;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot read " + path, e
            );
        }
    }

    private static void requireFile(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "Missing file: " + path
            );
        }
    }

    // Lasketaan tiedoston SHA-256-tarkistussumma.
    private static String sha256(Path path) {
        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            try (var in = Files.newInputStream(path)) {

                byte[] buffer = new byte[8192];
                int read;

                while ((read = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }

            return HexFormat.of().formatHex(digest.digest());

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Cannot checksum " + path, e
            );
        }
    }

    // MariaDB:n komentoriviasiakkaan suorittaminen.
    // Toimii Windowsissa, macOS:ssä ja Linuxissa.
    private static final class Client {

        private final String username;
        private final String password;

        private Client(String username, String password) {
            this.username = username;
            this.password = password;
        }

        private String execute(String sql, boolean useDatabase) {
            return run(null, sql, useDatabase, false);
        }

        private String executeFile(Path sqlFile) {
            return run(sqlFile, null, true, false);
        }

        private String executeText(String sql) {
            return run(null, sql, true, true);
        }

        private String run(
                Path inputFile,
                String sql,
                boolean useDatabase,
                boolean viaStdin
        ) {

            List<String> command = new java.util.ArrayList<>();

            command.add(env("DB_CLIENT", "mariadb"));
            command.add("--host=" + env("DB_HOST", "127.0.0.1"));
            command.add("--port=" + env("DB_PORT", "3306"));
            command.add("--protocol=tcp");
            command.add("--user=" + username);
            command.add("--default-character-set=utf8mb4");
            command.add("--batch");
            command.add("--skip-column-names");

            if (useDatabase) {
                command.add("--database=" + DB_NAME);
            }

            if (sql != null && !viaStdin) {
                command.add("--execute=" + sql);
            }

            ProcessBuilder processBuilder =
                    new ProcessBuilder(command);

            // Salasanaa ei anneta komentoriviargumenttina.
            processBuilder.environment().put(
                    "MYSQL_PWD", password
            );

            processBuilder.redirectErrorStream(true);

            // Suoritetaan SQL-tiedosto suoraan ilman
            // koko dumpin lukemista Java-muistiin.
            if (inputFile != null) {
                processBuilder.redirectInput(inputFile.toFile());
            }

            try {

                Process process = processBuilder.start();

                // Käyttöoikeusskriptin täydennetty sisältö
                // lähetetään suoraan MariaDB-asiakkaalle.
                if (viaStdin) {
                    try (var stdin = process.getOutputStream()) {
                        stdin.write(
                                sql.getBytes(StandardCharsets.UTF_8)
                        );
                    }
                }

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream();

                process.getInputStream().transferTo(output);

                int exitCode = process.waitFor();

                String result = output.toString(
                        StandardCharsets.UTF_8
                );

                if (exitCode != 0) {
                    throw new IllegalStateException(
                            "MariaDB command failed (exit " +
                                    exitCode + "):\n" + result
                    );
                }

                return result;

            } catch (IOException e) {

                throw new IllegalStateException(
                        "Cannot run MariaDB client. " +
                                "Install mariadb client or set DB_CLIENT.",
                        e
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Database setup interrupted", e
                );
            }
        }
    }
}
