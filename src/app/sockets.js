const User = require('../app/models/user');
const Offer = require('../app/models/offer')

module.exports = (io) => {
	io.on('connection', function (socket){ //Se ejecuta con un socket.on
	console.log("User Connected :)");

		socket.on('attemptLogin', function (data) { //Se ejecuta cuando se inicia sesion
			console.log("login with: " + data);
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
				if(!user.info.habilidades || !user.info.nombre || !user.info.tel || !user.info.carrera){
					socket.emit('loginResponse', {response: 3});
					return;
				}
			    socket.emit('loginResponse', {response: 0});

		    });
		});
		
		socket.on('autoLogin', function(data){
			//TODO registrar login automatico 
		});

	    socket.on('attemptSignup', function (data){ //Se ejecuta cuando se envia informacion de registro
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
			var id;
			var imgpath;
			User.findOne({'info.email': data['email']}, function(err, user) {
				if(err){
					console.log(err);
					return;
				}
				if(user){
					id = user.id;
					imgpath = '/IMGUS/'+id+'-profile.jpg';
					user.info.img = imgpath;
					user.save(function(err) {
						if (err) {
							console.log(err);
							return;
						}
					});
					var img = new Buffer(data['img'], 'base64');
					require('fs').writeFile(__dirname+'/../public'+imgpath, img, function(err){
						if(err){
							console.log(err);
							return;
						}
					});
					socket.emit('uploadImgResponse', {response: 0, img: imgpath});
				}
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
					user.info.habilidades = data['habs'];
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
						habs: user.info.habilidades
					});
				}
			});
		});

		socket.on('publishOffer', function(data){
			User.findOne({'info.email': data['email']}, function(err, user){
				if(err){
					console.log(err);
					return;
				}
				if(user){
					var newOffer = new Offer();
				    newOffer.email = user.info.email;
					newOffer.titulo = data['titulo'];
					newOffer.materia = data['materia'];
					newOffer.tema = data['tema'];
					newOffer.descripcion = data['descripcion'];
				    newOffer.save(function(err) {
				    	if (err) {
						    throw err;
					    }
					    socket.emit('publishOfferResponse', {response: 0}); 		
				    	return;
				    });
				}
			})
		})

		socket.on('populateOffers', function(data){
			console.log(data);
			User.findOne({'info.email': data['email']}, function(err, user){
				if(err){
					console.log(err);
					return;
				}
				Offer.countDocuments({'email': {"$ne": user.info.email}},function(err, result){
					if(err){
						console.log(err);
						return;
					}
					if(result == 0){
						socket.emit('populateOffersResponse', {response: 1})
						return;
					}
					else{
						var num = result;
						Offer.find({'email': {"$ne": user.info.email}}, function(err, offer){
							if(err){
								console.log(err);
								return;	
							}
							offer.forEach(function(offer, position){
								socket.emit('populateOffersResponse', {
									response: 0,
									materia: offer.materia,
									titulo: offer.titulo,
									email: offer.email,
									tema: offer.tema,
									descripcion: offer.descripcion
								}) 
								if(position == num-1){
									socket.emit('finishPopulatingOffers', { response: 0	})
								}
							})
						});
					}
				});
			});
		});

		
	    socket.on('disconnect', function(data){
	    	console.log('User disconnected :( ');
    	});
    });
}