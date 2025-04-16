import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { CoursesComponent } from './courses/courses.component';
import { OurTeamComponent } from './our-team/our-team.component';
import { TestimonialComponent } from './testimonial/testimonial.component';
import {AboutUsComponent} from "./about-us/about-us.component";
import {ContactComponent} from "./contact/contact.component";
<<<<<<< Updated upstream
=======
import {LoginComponent} from "./auth/login/login.component";
import {RegisterComponent} from "./auth/register/register.component";
import {SubscriptionComponent} from "./mic2/subscription/subscription.component";
import {AuthGuard} from "../services/auth/auth.guard";
import {ForumListComponent} from "./Communications/forum-list.component";
import {PostListComponent} from "./Communications/post-list.component";
import {AddPostComponent} from "./Communications/add-post/add-post.component";
import {UpdatePostComponent} from "./Communications/UpdatePost/update-post/update-post.component";
>>>>>>> Stashed changes

// Declare routes outside the class
const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'courses', component: CoursesComponent },
  { path: 'team', component: OurTeamComponent },
  { path: 'testemonial', component: TestimonialComponent },
  { path: 'aboutus', component: AboutUsComponent },
  { path: 'contact', component: ContactComponent },
<<<<<<< Updated upstream
=======
  {path: 'login', component: LoginComponent },
  {path: 'register', component: RegisterComponent },
  { path: 'subscription/:courseId', component: SubscriptionComponent, canActivate: [AuthGuard] },
  { path: 'forums', component: ForumListComponent },
  { path: 'forum/:id/posts', component: PostListComponent },
  { path: 'add-post', component: AddPostComponent },
{ path: 'update-post', component: UpdatePostComponent },


>>>>>>> Stashed changes
  { path: '**', component: NotfoundComponent }, // Keep this as the last route
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
