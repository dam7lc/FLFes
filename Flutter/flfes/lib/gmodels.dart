class Gender{
  Metadata metadata;
  String value;

  Gender({
    this.metadata,
    this.value,
  });

  factory Gender.fromJson(Map<String, dynamic> parsedJson){
    return Gender(
      metadata: Metadata.fromJson(parsedJson['metadata']),
      value: parsedJson['value'],
    );
  }
}

class GenderList{
  final List<Gender> gender;

  GenderList({
    this.gender,
  });

  factory GenderList.fromJson(List<dynamic> parsedJson){
    List<Gender> gender = new List<Gender>();
    gender = parsedJson.map((i) => Gender.fromJson(i)).toList();

    return new GenderList(gender: gender);
  }
}

class EMails{
  EMailsMetadata metadata;
  String value;

  EMails({
    this.metadata,
    this.value,
  });

  factory EMails.fromJson(Map<String, dynamic> parsedJson){
    return EMails(
      metadata: EMailsMetadata.fromJson(parsedJson['metadata']),
      value: parsedJson['value'],
    );
  }
}

class EMailsList{
  final List<EMails> emails;

  EMailsList({
    this.emails,
  });

  factory EMailsList.fromJson(List<dynamic> parsedJson){
    List<EMails> emails = new List<EMails>();
    emails = parsedJson.map((i) => EMails.fromJson(i)).toList();

    return new EMailsList(emails: emails);
  }
}

class Photos{
  Metadata metadata;
  String url;

  Photos({
    this.metadata,
    this.url,
  });
  
  factory Photos.fromJson(Map<String, dynamic> parsedJson){
    return Photos(
      metadata: Metadata.fromJson(parsedJson['metadata']),
      url: parsedJson['url'],
    );
  }
}

class PhotosList{
  final List<Photos> photos;

  PhotosList({
    this.photos,
  });

  factory PhotosList.fromJson(List<dynamic> parsedJson){
    List<Photos> photos = new List<Photos>();
    photos = parsedJson.map((i) => Photos.fromJson(i)).toList();

    return new PhotosList(photos: photos);
  }
}

class Nms{
  Metadata metadata;
  String displayName;
  String familyName;
  String givenName;
  String displayNameLastFirst;

  Nms({
    this.metadata,
    this.displayName,
    this.familyName,
    this.givenName,
    this.displayNameLastFirst
  });

  factory Nms.fromJson(Map<String, dynamic> pJson){
    return Nms(
      metadata: Metadata.fromJson(pJson['metadata']),
      displayName: pJson['displayName'],
      familyName: pJson['familyName'],
      givenName: pJson['givenName'],
      displayNameLastFirst: pJson['displayNameLastFirst']
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

class Metadata{
  bool primary;
  MetadataSource nmsMetaSrc;

  Metadata({
    this.primary,
    this.nmsMetaSrc
  });

  factory Metadata.fromJson(Map<String, dynamic> parsedJson){
    return Metadata(
      primary: parsedJson['primary'],
      nmsMetaSrc: MetadataSource.fromJson(parsedJson['source'])
    );
  }
}

class EMailsMetadata{
  bool primary;
  bool verified;
  MetadataSource nmsMetaSrc;

  EMailsMetadata({
    this.primary,
    this.verified,
    this.nmsMetaSrc
  });

  factory EMailsMetadata.fromJson(Map<String, dynamic> parsedJson){
    return EMailsMetadata(
      primary: parsedJson['primary'],
      verified: parsedJson['verified'],
      nmsMetaSrc: MetadataSource.fromJson(parsedJson['source'])
    );
  }
}

class MetadataSource{
  String type;
  String id;

  MetadataSource({
    this.type,
    this.id
  });

  factory MetadataSource.fromJson(Map<String, dynamic> json){
    return MetadataSource(
      type: json['type'],
      id: json['id']
    );
  }
}

