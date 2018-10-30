const mongoose = require('mongoose');
const bcrypt = require('bcrypt-nodejs');

const userSchema = new mongoose.Schema({
	info: {
		email: String,
		password: String,
		nombre: String,
		ncuenta: String,
		sexo: String,
		tel: String,
		img: String,
		carrera: String,
		semestre: String,
		grupo: String,
		turno: String,
		habilidades: [String],
		tokenPass: String,
		tokenValid: String
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

