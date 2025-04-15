import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import { AboutUsComponent } from "./about-us/about-us.component";
import { ContactComponent } from "./contact/contact.component";
import { LoginComponent } from "./auth/login/login.component";
import { RegisterComponent } from "./auth/register/register.component";
import { SubscriptionComponent } from "./mic2/subscription/subscription.component";
import { AuthGuard } from "./services/auth/auth.guard";
import { ChallengeListComponent } from './gamification/challenge/challenge-list/challenge-list.component';
import { CreateChallengeComponent } from './gamification/challenge/create-challenge/create-challenge.component';
import { PointHistoryComponent } from './gamification/point/point-history/point-history.component';
//import { TeacherGuard } from './services/auth/teacher.guard';

// Configuration constants
const CONFIG = {
  currentDate: '2025-03-06 04:50:10',
  currentUser: 'nessimayadi12'
};

// Main routes configuration
const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'courses', component: CoursesComponent },
  { path: 'team', component: OurTeamComponent },
  { path: 'testemonial', component: TestimonialComponent },
  { path: 'aboutus', component: AboutUsComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'points/history', component: PointHistoryComponent },

  // Protected routes - Gamification module
  {
    path: 'gamification',
    canActivate: [AuthGuard],
    children: [
      { 
        path: '', 
        redirectTo: 'challenges', 
        pathMatch: 'full' 
      },
      { 
        path: 'challenges', 
        component: ChallengeListComponent 
      },
      { 
        path: 'challenges/create', 
        component: CreateChallengeComponent,
        //canActivate: [TeacherGuard] // Protect with TeacherGuard
        canActivate: [AuthGuard],

      }
    ]
  },
 
  // Protected route - Subscription
  { 
    path: 'subscription/:courseId', 
    component: SubscriptionComponent, 
    canActivate: [AuthGuard] 
  },

  // 404 route - Always keep last
  { path: '**', component: NotfoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'enabled',
      paramsInheritanceStrategy: 'always'
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
  static readonly CONFIG = CONFIG;
}