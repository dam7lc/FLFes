import 'package:flutter/material.dart';
import 'package:adhara_socket_io/adhara_socket_io.dart';
import 'dart:convert';
import 'dart:io';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:image/image.dart' as pkgimage;


class ProfileWidget extends StatefulWidget{
  final String name, email, gender, picture;

  ProfileWidget({Key key, @required this.name, @required this.email, @required this.gender, @required this.picture}) : super (key: key);

  @override
  _ProfileState createState() => _ProfileState();
}

const String URI = "http://192.168.0.12:3000/";

class _ProfileState extends State<ProfileWidget> {
  Image _image;
  SocketIO socket;
  int _radiovalue = 2;
  final _formKey = GlobalKey<FormState>();
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
      "Relaciones Internacionales",
      "Sociología"
    ];
  List<DropdownMenuItem<String>> _dropDownItems;
  TextEditingController _nameController;
  final _phoneController = TextEditingController();

  String _carrera;


  @override
  void initState(){
    super.initState();
    initSocket();
    _dropDownItems = getDropDownMenuItems();
    _carrera = _dropDownItems[0].value;
    _nameController = TextEditingController(text: '${widget.name}');

    //var img = base64.encode([widget.picture.file]);
    _sendimg();
  }

  Future getImage() async{
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);


    File croppedFile = await ImageCropper.cropImage(
      sourcePath: image.path,
      ratioX: 1.0,
      ratioY: 1.0,
      maxWidth: 512,
      maxHeight: 512,
      circleShape: true,
    );

    pkgimage.Image resized = pkgimage.copyResize(
      pkgimage.decodeImage(croppedFile.readAsBytesSync()), 
      640
    );

    var imgAsBytes = pkgimage.encodeJpg(resized);
    
    Image nimage = Image.memory(imgAsBytes);

    var img = base64.encode(imgAsBytes);

    var jdata = json.encode({
        'email': widget.email.trim(),
        'img': img
      });
    socket.emit('uploadImg', [jdata]);

    setState((){
      _image = nimage;
    });
  }


  Future<void> _sendimg() async{
    final httpresponse = await http.readBytes(widget.picture); 
    var img = base64.encode(httpresponse);

    var jdata = json.encode({
        'email': widget.email.trim(),
        'img': img
      });
    socket.emit('uploadImg', [jdata]);

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

  initSocket() async{
    socket = await SocketIOManager().createInstance(URI);
    socket.onConnect((data){
      print("connected to socket");
    });

    socket.onConnectError((data){
      print('Connect Error');
    });
    socket.onConnectTimeout((data){
      print('Connect Timeout');
    });
    socket.onError((data){
      print('Error');
    });
    socket.onDisconnect((data){
      print('Disconnected from socket');
    });

    socket.on('profileInfoResponse', (data){
      int response = data['response'];

      switch(response){
        case 0:
          //Navigator.of(context).pushReplacement();

        break;
      }
    });

    socket.connect();
  }
  
  final _scaffoldKey = GlobalKey<ScaffoldState>();
  
  @override
  void dispose(){
    _nameController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context){
    if(widget.gender == "male"){
      _radiovalue = 1;
    } else if(widget.gender == "female"){
      _radiovalue = 0;
    }
    return new Scaffold(
      key: _scaffoldKey,
      backgroundColor: Color.fromARGB(255, 0, 21, 81),
      body: new Form(
        key: _formKey,
        child: ListView(
          children: [
            
            new Align(
              alignment: Alignment.center,
              child: new Stack(
                children: [
                  new Container(
                    padding: EdgeInsets.only(top: 20.0),
                    child: new Container(
                      height: 200.0,
                      width: 200.0,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        image: DecorationImage(
                          fit: BoxFit.cover,
                          image: _image == null 
                          ? NetworkImage(
                            widget.picture,
                            )
                          : _image.image
                        ),
                      ),
                    ),
                  ),

                  new Positioned(
                    bottom: 5.0,
                    height: 30.0,
                    width: 30.0,
                    right: 5.0,
                    child: new IconButton(
                      icon: new Icon(
                        Icons.photo,
                        color: Color.fromARGB(255, 213, 159, 15),
                        size: 30.0,
                      ), 
                      onPressed: getImage,
                    ),
                  ),
                ],
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
                      controller: _nameController,
                      validator: (value){
                        if(value.isEmpty){
                          return 'No puede estar vacio';
                        }
                      },
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
                    child: new Text(
                      widget.email,
                      style: Theme.of(context).textTheme.display1,
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
                      controller: _phoneController,
                      validator: (value){
                        if(value.isEmpty){
                          return 'Por favor proporciona un telefono de contacto';
                        } else if(value.length < 10 || value.length > 10){
                          return 'Comprueba que sean 10 digitos';
                        } 
                      },
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

                            
                              new  Radio(
                              value: 1,
                              groupValue: _radiovalue,
                              onChanged: _onRadioValueChanged,
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

                            new Radio(
                              value: 2,
                              groupValue: _radiovalue,
                              onChanged: _onRadioValueChanged,
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
                      isExpanded: true,
                      value: _carrera,
                      items: _dropDownItems,
                      onChanged: _changedDropDownItem,
                      style: Theme.of(context).textTheme.display1                    
                    )
                  ),
                ],
              ),
            ),

            new Align(
              alignment: Alignment.bottomRight,
              child: new Container(
                padding: EdgeInsets.all(10.0),
                child: new Container(
                  height: 50.0,
                  width: 50.0,
                  child: new IconButton(
                    icon: new Icon(
                      Icons.navigate_next,
                      color: Color.fromARGB(255, 150, 150, 150), 
                      size: 50.0
                    ), 
                    onPressed:  _onNextPressed               
                  ),  
                ),
              ),
            ),

          ],
        ), 
      ),
    );
  }


  void _onNextPressed(){
    if(_formKey.currentState.validate()){

      String name = _nameController.text.trim();
      String email = widget.email.trim();
      String phone = _phoneController.text.trim();
      String carrera = _carrera.trim();

      var jdata = json.encode({
        'name': name,
        'email': email,
        'tel': phone,
        'career': carrera 
      });

      showDialog(
        context: context,
        builder: (BuildContext context){
          return AlertDialog(
            content: new Text('¿Estas seguro de querer realizar tu registro con estos datos?', softWrap: true, textAlign: TextAlign.center,),
            actions: [
              new FlatButton(child: new Text('Cancelar'), onPressed: (){Navigator.of(context).pop();}), 
              new FlatButton(child: new Text('Confirmar'), onPressed: (){socket.emit('profileInfo', [jdata]);}),
            ],
          );
        } 
      );
    }
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