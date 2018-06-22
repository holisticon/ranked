import * as React from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import { Player } from '../../types/types';
import { RankingChart } from '../components/ranking-chart';
import { ChartData2D, ChartData3D } from '../types';
import { EloAdapter } from '../services/elo-adapter';
import { GoalsAdapter } from '../services/goals-adapter';
import './score-board.css';
import { Heading } from '../services/heading.service';
import { HeadingComponent, HeadingConfig } from '../components/heading';

type ProfileState = { };

export class Profile extends React.Component<any, ProfileState> {

  constructor(props: any) {
    super(props);
  }

  public render() {
    return (
      <div className="profile">

      </div>
    );
  }
}
