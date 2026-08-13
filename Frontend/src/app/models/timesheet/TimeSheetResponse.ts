import {UserResponse} from '../user/UserResponse';

export interface TimeSheetResponse {
  startTime: string;
  endTime: string;
  description: string;

  date: string;
  project: string;

  user: UserResponse;
}
