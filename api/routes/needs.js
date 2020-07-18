#!/usr/bin/node

const { checkLoan } = require('../storage/get_information');
const { sendDebt } = require('../storage/send_information');
const { response } = require('express');


//Function that creates a new Need in the DB
const newNeed = (req, res, next) => {
  const allData = req.body;
  const id = allData.idWorker; 
  checkLoan(id).then(response => {
    if (response.length === 0)
    {
      sendDebt(allData).then(response => {
        res.send({"status" : "pending"});
      }).catch(err => {
        console.error(err);
        res.status(500).send("DB Error, NOT FOUND!");
    });
    } else {
      res.send({"status" : "already_exists"})
    }
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
}

module.exports = { newNeed };
