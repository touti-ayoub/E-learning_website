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
import { PaymentComponent } from './mic2/payment/payment.component';
import { SubscriptionPlanComponent } from './mic2/subscription-plan/subscription-plan.component';
import { PricingComponent } from './mic2/pricing/pricing.component';
import { PaymentSuccessComponent } from './mic2/payment-success/payment-success.component';
import { PaymentHistComponent } from './mic2/payment-hist/payment-hist.component';
import { QuizCreateComponent } from './assessments/quiz-create/quiz-create.component';
import { QuizListComponent } from './assessments/quiz-list/quiz-list.component';
import { QuizTakeComponent } from './assessments/quiz-take/quiz-take.component';
import { QuizResultComponent } from './assessments/quiz-result/quiz-result.component';
import { CourseDetailComponent } from './course-detail/course-detail.component';
import { CourseAccessGuard } from './guards/course-access.guard';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { ExamListComponent } from './components/exam-list/exam-list.component';
import { ExamSubmitComponent } from './components/exam-submit/exam-submit.component';
import { CertificateViewComponent } from './examCertif/certificate-view/certificate-view.component';
import { ExamDetailComponent } from './examCertif/exam-detail/exam-detail.component';
import { StudentExamsComponent } from './components/student-exams/student-exams.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'courses', component: CoursesComponent },
  { 
    path: 'courses/:id', 
    component: CourseDetailComponent,
    canActivate: [CourseAccessGuard] // Add course access guard
  },
  { path: 'team', component: OurTeamComponent },
  { path: 'testemonial', component: TestimonialComponent },
  { path: 'aboutus', component: AboutUsComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'subscription/:courseId', component: SubscriptionComponent, canActivate: [AuthGuard] },
  { path: 'events', component: EventListComponent },
  { path: 'events/:id', component: EventDetailsComponent },

  { path: 'payment', component: PaymentComponent, canActivate: [AuthGuard] },
  {
    path: 'payment-success',
    component: PaymentSuccessComponent,
    canActivate: [AuthGuard]  // If you're using authentication
  },
  { path: 'payment_hist', component: PaymentHistComponent, canActivate: [AuthGuard] },
  { path: 'subscription-plan/:planId', component: SubscriptionPlanComponent, canActivate: [AuthGuard] },
  { path: 'pricing', component: PricingComponent },
  { path: 'quizzes/create', component: QuizCreateComponent, canActivate: [AuthGuard] }, // Restrict quiz creation
  { path: 'quizzes/list', component: QuizListComponent },
  { path: 'quiz-take/:id', component: QuizTakeComponent, canActivate: [AuthGuard] }, // Restrict quiz-taking
  { path: 'quiz-results/:quizId', component: QuizResultComponent },
  { path: 'chatbot', component: ChatbotComponent, canActivate: [AuthGuard] },

   // Exams-related routes
   { path: 'exams', component: ExamListComponent },
   { path: 'exams/:id', component: ExamDetailComponent },
   { path: 'exams/:id/submit', component: ExamSubmitComponent },
   { path: 'certificates/:id', component: CertificateViewComponent },
   { path: 'student/exams', component: StudentExamsComponent },
 
 
  { path: '**', component: NotfoundComponent }, // Keep this as the last route
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}