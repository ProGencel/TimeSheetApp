import {UserResponse} from './UserResponse';

export interface TimeSheet {
  startTime: string;
  endTime: string;
  description: string;
  date: string;

  user: UserResponse;
}
