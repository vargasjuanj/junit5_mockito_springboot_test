
-- el archivo import.sql es detectado automaticamente al iniciar alguna de las clases de prueba, se crea un bean con este archivo
-- cada insert debe estar en una misma linea si no no lo inserta
INSERT INTO cuentas (persona, saldo)VALUES ('Andrés',1000)
INSERT INTO cuentas (persona, saldo) VALUES ('Jhon',2000)
INSERT INTO bancos (nombre, total_transferencias)VALUES ('El banco financiero',0)