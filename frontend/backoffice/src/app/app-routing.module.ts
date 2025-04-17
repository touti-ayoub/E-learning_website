import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './theme/layout/admin/admin.component';
import { GuestComponent } from './theme/layout/guest/guest.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: '',
        redirectTo: 'default',
        pathMatch: 'full'
      },
      {
        path: 'default',
        loadComponent: () =>
          import('./demo/dashboard/default/default.component').then((c) => c.DefaultComponent)
      },
      {
        path: 'typography',
        loadComponent: () =>
          import('./demo/elements/typography/typography.component')
      },
      {
        path: 'color',
        loadComponent: () =>
          import('./demo/elements/element-color/element-color.component')
      },
      {
        path: 'sample-page',
        loadComponent: () =>
          import('./demo/other/sample-page/sample-page.component')
      },
      // --------------------------------------------
      // STEP 3: Nest the "events" route under Admin
      // --------------------------------------------
      {
        path: 'events',
        loadComponent: () =>
          import('./Component/event-list/event-list.component').then((m) => m.EventListComponent)
      },
      // Course management component
      {
        path: 'courses',
        loadComponent: () =>
          import('./course-management/course-management.component').then((m) => m.CourseManagementComponent)
      },
      // Category management component
      {
        path: 'categories',
        loadComponent: () =>
          import('./category-management/category-management.component').then((m) => m.CategoryManagementComponent)
      },
      {
        path: 'quizzes',
        loadComponent: () =>
          import('./Component/assessments/quiz/quiz.component').then((m) => m.QuizComponent),
        children: [
          {
            path: 'questions',
            loadComponent: () =>
              import('./Component/assessments/quiz-question/quiz-question.component').then((m) => m.QuizQuestionComponent)
          },
          {
            path: 'results',
            loadComponent: () =>
              import('./Component/assessments/quiz-result/quiz-result.component').then((m) => m.QuizResultComponent)
          }
        ]
      },
      {
        path: 'quiz/list',
        loadComponent: () =>
          import('./Component/assessments/quiz-list/quiz-list.component').then((m) => m.QuizListComponent) // Standalone route
      },
      {
        path: 'coupon/create-coupon',
        loadComponent: () =>
          import('./Component/Payments/add-coupon/add-coupon.component').then((m) => m.AddCouponComponent)
      },
      {
        path: 'admin/pay_dashboard',
        loadComponent: () =>
          import('./Component/Payments/dashboard-summary/dashboard-summary.component').then((m) => m.DashboardSummaryComponent)
      }
    ]
  },
  {
    path: 'forum/list',
    loadComponent: () =>
      import('./Component/communication/forum/forum-list.component').then((m) => m.ForumListComponent)
  },
  {
    path: 'forum/:forumId/posts',
    loadComponent: () =>
      import('./Component/communication/post/post-list.component').then((m) => m.PostListComponent)
  },
  {
    path: 'add-forum',
    loadComponent: () =>
      import('./Component/communication/forum/add-forum.component').then((m) => m.AddForumComponent)
  },
  {
    path: 'edit-forum/:idForum',
    loadComponent: () =>
      import('./Component/communication/forum/edit-forum.component').then((m) => m.EditForumComponent)
  },
    
  {
    path: '',
    component: GuestComponent,
    children: [
      {
        path: 'guest',
        loadChildren: () =>
          import('./demo/pages/authentication/authentication.module').then((m) => m.AuthenticationModule)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
