import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../../core/auth.service';
@Injectable()
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) { }

canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {
    if (this.authService.hasValidToken()) {
      return true;
    }

    const promise = new Promise<boolean>( (resolve, reject) => {
     return this.authService.loadDiscoveryDocumentAndTryLogin().then( (success: boolean) => {
      if (!success) {
        this.router.navigate(['/login']);
        resolve(false);
      } else {
        resolve(true);
      }
    });

    });

    return promise;
  }
}
