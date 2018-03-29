import * as React from 'react';
import { Swipeable } from 'react-touch';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Composition, TeamColor, PlayerKey, Team, TeamKey, Set } from '../../types/types';
import { PlayerIcon } from '../../components/player-icon';
import { push } from 'react-router-redux';
import { PartialStoreState } from '../store.state';
import { POINTS_PER_MATCH } from '../../config';

export interface TeamProps {
  color: TeamColor;
}

interface InternalTeamProps {
  team: Team;
  composition: Composition;
  showSwitchPlayerButtons: boolean;
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

function Team({ team, composition, showSwitchPlayerButtons, classes,
  incGoals, decGoals, selectPlayer, switchPlayerPositions }: InternalTeamProps) {

  return (
    <div className={ classes } onClick={ () => incGoals() }>
      <div className="goal-counter-container">
        <Swipeable onSwipeRight={ () => incGoals() } onSwipeLeft={ () => decGoals() }>
          <div className="goal-counter">
            <span className="current-goals">{ composition.goals.length }</span>
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
            <PlayerIcon
              click={ () => { return; } }
              img={ team[composition.defense].imageUrl }
              name={ team[composition.defense].displayName }
            />
        }
        <span className="name">Tor</span>
      </div>

      <div
        className={ showSwitchPlayerButtons ? 'change-positions' : 'hidden' }
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
            <PlayerIcon
              click={ () => { return; } }
              img={ team[composition.attack].imageUrl }
              name={ team[composition.attack].displayName }
            />
        }
        <span className="name">Angriff</span>
      </div>
    </div>
  );
}

export function mapStateToProps({ranked: store}: PartialStoreState, { color }: TeamProps) {
  const currentSet: Set = store.sets[store.sets.length - 1];
  const composition: Composition = currentSet[color];
  const isCurrentSetStarted = (currentSet.blue.goals.length > 0) || (currentSet.red.goals.length > 0);
  const isFirstOrLastSet =  (store.sets.length === 1) || (store.sets.length === POINTS_PER_MATCH * 2 - 1);

  return {
    team: store[composition.team],
    composition,
    classes: 'team-' + color,
    showSwitchPlayerButtons: !isCurrentSetStarted && isFirstOrLastSet

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
