import {UserResponse} from '../user/UserResponse';
import {Project} from '../project/Project';

export interface TimeSheet {
  id: number;

  startTime: string;
  endTime: string;
  description: string;
  date: string;
  project: Project;

  user: UserResponse;
}
