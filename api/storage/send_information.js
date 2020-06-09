#!/usr/bin/node
const connection = require('./conection_database');

// solo inserta datos de un json a la base de datos
function saveData(data) {
  const userType = data.userType + 's';
  delete data['userType'];
  if (userType == "investors") {
    data['investStack'] = 1000000
  }
  return new Promise((resolve, reject)=> {
    const callbackDB =  function (err, result) {
      if (err) {
        reject(err);
        return;
      } else {
        resolve({'status': 'ok'});
      }
    }
    connection.query('INSERT INTO ' + userType + ' SET ?', data , callbackDB);
  });
}


//inserta en la base de datos una necesidad con el id del worker
function sendDebt(data) {
  data['status'] = 'pending';
  data['amountRemaining'] = data['loanAmount']
  return new Promise((resolve, reject) => {
    const callbackInserDebt = function (err, result) {
      if (err) {
        reject(err);
        return;
      } else {
        resolve({'status': 'pending'});
        return;
      }
    }
    connection.query('INSERT INTO needs SET ?', data , callbackInserDebt);
  });
}


function updateUser(data) {
  const userType = data.userType + 's';
  if (userType === "workers") {
    const id = "idWorker";
  } else {
    const id = "idInvestor";
  }
  const callbackDB = (err, result) => {
    if (err) throw err;
    resolve({"status": "Profile has been succesfully updated"});
  }
  connection.query("UPDATE " + usertype + " SET ? WHERE id = ?", id, callbackDB);
}


module.exports = { saveData, sendDebt, updateUser };
