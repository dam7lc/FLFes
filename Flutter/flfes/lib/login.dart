import 'package:flutter/material.dart';

class AppLogin extends StatelessWidget{
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

              

              
            ],
          ),
        ]
      ),
    );
  }
}