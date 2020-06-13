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
  investStack INT(7) NOT NULL,
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
  loanAmount INT(7) NOT NULL,
  amountRemaining INT(7) NOT NULL,
  totalToPay INT(7) NOT NULL,
  timeToPay INT(2) NOT NULL,
  valueToPayWeekly INT(7) NOT NULL,
  interests INT(6) NOT NULL,
  status VARCHAR(255) NOT NULL,
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
  loanShare FLOAT(24) NOT NULL,
  PRIMARY KEY (idInvestment),
  KEY idInvestor (idInvestor),
  CONSTRAINT investment_ibfk_1 FOREIGN KEY (idInvestor) REFERENCES investors (idInvestor) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- TABLE FUNDING WHERE CONECT INVESTORS WITH WORKERS
CREATE TABLE IF NOT EXISTS funding(
  idFunding INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  idNeed INT(11) NOT NULL,
  idInvestment INT(11) NOT NULL,
  PRIMARY KEY (idFunding),
  KEY idNeed (idNeed),
  CONSTRAINT funding_ibfk_1 FOREIGN KEY (idNeed) REFERENCES needs (idNeed) ON DELETE NO ACTION,
  KEY idInvestment (idInvestment),
  CONSTRAINT funding_ibfk_2 FOREIGN KEY (idInvestment) REFERENCES investment (idInvestment) ON DELETE NO ACTION
) ENGINE=InnoDB;

-- TABLE WORKERS' PAYMENTS 
CREATE TABLE IF NOT EXISTS payments(
  idPayment INT(11) NOT NULL UNIQUE AUTO_INCREMENT,
  idWorker INT(11) NOT NULL,
  payment INT(6) NOT NULL,
  datePayment TIMESTAMP NOT NULL,
  PRIMARY KEY (idPayment),
  KEY idNeed (idWorker),
  CONSTRAINT payments_ibfk_1 FOREIGN KEY (idWorker) REFERENCES needs (idWorker) ON DELETE NO ACTION
  ) ENGINE=InnoDB;
  
-- TABLE RELATION PAYMENTS WITH INVESTORS
CREATE TABLE IF NOT EXISTS benefits(
  idReturn INT(11) NOT NULL NOT NULL UNIQUE AUTO_INCREMENT,
  idInvestment INT(11) NOT NULL,
  idPayment INT(11) NOT NULL,
  idNeed INT(11) NOT NULL,
  investorShare INT(6) NOT NULL,
  PRIMARY KEY (idReturn),
  CONSTRAINT benefits_ibfk_1 FOREIGN KEY (idPayment) REFERENCES payments (idPayment) ON DELETE NO ACTION,
  CONSTRAINT benefits_ibfk_2 FOREIGN KEY (idInvestment) REFERENCES  funding (idInvestment) ON DELETE NO ACTION,
  CONSTRAINT benefits_ibfk_3 FOREIGN KEY (idNeed) REFERENCES  funding (idNeed) ON DELETE NO ACTION
  ) ENGINE=InnoDB;

-- table para simular dinero
CREATE TABLE IF NOT EXISTS moneyWorkers(
  idMoney INT(11) NOT NULL,
  money INT(11) NOT NULL,
  idWorker INT(11) NOT NULL,
  idNeed INT(11) NOT NULL,
  PRIMARY KEY (idMoney),
  CONSTRAINT moneyWorkers_ibfk_1 FOREIGN KEY (idWorker) REFERENCES needs (idWorker) ON DELETE NO ACTION,
  CONSTRAINT moneyWorkers_ibfk_2 FOREIGN KEY (idNeed) REFERENCES  funding (idNeed) ON DELETE NO ACTION
) ENGINE=InnoDB;

