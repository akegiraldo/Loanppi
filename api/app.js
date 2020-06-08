var bodyParser = require('body-parser');
var express = require('express');
const saveData = require('./storage').saveData;
const getUser = require('./storage').getUser;
const findUser = require('./storage').findUser;
const sendDebt = require('./storage').sendDebt;

var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.get('/app/api/v1/', (req,res,next) => {
        res.send('hola mundo');
        console.log("get");
});

app.get('/app/api/v1/user', (req, res, next) => {
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
});

// busca si el usuario existe y si no existe lo guartda si existe no lo guarda
app.post('/app/api/v1/new_user', (req, res, next) => {
  const allData = req.body;
  findUser(allData).then(response => {
    if (response.status === 'exists') {
      res.send(response);
    } else {
      saveData(allData).then(responseSave => {
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
});

app.put('/app/api/v1/update',(req, res, next) => {
  const allData = req.body;
  updateUser(allData).then(response => {
    const data = JSON.stringify({"updated": response});
    res.send(data);
    console.log(data);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
});

// cuando se crea una nueva necesidad 
app.post('/app/api/v1/lend', (req, res, next) => {
  const allData = req.body;
  sendDebt(allData).then(response => {
    res.send(response);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
});


app.listen(3000,()=>{
        console.log('Conecting ...');
});
