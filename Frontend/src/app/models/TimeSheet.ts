import {UserResponse} from './UserResponse';

export interface TimeSheet {
  id: number;

  startTime: string;
  endTime: string;
  description: string;
  date: string;
  project: string;

  user: UserResponse;
}
