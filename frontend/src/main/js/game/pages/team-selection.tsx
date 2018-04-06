import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, TeamKey, Team } from '../../types/types';
import { Link } from 'react-router-dom';
import { match as Match } from 'react-router';
import { push } from 'react-router-redux';
import './player-selection.css';
import { PlayerService } from '../../services/player-service';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export interface TeamSelectionProps {
  match?: Match<any>;
  unavailableLetters: string;
  availableTeams: Array<Team>;
  currentTeamId: any;
  select: (team: TeamKey, selected: Team) => void;
  updateAvailableTeams: (teams: Array<Team>) => void;
}

function getLetters(unavailableLetters: string) {
  return alphabet.split('').map((letter, index) => {

    const available = !unavailableLetters.includes(letter);

    return (
      <Link key={ index } to={'/select/team/' + letter}>
        <div className={ available ? 'letter' : 'letter gray' }>
          <div className="letter-content">
            <div className="letter-absolute">{ letter.toUpperCase() }</div>
          </div>
        </div>
      </Link>
    );
  });
}

function getTeamList(availableTeams: Array<Team>, select: (team: Team) => void, selectedLetter?: string) {
  const teams = !selectedLetter ? availableTeams :
    availableTeams.filter(team => team.name!!.toLowerCase() === selectedLetter);

  return teams.map((team, index) => {
      return (
        <div key={ index } onClick={ () => select(team) } className={ 'team-entry' }>{ team.name }</div>
      );
    });
}

function TeamSelection({ unavailableLetters, availableTeams,
  currentTeamId, updateAvailableTeams, select, match }: TeamSelectionProps) {

  if (availableTeams.length === 0) {
    // no player available -> try to load them from backend
    PlayerService.getAllTeams().then(updateAvailableTeams);
  }

  let selectedLetter = '';
  if (match && match.params) {
    selectedLetter = match.params.letter;
  }

  const selectTeam = (team: Team) => select(currentTeamId.team, team);

  return (
    <div className={ 'player-selection' }>
      { !selectedLetter ?
        getLetters(unavailableLetters) :
        getTeamList(availableTeams, selectTeam, selectedLetter ) }
    </div>
  );
}

export function mapStateToProps({ ranked: { availableTeams, currentTeamId } }: any) {
  let unavailableLetters = alphabet;
  availableTeams.forEach((player: Player) => {
    unavailableLetters = unavailableLetters.replace(player.displayName[0].toLowerCase(), '');
  });

  return {
    unavailableLetters,
    availableTeams,
    currentTeamId
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>) {
  return {
    updateAvailableTeams: (teams: Array<Team>) => dispatch(Actions.updateAvailableTeams(teams)),
    select: (team: TeamKey, selected: Team) => {
      dispatch(Actions.setTeam(team, selected));
      dispatch(push('/'));
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(TeamSelection);
