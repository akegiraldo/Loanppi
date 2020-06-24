#!/usr/bin/node

const { checkLoan } = require('../storage/get_information');
const { sendDebt } = require('../storage/send_information');
const { response } = require('express');


//Function that creates a new Need in the DB
const newNeed = (req, res, next) => {
  const allData = req.body;
  const id = allData.idWorker;
  sendDebt(allData).then(response => {
    checkLoan(id).then(response => {
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


module.exports = { newNeed };