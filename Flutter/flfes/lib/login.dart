import 'package:flutter/material.dart';
import 'package:flfes/profile.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'package:flutter_signin_button/flutter_signin_button.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: [
    'email',
    'profile',
  ],
);

class AppLogin extends StatefulWidget{
  @override
  _LoginState createState() => _LoginState();
}

class GData{
  final String resourceName;
  final String etag;
  final List<GNames> names;

  GData({this.resourceName, this.etag, this.names});
}

class GNames{
  
}

class GPhotos{

}

class _LoginState extends State<AppLogin> {

  bool _start = false;
  bool isLoggedIn = false;
  bool isGLoggedIn = false;
  String _contactText;
  var profileData;
  GoogleSignInAccount _currentUser;

  @override
  void initState(){
    super.initState();

    _googleSignIn.onCurrentUserChanged.listen((GoogleSignInAccount account){
      _currentUser = account;
      if(_currentUser != null){
        isGLoggedIn = true;
        _handleLogin();   
      }
    });

    
    WidgetsBinding.instance.addPostFrameCallback((_) => _startAnimation(context));
  }

  
  void _startAnimation(BuildContext context){
    setState((){_start = true;});
  }
  Future <void> _handleLogin() async {
    print("executed");
    final http.Response response = await http.get(
          'https://people.googleapis.com/v1/people/me?requestMask.includeField=person.names,person.photos,person.emailAddresses,person.genders',
          headers: await _currentUser.authHeaders,
    );

    if (response.statusCode != 200) {
      setState(() {
        _contactText = "People API gave a ${response.statusCode} "
            "response. Check logs for details.";
      });
      print('People API ${response.statusCode} response: ${response.body}');
      return;
    }

    


    final data = json.decode(response.body);
    final names = data['names'];

    //print(data['names']['displayName']);

    //final jsonResponse = json.decode(jsonString);
    NamesList namesList = NamesList.fromJson(names);
    print(namesList.names[0].displayName);

  
  }

  Future<void> _handleSignIn() async {
    try {
      await _googleSignIn.signIn();
    } catch (error) {
      print(error);
    }
  }

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
  'https://graph.facebook.com/v3.2/me?fields=name,picture.height(200),email&access_token=${facebookLoginResult
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

    _startProfile(profileData['name'], profileData['email'], '', profileData['picture']['data']['url']);
  }

  void _startProfile(String name, String email, String gender, String picture){
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProfileWidget(name: name, email: email, gender: gender, picture: picture),
      ),
    );
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
                  child: isLoggedIn
                  ? _showdata(profileData)
                  : _showFLogin(),
                ),
              ),

              new AnimatedOpacity(
                opacity: _start ? 1.0 : 0.0,
                duration: Duration(milliseconds: 3000), 
                  child: new Container(
                  padding: EdgeInsets.only(left: 50.0, right: 50.0, top: 20.0),
                  child: isGLoggedIn
                  ? new Text(_contactText, softWrap: true,)
                  : _showGLogin(),
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
    return new SignInButton(Buttons.Facebook, onPressed: initiateFacebookLogin);
  }

  _showGLogin(){
    return new SignInButton(Buttons.Google, onPressed: _handleSignIn);
  }

}

class Nms{
  NmsMeta nmsMeta;
  String displayName;
  String familyName;
  String givenName;
  String displayNameLastFirst;

  Nms({
    this.nmsMeta,
    this.displayName,
    this.familyName,
    this.givenName,
    this.displayNameLastFirst
  });

  factory Nms.fromJson(Map<String, dynamic> pJson){
    return Nms(
      nmsMeta: NmsMeta.fromJson(pJson['metadata']),
      displayName: pJson['displayName'],
      familyName: pJson['familyName'],
      givenName: pJson['givenName'],
      displayNameLastFirst: pJson['displayNameLastFirst']
    );
  }

}

class NmsMeta{
  bool primary;
  NmsMetaSrc nmsMetaSrc;

  NmsMeta({
    this.primary,
    this.nmsMetaSrc
  });

  factory NmsMeta.fromJson(Map<String, dynamic> parsedJson){
    return NmsMeta(
      primary: parsedJson['primary'],
      nmsMetaSrc: NmsMetaSrc.fromJson(parsedJson['source'])
    );
  }
}

class NmsMetaSrc{
  String type;
  String id;

  NmsMetaSrc({
    this.type,
    this.id
  });

  factory NmsMetaSrc.fromJson(Map<String, dynamic> json){
    return NmsMetaSrc(
      type: json['type'],
      id: json['id']
    );
  }
}

class NamesList{
  final List<Nms> names;

  NamesList({
    this.names,
  });

  factory NamesList.fromJson(List<dynamic> parseJson){
    List<Nms> names = new List<Nms>();
    names = parseJson.map((i) => Nms.fromJson(i)).toList();
    
    return new NamesList(
      names: names,
    );
  }
}

  