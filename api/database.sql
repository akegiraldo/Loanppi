DROP DATABASE loanppi;
-- como correr eta monda sudo cat database.sql |sudo  mysql -hlocalhost -uroot -p
CREATE DATABASE IF NOT EXISTS loanppi;
USE loanppi;
CREATE TABLE  IF NOT EXISTS investors(
  id_investor INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  firstName  VARCHAR(255) NOT NULL,
  idDocument VARCHAR(255) NOT NULL,
  secondName VARCHAR(255) NOT NULL,
  firstLastName VARCHAR(255) NOT NULL,
  secondLastName VARCHAR(255) NOT NULL,
  emailAddress VARCHAR(255) NOT NULL,
  phoneNumber VARCHAR(255) NOT NULL,
  homeAddress VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_investor)
) ENGINE=InnoDB;

-- ANOTHER TABLE WORKER
CREATE TABLE IF NOT EXISTS workers(
  id_worker INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  firstName  VARCHAR(255) NOT NULL,
  idDocument VARCHAR(255) NOT NULL,
  secondName VARCHAR(255) NOT NULL,
  firstLastName VARCHAR(255) NOT NULL,
  secondLastName VARCHAR(255) NOT NULL,
  emailAddress VARCHAR(255) NOT NULL,
  phoneNumber VARCHAR(255) NOT NULL,
  homeAddress VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_worker)
) ENGINE=InnoDB;

-- INSERT INTO `workers` (first_name, identification_workers, second_name, first_last_name, second_last_name, email, phone_number, address)
-- VALUES ('santiaguitp', '1010', 'no tengo', 'aldana', 'naranjo', 'este@gmail', '5555', 'mi casita');

-- TABLE NEED OF WORKERS
CREATE TABLE IF NOT EXISTS needs (
  id_need INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  id_worker INT(11) NOT NULL,
  debt INT(7) NOT NULL,
  dues INT(2) NOT NULL,
  PRIMARY KEY (id_need),
  KEY id_worker (id_worker),
  CONSTRAINT needs_ibfk_1 FOREIGN KEY (id_worker) REFERENCES workers (id_worker) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- INSERT INTO `needs` (id_worker, debt, dues)
-- VALUES (1, 12, 1);

-- TABLE INVESTMENT DEPENT OF INVESTORS
CREATE TABLE IF NOT EXISTS investment(
  id_investment INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  id_investor int(11) NOT NULL,
  money_investment INT(7) NOT NULL,
  PRIMARY KEY (id_investment),
  KEY id_investor (id_investor),
  CONSTRAINT investment_ibfk_1 FOREIGN KEY (id_investor) REFERENCES investors (id_investor) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- TABLE FUNDING WHERE CONECT INVESTORS WITH WORKERS
CREATE TABLE IF NOT EXISTS funding(
  id_founding INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  PRIMARY KEY (id_founding),
  KEY id_need (id_need),
  CONSTRAINT funding_ibfk_1 FOREIGN KEY (id_need) REFERENCES needs (id_need) ON DELETE NO ACTION,
  KEY id_investment (id_investment),
  CONSTRAINT funding_ibfk_1 FOREIGN KEY (id_investment) REFERENCES investment (id_investment) ON DELETE NO ACTION
) ENGINE=InnoDB;

