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

exports.saveData = function (data) {
  const userType = data.userType + 's';
  delete data['userType'];
  return new Promise((resolve, reject)=> {
    const callbackDB =  function (err, result) {
      if (err) {
        reject(err);
        return;
      }

      if (result && result.length > 0) {
        resolve("status");
      } else {
        const sql = 'INSERT INTO ' + userType + ' SET ?';
        connection.query(sql, data, function (err, result2) {
          if (err) {
            reject(err);
            return;
          }

          console.log("insertado");
          resolve("ok")
        });
      }
    };
    connection.query("SELECT * FROM " + userType  + " WHERE emailAddress = ?", data.emailAddress , callbackDB);
  })
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
      delete result[0]['id_investor'];
      result[0]['status'] = 'exists';
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
