export interface PageResponse<TimeSheet> {
  content: TimeSheet[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
