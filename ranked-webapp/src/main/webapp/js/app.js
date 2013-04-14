angular.module('ranked', []).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/player', {templateUrl: 'partials/playerList.html', controller: PlayerController}).
            when('/tournament', {templateUrl: 'partials/tournamentList.html', controller: TournamentController}).
            when('/discipline', {templateUrl: 'partials/disciplineList.html', controller: DisciplineController}).
            otherwise({redirectTo: '/player'});
    }]);







