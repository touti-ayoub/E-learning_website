import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import {AboutUsComponent} from "./about-us/about-us.component";
import {ContactComponent} from "./contact/contact.component";
import {LoginComponent} from "./auth/login/login.component";
import {RegisterComponent} from "./auth/register/register.component";
import {SubscriptionComponent} from "./mic2/subscription/subscription.component";
import {AuthGuard} from "../services/auth/auth.guard";
import {EventListComponent} from "./mic5/event-list/event-list.component";
import {EventDetailsComponent}  from "./mic5/event-details/event-details.component";

// Declare routes outside the class
const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'courses', component: CoursesComponent },
  { path: 'team', component: OurTeamComponent },
  { path: 'testemonial', component: TestimonialComponent },
  { path: 'aboutus', component: AboutUsComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'subscription/:courseId', component: SubscriptionComponent, canActivate: [AuthGuard] },
  { path: 'events', component: EventListComponent },
  { path: 'events/:id', component: EventDetailsComponent },

  { path: '**', component: NotfoundComponent }, // Keep this as the last route
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
