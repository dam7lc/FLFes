const User = require('../app/models/user');
const Offer = require('../app/models/offer')

module.exports = (io) => {
	io.on('connection', function (socket){ //Se ejecuta con un socket.on
	console.log("User Connected :)");

		socket.on('autoLogin', function(data){
			//TODO registrar login automatico 
		});

		socket.on('uploadImg', function (data){ //Cuando se sube la imagen de perfil al servidor
			var id; 
			var imgpath;
			User.findOne({'info.email': data['email']}, function(err, user) {
				if(err){
					console.log(err);
					return;
				} if(!user){ //Si el usuario se esta registrando
					var newUser = new User();
					newUser.info.email = data['email'];
					id = newUser.id;
					imgpath = '/IMGUS/'+id+'-profile.jpg';
					newUser.info.img = imgpath;

				    newUser.save(function(err) {
						if (err) {
							throw err;
						}
					});
					var img = new Buffer(data['img'], 'base64');
					require('fs').writeFile(__dirname+'/../public'+imgpath, img, function(err){
						if(err){
							console.log(err);
							return;
						}
					});
					return;
				} else if(user){ //Si el usuario ya existe
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
					//user.info.sexo = data['sexo'];
					user.info.carrera = data['career'];
					//user.info.habilidades = data['habs'];
					user.save(function (err){
						if(err){
							console.log(err);
							return;
						}
					});
					socket.emit('profileInfoResponse', {response: 0});
				} else{
					var newUser = new User();
					newUser.info.nombre = data['name'];
					newUser.info.tel = data['tel'];
					newUser.info.carrera = data['career'];
				    newUser.info.email = data['email'];
				    newUser.save(function(err) {
						if (err) {
							throw err;
						}
					});
				}
			});
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