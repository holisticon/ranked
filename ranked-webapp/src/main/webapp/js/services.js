angular.module('rankedServices', ['ngResource']).
    factory('Player', function($resource){
        return $resource('http://localhost\\:8080/ranked/player/:playerId', {}, {

        });
    }).
    factory('Tournament', function($resource){
        return $resource('http://localhost\\:8080/ranked/tournament', {}, {

        });
    }).
    factory('Discipline', function($resource){
        return $resource('http://localhost\\:8080/ranked/discipline', {}, {

        });
    });