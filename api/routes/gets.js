#!/usr/bin/node
const { findUser, getUser, availableNeeds } = require('../storage/get_information');
const { createNewUSerDB, sendDebt, updateUser, createInvestment, createFunding } = require('../storage/send_information');
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
  sendDebt(allData).then(response => {
    res.send(response);
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
      console.err(err);
      res.status(500).send("Not options found");
    });
}

//Function that creates a new invesment in the DB
const newInvestment = (req, res, next) => {
  const allData = req.body;
  console.log(allData);
  const backup = { ...req.body };
  createInvestment(allData).then(response => {
    backup['idInvestment'] = response;
    return backup;
  }).then( createFunding ).then(response => {
    res.send(response);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
  
}

module.exports = { helloWorld, searchUSer, NewUser, update, newNeed, options, newInvestment}
