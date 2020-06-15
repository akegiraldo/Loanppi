/**
 * This module connects rendering modules to routes
 */
const express = require('express');
const router = express.Router()
const { helloWorld, searchUSer, NewUser, update, newNeed, options, newInvestment, loanStatus, myInvestments, payments, newPayment, listInvestments } = require('./gets')

// Api's Routes 
router.get('/app/api/v1/', helloWorld);
router.get('/app/api/v1/user', searchUSer);
router.post('/app/api/v1/new_user', NewUser);
router.post('/app/api/v1/lend', newNeed);
router.get('/app/api/v1/invest_options', options);
router.post('/app/api/v1/new_investment', newInvestment);
router.get('/app/api/v1/my_investments', myInvestments);
router.put('/app/api/v1/update', update);
router.post('/app/api/v1/payment', newPayment);
router.get('/app/api/v1/my_loan', payments);
router.get('/app/api/v1/my_investment', listInvestments);

module.exports = router
