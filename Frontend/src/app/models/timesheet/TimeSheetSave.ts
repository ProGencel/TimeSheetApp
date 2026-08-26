import {Project} from '../project/Project';

export interface TimeSheetSave {
  date: string;
  startTime: string;
  endTime: string;
  description: string;
  project: Project;
}
