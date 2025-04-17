import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './theme/layout/admin/admin.component';
import { GuestComponent } from './theme/layout/guest/guest.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'guest/login',
    pathMatch: 'full'
  },
  {
    path: '',
    component: AdminComponent,
    children: [
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
        path: 'coupon/list',
        loadComponent: () =>
          import('./Component/Payments/coupon-list/coupon-list.component').then((m) => m.CouponListComponent)
      },
      {
        path: 'subs/list',
        loadComponent: () =>
          import('./Component/Payments/subscription-list/subscription-list.component').then((m) => m.SubscriptionListComponent)
      },{
        path: 'pay/list',
        loadComponent: () =>
          import('./Component/Payments/payment-list/payment-list.component').then((m) => m.PaymentListComponent)
      },
      {
        path: 'admin/pay_dashboard',
        loadComponent: () =>
          import('./Component/Payments/dashboard-summary/dashboard-summary.component').then((m) => m.DashboardSummaryComponent)
      }
    ]
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
