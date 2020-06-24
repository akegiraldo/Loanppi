#!/usr/bin/node

const { availableNeeds, getAmountStack } = require('../storage/get_information');


//Function that gets invesments' options
const options = (req, res, next) => {
  list = [];
  const id = req.query.idInvestor;
  Promise.all([availableNeeds(), getAmountStack(id)]).then(response => {
    list.push(response[0])
    list.push(response[1][0])
    res.send(list);
  }).catch(err => {
      console.error(err);
      res.status(500).send("Not options found");
    });
}

module.exports = { options };