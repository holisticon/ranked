httpRequest = new XMLHttpRequest();

var teams = [
  {'name': "Deutschland", 'imageUrl': "/img/flags/germany.png"},
  {'name': "Schweden", 'imageUrl': "/img/flags/sweden.png"},
  {'name': "Russland", 'imageUrl': "/img/flags/russia.png"},
  {'name': "Uruguay", 'imageUrl': "/img/flags/uruguay.png"},
  {'name': "Portugal", 'imageUrl': "/img/flags/portugal.png"},
  {'name': "Spanien", 'imageUrl': "/img/flags/spain.png"},
  {'name': "Wallis und Futuna", 'imageUrl': "/img/flags/wallis_and_futuna.png"},
  {'name': "Dänemark", 'imageUrl': "/img/flags/denmark.png"},
  {'name': "Argentinien", 'imageUrl': "/img/flags/argentina.png"},
  {'name': "Island", 'imageUrl': "/img/flags/iceland.png"},
  {'name': "Brasilien", 'imageUrl': "/img/flags/brazil.png"},
  {'name': "Schweiz", 'imageUrl': "/img/flags/switzerland.png"},
  {'name': "Belgien", 'imageUrl': "/img/flags/belgium.png"},
  {'name': "England", 'imageUrl': "/img/flags/england.png"},
  {'name': "Japan", 'imageUrl': "/img/flags/japan.png"},
  {'name': "Polen", 'imageUrl': "/img/flags/poland.png"},
];


for (var i = 0; i < teams.length; i++) {
  httpRequest.open('POST', '/command/team', false);
  httpRequest.setRequestHeader('Content-Type', 'application/json');
  httpRequest.send(JSON.stringify(teams[i]));
}