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
      {
        path: 'events',
        loadComponent: () =>
          import('./Component/event-list/event-list.component').then((m) => m.EventListComponent)
      },
      {
        path: 'feedbacks',
        loadComponent: () =>
          import('./Component/feedback-list/feedback-list.component').then((m) => m.FeedbackListComponent)
      },
      {
        path: 'registrations',
        loadComponent: () =>
          import('./Component/registration-list/registration-list.component').then((m) => m.RegistrationListComponent)
      },
      {
        path: 'calendar',
        loadComponent: () =>
          import('./Component/calendar/calendar.component').then((m) => m.CalendarComponent)
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
