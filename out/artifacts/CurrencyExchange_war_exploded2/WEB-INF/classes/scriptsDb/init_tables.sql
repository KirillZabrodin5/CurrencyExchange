CREATE TABLE Currencies (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            code VARCHAR(128) UNIQUE NOT NULL ,
                            full_name VARCHAR(128) NOT NULL,
                            sign VARCHAR(128) NOT NULL
);

CREATE TABLE ExchangeRates (
                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                               base_currency_id INT NOT NULL REFERENCES Currencies(id),
                               target_currency_id INT NOT NULL REFERENCES Currencies(id),
                               rate Decimal(6),
                               UNIQUE (base_currency_id, target_currency_id)
);