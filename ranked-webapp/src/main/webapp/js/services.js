angular.module('rankedServices', ['ngResource']).
    factory('Player', function($resource){
        return $resource('/ranked/player', {}, {
            getAllPlayers: {method:'GET', isArray:true},
            getPlayerById: {method:'GET', isArray:true}
        });
    }).
    factory('Tournament', function($resource){
        return $resource('/ranked/tournament', {}, {
            query: {method:'GET', params:{phoneId:'phones'}, isArray:true}
        });
    }).
    factory('Discipline', function($resource){
        return $resource('/ranked/discipline', {}, {
            query: {method:'GET', params:{phoneId:'phones'}, isArray:true}
        });
    });