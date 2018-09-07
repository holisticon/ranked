import * as React from 'react';
import { connect, Dispatch } from 'react-redux';
import { push } from 'react-router-redux';
import { Swipeable } from 'react-touch';

import { PlayerIcon } from '../../components/player-icon';
import { Config } from '../../config';
import { SoundService } from '../../services/sound-service';
import { Composition, Player, PlayerKey, Set, Team, TeamColor, TeamKey } from '../../types/types';
import * as Actions from '../actions';
import { PartialStoreState } from '../store.state';

export interface TeamProps {
  devicePosition: TeamColor | null;
  color: TeamColor;
}

interface InternalTeamProps {
  devicePosition: TeamColor | null;
  isDefense: boolean;
  team: Team;
  composition: Composition;
  showSwitchPlayerButtons: boolean;
  classes: string;
  goalScored: (playerId?: string) => void;
  decGoals: () => void;
  selectPlayer: (team: TeamKey, player: PlayerKey) => void;
  selectTeam: (team: TeamKey) => void;
  switchPlayerPositions: () => void;
}

function stopEvent(event: React.SyntheticEvent<Object>): boolean {
  event.preventDefault();
  event.stopPropagation();
  return true;
}

function renderOnePlayerUI(props: InternalTeamProps, player: Player, selectPlayer: () => void) {

  return (
    <div className={ 'one-player-ui' }>

      { !props.isDefense && props.showSwitchPlayerButtons ? renderSwitchPlayerButtons(props.switchPlayerPositions, false) : '' }

      { renderPlayerIcon(player, props.isDefense, selectPlayer) }

      { props.isDefense && props.showSwitchPlayerButtons ? renderSwitchPlayerButtons(props.switchPlayerPositions, true) : '' }
    </div>
  );
}

function renderTwoPlayerUI(
  { team, composition, showSwitchPlayerButtons, selectPlayer, switchPlayerPositions }: InternalTeamProps
) {
  return (
    <div className={ 'two-player-ui' }>
      { renderPlayerIcon(team[composition.defense], true, () => { selectPlayer(composition.team, composition.defense); } )}

      { showSwitchPlayerButtons ? renderSwitchPlayerButtons(switchPlayerPositions) : ''}

      { renderPlayerIcon(team[composition.attack], false, () => { selectPlayer(composition.team, composition.attack); } )}
    </div>
  );
}

function renderSwitchPlayerButtons(switchPlayerPositions: () => void, isDefense?: boolean) {
  // TODO two-players: button centered (no class suffix)
  return (
    <div
      className={ 'change-positions-' + (isDefense ? 'defense' : 'offense') }
      onClick={ (e) => stopEvent(e) && switchPlayerPositions() }
    >
      <i className="material-icons">&#xE0C3;</i>
    </div>
  );
}

function renderPlayerIcon(player: Player, isDefense: boolean, selectPlayer: () => void) {

  return (
      <div
        className={ 'add-' + (isDefense ? 'defense' : 'attack') }
        onClick={ (e) => stopEvent(e) && selectPlayer() }
      >

        {
          !player.id ?
            <i className="material-icons">&#xE853;</i> :
            <PlayerIcon
              click={ () => { return; } }
              img={ player.imageUrl }
              name={ player.displayName }
            />
        }

        <span className="name">{ isDefense ? 'Tor' : 'Angriff' }</span>

      </div>
  );
}

function renderTeamIcon( { team, composition, selectTeam }: InternalTeamProps ) {
  return(
    <div
      className="add-team"
      onClick={ (e) => stopEvent(e) && selectTeam(composition.team) }
    >
      { team.imageUrl ?
        <img src={ team.imageUrl } /> :
        <i className="material-icons">&#xE7FB;</i>
      }
      { Config.showTeamName ?
        <span className="team-name">{team.name || 'Team'}</span> :
        <span />
      }
    </div>
  );
}

function renderWonSetDots(wonSets: number) {
  return( <div className="won-sets-count">{ '\u2022'.repeat(wonSets) }</div> );
}

function RenderTeam(props: InternalTeamProps) {

  const displayPlayer: Player = props.isDefense ? props.team[props.composition.defense] : props.team[props.composition.attack];
  const displayPlayerId = props.devicePosition ? displayPlayer.id : undefined;

  return (
    <div className={ props.classes } onClick={ () => { SoundService.playGoalSound(); props.goalScored(displayPlayerId); } }>

      { renderWonSetDots(props.team.wonSets) }

      <div className="goal-counter-container">
        <Swipeable onSwipeRight={ () => props.goalScored(displayPlayerId) } onSwipeLeft={ () => props.decGoals() }>
          <div className="goal-counter">
            <span className="current-goals">{ props.composition.goals.length }</span>
          </div>
        </Swipeable>
      </div>

      { Config.teamMode
        ? renderTeamIcon(props)
        : (props.devicePosition
          ? renderOnePlayerUI(props, displayPlayer, () => { props.selectPlayer(props.composition.team, props.isDefense ? props.composition.defense : props.composition.attack); })
          : renderTwoPlayerUI(props) )
      }

    </div>
  );
}

export function mapStateToProps({ranked: store}: PartialStoreState, { color, devicePosition }: TeamProps) {
  const currentSet: Set = store.sets[store.sets.length - 1];
  const composition: Composition = currentSet[color];
  const isCurrentSetStarted = (currentSet.blue.goals.length > 0) || (currentSet.red.goals.length > 0);
  const isFirstOrLastSet =  (store.sets.length === 1) || (store.sets.length === Config.pointsPerMatch * 2 - 1);

  return {
    devicePosition,
    isDefense: color === devicePosition,
    team: store[composition.team],
    composition,
    classes: 'team-' + color,
    showSwitchPlayerButtons: !isCurrentSetStarted && isFirstOrLastSet
  };
}

export function mapDispatchToProps(dispatch: Dispatch<Actions.RankedAction>, { color, devicePosition }: TeamProps) {
  const isDefense = color === devicePosition;

  return {
    goalScored: (playerId?: string) => {
      dispatch(push(`/selectManikin/${ color }/${ isDefense ? 'defense' : 'attack'}${ playerId ? ('/' + playerId) : '' }`));
    },
    decGoals: () => dispatch(Actions.decGoals(color)),
    selectPlayer: (team: TeamKey, player: PlayerKey) => {
      dispatch(Actions.selectEntity(team, player));
      dispatch(push('/select'));
    },
    selectTeam: (team: TeamKey) => {
      dispatch(Actions.selectEntity(team));
      dispatch(push('/selectTeam'));
    },
    switchPlayerPositions: () => dispatch(Actions.switchPlayerPositions(color))
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(RenderTeam);
