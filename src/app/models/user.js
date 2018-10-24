const mongoose = require('mongoose');
const bcrypt = require('bcrypt-nodejs');

const userSchema = new mongoose.Schema({
	local: {
		nickname: String,
		password: String,
		email: String,
		sex: String,
		career: String,
		age: String,
		img: String,
		phone: String,
		description: String,
	},
	
	intereses: {
		sex: String
	}
	/*facebook: {
		email: String,
		password: String,
		id: String,
		token: String
	},
	twitter: {
		email: String,
		password: String,
		id: String,
		token: String
	},
	google: {
		email: String,
		password: String,
		id: String,
		token: String
	}*/
});

//Generando un hash
userSchema.methods.generateHash = function (password) {
	return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
}

//Validando
userSchema.methods.validatePassword = function (password) {
	return bcrypt.compareSync(password, this.local.password);
}

module.exports = mongoose.model('User', userSchema);

