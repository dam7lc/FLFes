const LocalStrategy = require('passport-local').Strategy;

const User = require('../app/models/user');

module.exports = function (passport) {
	passport.serializeUser(function (user, done) {
		done(null, user.id);
	});

	passport.deserializeUser(function (id, done) {
		User.findById(id, function (err, user) {
			done(err, user);
		});
	});

	//signup
	passport.use('local-signup', new LocalStrategy({
		usernameField: 'email',
		passwordField: 'password',
		passReqToCallback: true
	},
	function (req, email, password, done) {
		User.findOne({'info.email': email}, function(err, user) {
			if (err) {
				return done(err);
			}
			if (user) {
				return done(null, false, req.flash('signupMessage', 'El email que quieres registrar ya se encuentra en uso.'));
			} else {
				var newUser = new User();
				newUser.info.email = email;
				newUser.info.password = newUser.generateHash(password);
				newUser.save(function(err) {
					if (err) {
						throw err;
					}
					return done(null, newUser);
				});
			}
		})
	}));

	//login
	passport.use('local-login', new LocalStrategy({
		usernameField: 'email',
		passwordField: 'password',
		passReqToCallback: true
	},
	function (req, email, password, done) {
		User.findOne({'info.email': email}, function(err, user) {
			if (err) {
				return done(err);
			}
			if (!user) {
				return done(null, false, req.flash('loginMessage', 'Usuario no encontrado.'));
			} 

			if (!user.validatePassword(password)) {
				return done(null, false, req.flash('loginMessage', 'Contraseña invalida'));
			}

			return done(null, user);
		});
	}));
}