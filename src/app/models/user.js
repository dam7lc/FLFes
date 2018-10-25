const mongoose = require('mongoose');
const bcrypt = require('bcrypt-nodejs');

const userSchema = new mongoose.Schema({
	info: {
		nombre: String,
		password: String,
		email: String,
		sexo: String,
		carrera: String,
		img: String,
		tel: String,
		habilidades: [String],
	}
});

//Generando un hash
userSchema.methods.generateHash = function (password) {
	return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
}

//Validando
userSchema.methods.validatePassword = function (password) {
	return bcrypt.compareSync(password, this.info.password);
}

module.exports = mongoose.model('User', userSchema);

