/**
 * This module connects rendering modules to routes
 */
const express = require('express');
const router = express.Router()
const { helloWorld, searchUSer, NewUser, update, newNeed, options, newInvestment, activeLoan, loanStatus, myInvestments, makePago } = require('./gets')

// Api's Routes 
router.get('/app/api/v1/', helloWorld);
router.get('/app/api/v1/user', searchUSer);
router.post('/app/api/v1/new_user', NewUser);
router.post('/app/api/v1/lend', newNeed);
router.get('/app/api/v1/invest_options', options);
router.post('/app/api/v1/new_investment', newInvestment);
router.get('/app/api/v1/active_loan', activeLoan);
router.get('/app/api/v1/my_investments', myInvestments);
router.put('/app/api/v1/update', update);
router.post('/app/api/v1/pagar', makePago);

module.exports = router
