#!/usr/bin/node

const { getUser, findUser } = require('../storage/get_information');
const { createNewUSerDB, updateUser } = require('../storage/send_information');
const { response } = require('express');


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


module.exports = { searchUSer, NewUser, update };