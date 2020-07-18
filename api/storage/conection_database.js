#!/usr/bin/node
const mysql = require('mysql');

const dbhost = process.env.DBHOST;
const password = process.env.DBPASS;

const connection = mysql.createConnection({
  host: dbhost,
  user: 'trash',
  password: password,
  database: 'loanppi'
});


connection.connect((err) => {
  if(err){
    console.log('Error connecting to Db');
    return;
  }
  console.log('Connection established');
});

module.exports = connection;
