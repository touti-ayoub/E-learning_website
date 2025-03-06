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
import {PaymentComponent} from "./mic2/payment/payment.component";
import {SubscriptionPlanComponent} from "./mic2/subscription-plan/subscription-plan.component";
import {PricingComponent} from "./mic2/pricing/pricing.component";
import {PaymentSuccessComponent} from "./mic2/payment-success/payment-success.component";
import {PaymentHistComponent} from "./mic2/payment-hist/payment-hist.component";

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
  { path: 'payment',component: PaymentComponent,canActivate: [AuthGuard]},
  {
    path: 'payment-success',
    component: PaymentSuccessComponent,
    canActivate: [AuthGuard]  // If you're using authentication
  },
  { path: 'payment_hist', component: PaymentHistComponent,canActivate: [AuthGuard] },
  { path: 'subscription-plan/:planId', component: SubscriptionPlanComponent, canActivate: [AuthGuard] },
  { path: 'pricing', component: PricingComponent },
  { path: '**', component: NotfoundComponent }, // Keep this as the last route
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
