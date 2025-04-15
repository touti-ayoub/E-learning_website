import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { NgOptimizedImage } from "@angular/common";
import { BannerComponent } from './banner/banner.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { ContactComponent } from './contact/contact.component';
import { HttpClientModule } from "@angular/common/http";
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { SubscriptionComponent } from './mic2/subscription/subscription.component';
import { ChallengeListComponent } from './gamification/challenge/challenge-list/challenge-list.component';

// Services
import { ChallengeService } from './services/gamification/challenge/challenge-service.service';
import { UserChallengeService } from './services/gamification/userchallenge/user-challenge-service.service';
import { MaterialModule } from './shared/material.modul';
import { CreateChallengeComponent } from './gamification/challenge/create-challenge/create-challenge.component';

// Modules
import { PointModule } from './gamification/point/point.module';

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
    ChallengeListComponent,
    CreateChallengeComponent
    // Supprimé PointHistoryComponent car il est déjà déclaré dans PointModule
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    NgOptimizedImage,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    PointModule,
    MaterialModule
  ],
  providers: [
    ChallengeService,
    UserChallengeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }