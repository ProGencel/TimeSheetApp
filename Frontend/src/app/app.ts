import {Component, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AnimatedGradient } from './shared/components/animated-gradient/animated-gradient';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AnimatedGradient],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = signal('timesheet-frontend');
}
