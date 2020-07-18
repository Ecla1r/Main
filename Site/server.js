const express = require('express');
const bodyParser = require('body-parser');
const app = express();

app.use(bodyParser.urlencoded({extended: true}));
require('./app/routes')(app);
app.use(express.static('public'));
app.listen(80);
console.log("Server started at 80");