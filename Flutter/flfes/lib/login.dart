import 'package:flutter/material.dart';
import 'package:flfes/profile.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flfes/gmodels.dart';


//Se definen los scopes a utilizar por google api
GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: [
    'email',
    'profile',
  ],
);

class AppLogin extends StatefulWidget{ //La app entra a este stateful widget al ser creada
  @override
  _LoginState createState() => _LoginState(); //Se crea y asigna el State del StatefulWidget 
}

class _LoginState extends State<AppLogin> { //State que utilizara el StatefulWidget AppLogin

  bool _start = false; //variable que inicia la animacion de fadein de los widgets
  var profileData; //variable que almacena los datos de la api de facebook
  GoogleSignInAccount _currentUser; //variable que almacena el token del usuario para solicitar los datos de la api de google

  //initState se ejecuta cuando los widgets han sido cargados y mostrados
  @override
  void initState(){ 
    super.initState();

    //_googleSignIn.onCurrentUserChanged.listen se ejecuta cuando se registra el login en el api de google
    _googleSignIn.onCurrentUserChanged.listen((GoogleSignInAccount account){ 
      _currentUser = account;
      if(_currentUser != null){
        _handleLogin();   
      }
    });

    
    WidgetsBinding.instance.addPostFrameCallback((_) => _startAnimation(context));
  }

  //_startAnimation inicia la animacion de fadein de los widgets
  void _startAnimation(BuildContext context){ 
    setState((){_start = true;});
  }

  //_handleLogin se ejecuta cuando se recibe una respuesta exitosa de la api de google
  Future <void> _handleLogin() async { 
    
    //se manda un get al api de google 
    final http.Response response = await http.get( 
          'https://people.googleapis.com/v1/people/me?requestMask.includeField=person.names,person.photos,person.emailAddresses,person.genders',
          headers: await _currentUser.authHeaders,
    );

    //Si la respuesta no es exitosa (200), entonces salir de la funcion
    if (response.statusCode != 200) { 
      print('People API ${response.statusCode} response: ${response.body}');
      return;
    }

    final data = json.decode(response.body); 
    final names = data['names'];
    final photos = data['photos'];
    final gender = data['genders'];
    final emails = data['emailAddresses'];

    //Se hace parse de la respuesta de google en formato JSON a una clase nativa de dart

    NamesList namesList = NamesList.fromJson(names); 
    PhotosList photosList = PhotosList.fromJson(photos);
    GenderList genderList = GenderList.fromJson(gender);
    EMailsList emailsList = EMailsList.fromJson(emails);

    //se anexa un string para solicitar la imagen de google con una anchura de 640 pixeles
    String photo = photosList.photos[0].url.replaceAll('s100/', '');
    photo = photo + '-wn=640'; 

    //Inicia la pantalla de registro de un perfil con los datos obtenidos de la api de google
    _startProfile(namesList.names[0].displayName, emailsList.emails[0].value, genderList.gender[0].value, photo);
  }


  //_handleGoogleSignIn se ejecuta cuando se presiona el widget de registrarse con google
  Future<void> _handleGoogleSignIn() async {
    try {
      //se espera la respuesta de registrarse con googlesignin
      await _googleSignIn.signIn();
    } catch (error) {
      print(error);
    }
  }

  //_handleFacebookLogin se ejecuta cuando se presiona el widget de registrarse con facebook
  Future<void> _handleFacebookLogin() async {
    var facebookLogin = FacebookLogin(); //se inicia la clase facebooklogin
    var facebookLoginResult = await facebookLogin.logInWithReadPermissions(['email']);
    switch (facebookLoginResult.status) {
        case FacebookLoginStatus.error:
          print(facebookLoginResult.errorMessage);
          onLoginStatusChanged();
          break;
        case FacebookLoginStatus.cancelledByUser:
          print("CancelledByUser");
          onLoginStatusChanged();
          break;
        case FacebookLoginStatus.loggedIn:
          print("LoggedIn");
          var graphResponse = await http.get(
  'https://graph.facebook.com/v3.2/me?fields=name,picture.height(400),email&access_token=${facebookLoginResult
  .accessToken.token}');

          var profile = json.decode(graphResponse.body);
          onLoginStatusChanged(profileData: profile);
          break;
    }
  }

  void onLoginStatusChanged({profileData}) {
    if(profileData){
      setState(() {
        this.profileData = profileData;
      });
      _startProfile(profileData['name'], profileData['email'], '', profileData['picture']['data']['url']);

    }
  }

  void _startProfile(String name, String email, String gender, String picture){
    Navigator.of(context).pushReplacement(MaterialPageRoute(
        builder: (context) => ProfileWidget(name: name, email: email, gender: gender, picture: picture),
      ));
  }

  @override
  Widget build(BuildContext context){

    return new Scaffold(
      body: new Stack(
        alignment: AlignmentDirectional.center,
        children: <Widget> [
          new Container(
              decoration: new BoxDecoration(
                image: new DecorationImage(
                  image: new AssetImage('images/backgroundmodified.jpg'), fit: BoxFit.cover,),
              ),
            ),
          
          new ListView(
            children: [
              new AnimatedOpacity(
                opacity: _start ? 1.0 : 0.0,
                duration: Duration(milliseconds: 3000), 
                child: new Container(
                  padding: EdgeInsets.only(left: 20.0, right: 20.0, top: 100.0),
                  child: new AnimatedOpacity(
                    opacity: 1.0,
                    duration: Duration(milliseconds: 500),
                    child: new Text(
                      'Titulo Atractivo ( ͡° ͜ʖ ͡°)',
                      style: Theme.of(context).textTheme.title,
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ),

              new AnimatedOpacity(
                opacity: _start ? 1.0 : 0.0,
                duration: Duration(milliseconds: 3000), 
                child: new Container(
                  padding: EdgeInsets.only(left: 20.0, right: 20.0, top: 100.0),
                  child: new AnimatedOpacity(
                    opacity: 1.0,
                    duration: Duration(milliseconds: 500),
                    child: new Text(
                      '¿Necesitas ayuda para estudiar algun tema? ¿Deseas ayudar a otros estudiantes? ¡Bienvenido!',
                      textAlign: TextAlign.center,
                      softWrap: true,
                      
                      style: Theme.of(context).textTheme.subtitle
                    ),
                  ),
                ),
              ),

              new AnimatedOpacity(
                opacity: _start ? 1.0 : 0.0,
                duration: Duration(milliseconds: 3000), 
                child: new Container(
                  padding: EdgeInsets.only(left: 50.0, right: 50.0, top:100.0),
                  child: _showFLogin(),
                ),
              ),

              new AnimatedOpacity(
                opacity: _start ? 1.0 : 0.0,
                duration: Duration(milliseconds: 3000), 
                  child: new Container(
                  padding: EdgeInsets.only(left: 50.0, right: 50.0, top: 20.0),
                  child: _showGLogin(),
                ),
              ),
            ],
          ),
        ]
      ),
    );
  }

  

  _showdata(profileData){
    return new Text("${profileData['email']}", softWrap: true,);
  }

  _showFLogin(){
    return new SignInButton(Buttons.Facebook, onPressed: _handleFacebookLogin);
  }

  _showGLogin(){
    return new SignInButton(Buttons.Google, onPressed: _handleGoogleSignIn);
  }

}



  