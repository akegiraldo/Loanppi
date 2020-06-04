var bodyParser = require('body-parser');
var express = require('express');
const saveData = require('./storage').saveData;
const getUser = require('./storage').getUser;

var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.get('/app/api/v1/', (req,res,next) => {
        res.send('hola mundo');
        console.log("get");
});

app.get('/app/api/v1/user', (req, res, next) => {
  var data = "";
  const user_email = req.query.email;
  console.log(user_email);
  getUser(user_email, "investors").then (response => {
    data = response;
    if (data.status === "exists") {
      res.send(JSON.stringify(data));
    } else {
      getUser(user_email, "workers").then (response => {
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

app.post('/app/api/v1/new_user', (req, res, next) => {
  const all_data = req.body;
  console.log(all_data);
  saveData(all_data).then(response => {
    const data = JSON.stringify({"exists": response});
    res.send(data);
    console.log(data);
  }).catch(err => {
    console.error(err);
    res.status(500).send("DB Error, NOT FOUND!");
  });
});



app.listen(3000,()=>{
        console.log('Conecting ...');
});
