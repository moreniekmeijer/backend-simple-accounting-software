INSERT INTO company_details (id, name, street, postal_code, city, phone, email, iban, vat_number, chamber_of_commerce)
VALUES
    (1, 'Mijn Bedrijf', 'Straatnaam 1', '1234 AA', 'Amsterdam', '0612345678', 'info@mijnbedrijf.nl', 'NL91ABNA0417164300', 'NL123456789B01', '12345678');

INSERT INTO expense (date, invoice_number, amount, vat, category)
VALUES ('2024-12-01', 'INV-2024-001', 120.00, 0.21, 'Kantoorbenodigdheden'),
       ('2024-12-05', 'INV-2024-002', 45.50, 0.09, 'Reiskosten'),
       ('2024-12-10', 'INV-2024-003', 300.00, 0.21, 'Software'),
       ('2024-12-12', 'INV-2024-004', 88.00, 0.21, 'Maaltijd'),
       ('2024-12-15', 'INV-2024-005', 150.00, 0.21, 'Apparatuur');

INSERT INTO client (name, contact_person, street, postal_code, city)
VALUES ('WAMV', 'Kees Kaas', 'Lesstraat 1', '1234 AB', 'Arnhem'),
       ('TJSO', 'Peter Beter', 'Repeteerlaan 2', '1234 BC', 'Hengelo'),
       ('LeukFestival', 'Daan van Rijn', 'Festivalstraat 99', '1234 CD', 'Amsterdam');

INSERT INTO invoice (invoice_number, invoice_date, client_id, total_excl_vat, total_incl_vat, vat_exempt)
VALUES ('ACM-2024-001', '2024-12-01', 1, 1000.00, 1000.00, true),
       ('TN-2024-002', '2024-12-05', 2, 750.00, 750.00, true);

INSERT INTO invoice_line (invoice_id, description, date, duration_minutes, hourly_rate, amount)
VALUES (1, 'Consultancy sessie backend-architectuur', '2024-11-28', 180, 80.00, 240.00),
       (1, 'Projectmanagement sprint 12', '2024-11-30', 120, 75.00, 150.00),
       (2, 'Opstellen security audit rapport', '2024-12-02', 240, 85.00, 340.00),
       (2, 'Telefonisch advies', NULL, NULL, NULL, 20.00);

