/**
 * This module connects rendering modules to routes
 */
const express = require('express');
const router = express.Router()
const app = require('../app')
const { helloWorld, searchUSer, NewUser, update, newNeed, options } = require('./gets')


app.get('/app/api/v1/', helloWorld)
app.get('/app/api/v1/user', searchUSer)
app.post('/app/api/v1/new_user', NewUser)
app.put('/app/api/v1/update', update)
app.post('/app/api/v1/lend', newNeed)
app.get('/app/api/v1/options', options)
