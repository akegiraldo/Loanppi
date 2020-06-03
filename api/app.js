var bodyParser = require('body-parser');
var express = require('express');
const saveData = require('./storage').saveData;


var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.get('/app/api/v1/', (req,res,next) => {
        res.send('hola mundo');
        console.log("get");
});

app.post('/app/api/v1/new_user', (req, res, next) => {
  const all_data = req.body;
  console.log(all_data);
  saveData(all_data).then(response => {
    const data = JSON.stringify({"STATUS": response});
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

