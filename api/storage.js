#!/usr/bin/node
const mysql = require('mysql');

const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: 'root',
  database: 'loanppi'
});

connection.connect((err) => {
  if(err){
    console.log('Error connecting to Db');
    return;
  }
  console.log('Connection established');
});

// solo inserta datos de un json a la base de datos
exports.saveData = function (data) {
  const userType = data.userType + 's';
  delete data['userType'];
  return new Promise((resolve, reject)=> {
    const callbackDB =  function (err, result) {
      if (err) {
        reject(err);
        return;
      } else {
        resolve("ok");
      }
    }
    connection.query('INSERT INTO ' + userType + ' SET ?', data , callbackDB);
  });
};

//busca si un usuario existe en la base de datos
exports.findUser = function (data) {
  const userType = data.userType + 's';
  return new Promise((resolve, reject) => {
    const userExists = function (err, result) {
      if (err) {
        reject(err);
        return;
      }
      if (result && result.length > 0) {
        resolve({'status': 'exists'});
        return;
      } else {
        resolve({'status' : 'not exists'});
        return;
      }
  }
  connection.query('SELECT * FROM ' + userType + ' WHERE emailAddress = ?', data.emailAddress, userExists);
});
};


exports.getUser = function (email, userType) {
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
  };
  connection.query("SELECT * FROM "+  userType  +" WHERE emailAddress = ?", email , callbackDB);
})
};

exports.updateUser = data => {
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
}; 


//inserta en la base de datos una necesidad con el id del worker
exports.sendDebt = function (data) {
  data['status'] = 'pending';
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

//Verficar el estado del prestamo
exports.availableNeeds = data => {
  delete data['idNeed'];
  delete data['timeToPayWeekly'];
  delete data['interests'];
  return new Promise ((resolve, reject) => {
    const callbackAvailable = (erro,result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(data);
        return;
      }
    }
    connection.query("SELECT * FROM needs WHERE status IN ('pending')");
  });  
}
