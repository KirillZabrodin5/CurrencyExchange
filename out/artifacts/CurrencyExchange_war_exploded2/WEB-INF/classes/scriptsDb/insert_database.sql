INSERT INTO Currencies (code, full_name, sign)
VALUES ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('RUB', 'Russian Ruble', '₽'),
       ('UAH', 'Hryvnia', '₴'),
       ('KZT', 'Tenge', '₸'),
       ('GBP', 'Pound Sterling', '£');

INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate)
VALUES (1, 2,0.94),
       (1, 3, 63.75),
       (1, 4, 36.95),
       (1, 5, 469.88),
       (1, 6, 0.81);

