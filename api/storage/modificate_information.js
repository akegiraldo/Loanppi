#!/usr/bin/node
const connection = require('./conection_database');


// resta inversion total del investor que hizo una inversion
const newBalanceInvestor = data => {
  connection.query('UPDATE investors SET investStack = investStack - ' + data.moneyInvestment + ' WHERE idInvestor = ' + data.idInvestor, (err,rows) => {
    if(err) throw err;
  
    console.log('cantidad de inversion disponible cambiada');
  });
}

const updatemoneyNeed = data => {
  connection.query('UPDATE needs SET amountRemaining = amountRemaining - ' + data.moneyInvestment + ' WHERE idNeed = ' + data.idNeed, (err, rows) => {
    if(err) throw err;

    console.log("dinero cambiado en cuanto le falta ala necesidad");
  })
}

const checkStatusNeed = data => {
  const id = data.idNeed
  connection.query("UPDATE needs SET status='resolved' WHERE idNeed = " + id + " AND amountRemaining = 0", (err, rows) => {
    if(err) throw err;

    console.log("UNA NECESIDAD FUE COMPLETADA YEI!!!!!!!");
  })
}

module.exports = { newBalanceInvestor, updatemoneyNeed, checkStatusNeed };
