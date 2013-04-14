var serverBasePath = '/ranked'

function PlayerController($scope,$http) {
    $http.get(serverBasePath+'/player').success(function(data) {
        $scope.players = data;
    });
}

function TournamentController($scope) {
    $.getJSON(serverBasePath+'/tournament', function(data) {
        $scope.tournaments = data;
    });
}

function DisciplineController($scope) {
    $.getJSON(serverBasePath+'/discipline', function(data) {
        $scope.disciplines = data;
    });
}




