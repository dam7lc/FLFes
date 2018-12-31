import 'package:flutter/material.dart';


class ProfileWidget extends StatefulWidget{
  final String name, email, gender, picture;

  ProfileWidget({Key key, @required this.name, @required this.email, @required this.gender, @required this.picture}) : super (key: key);

  @override
  _ProfileState createState() => _ProfileState();
}

class _ProfileState extends State<ProfileWidget> {

  

  @override
  Widget build(BuildContext context){
    
    return new Scaffold(
      backgroundColor: Color.fromARGB(255, 0, 21, 81),
      body: new ListView(
        children: [
          
          new Align(
            alignment: Alignment.center,
            child: new Container(
              padding: EdgeInsets.only(top: 20.0),
              child: new Container(
                height: 200.0,
                width: 200.0,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  image: DecorationImage(
                    fit: BoxFit.cover,
                    image: NetworkImage(
                      widget.picture,
                    ),
                  ),
                ),
              ),
            ),
          ),

          new Container(
            padding: EdgeInsets.only(top: 60.0, left: 20.0, right: 20.0),
            child: new Row(
              children:[
                new Text(
                  'Nombre: ',
                  style: Theme.of(context).textTheme.title,
                ),

                new Expanded(
                  child: new TextFormField(
                    initialValue: '${widget.name}',
                    style: Theme.of(context).textTheme.subtitle,
                    decoration: InputDecoration(
                    ),
                  ), 
                ),
              ],
            ),
          ),

          new Container(
            padding: EdgeInsets.only(top: 30.0, left: 20.0, right: 20.0),
            child: new Row(
              children: [

                new Text(
                  'Correo: ',
                  style: Theme.of(context).textTheme.title,
                ),
                new Expanded(
                  child: new TextFormField(
                    initialValue: '${widget.email}',
                    style: Theme.of(context).textTheme.subtitle,
                    decoration: InputDecoration(
                    ),
                  ),
                ),
              ],
            ),
          ),

          new Container(
            padding: EdgeInsets.only(top: 30.0, left: 20.0, right: 20.0),
            child: new Row(
              children: [

                new Text(
                  'Genero: ',
                  style: Theme.of(context).textTheme.title,
                ),
                new Expanded(
                  child: new TextFormField(
                    initialValue: '${widget.gender}',
                    style: Theme.of(context).textTheme.subtitle,
                    decoration: InputDecoration(
                    ),
                  ),
                ),
              ],
            ),
          ),

        ],
      ), 
    );
  }
}