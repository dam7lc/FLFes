const User = require('../app/models/user');

module.exports = (io) => {
    io.on('connection', function (socket){ //Se ejecuta con un socket.on
		console.log("Client connected to socket");

	    socket.on('attemptLogin', function (data) { //Se ejecuta cuando se inicia sesion
		    console.log("Intento de inicio de sesion con "+ data['email'] + " " + data['password']);
		    var email = data['email'];
		    var password = data['password'];
		    User.findOne({'info.email': email}, function(err, user) {
		    	if(err){
			    	return;
			    }
			    if(!user){
			    	socket.emit('loginResponse', {response: 1, error: "el email no se encuentra registrado"});
			    	return;
			    }
			    if(!user.validatePassword(password)){
				    socket.emit('loginResponse', {response: 2, error: "Contraseña incorrecta"});
				    return;
				}
				if(!user.info.habilidades || !user.info.nombre || !user.info.email || !user.info.sexo || !user.info.carrera){
					socket.emit('loginResponse', {response: 3});
					return;
				}
			    socket.emit('loginResponse', {response: 0});

		    });
	    });

	    socket.on('attemptSignup', function (data){ //Se ejecuta cuando se envia informacion de registro
		    console.log("intento de registro con "+ data['email'] + " " + data['password']);
		    User.findOne({'info.email': data['email']}, function(err, user) {
		    	if (err) {
			    	return;
			    }
		        if (user) {
			    	socket.emit('signupResponse', {response: 1, error: "El email ya se encuentra registrado"});
			    	return
			    } else {
				    var newUser = new User();
				    newUser.info.email = data['email'];
				    newUser.info.password = newUser.generateHash(data['password']);
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
				user.info.img = '/IMGUS/'+data['phone']+'-profile.jpg';
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
			User.findOne({'info.email': data['email']}, function(err, user){
				if(err){
					console.log(err);
					return;
				} if(user){
					user.info.nombre = data['name'];
					user.info.tel = data['tel'];
					user.info.sexo = data['sexo'];
					user.info.carrera = data['career'];
					//TODO: añadir al array user.info.habilidades = data['hab'];
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
			User.findOne({'info.email': data}, function(err, user){
				if(err){
					console.log(err);
					return;
				} if(user){
					var imagen;
					if(!user.info.img){
						imagen = "/IMG/AvatarHombre.jpg"
					} else{
						imagen = user.info.img;
					}
					socket.emit('getProfileInfoResponse', {
						response: 0,
						img: imagen,
						name: user.info.nombre,
						tel: user.info.tel,
						career: user.info.carrera,
						//descr: user.local.description
					});
				}
			});
		});

		
	    socket.on('disconnect', function(data){
	    	console.log('User disconnected :( ');
    	});
    });
}