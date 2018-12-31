import 'package:flutter/material.dart';


class ProfileWidget extends StatefulWidget{
  final String name, email, gender, picture;

  ProfileWidget({Key key, @required this.name, @required this.email, @required this.gender, @required this.picture}) : super (key: key);

  @override
  _ProfileState createState() => _ProfileState();
}

class _ProfileState extends State<ProfileWidget> {
  int _radiovalue = -1;
  List _carreras = [
      "Arquitectura",
      "Comunicación y periodismo",
      "Derecho",
      "Diseño industrial",
      "Economia",
      "Ingeniería Civil",
      "Ingeniería en Computación",
      "Ingeniería Eléctrica Electrónica",
      "Ingeniería Industrial",
      "Ingeniería Mecánica",
      "Pedagogía",
      "Planificación para el Desarrollo Agropecuario",
      "Relaciones Internacionales"
      "Sociología"
    ];
  List<DropdownMenuItem<String>> _dropDownItems;
  String _carrera;


  @override
  void initState(){
    super.initState();

    _dropDownItems = getDropDownMenuItems();
    _carrera = _dropDownItems[0].value;

  }

  List<DropdownMenuItem<String>> getDropDownMenuItems(){
    List<DropdownMenuItem<String>> items = new List();
    for(String car in _carreras){
      items.add(new DropdownMenuItem(
        value: car,
        child: new Text(car)
      ));
    }
    return items;
  }
  @override
  Widget build(BuildContext context){
    //if(widget.gender == )
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
                  style: Theme.of(context).textTheme.display1,
                ),

                new Expanded(
                  child: new TextFormField(
                    initialValue: '${widget.name}',
                    style: Theme.of(context).textTheme.display1,
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
                  style: Theme.of(context).textTheme.display1,
                ),
                new Expanded(
                  child: new TextFormField(
                    keyboardType: TextInputType.emailAddress,
                    initialValue: '${widget.email}',
                    style: Theme.of(context).textTheme.display1,
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
                  'Telefono: ',
                  style: Theme.of(context).textTheme.display1,
                ),
                new Expanded(
                  child: new TextFormField(
                    keyboardType: TextInputType.phone,
                    style: Theme.of(context).textTheme.display1,
                    decoration: InputDecoration(
                      
                    ),
                  ),
                ),
              ],
            ),
          ),

          new Container(
            padding: EdgeInsets.only(top: 40.0, left: 20.0, right: 20.0),
            child: new Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [

                new Column(mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                  children:[
                    new Text(
                    'Genero: ',
                    style: Theme.of(context).textTheme.display1,
                  ),
                  ],  
                ),
                
                new Expanded(
                  child: new Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      new Row(
                        children: [

                         
                              new Radio(
                                value: 0,
                                groupValue: _radiovalue,
                                onChanged: _onRadioValueChanged,
                                /*initialValue: '${widget.gender}',
                                style: Theme.of(context).textTheme.display1,
                                decoration: InputDecoration(

                                ),*/
                          ),

                          new Expanded(
                            child:  new Text(
                              'Masculino',
                              style: Theme.of(context).textTheme.display1,
                              textAlign: TextAlign.start,
                            ),   
                          ),  
                        ],             
                      ),

                      new Row(
                        children: [

                          
                            new  Radio(
                            value: 1,
                            groupValue: _radiovalue,
                            onChanged: _onRadioValueChanged,
                            /*initialValue: '${widget.gender}',
                            style: Theme.of(context).textTheme.display1,
                            decoration: InputDecoration(

                            ),*/
                          ),
                            

                          new Expanded(
                            child:  new Text(
                              'Femenino',
                              style: Theme.of(context).textTheme.display1,
                              textAlign: TextAlign.start,
                            ),   
                          ),  
                        ],             
                      ),

                      new Row(
                        children: [

                          new Radio(
                            value: 2,
                            groupValue: _radiovalue,
                            onChanged: _onRadioValueChanged,
                            /*initialValue: '${widget.gender}',
                            style: Theme.of(context).textTheme.display1,
                            decoration: InputDecoration(

                            ),*/
                          ),

                          new Expanded(
                            child:  new Text(
                              'Otro',
                              style: Theme.of(context).textTheme.display1,
                              textAlign: TextAlign.start,
                            ),   
                          ),  
                        ],             
                      ),

                    ],
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
                  'Carrera: ',
                  style: Theme.of(context).textTheme.display1,
                ),
                new Expanded(
                  child: new DropdownButton(
                    value: _carrera,
                    items: _dropDownItems,
                    onChanged: _changedDropDownItem,
                    style: Theme.of(context).textTheme.display1,
                  )
                ),
              ],
            ),
          ),

        ],
      ), 
    );
  }

  void _changedDropDownItem(String item){
    setState((){
      _carrera = item;
    });
  }
  
  void _onRadioValueChanged(int value){
    setState((){
      switch(value){
        case 0:
          _radiovalue = value;
          break;
        case 1: 
          _radiovalue = value;
          break;
        case 2:
          _radiovalue = value;
          break;
      }
    });
  }
}