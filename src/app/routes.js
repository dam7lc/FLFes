module.exports = (app, passport) => {
	app.get('/', (req, res) => {
		res.render('index');
	});

	app.get('/login', (req, res) => {
		res.render('login', {
			message: req.flash('loginMessage')
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

	const User = require('./models/user');
	app.post('/uploadpi', (req, res) => {
		var nickname = req.body.nickname;
		var email = req.body.email;
		var sex = req.body.sex;
		var age = req.body.age;
		var career = req.body.career;

		User.findOne({'local.nickname': nickname}, function(err, user) {
		    if (err) {
			   	return err;
			}
		    if (user) {
		    	User.updateOne(
				  {
				  	'local.nickname' : nickname
				  },
				  {
				  	$set: {
				  		'local.email' : email,
				  		'local.sex' : sex,
				  		'local.age' : age,
				  		'local.career' : career
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
	    var nickname = req.user.local.nickname;
	    var path = `./src/public/IMGUS/${nickname}.jpg`;
	    var imgbdPath = '/IMGUS/' + nickname + '.jpg';
	    imgFile.mv(path, err => {
	        if(err) {
	        	return res.status(500).send({ message : err })
	        } else {
			    User.updateOne(
			   		{
					  	'local.nickname' : nickname
					},
					{
					 	$set: {
					  		'local.img' : imgbdPath
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