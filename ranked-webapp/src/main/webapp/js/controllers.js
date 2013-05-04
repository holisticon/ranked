var serverBasePath = '/ranked'

function NavigationController($scope) {

}

function PlayerController($scope, Player) {
    $scope.players = Player.query();
}

function PlayerDetailsController($scope, Player) {

    $scope.player = Player.get({playerId:3});

}

function GameController($scope, Discipline) {

    $scope.disciplines = Discipline.query();

    $scope.submit = function() {
        alert("submit!");
    }

    $scope.tournamentsForDiscipline = function(disciplineId) {

    }

}

function TournamentController($scope, Tournament) {

    $scope.tournaments = Tournament.query();

}

function DisciplineController($scope, Discipline) {
    $scope.disciplines = Discipline.query();
}