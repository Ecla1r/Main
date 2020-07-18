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

};