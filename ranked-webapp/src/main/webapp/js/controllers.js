var serverBasePath = '/ranked'

function PlayerController($scope, $http) {
    $http.get(serverBasePath + '/player').success(function (data) {
        $scope.players = data;
    });
}

function PlayerDetailsController($scope, $http) {
    $http.get(serverBasePath + '/player/').success(function (data) {
        $scope.player = data;
    });
}

function TournamentController($scope, $http) {
    $http.get(serverBasePath + '/tournament').success(function (data) {
        $scope.tournaments = data;
    });
}

function DisciplineController($scope, $http) {
    $http.get(serverBasePath + '/discipline').success(function (data) {
        $scope.disciplines = data;
    });
}