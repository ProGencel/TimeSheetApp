import {UserResponse} from '../user/UserResponse';

export interface Project {
  id: number;
  name: string;
  description: string;
  user: UserResponse;
  finished: boolean;
}
