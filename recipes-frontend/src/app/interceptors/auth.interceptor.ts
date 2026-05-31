import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Retrieve the JWT token from localStorage (standard for Angular-based JWT auth)
  const token = localStorage.getItem('token');

  // If a token is found, clone the request and add the Authorization header
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  // Otherwise, proceed with the original request
  return next(req);
};
