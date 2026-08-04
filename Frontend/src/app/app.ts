import {Component, signal} from '@angular/core';
import { AnimatedGradient } from './components/animated-gradient/animated-gradient';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AnimatedGradient, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = signal('timesheet-frontend');
}
