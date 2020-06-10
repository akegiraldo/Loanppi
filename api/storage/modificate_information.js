#!/usr/bin/node
const connection = require('./conection_database');


// resta inversion total del investor que hizo una inversion
const newBalanceInvestor = data => {
  connection.query('UPDATE investors SET investStack = investStack - ' + data.moneyInvestment + ' WHERE idInvestor = ' + data.idInvestor, (err,rows) => {
    if(err) throw err;
  
    console.log('monto maximo cambiado');
  });
}


module.exports = newBalanceInvestor;