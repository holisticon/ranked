angular.module('ranked', ['rankedServices','ngResource']).
    config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        //$locationProvider.html5Mode(true);
        $routeProvider.
            when('/player', {templateUrl: 'partials/playerList.html', controller: PlayerController}).
            when('/player/:playerId', {templateUrl: 'partials/playerDetail.html',  controller: PlayerDetailsController}).
            when('/tournament', {templateUrl: 'partials/tournamentList.html', controller: TournamentController}).
            when('/discipline', {templateUrl: 'partials/disciplineList.html', controller: DisciplineController}).
            when('/game', {templateUrl: 'partials/gameForm.html', controller: GameController}).
            otherwise({redirectTo: '/player'});
    }]);







