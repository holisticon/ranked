import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { TeamKey, Team } from '../../types/types';
import { push } from 'react-router-redux';
import './team-selection.css';
import { PlayerService } from '../../services/player-service';
import { PartialStoreState } from '../store.state';

export interface TeamSelectionProps {
  availableTeams: Array<Team>;
  currentTeamKey: TeamKey;
  select: (team: TeamKey, selected: Team) => void;
  updateAvailableTeams: (teams: Array<Team>) => void;
}

function renderTeamList(availableTeams: Array<Team>, selectTeam: (team: Team) => void) {
  return availableTeams.map( (team, index) => {

    return (
      <div key={index} className={ 'team-entry' } onClick={ () => selectTeam(team) }> { team.name } </div>
    )
  })
}

function TeamSelection({ availableTeams, currentTeamKey, updateAvailableTeams, select }: TeamSelectionProps) {

  if (availableTeams.length === 0) {
    // no player available -> try to load them from backend
    PlayerService.getAllTeams().then(updateAvailableTeams);
  }

  const selectTeam = (team: Team) => select(currentTeamKey, team);

  return (
    <div className={ 'team-selection' }>
      { renderTeamList(availableTeams, selectTeam) }
    </div>
  );
}

export function mapStateToProps({ ranked: { availableTeams, selectFor } }: PartialStoreState) {
  return {
    availableTeams,
    currentTeamKey: selectFor!!.team
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
