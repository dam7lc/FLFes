const User = require('../app/models/user');

module.exports = (io) => {
    io.on('connection', function (socket){ //Se ejecuta con un socket.on
		console.log("Client connected to socket");

	    socket.on('attemptLogin', function (data) { //Se ejecuta cuando se inicia sesion
		    console.log("Intento de inicio de sesion con "+ data['phone'] + " " + data['password']);
		    var phone = data['phone'];
		    var password = data['password'];
		    User.findOne({'local.phone': phone}, function(err, user) {
		    	if(err){
			    	return;
			    }
			    if(!user){
			    	socket.emit('loginResponse', {response: 1, error: "Usuario no existe"});
			    	return;
			    }
			    if(!user.validatePassword(password)){
				    socket.emit('loginResponse', {response: 2, error: "Contraseña incorrecta"});
				    return;
				}
				if(!user.local.description || !user.local.nickname || !user.local.email || !user.local.sex || !user.local.career || !user.local.age){
					socket.emit('loginResponse', {response: 3});
					return;
				}
			    socket.emit('loginResponse', {response: 0});

		    });
	    });

	    socket.on('attemptSignup', function (data){ //Se ejecuta cuando se envia informacion de registro
		    console.log("intento de registro con "+ data['phone'] + " " + data['password']);
		    phone = data['phone'];
		    password = data['password'];
		    User.findOne({'local.phone': phone}, function(err, user) {
		    	if (err) {
			    	return;
			    }
		        if (user) {
			    	socket.emit('signupResponse', {response: 1, error: "El telefono ya se encuentra registrado"});
			    	return
			    } else {
				    var newUser = new User();
				    newUser.local.phone = phone;
				    newUser.local.password = newUser.generateHash(password);
				    newUser.save(function(err) {
				    	if (err) {
						    throw err;
					    }
					    socket.emit('signupResponse', {response: 0}); //se responde a un registro exitoso		
				    	return;
				    });
			    }
		    });
    	});

	    socket.on('uploadImg', function (data){ //Se ejecuta cuando se sube una imagen al servidor
			console.log("received:");
			var img = new Buffer(data['img'], 'base64');
			var imgpath = '/IMGUS/'+data['phone']+'-profile.jpg';
			require('fs').writeFile(__dirname+'/../public'+imgpath, img, function(err){
				if(err){
					console.log(err);
					return;
				}
			});
			User.findOne({'local.phone': data['phone']}, function(err, user) {
				if(err){
					console.log(err);
					return;
				}
				user.local.img = '/IMGUS/'+data['phone']+'-profile.jpg';
				user.save(function(err) {
					if (err) {
						console.log(err);
						return;
					}
					socket.emit('uploadImgResponse', {response: 0, img: imgpath});

					
				});

			});
					
		});
		
		socket.on('profileInfo', function(data){ //Se ejecuta cuando se sube informacion del perfil
			User.findOne({'local.phone': data['phone']}, function(err, user){
				if(err){
					console.log(err);
					return;
				} if(user){
					user.local.nickname = data['nick'];
					user.local.email = data['email'];
					user.local.sex = data['sex'];
					user.local.career = data['career'];
					user.local.age = data['age'];
					user.local.description = data['desc'];
					user.save(function (err){
						if(err){
							console.log(err);
							return;
						}
					});
				}
			});
			socket.emit('profileInfoResponse', {response: 0});
		});

		socket.on('getProfileInfo', function(data){
			console.log(data);
			User.findOne({'local.phone': data}, function(err, user){
				if(err){
					console.log(err);
					return;
				} if(user){
					var imagen;
					if(!user.local.img){
						imagen = "/IMG/AvatarHombre.jpg"
					} else{
						imagen = user.local.img;
					}
					socket.emit('getProfileInfoResponse', {
						response: 0,
						img: imagen,
						nick: user.local.nickname,
						email: user.local.email,
						sex: user.local.sex,
						career: user.local.career,
						age: user.local.age,
						descr: user.local.description
					});
				}
			});
		});

		socket.on('QuestionsInfo', function(data){
			User.findOne({'local.phone': data['phone']}, function(err, user){
				if(err){
					console.log(err);
					return;
				}
				if(user){
					user.intereses.sex = data['sexInteres'];
					user.save(function (err){
						if(err){
							console.log(err);
							return;
						}
					});
				}
				socket.emit('QuestionsInfoResponse', {response: 0})
			});
		});

		socket.on('populateFeed', function(data){
			
			User.find({'local.sex': data['sexInteres']}, (function(err, user){
				if(err){
					console.log(err);
					return;
				}

				user.forEach(function(user){
					if(user){
						/*
						var imagen = "/IMG/AvatarHombre.jpg";;
						if(user.local.img){
							imagen = user.local.img;
						}*/
						console.log(user);
						socket.emit('populateFeedResponse', {
							response: 0,
							img: user.local.img,
							nick: user.local.nickname,
							sex: user.local.sex,
							career: user.local.career,
							age: user.local.age,
							descr: user.local.description
						});
					}
				});
			}));
		});

	    socket.on('disconnect', function(data){
	    	console.log('User disconnected :( ');
    	});
    });
}