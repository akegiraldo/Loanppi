#!/usr/bin/node

const connection = require('./conection_database');
const { newBalanceInvestor, updatemoneyNeed } = require('./modificate_information');


//Function that  creates a new user in DB
const createNewUSerDB = data => {
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


//Function that creates new debt with id of user in DB
const sendDebt = data => {
  data['status'] = 'pending';
  data['amountRemaining'] = data['loanAmount']
  return new Promise((resolve, reject) => {
    const callbackInsertDebt = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result);
        return;
      }
    }
    connection.query('INSERT INTO needs SET ?', data , callbackInsertDebt);
  });
}


//Function that creates a new Investment with needs' id in DB
const createInvestment = data => {
  data['status'] = 'active';
  delete data['idNeed'];
  return new Promise((resolve, reject) => {
    const callbackInsertInvestment =  (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        
        result['idInvestment'] = result.insertId;
        resolve(result);
        return;
      }
    }
    connection.query('INSERT INTO investment SET ?', data , callbackInsertInvestment);
  });
}

//Function that creates relation between investors and workers in the DB
const createFunding = data => {
  const backup = { ...data };
  const values = {'idNeed': data.idNeed, 'idInvestment': data.idInvestment}
  return new Promise((resolve, reject) => {
    const callbackCreateFunding =  (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        updatemoneyNeed(backup);
        newBalanceInvestor(backup);
        resolve({'status': 'created'});
        return;
      }
    }
    connection.query('INSERT INTO funding SET ?', values, callbackCreateFunding);
  });
}


//Function that updates user's profile in DB
const updateUser = data => {
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
  connection.query("UPDATE " + usertype + " SET ? WHERE id = ?", data, id, callbackDB);
}

//Function that creates a new payment
const createPayment = data => {
  data['datePayment'] = new Date()
  return new Promise((resolve, reject) => {
    const callbackCreatePayment = (err, result) => {
      if (err) {
        reject(err);
        return;
      } else {
        resolve(result.insertId);
      }
    }
    connection.query("INSERT INTO payments SET ?", data, callbackCreatePayment);
  })
}


//Function that inserts data to the benefit Table
const sendBenefit = data => {
  delete data['idNeed'];
  data['dateReturn'] = new Date();
  connection.query("INSERT INTO benefits SET ?", data, (err, rows) => {
    if(err) throw err;
    console.log("Data inserted");
  })
}

//Function that that inserst data in the money table
const saveMoney = (id, amount) => {
  data = {'idInvestor': id, 'money': amount};
  console.log("dataaa", data);
  connection.query("INSERT INTO money_investor SET ?", data, (err, rows) => {
    if(err) throw err;
    console.log("Data inserted");
  })

}

module.exports = { saveMoney, createNewUSerDB, sendDebt, updateUser, createInvestment, createFunding, createPayment, sendBenefit};
