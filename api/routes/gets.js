#!/usr/bin/node
const { findUser, getUser, availableNeeds, checkLoan, investments, returnInvestment, getIdinvestment, share, insertBenefits, getPayments, getInvestments, getReturns } = require('../storage/get_information');
const { createNewUSerDB, sendDebt, updateUser, createInvestment, createFunding, createPayment, sendBenefit } = require('../storage/send_information');
const { newBalanceInvestor, updatemoneyNeed, checkStatusNeed, pay, needResolved, changeStatusInvestment, updateInvestment } = require('../storage/modificate_information');
const { response } = require('express');


const helloWorld = (req,res,next) => {
  res.send('hola mundo');
  console.log("get");
}

// Function that checks wether the user exists
const searchUSer = (req, res, next) => {
  var data = "";
  const userEmail = req.query.email;
  getUser(userEmail, "investors").then (response => {
    data = response;
    if (data.status === "exists") {
      res.send(JSON.stringify(data));
    } else {
      getUser(userEmail, "workers").then (response => {
        data = JSON.stringify(response);
        res.send(data);
      }).catch(err => {
        console.error(err);
        res.status(500).send("DB Error, NOT FOUND!");
      });
    }
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}


//Function that checks the existence of the user and creates it
const NewUser = (req, res, next) => {
  const allData = req.body;
  findUser(allData).then(response => {
    if (response.status === 'exists') {
      res.send(response);
    } else {
      createNewUSerDB(allData).then(responseSave => {
        res.send(responseSave);
      }).catch(err => {
        console.error(err);
        res.status(500).send("DB Error, NOT FOUND!");
      })
    }
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}

// Function that updates user
const update = (req, res, next) => {
  const allData = req.body;
  updateUser(allData).then(response => {
    const data = JSON.stringify({"updated": response});
    res.send(data);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}

//Function that creates a new Need in the DB
const newNeed = (req, res, next) => {
  const allData = req.body;
  const id = allData.idWorker;
  sendDebt(allData).then(response => {
     const loan = checkLoan(id).then(response => {
      res.send(response[0]);
}).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}

//Function that gets invesments' options
const options = (req, res, next) => {
  const allData = req.body;
  availableNeeds(allData).then(response => {
    res.send(response);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not options found");
    });
}

//Function that creates a new invesment in the DB
const newInvestment = (req, res, next) => {
  let allData = req.body;
  const backup = { ...req.body };
  createInvestment(allData).then(response => {
    backup['idInvestment'] = response.idInvestment;
    allData = backup;
    return backup;
  }).then( createFunding ).then(response => {
    checkStatusNeed(backup);
    return allData;
  }).then( returnInvestment ).then(response => {
    allData = response[0];
    allData['status'] = 'created';
    res.send(allData);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });  
}


//Function that gets the investments by Investors' Id
const myInvestments = (req, res, next) => {
  const idInvestor = req.query.idInvestor;
  investments(idInvestor).then(response => {
    res.send(response);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not investments found!");
    });
}


//Function that creates a payment
const newPayment = (req, res, next) => {
  const allData = req.body;
  let jsonTobenefits = {};
  jsonTobenefits['idNeed'] = allData.idNeed;
  Promise.all([getIdinvestment(allData), createPayment(allData)]).then((values) => {
    jsonTobenefits['idPayment'] = values[1];
    saveBenefit(jsonTobenefits, values[0], allData.payment);
    pay(allData);
    needResolved(jsonTobenefits);

    res.send({'status':'paid'});
  })
}

//Function that sends data to benefits DB
const saveBenefit = (Json, values, money) => {
  for (let i = 0; i < values.length; i++) {
    share(values[i].idInvestment).then(response => {
      Json['idInvestment'] = values[i].idInvestment;
      Json['investorShare'] = response[0].loanShare * money;
      sendBenefit(Json);
      changeStatusInvestment(Json);
      updateInvestment(Json);
    }).catch(err => {
      console.error(err);
      res.status(500).send("Not investments found!");
    });
  }
}

//Function that charges needs and number of payments
const payments = (req, res, next) => {
  const id = req.query.idWorker;
  let payments = [];
  Promise.all([checkLoan(id), getPayments(id)]).then(values => {
    if (values[0].length > 0) { 
    payments.push(values[0][0]);
     payments.push(values[1]);
   }
   res.send(payments);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not payments found!");
    }); 
}

//Function that gets investment details by its id
const listInvestments = (req, res, next) => {
  const id = req.query.idInvestment;
  let investments = [];
  Promise.all([getInvestments(id), getReturns(id)]).then(values => {
  if (values[0].length > 0) {
    investments.push(values[0][0]);
    investments.push(values[1]);
  }
   res.send(investments);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not investments found!");
    });
}

module.exports = { helloWorld, searchUSer, NewUser, update, newNeed, options, newInvestment, myInvestments, newPayment, payments, listInvestments  }
