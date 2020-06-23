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
    connection.query("SELECT * FROM needs WHERE idWorker = " + id + " AND status != 'paid'", callbackCheckStatus);
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


//Function that returns investments details by id
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

//Function that gest Id investment
const getIdinvestment = (data) => {
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
    connection.query("SELECT loanShare, idInvestor FROM investment WHERE idInvestment=?", id, callbackShare);
  })
}

//Function that gets payments by worker's id
const getPayments = id => {
  return new Promise((resolve, reject) => {
    const callbackGetPayments = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        console.log(result);
        resolve(result);
      }
    }
    connection.query("SELECT * FROM payments JOIN needs ON payments.idNeed = needs.idNeed WHERE needs.idWorker = " + id + " AND needs.status = 'resolved'", callbackGetPayments);
  });
}

//Function that gets investments by its Id
const getInvestments = id => {
  return new Promise((resolve, reject) => {
    const callbackGetInvestments = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT * FROM investment WHERE idInvestment=?", id, callbackGetInvestments);
  });
}


//Function that gets returns by investment's id
const getReturns = id => {
  return new Promise((resolve, reject) => {
    const callbackGetReturns = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT * FROM benefits WHERE idInvestment=?", id, callbackGetReturns);
  });
}

//Function that gets the invest STack byInvestors id
const getAmountStack = id => {
  return new Promise((resolve, reject) => {
    const callbackGetStack = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
      }
    }
    connection.query("SELECT investStack FROM investors WHERE idInvestor=?", id, callbackGetStack);
  });
}


module.exports = { findUser, getUser, availableNeeds, checkLoan, investments, returnInvestment, getIdinvestment, share, getPayments, getInvestments, getReturns, getAmountStack };
