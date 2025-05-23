import { Quiz } from './models/quiz.model';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { NgOptimizedImage } from '@angular/common';
import { BannerComponent } from './banner/banner.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { ContactComponent } from './contact/contact.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { SubscriptionComponent } from './mic2/subscription/subscription.component';
import { EventListComponent } from './mic5/event-list/event-list.component';
import { EventDetailsComponent } from './mic5/event-details/event-details.component';
import {RegistrationService} from "../services/mic5/registration.service";
import { PaymentComponent } from './mic2/payment/payment.component';
import { PricingComponent } from './mic2/pricing/pricing.component';
import { SubscriptionPlanComponent } from './mic2/subscription-plan/subscription-plan.component';
import { PaymentSuccessComponent } from './mic2/payment-success/payment-success.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { PaymentHistComponent } from './mic2/payment-hist/payment-hist.component';
import { QuizListComponent } from './assessments/quiz-list/quiz-list.component';
import { QuizTakeComponent } from './assessments/quiz-take/quiz-take.component';
import { QuizCreateComponent } from './assessments/quiz-create/quiz-create.component';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { QuizResultComponent } from './assessments/quiz-result/quiz-result.component';
import { ExamListComponent } from './components/exam-list/exam-list.component';
import { ExamSubmitComponent } from './components/exam-submit/exam-submit.component';
import { ExamDetailComponent } from './examCertif/exam-detail/exam-detail.component';
import { CertificateViewComponent } from './examCertif/certificate-view/certificate-view.component';
import { StudentExamsComponent } from './components/student-exams/student-exams.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    BannerComponent,
    AboutUsComponent,
    CoursesComponent,
    OurTeamComponent,
    TestimonialComponent,
    NotfoundComponent,
    ContactComponent,
    RegisterComponent,
    LoginComponent,
    SubscriptionComponent,
    EventListComponent,
    EventDetailsComponent,
    PaymentComponent,
    PricingComponent,
    SubscriptionPlanComponent,
    PaymentSuccessComponent,
    PaymentHistComponent,
    QuizListComponent,
    QuizResultComponent,
    QuizTakeComponent,
    QuizCreateComponent,
    ChatbotComponent,
    ExamListComponent,
    ExamSubmitComponent,
    ExamDetailComponent,
    CertificateViewComponent,
    StudentExamsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
    ReactiveFormsModule
  ],
  providers: [RegistrationService],
  bootstrap: [AppComponent]
})
export class AppModule { }