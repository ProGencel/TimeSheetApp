import {UserResponse} from './UserResponse';

export interface TimeSheetResponse {
  startTime: string;
  endTime: string;
  description: string;

  date: string;

  user: UserResponse;
}
