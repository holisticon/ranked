import React from 'react';
import { PlayerIcon } from './player_icon';
import axios from 'axios';

const alphabet = 'abcdefghijklmnopqrstuvwxyz';

export class SelectPlayer extends React.Component {

  constructor(props) {
    super(props);

    this.state = { showLetters: true, playerFilter: null, unavailableLetters: [], players: [] };
    this.getPlayers();
  }

  getPlayersFromWebsite() {
    const that = this;

    return axios.get('https://holisticon.de/team.html')
      .then(res => {
        const site = document.createElement('html');
        site.innerHTML = res.data;
  
        const list = site.getElementsByClassName("members-list")[0].children;
        const images = [];
  
        let title, name, photo, src;
  
        for (let member of list) {
          title = member.getElementsByClassName("title")[0];
          name = !title ? "" : title.innerText;
  
          if (!!name) {
            photo = member.getElementsByClassName("photo")[0];
            photo = !photo ? null : photo.getElementsByTagName("img")[0];
  
            if (photo && photo.hasAttribute("src")) {
              src = photo.getAttribute("src");
            } else if (photo && photo.hasAttribute("data-src")) {
              src = photo.getAttribute("data-src");
            } else {
              src = null;
            }
  
            if (src != null && src.endsWith(".png")) {
              images.push({ name, img: 'https://holisticon.de/' + src });
            }
          }
        }
  
        that.setState({ players: images });
  
        console.log(images);
      });
  }

  getPlayersFromLocal() {
    const that = this;

    return axios.get('players.json').then(res => {
      that.setState({ players: res.data });
    });
  }

  getPlayers() {
    this.getPlayersFromLocal().then(() => {
      var unavailableLetters = alphabet;
      this.state.players.forEach(player => {
        unavailableLetters = unavailableLetters.replace(player.name[0].toLowerCase(), '');
      });
      this.state.unavailableLetters = unavailableLetters;
    });
  }

  getPlayerIcons() {
    const that = this;

    if (!this.state.playerFilter) {
      this.state.playerFilter = (player) => true;
    }

    const filtered = this.state.players.filter(that.state.playerFilter);

    return filtered.map(function (player, index) {
        return <PlayerIcon
          key={ index }
          name={ player.name }
          img={ player.img }
          onClick={ () => that.selectPlayer(player) }></PlayerIcon>
      });
  }

  selectPlayer(player) {
    this.state.showLetters = true;
    this.props.select(player);
  }

  showPlayers(firstLetter) {
    this.state.showLetters = false;
    this.state.playerFilter = (player) => player.name[0].toLowerCase() == firstLetter;
    this.forceUpdate();
  }

  getLetters() {
    const that = this;

    return alphabet.split('').map(function (letter, index) {

      let available = that.state.unavailableLetters.includes(letter);
      
      return <div key={ index } className={ available ? 'letter' : 'letter gray' } onClick={ () => available && that.showPlayers(letter) }>
        <div className="letter-content">
          <div className="letter-absolute">{ letter.toUpperCase() }</div>
        </div></div>
    });
  }

  render() {
    const visible = this.props.visible ? '' : 'hidden';
    const classes = `${ visible } player-selection`;

    return (
      <div className={ classes }>
        { this.state.showLetters ? this.getLetters() : this.getPlayerIcons() }
      </div>
    );
  }
}