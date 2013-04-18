angular.module('rankedServices', ['ngResource']).
    factory('Player', function($resource){
        return $resource('/ranked/player/:playerId', {}, {

        });
    }).
    factory('Tournament', function($resource){
        return $resource('/ranked/tournament', {}, {

        });
    }).
    factory('Discipline', function($resource){
        return $resource('/ranked/discipline', {}, {

        });
    });