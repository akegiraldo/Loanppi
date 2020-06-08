-- DROP DATABASE loanppi;
-- como correr eta monda sudo cat database.sql |sudo  mysql -hlocalhost -uroot -p
CREATE DATABASE IF NOT EXISTS loanppi;
USE loanppi;
CREATE TABLE  IF NOT EXISTS investors(
  idInvestor INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  firstName  VARCHAR(255) NOT NULL,
  idDocument VARCHAR(255) NOT NULL,
  secondName VARCHAR(255) NOT NULL,
  firstLastName VARCHAR(255) NOT NULL,
  secondLastName VARCHAR(255) NOT NULL,
  emailAddress VARCHAR(255) NOT NULL,
  phoneNumber VARCHAR(255) NOT NULL,
  homeAddress VARCHAR(255) NOT NULL,
  PRIMARY KEY (idInvestor)
) ENGINE=InnoDB;

-- ANOTHER TABLE WORKER
CREATE TABLE IF NOT EXISTS workers(
  idWorker INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  firstName  VARCHAR(255) NOT NULL,
  idDocument VARCHAR(255) NOT NULL,
  secondName VARCHAR(255) NOT NULL,
  firstLastName VARCHAR(255) NOT NULL,
  secondLastName VARCHAR(255) NOT NULL,
  emailAddress VARCHAR(255) NOT NULL,
  phoneNumber VARCHAR(255) NOT NULL,
  homeAddress VARCHAR(255) NOT NULL,
  PRIMARY KEY (idWorker)
) ENGINE=InnoDB;

-- INSERT INTO `workers` (first_name, identification_workers, second_name, first_last_name, second_last_name, email, phone_number, address)
-- VALUES ('santiaguitp', '1010', 'no tengo', 'aldana', 'naranjo', 'este@gmail', '5555', 'mi casita');

-- TABLE NEED OF WORKERS
CREATE TABLE IF NOT EXISTS needs (
  idNeed INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  idWorker INT(11) NOT NULL,
  totalToPay INT(7) NOT NULL,
  timeToPay INT(2) NOT NULL,
  valueToPayWeekly INT(7) NOT NULL,
  interests INT(6) NOT NULL,
  PRIMARY KEY (idNeed),
  KEY idWorker (idWorker),
  CONSTRAINT needs_ibfk_1 FOREIGN KEY (idWorker) REFERENCES workers (idWorker) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- INSERT INTO `needs` (id_worker, debt, dues)
-- VALUES (1, 12, 1);

-- TABLE INVESTMENT DEPENT OF INVESTORS
CREATE TABLE IF NOT EXISTS investment(
  idInvestment INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  idInvestor int(11) NOT NULL,
  moneyInvestment INT(7) NOT NULL,
  PRIMARY KEY (idInvestment),
  KEY idInvestor (idInvestor),
  CONSTRAINT investment_ibfk_1 FOREIGN KEY (idInvestor) REFERENCES investors (idInvestor) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- TABLE FUNDING WHERE CONECT INVESTORS WITH WORKERS
CREATE TABLE IF NOT EXISTS funding(
  idFounding INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  idNeed INT(11) NOT NULL,
  idInvestment INT(11) NOT NULL,
  PRIMARY KEY (idFounding),
  KEY idNeed (idNeed),
  CONSTRAINT funding_ibfk_1 FOREIGN KEY (idNeed) REFERENCES needs (idNeed) ON DELETE NO ACTION,
  KEY idInvestment (idInvestment),
  CONSTRAINT funding_ibfk_2 FOREIGN KEY (idInvestment) REFERENCES investment (idInvestment) ON DELETE NO ACTION
) ENGINE=InnoDB;

