import * as React from 'react';
import { Swipeable } from 'react-touch';
import { connect, Dispatch } from 'react-redux';
import * as Actions from '../actions';
import { Player, TeamColor, PlayerPostion, Team } from '../types/types';
import { PlayerIcon } from './player_icon';
import { push } from 'react-router-redux';

interface ActiveTeam {
  goals: number;
  attack: Player;
  defense: Player;
}

export interface TeamProps {
  color: TeamColor;
  isLastSet: boolean;
}

interface InternalTeamProps {
  team: ActiveTeam;
  isLastSet: boolean;
  classes: string;
  incGoals: () => void;
  decGoals: () => void;
  selectPlayer: (position: PlayerPostion) => void;
  switchPlayerPositions: () => void;
}

function stopEvent(event: React.SyntheticEvent<Object>): boolean {
  event.preventDefault();
  event.stopPropagation();
  return true;
}

function Team({ team, isLastSet, classes,
  incGoals, decGoals, selectPlayer, switchPlayerPositions }: InternalTeamProps) {

  return (
    <div className={ classes } onClick={ () => incGoals() }>
      <div className="goal-counter-container">
        <Swipeable onSwipeRight={ () => incGoals() } onSwipeLeft={ () => decGoals() }>
          <div className="goal-counter">
            <span className="current-goals">{ team.goals }</span>
          </div>
        </Swipeable>
      </div>

      <div className="add-defense" onClick={ (e) => stopEvent(e) && selectPlayer('defense') }>
        {
          !team.defense.username ?
            <i className="material-icons">&#xE853;</i> :
            <PlayerIcon click={ () => { return; } } img={ team.defense.img } />
        }
        <span className="name">Tor</span>
      </div>

      <div
        className={ isLastSet ? 'change-positions' : 'hidden' }
        onClick={ (e) => stopEvent(e) && switchPlayerPositions() }
      >
        <i className="material-icons">&#xE0C3;</i>
      </div>

      <div className="add-attack" onClick={ (e) => stopEvent(e) && selectPlayer('attack') }>
        {
          !team.attack.username ?
            <i className="material-icons">&#xE853;</i> :
            <PlayerIcon click={ () => { return; } } img={ team.attack.img } />
        }
        <span className="name">Angriff</span>
      </div>
    </div>
  );
}

export function mapStateToProps({ranked: { selectPlayerFor, teams, sets }}: any, { color, isLastSet }: TeamProps) {
  const currentSet = sets[sets.length - 1];

  const getTeamPositions = (team: Team, offense: string) => {
    if (offense === 'player1') {
      return { attack: team.player1, defense: team.player2 };
    } else {
      return { attack: team.player2, defense: team.player1 };
    }
  };

  return {
    team: { ...getTeamPositions(teams[color], currentSet.offense[color]), goals: currentSet.goals[color]},
    classes: 'team-' + color,
    isLastSet
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>, { color }: TeamProps) {
  return {
    incGoals: () => dispatch(Actions.incGoals(color)),
    decGoals: () => dispatch(Actions.decGoals(color)),
    selectPlayer: (position: PlayerPostion) => {
      dispatch(Actions.selectPlayer(color, position));
      dispatch(push('/select'));
    },
    switchPlayerPositions: () => dispatch(Actions.switchPlayerPositions(color))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Team);