const mongoose = require('mongoose');

const offerSchema = new mongoose.Schema({
	email: String,
	nombre: String,
	titulo: String,
	materia: String,
	descripcion: String,
	plazo: String,
	urgencia: String,
	habilidades: [String],
	fechaPublicacion:String,
	horaPublicacion:String
});

module.exports = mongoose.model('Offer', offerSchema);

