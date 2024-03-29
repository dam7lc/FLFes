const EmailCtrl = require('./mailCtrl');
const User = require('./models/user');
const Offer = require('./models/offer');

module.exports = (app, passport) => {
	app.get('/', (req, res) => {
		res.render('index');
	});

	app.get('/login', (req, res) => {
		res.render('login', {
			message: req.flash('loginMessage'),
			succes: ''
		});
	});

	app.post('/login', passport.authenticate('local-login', {
		successRedirect: '/dashboard',
		failureRedirect: '/login',
		failureFlash: true
	}));

	app.get('/signup', (req, res) => {
		res.render('signup', {
			message: req.flash('signupMessage')
		});
	});

	app.post('/signup', passport.authenticate ('local-signup', {
		successRedirect: '/dataRegister',
    	failureRedirect: '/signup',
    	failureFlash: true
   	}));

   	app.get('/dataRegister', (req, res) => {
		res.render('dataRegister', {
            message: "",
            user: req.user
        });
	});

	app.post('/dataRegister', (req, res) => {
		var img = req.files.img;
	    var id = req.user._id;
	    var path = `./src/public/IMGUS/${id}.jpg`;
	    var imgbdPath = '/IMGUS/' + id + '.jpg';
		var email = req.body.email;
	    var nombre = req.body.nombre;
	    var ncuenta = req.body.ncuenta;
	    var sexo = req.body.sexo;
	    var tel = req.body.tel;
	    var carrera = req.body.carrera;
	    var semestre = req.body.semestre;
	    var grupo = req.body.grupo;
	    var turno = req.body.turno;

	    img.mv(path, err => {
	        if(err) {
	        	return res.status(500).send({ message : err })
	        } else {
			    User.updateOne(
			   		{
					  	'_id' : id,
					  	'info.email' : email
					},
					{
					 	$set: {
					  		'info.img' : imgbdPath,
					  		'info.nombre' : nombre,
					  		'info.ncuenta' : ncuenta,
					  		'info.sexo' : sexo,
					  		'info.tel' : tel,
					  		'info.carrera' : carrera,
					  		'info.semestre' : semestre,
					  		'info.grupo' : grupo,
					  		'info.turno' : turno
					  	},
					  	$push: {
					  		'info.habilidades' : {
					  			$each: 
					  				JSON.parse(req.body.habilid)
					  		}
					  	}
					}
				).then((rawResponse) => {
					res.redirect('/dashboard');
				}).catch((err) => {
					console.log(err);
				});
			}
	    });
	});

	app.get('/getpassword', (req, res) => {
		res.render('getpassword', {
            message: ""
        });
	});

	app.post('/getpassword', EmailCtrl.sendEmail);

	app.get('/changepassword', (req, res) => {
		var code = req.query.code || '';
		var email = req.query.email || '';

		if (code != '') {
			res.render('changepassword', {
            	email: email,
            	codigo: code
        	});
		} else {
			res.render('getpassword', {
            	message: ""
        	});
		}
	});

	app.post('/changepassword', (req, res) => {
		var code = req.body.codigo;
		var email = req.body.email;
		var password = req.body.password;

		User.findOne({'info.email': email, 'info.tokenPass': code}, function(err, user) {
		    if (err) {
			   	return err;
			}
		    if (user) {
		    	var upUser = new User();
		    	var passhash = upUser.generateHash(password);
		    	User.updateOne(
				  {
				  	'info.email' : email,
				  	'info.tokenPass' : code
				  },
				  {
				  	$set: {
				  		'info.password' : passhash,
				  		'info.tokenPass' : ''
				  	}
				  }
				).then((rawResponse) => {
					res.render('login', {
						succes: 'La contraseña se ha cambiado satisfactoriamente, ahora puede iniciar sesión con su nueva contraseña.',
						message: ''
					});
				})
				.catch((err) => {
				  console.log(err);
				});
			}
		});
	});

	app.get('/dashboard', isLoggedIn, async (req, res) => {
		const offers = await Offer.find(
			{ 
				$and: [ 
					{
						'email': {
							$ne: req.user.info.email
						} 
					}, 
					{
						'habilidades': {
							$in: req.user.info.habilidades
						} 
					}
				]
			}
		).sort(
			{
				'fechaPublicacion': 1, //Ordena de la fecha más próxima a la más vieja
				'horaPublicacion': -1 //Ortdena de la hora más próxima a la más vieja
			}
		);

		var contenido = "";

		if (offers.length == 0) {
			contenido += "No Nay ofertas en éste momento";
		}

		res.render('dashboard', {
			message: contenido,
			offers,
			user: req.user
		});
	});

	app.get('/publicProyect', isLoggedIn, (req, res) => {
		res.render('publicProyect', {
			user: req.user
		});
	});

	app.post('/publicProyect', async (req, res) => {
		var email = req.body.email;
		var nombre = req.body.nombre;
		var titulo = req.body.titulo;
	    var materia = req.body.materia;
	    var descripcion = req.body.descripcion;
	    var plazo = req.body.plazo;
	    var urgencia = req.body.urgencia;
	    var fecha = req.body.fecha;
	    var hora = req.body.hora;

	    const offers = await Offer.find({'email': {$ne : req.user.info.email}}).sort({'fechaPublicacion': 1, 'horaPublicacion': -1});

	    var newOffer = new Offer();
			newOffer.email = email;
			newOffer.nombre = nombre;
			newOffer.grupo = req.user.info.grupo;
			newOffer.semestre = req.user.info.semestre;
			newOffer.titulo = titulo;
			newOffer.materia = materia;
			newOffer.descripcion = descripcion;
			newOffer.plazo = plazo;
			newOffer.urgencia = urgencia;
			newOffer.fechaPublicacion = fecha;
			newOffer.horaPublicacion = hora;
			newOffer.habilidades = JSON.parse(req.body.habilid);

			newOffer.save(function(err) {
				if (err) {
					throw err;
				}

				res.redirect('/dashboard');
			});
	});

	app.get('/profile', isLoggedIn, (req, res) => {
		res.render('profile', {
			user: req.user
		});
	});

	app.get('/settings', isLoggedIn, (req, res) => {
		res.render('settings', {
			user: req.user
		});
	});

	app.post('/uploadpi', (req, res) => {
		var id = req.body.id;
		var name = req.body.name;
		var sex = req.body.sex;
		var career = req.body.career;

		User.findOne({'_id': id}, function(err, user) {
		    if (err) {
			   	return err;
			}
		    if (user) {
		    	User.updateOne(
				  {
				  	'_id' : id
				  },
				  {
				  	$set: {
				  		'info.nombre' : name,
				  		'info.sexo' : sex,
				  		'info.carrera' : career
				  	}
				  }
				).then((rawResponse) => {
					res.redirect('/settings');
				})
				.catch((err) => {
				  console.log(err);
				});
			}
		});
	});

	app.post('/uploadimgprofile', (req, res) => {
	    var imgFile = req.files.file;
	    var id = req.user._id;
	    var path = `./src/public/IMGUS/${id}.jpg`;
	    var imgbdPath = '/IMGUS/' + id + '.jpg';
	    imgFile.mv(path, err => {
	        if(err) {
	        	return res.status(500).send({ message : err })
	        } else {
			    User.updateOne(
			   		{
					  	'_id' : id
					},
					{
					 	$set: {
					  		'info.img' : imgbdPath
					  	}
					}
				).then((rawResponse) => {
					res.redirect('/settings');
				}).catch((err) => {
					console.log(err);
				});
			}
	    });
	});

	app.get('/logout', (req, res) => {
		req.logout();
		res.redirect('/');
	})

	function isLoggedIn (req, res, next) {
		if (req.isAuthenticated()) {
			return next();
		}
		return res.redirect('/login');
	}
};