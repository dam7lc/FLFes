const express = require('express');
const app = express();
const path = require('path');
const mongoose = require('mongoose');
const passport = require('passport');
const flash = require('connect-flash');
const morgan = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const session = require('express-session');
const {url} = require('./config/database');
const fileUpload = require('express-fileupload');

mongoose.connect(url,{
	useNewUrlParser: true
});

require('./config/passport')(passport);

//settings
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

//middlewares
app.use(morgan('dev'));
app.use(cookieParser());
app.use(bodyParser.urlencoded({extended: false}));
app.use(session({
	secret: 'enriquecarranza',
	resave: false,
	saveUninitialized: false
}));
app.use(passport.initialize());
app.use(passport.session());
app.use(flash());
app.use(fileUpload());

//routes
require('./app/routes')(app, passport);

//static files
app.use(express.static(path.join(__dirname, 'public')));

const server = app.listen(app.get('port'), () => {
	console.log('Server on port', app.get('port'));
});

const io = require('socket.io').listen(server);
require('./app/sockets')(io);
