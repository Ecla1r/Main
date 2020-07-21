bodyParser = require('body-parser').json();
var fs = require('fs');

module.exports = function (app) {

    app.get('/users', (request, respond) => {
        var result = [{"name" : "Коробов Данил Андреевич",
                        "age" : "18",
                        "goal" : "Ворваться в айти индустрию",
                        "phone" : "89172999333",
                        "exp" : "No",
                        "prog_langs" : "Java",
                        "email" : "ecla111r@gmail.com"}
                        ];
        respond.send(JSON.stringify(result));
    });

    app.post('/register', bodyParser, (request, response) => {
        let body = request.body;
        console.log(body);
        fs.readFile('forms.json', 'utf-8', function(err, data) {
            if (err) throw err;

            var arrayOfObjects = JSON.parse(data);
            arrayOfObjects.forms.push(body);


            fs.writeFile('forms.json', JSON.stringify(arrayOfObjects), 'utf-8', function(err) {
                if (err) throw err;
                console.log('Done!')
            })
        });
        response.redirect('/search.html');
    });

    app.get('/forms', (request, response) => {
        fs.readFile('forms.json', 'utf8', function(err, contents) {
            response.setHeader('Content-Type', 'application/json');
            response.send(contents);
        });
    });

};