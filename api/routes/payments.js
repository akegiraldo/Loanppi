#!/usr/bin/node

const { getIdinvestment, share, checkLoan, getPayments } = require('../storage/get_information');
const { createPayment, sendBenefit, saveMoney } = require('../storage/send_information');
const { pay, needResolved, changeStatusInvestment, updateInvestment, changeStack } = require('../storage/modificate_information');


//Function that creates a payment
const newPayment = (req, res, next) => {
  const allData = req.body;
  let jsonTobenefits = {};
  jsonTobenefits['idNeed'] = allData.idNeed;
  Promise.all([getIdinvestment(allData), createPayment(allData), pay(allData), needResolved(jsonTobenefits)]).then((values) => {
    jsonTobenefits['idPayment'] = values[1];
    saveBenefit(jsonTobenefits, values[0], allData.payment);
    res.send({'status':'paid'});
  })
}

//Function that sends data to benefits DB
const saveBenefit = (newBenefit, values, money) => {
  const idNeed = newBenefit['idNeed'];
  for (let i = 0; i < values.length; i++) {
    share(values[i].idInvestment).then(response => {
      newBenefit['idInvestment'] = values[i].idInvestment;
      newBenefit['investorShare'] = response[0].loanShare * money;
      sendBenefit(newBenefit);
      changeStatusInvestment(newBenefit['idInvestment'], idNeed);
      updateInvestment(newBenefit);
      saveMoney(response[0].idInvestor, newBenefit.investorShare);
      changeStack(response[0].idInvestor, newBenefit.investorShare);
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



module.exports = { newPayment, payments };