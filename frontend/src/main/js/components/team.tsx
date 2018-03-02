import * as React from 'react';
import { Swipeable } from 'react-touch';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Composition, TeamColor, PlayerKey, Team, TeamKey, Set } from '../types/types';
import { PlayerIcon } from './player_icon';
import { push } from 'react-router-redux';

export interface TeamProps {
  color: TeamColor;
  isLastSet: boolean;
}

interface InternalTeamProps {
  team: Team;
  composition: Composition;
  isLastSet: boolean;
  classes: string;
  incGoals: () => void;
  decGoals: () => void;
  selectPlayer: (team: TeamKey, player: PlayerKey) => void;
  switchPlayerPositions: () => void;
}

function stopEvent(event: React.SyntheticEvent<Object>): boolean {
  event.preventDefault();
  event.stopPropagation();
  return true;
}

function Team({ team, composition, isLastSet, classes,
  incGoals, decGoals, selectPlayer, switchPlayerPositions }: InternalTeamProps) {

  return (
    <div className={ classes } onClick={ () => incGoals() }>
      <div className="goal-counter-container">
        <Swipeable onSwipeRight={ () => incGoals() } onSwipeLeft={ () => decGoals() }>
          <div className="goal-counter">
            <span className="current-goals">{ composition.goals }</span>
          </div>
        </Swipeable>
      </div>

      <div
        className="add-defense"
        onClick={ (e) => stopEvent(e) && selectPlayer(composition.team, composition.defense) }
      >
        {
          !team[composition.defense].id ?
            <i className="material-icons">&#xE853;</i> :
            <PlayerIcon click={ () => { return; } } img={ team[composition.defense].imageUrl } />
        }
        <span className="name">Tor</span>
      </div>

      <div
        className={ isLastSet ? 'change-positions' : 'hidden' }
        onClick={ (e) => stopEvent(e) && switchPlayerPositions() }
      >
        <i className="material-icons">&#xE0C3;</i>
      </div>

      <div
        className="add-attack"
        onClick={ (e) => stopEvent(e) && selectPlayer(composition.team, composition.attack) }
      >
        {
          !team[composition.attack].id ?
            <i className="material-icons">&#xE853;</i> :
            <PlayerIcon click={ () => { return; } } img={ team[composition.attack].imageUrl } />
        }
        <span className="name">Angriff</span>
      </div>
    </div>
  );
}

export function mapStateToProps({ranked: store}: any, { color, isLastSet }: TeamProps) {
  const currentSet: Set = store.sets[store.sets.length - 1];
  const composition: Composition = currentSet[color];

  return {
    team: store[composition.team],
    composition,
    classes: 'team-' + color,
    isLastSet
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>, { color }: TeamProps) {
  return {
    incGoals: () => dispatch(Actions.incGoals(color)),
    decGoals: () => dispatch(Actions.decGoals(color)),
    selectPlayer: (team: TeamKey, player: PlayerKey) => {
      dispatch(Actions.selectPlayer(team, player));
      dispatch(push('/select'));
    },
    switchPlayerPositions: () => dispatch(Actions.switchPlayerPositions(color))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Team);
