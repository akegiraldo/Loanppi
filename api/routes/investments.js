#!/usr/bin/node


const { returnInvestment, investments, getInvestments, getReturns } = require('../storage/get_information');
const { createInvestment, createFunding } = require('../storage/send_information');
const { checkStatusNeed } = require('../storage/modificate_information');


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



module.exports = { newInvestment, myInvestments, listInvestments };