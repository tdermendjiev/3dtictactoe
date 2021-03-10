import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthErrorEvent, OAuthService, NullValidationHandler, AuthConfig } from 'angular-oauth2-oidc';
import { BehaviorSubject, combineLatest, Observable, ReplaySubject } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {

  authConfig: AuthConfig = {
    issuer: 'http://46.101.183.89:8080/auth/realms/tictactoe',
    redirectUri: window.location.origin + "/board",
    clientId: 'tictactoe-fe',
    requestAccessToken: true,
    scope: 'openid profile email offline_access',
    responseType: 'code',
    disableAtHashCheck: true,
    showDebugInformation: true,
    requireHttps: false,
    logoutUrl: window.location.origin + "/login"
  }

  private isAuthenticatedSubject$ = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject$.asObservable();

  private isDoneLoadingSubject$ = new ReplaySubject<boolean>();
  public isDoneLoading$ = this.isDoneLoadingSubject$.asObservable();

  public canActivateProtectedRoutes$: Observable<boolean> = combineLatest([
    this.isAuthenticated$,
    this.isDoneLoading$
  ]).pipe(map(values => values.every(b => b)));

  private navigateToLoginPage() {
    this.router.navigateByUrl('/login');
  }

  constructor(
    private oauthService: OAuthService,
    private router: Router,
  ) {


    this.oauthService.events.subscribe(event => {
        if (event instanceof OAuthErrorEvent) {
          console.error('OAuthErrorEvent Object:', event);
        } else {
          console.warn('OAuthEvent Object:', event);
        }
      });

      window.addEventListener('storage', (event) => {
        if (event.key !== 'access_token' && event.key !== null) {
          return;
        }

        console.warn('Noticed changes to access_token (most likely from another tab), updating isAuthenticated');
        this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());

        if (!this.oauthService.hasValidAccessToken()) {
          this.navigateToLoginPage();
        }
      });

      this.oauthService.events
        .subscribe(_ => {
          this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());
        });

      this.oauthService.events
        .pipe(filter(e => ['token_received'].includes(e.type)))
        .subscribe(e => this.oauthService.loadUserProfile());

      this.oauthService.events
        .pipe(filter(e => ['session_terminated', 'session_error'].includes(e.type)))
        .subscribe(e => this.navigateToLoginPage());

      this.oauthService.setupAutomaticSilentRefresh();
  }

//   public runInitialLoginSequence(){
//     this.configure();
//   }

  private configure() {
    this.oauthService.configure(this.authConfig);
    this.oauthService.tokenValidationHandler = new  NullValidationHandler();
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  public loadDiscoveryDocumentAndTryLogin(): Promise<boolean> {
    return this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  public login() {
    this.oauthService.initLoginFlow();
  }

  public logout() {
      this.oauthService.logOut();

  }

  public refresh() { this.oauthService.silentRefresh(); }
  public hasValidToken() { return this.oauthService.hasValidIdToken(); }

  public runInitialLoginSequence(): Promise<void> {
    this.oauthService.configure(this.authConfig);

    return this.oauthService.loadDiscoveryDocument()

      .then(() => this.oauthService.tryLogin())

      .then(() => {
        if (this.oauthService.hasValidAccessToken()) {
          return Promise.resolve();
        }

        return this.oauthService.silentRefresh()
          .then(() => Promise.resolve())
          .catch(result => {

            const errorResponsesRequiringUserInteraction = [
              'interaction_required',
              'login_required',
              'account_selection_required',
              'consent_required',
            ];

            if (result
              && result.reason
              && errorResponsesRequiringUserInteraction.indexOf(result.reason.error) >= 0) {

              return Promise.resolve();
            }

            return Promise.reject(result);
          });
      })

      .then(() => {
        this.isDoneLoadingSubject$.next(true);

        if (this.oauthService.state && this.oauthService.state !== 'undefined' && this.oauthService.state !== 'null') {
          let stateUrl = this.oauthService.state;
          if (stateUrl.startsWith('/') === false) {
            stateUrl = decodeURIComponent(stateUrl);
          }
          console.log(`There was state of ${this.oauthService.state}, so we are sending you to: ${stateUrl}`);
          this.router.navigateByUrl(stateUrl);
        }
      })
      .catch(() => this.isDoneLoadingSubject$.next(true));
  }

}