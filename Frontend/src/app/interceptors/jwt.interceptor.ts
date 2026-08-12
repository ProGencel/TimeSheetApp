import {HttpInterceptorFn, HttpRequest} from '@angular/common/http';

export const jwtInterceptors: HttpInterceptorFn = (req, next) => {

  let token: string | null = localStorage.getItem('token');

  if(token)
  {
    let reqClone : HttpRequest<any> = req.clone({
      setHeaders : {
        Authorization: `Bearer ${token}`
      }
    });

    return next(reqClone);
  }

  return next(req);
};
