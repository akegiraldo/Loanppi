#!/usr/bin/node
const { findUser, getUser, availableNeeds, checkLoan, investments, returnInvestment, getInvestorConectToNeed, share, insertBenefits } = require('../storage/get_information');
const { createNewUSerDB, sendDebt, updateUser, createInvestment, createFunding, createPayment } = require('../storage/send_information');
const { newBalanceInvestor, updatemoneyNeed, checkStatusNeed } = require('../storage/modificate_information');
const { response } = require('express');


const helloWorld = (req,res,next) => {
  res.send('hola mundo');
  console.log("get");
}

// Function that checks wether the user exists
const searchUSer = (req, res, next) => {
  var data = "";
  const userEmail = req.query.email;
  console.log(userEmail);
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
    console.log(data);
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

//Function that checks whether a worker's loan is active
const activeLoan = (req, res, next) => {
  const workerId = req.query.idWorker;
   checkLoan(workerId).then(response => {
   if (response.length === 0){
      const json = {};
      res.send(json);
   } else {
     res.send(response[0]);
   }
  }).catch(err => {
     console.error(err);
     res.status(500).send("DB Error, NOT FOUND");
  });
}

//Function that gets the investments by Investors' Id
const myInvestments = (req, res, next) => {
  const idInvestor = req.query.idInvestor;
  investments(idInvestor).then(response => {
   console.log(response)
    res.send(response);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not investments found!");
    });
}

const makePago = (req, res, next) => {
  console.log(req.body);
  let allData = req.body;
  let jsonTobenefits = {};
  let aux = {};
  createPayment(allData).then(response => {
    allData['idPayment'] = response;
    return allData;
  }).then( getInvestorConectToNeed ).then(response => {
    for (let i = 0; i < response.length; i++) {
      jsonTobenefits['idPayment'] = allData.idPayment;
      aux = benefits(response[i].idInvestment);
      jsonTobenefits['investorShare'] = allData.payment * aux.loanShare;
      console.log(jsonTobenefits);
    }
  }).catch(err => {
    console.error(err);
    res.status(500).send("Not investments found!");
  });
}

//Function that insersts data
const benefits = data => {
  let data = {};
  share(id).then(response => {
    data = response;
    return data;
  }).catch(err => {
    console.error(err);
    res.status(500).send("Not investments found!");
  });
  return data;
}


module.exports = { helloWorld, searchUSer, NewUser, update, newNeed, options, newInvestment, activeLoan, myInvestments, makePago }
