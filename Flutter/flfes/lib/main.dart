import 'package:flutter/material.dart';
import 'package:flfes/login.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) { 
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);
    return MaterialApp(
      title: 'FlFes', 
      theme: ThemeData(
        unselectedWidgetColor: Color.fromARGB(255, 150, 150, 150),
        
        canvasColor: Color.fromARGB(255, 0, 15, 50),

        accentColor: Color.fromARGB(255, 213, 159, 15),
        cursorColor: Color.fromARGB(255, 150, 150, 150),
        hintColor: Color.fromARGB(255, 150, 150, 150),
        primaryColor: Color.fromARGB(255, 213, 159, 15),
        fontFamily: 'Mali',
        textTheme: new TextTheme(
          title: TextStyle(fontSize: 26.0, color: Color.fromARGB(255, 213, 159, 15)),
          subtitle: TextStyle(fontSize: 22.0, color: Color.fromARGB(255, 213, 159, 15)),
          display1: TextStyle(fontSize: 18.0, color: Color.fromARGB(255, 213, 159, 15)),
        ),
      ),
      home: new AppLogin(),
    );
  }
}
