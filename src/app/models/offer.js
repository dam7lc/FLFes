const mongoose = require('mongoose');

const offerSchema = new mongoose.Schema({
	titulo: String,
	materia: String,
	de: String,
	tema: String,
	descripcion: String
});

module.exports = mongoose.model('Offer', offerSchema);

