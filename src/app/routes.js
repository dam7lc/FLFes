const EmailCtrl = require('./mailCtrl');
const User = require('./models/user');

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
		successRedirect: '/profile',
		failureRedirect: '/login',
		failureFlash: true
	}));

	app.get('/signup', (req, res) => {
		res.render('signup', {
			message: req.flash('signupMessage')
		});
	});

	app.post('/signup', passport.authenticate('local-signup', {
		successRedirect: '/profile',
		failureRedirect: '/signup',
		failureFlash: true
	}));

	app.get('/getpassword', (req, res) => {
		res.render('getpassword', {
            message: ""
        });
	});

	app.post('/getpassword', EmailCtrl.sendEmail);

	app.get('/changepassword', (req, res) => {
		var code = req.query.code || '';
		var email = req.query.email || '';

		if (code != '' && email != '') {
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

	app.get('/profile', isLoggedIn, (req, res) => {
		res.render('profile', {
			user: req.user
		});
	});

	app.get('/logout', (req, res) => {
		req.logout();
		res.redirect('/');
	})

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
					res.render('settings', {
						user: req.user
					});
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
					res.render('settings', {
						user: req.user
					});
				}).catch((err) => {
					console.log(err);
				});
			}
	    });
	});

	function isLoggedIn (req, res, next) {
		if (req.isAuthenticated()) {
			return next();
		}
		return res.redirect('/');
	}
};