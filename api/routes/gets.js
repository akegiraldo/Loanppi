#!/usr/bin/node
const { findUser, getUser, availableNeeds } = require('../storage/get_information');
const { createNewUSerDB, sendDebt, updateUser } = require('../storage/send_information');


function helloWorld(req,res,next) {
  res.send('hola mundo');
  console.log("get");
}

// busca si el usuario existe si existe lo devuelve
function searchUSer(req, res, next) {
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


// busca si el usuario existe y si no existe lo guartda si existe no lo guarda
function NewUser(req, res, next) {
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

// actualiza datos
function update(req, res, next) {
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

// cuando se crea una nueva necesidad
function newNeed(req, res, next) {
  const allData = req.body;
  sendDebt(allData).then(response => {
    res.send(response);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}


function options(req, res, next) {
  const allData = req.body;
  availableNeeds(allData).then(response => {
    res.send(response);
  }).catch(err => {
      console.err(err);
      res.status(500).send("Not options found");
    });
}

module.exports = { helloWorld, searchUSer, NewUser, update, newNeed, options }
