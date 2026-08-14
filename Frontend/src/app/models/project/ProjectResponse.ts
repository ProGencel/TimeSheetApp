import {UserResponse} from '../user/UserResponse';

export interface ProjectResponse {

  name: string;
  description: string;
  user: UserResponse;
  finished: boolean;

}
