var serverBasePath = '/ranked'

function PlayerController($scope, Player) {
    $scope.players = Player.query();
}

function PlayerDetailsController($scope, Player) {

    $scope.player = Player.get({playerId:1});

}

function TournamentController($scope, Tournament) {

    $scope.tournaments = Tournament.query();

}

function DisciplineController($scope, Discipline) {
    $scope.disciplines = Discipline.query();
}