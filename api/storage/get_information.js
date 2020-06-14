#!/usr/bin/node
const connection = require('./conection_database');

//Function that checks is if the email is in the DB
const findUser = data => {
  const userType = data.userType + 's';
  return new Promise((resolve, reject) => {
    const userExists = (err, result) => {
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

//Function that gets an user and checks if the user exists
const  getUser= (email, userType) => {
  console.log(userType);
  return new Promise((resolve, reject)=> {
  const callbackDB =  (err, result) => {
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

//Funtion that verifies worker's debt status
const availableNeeds = data => {
  return new Promise ((resolve, reject) => {
    const callbackAvailable = (err ,result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
        return;
      }
    }
    connection.query("SELECT * FROM needs WHERE status IN ('pending') LIMIT 3", callbackAvailable);
  });
}

//Function that gets loan by worker's id
const checkLoan = id => {
  return new Promise((resolve, reject) => {
    const callbackCheckStatus = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT * FROM needs WHERE idWorker=?", id, callbackCheckStatus);
  });
}

//Function that gets invesments by Investor's id
const investments = id => {
  return new Promise((resolve, reject) => {
    const callbackInvestments = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT * FROM investment WHERE idInvestor=?", id, callbackInvestments);
  });
}

const returnInvestment = data => {
  const id = data.idInvestment;
  return new Promise((resolve, reject) => {
    const callbackInvestment = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        console.log(result);
        resolve(result);
      }
    }
    connection.query("SELECT * FROM investment WHERE idInvestment = " + id, callbackInvestment);
  });
}

// aldana (cambiar)
const getInvestorConectToNeed = data => {
  return new Promise((resolve, reject) => {
    const callbackGetInvestor = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT idInvestment FROM funding WHERE idNeed = " + data.idNeed, callbackGetInvestor);
  })
}

//Function that gets loanshare by id Investment
const share = id => {
  return new Promise((resolve, reject) => {
    const callbackShare = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result)
      }
    }
    connection.query("SELECT loanShare,idInvestment  FROM investment WHERE idInvestment=?", id, callbackShare);
  })
}


module.exports = { findUser, getUser, availableNeeds, checkLoan, investments, returnInvestment, getInvestorConectToNeed, share };
