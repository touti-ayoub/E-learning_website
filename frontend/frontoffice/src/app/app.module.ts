import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import {NgOptimizedImage} from "@angular/common";
import { BannerComponent } from './banner/banner.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { ContactComponent } from './contact/contact.component';
<<<<<<< Updated upstream

=======
import {HttpClientModule} from "@angular/common/http";
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import {FormsModule} from "@angular/forms";
import { SubscriptionComponent } from './mic2/subscription/subscription.component';
import { ForumListComponent } from './Communications/forum-list.component';
import { PostListComponent } from './Communications/post-list.component';
import { ForumService } from './../services/Communications/forum.sevice';
import { PostService } from './../services/Communications/post.service';
import { AddPostComponent } from './Communications/add-post/add-post.component';
import { UpdatePostComponent } from './Communications/UpdatePost/update-post/update-post.component';
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    ContactComponent
=======
    ContactComponent,
    RegisterComponent,
    LoginComponent,
    SubscriptionComponent,
    ForumListComponent,
    PostListComponent,
    AddPostComponent,
    UpdatePostComponent
>>>>>>> Stashed changes
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
<<<<<<< Updated upstream
    NgOptimizedImage
=======
    NgOptimizedImage,
    HttpClientModule, // Add this line
    FormsModule,

>>>>>>> Stashed changes
  ],
  providers: [ForumService, PostService],
  bootstrap: [AppComponent]
})
export class AppModule { }
