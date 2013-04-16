angular.module('ranked', ['rankedServices','ngResource']).
    config(['$routeProvider', function ($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $routeProvider.
            when('/player', {templateUrl: 'partials/playerList.html', controller: PlayerController}).
            when('/player/:playerName', {templateUrl: 'partials/playerDetail.html',  controller: PlayerController}).
            when('/tournament', {templateUrl: 'partials/tournamentList.html', controller: TournamentController}).
            when('/discipline', {templateUrl: 'partials/disciplineList.html', controller: DisciplineController}).
            otherwise({redirectTo: '/player'});
    }]);







