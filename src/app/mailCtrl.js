const User = require('./models/user');
const nodemailer = require('nodemailer');

function cod_rand(chars, lon){
    code = "";
    for (x = 0; x < lon; x++) {
        rand = Math.floor(Math.random() * chars.length);
        code += chars.substr(rand, 1);
    }
    return code;
}

chars = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789!#$%?¡¿";
long = 20;

//Email send function
exports.sendEmail = function(req, res){
    var codigo = cod_rand(chars, long);
    var email = req.body.email;

    User.findOne({'info.email': email}, function(err, user) {
        if (err) {
            return err;
        }
        if (user) {
            User.updateOne(
                {
                    'info.email' : email
                },
                {
                    $set: {
                        'info.tokenPass' : code,
                    }
                }
            ).then((rawResponse) => {
            //Definimos el transporter
                var transporter = nodemailer.createTransport({
                    service: 'Gmail',
                    auth: {
                        user: 'enriquecarranza38@gmail.com',
                        pass: 'kaky211199'
                    }
                });

            // Definimos el email
                var mailOptions = {
                    from: 'Fes Lancer',
                    to: email,
                    subject: 'Solicitud de cambio de contraseña',
                    html: '<h2>Se ha solicitado un cambio de contraseña de tu perfil en Fes Lancer</h2> <hr> ' 
                        + '<p>Tu código de verificación es: <strong>' + codigo + '</strong></p> <br> '
                        + '<p> Vaya a la página '
                        + '<a href="http://localhost:3000/changepassword?code=' + codigo + '&email=' + email + '">'
                        + 'http://localhost:3000/changepassword'
                        + '</a> e introduzca el código anterior.' 
                        + '</p> <br><br> <h4>Si no fuiste tu, porfavor ignora este mensaje.<strong>Recuerda que el codigo solo será valido una vez.</strong></h4>'
                };

            // Enviamos el email
                transporter.sendMail(mailOptions, function(error, info){
                    if (error){
                        console.log(error);
                        res.send(500, error.message);
                    } else {
                        res.render('getpassword', {
                            message: "¡El código de confirmación de cambio de contraseña ha sido enviado a tu correo satisfactoriamente.!"
                        });
                    }
                });

            }).catch((err) => {
                console.log(err);
            });
        }
    });
};