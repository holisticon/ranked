import * as React from 'react';
import { PlayerIcon } from './player_icon';
import axios from 'axios';
import { Player } from '../types/types';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export interface SelectPlayerProps {
  visible: boolean;
  upsideDown: boolean;
  select: (player: Player) => void;
}

export interface SelectPlayerState {
  showLetters: boolean;
  playerFilter: (player: Player) => boolean;
  unavailableLetters: string;
  players: Array<Player>;
}

export class SelectPlayer extends React.Component<SelectPlayerProps, SelectPlayerState> {

  constructor(props: SelectPlayerProps) {
    super(props);

    this.state = { showLetters: true, playerFilter: (player: Player) => true, unavailableLetters: '', players: [] };
    this.getPlayers();
  }

  /* getPlayersFromWebsite() {
    const that = this;

    return axios.get('https://holisticon.de/team.html')
      .then(res => {
        const site = document.createElement('html');
        site.innerHTML = res.data;
  
        const list = site.getElementsByClassName('members-list')[0].children;
        const images = [];
  
        let title, name, photo, src;
  
        for (let member of list) {
          title = member.getElementsByClassName('title')[0];
          name = !title ? '' : title.innerText;
  
          if (!!name) {
            photo = member.getElementsByClassName('photo')[0];
            photo = !photo ? null : photo.getElementsByTagName('img')[0];
  
            if (photo && photo.hasAttribute('src')) {
              src = photo.getAttribute('src');
            } else if (photo && photo.hasAttribute('data-src')) {
              src = photo.getAttribute('data-src');
            } else {
              src = null;
            }
  
            if (src != null && src.endsWith('.png')) {
              images.push({ name, img: 'https://holisticon.de/' + src });
            }
          }
        }
  
        that.setState({ players: images });
  
        console.log(images);
      });
  } */

  getPlayersFromLocal() {
    return axios.get('players.json').then(res => res.data);
  }

  getPlayers() {
    this.getPlayersFromLocal().then((players: Array<Player>) => {
      var unavailableLetters = alphabet;
      players.forEach(player => {
        unavailableLetters = unavailableLetters.replace(player.name[0].toLowerCase(), '');
      });

      this.setState({ ...this.state, unavailableLetters, players });
    });
  }

  getPlayerIcons() {
    const that = this;
    const filtered = this.state.players.filter(that.state.playerFilter);

    return filtered.map((player, index) => {
        return (
          <PlayerIcon key={ index } img={ player.img } click={ () => that.selectPlayer(player) } />
        );
      });
  }

  selectPlayer(player: Player) {
    this.setState({ ...this.state, showLetters: true });
    this.props.select(player);
  }

  showPlayers(firstLetter: string) {
    const available = !this.state.unavailableLetters.includes(firstLetter);
    if (!available) {
      return;
    }

    let playerFilter = (player: Player) => player.name[0].toLowerCase() === firstLetter;
    this.setState({ ...this.state, showLetters: false, playerFilter });
  }

  getLetters() {
    return alphabet.split('').map((letter, index) => {

      const available = !this.state.unavailableLetters.includes(letter);
      
      return (
        <div 
          key={ index }
          className={ available ? 'letter' : 'letter gray' }
          onClick={ () => this.showPlayers(letter) }
        >
          <div className="letter-content">
            <div className="letter-absolute">{ letter.toUpperCase() }</div>
          </div>
        </div>
      );
    });
  }

  render() {
    const visible = this.props.visible ? '' : 'hidden';
    const rotation = this.props.upsideDown ? 'upside-down' : '';
    const classes = `${ visible } ${ rotation } player-selection`;

    return (
      <div className={ classes }>
        { this.state.showLetters ? this.getLetters() : this.getPlayerIcons() }
      </div>
    );
  }
}