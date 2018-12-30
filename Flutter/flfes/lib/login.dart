import 'package:flutter/material.dart';
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AppLogin extends StatefulWidget{
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<AppLogin> {
  bool isLoggedIn = false;
  var profileData;

  void initiateFacebookLogin() async {
    var facebookLogin = FacebookLogin();
    var facebookLoginResult = await facebookLogin.logInWithReadPermissions(['email']);
    switch (facebookLoginResult.status) {
        case FacebookLoginStatus.error:
          print(facebookLoginResult.errorMessage);
          onLoginStatusChanged(false);
          break;
        case FacebookLoginStatus.cancelledByUser:
          print("CancelledByUser");
          onLoginStatusChanged(false);
          break;
        case FacebookLoginStatus.loggedIn:
          print("LoggedIn");
          var graphResponse = await http.get(
  'https://graph.facebook.com/v3.2/me?fields=name,first_name,last_name,email&access_token=${facebookLoginResult
  .accessToken.token}');

          var profile = json.decode(graphResponse.body);
          print(profile.toString());

          onLoginStatusChanged(true, profileData: profile);
          break;
    }
  }

  void onLoginStatusChanged(bool isLoggedIn, {profileData}) {
    setState(() {
      this.isLoggedIn = isLoggedIn;
      this.profileData = profileData;
    });
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
              new Container(
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
              
              new Container(
                padding: EdgeInsets.only(left: 20.0, right: 20.0, top: 150.0),
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

              new Container(
                padding: EdgeInsets.only(left: 100.0, right: 100.0, top:100.0),
                child: isLoggedIn
                ? _showdata(profileData)
                : _showLogin(),
              ),
            ],
          ),
        ]
      ),
    );
  }

  _showdata(profileData){
    return new Text("${profileData['name']}", softWrap: true,);
  }

  _showLogin(){
    return new SignInButton(Buttons.Facebook, onPressed: initiateFacebookLogin);
  }
}

  