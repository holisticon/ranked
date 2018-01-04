import React from 'react';
import { PlayerIcon } from './player_icon';
import axios from 'axios';

export class SelectPlayer extends React.Component {
  constructor(props) {
    super(props);

    this.state = { players: [] };
    this.getPlayers();
  }

  getPlayers() {
    const that = this;

    axios.get('https://holisticon.de/team.html', { headers: { 'Access-Control-Allow-Origin': '*' } })
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
      });
  }

  getPlayerIcons() {
    const that = this;
    return this.state.players.map(function (player, index) {
      return <PlayerIcon
        key={ index }
        name={ player.name }
        img={ player.img }
        onClick={ () => that.props.select(player) }></PlayerIcon>
    });
  }

  render() {
    const visible = this.props.visible ? '' : 'hidden';
    const classes = `${ visible } player-selection`;

    return (
      <div className={ classes }>
        { this.getPlayerIcons() }
      </div>
    );
  }
}