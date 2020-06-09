#!/usr/bin/node
const connection = require('./conection_database');

//busca si un usuario existe en la base de datos
function findUser(data) {
  const userType = data.userType + 's';
  return new Promise((resolve, reject) => {
    const userExists = function (err, result) {
      if (err) {
        reject(err);
        return;
      }
      if (result && result.length > 0) {
        resolve({'status': 'exists'});
      } else {
        resolve({'status' : 'not exists'});
      }
  }
  connection.query('SELECT * FROM ' + userType + ' WHERE emailAddress = ?', data.emailAddress, userExists);
});
}

function getUser(email, userType) {
  console.log(userType);
  return new Promise((resolve, reject)=> {
  const callbackDB =  function (err, result) {
    if (err) {
      reject(err);
      return;
    }

    if (result && result.length > 0) {
      console.log("User exists");
      result[0]['status'] = 'exists';
      result[0]['userType'] = userType;
      console.log(result[0]);
      resolve(result[0]);
    } else {
        console.log("User does not exist");
        resolve({"status": "not exists"});
    }
  }
  connection.query("SELECT * FROM "+  userType  +" WHERE emailAddress = ?", email , callbackDB);
});
}

//Verficar el estado del prestamo
function availableNeeds(data) {
  delete data['idNeed'];
  delete data['timeToPayWeekly'];
  delete data['interests'];
  return new Promise ((resolve, reject) => {
    const callbackAvailable = (err ,result) => {
      if (err) {
        reject(err);
        return;
      } else {
        delete result['idNeed'];
         
        resolve(result);
        return;
      }
    }
    connection.query("SELECT * FROM needs WHERE status IN ('pending') LIMIT 5", callbackAvailable);
  });
}

module.exports = { findUser, getUser, availableNeeds };
