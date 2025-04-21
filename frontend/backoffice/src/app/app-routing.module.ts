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
        path: 'exams', // Liste des examens
        loadComponent: () =>
          import('./examCertif/exam-list/exam-list.component').then((m) => m.ExamListComponent)
      },
      {
        path: 'exams/create', // Création d'un examen
        loadComponent: () =>
          import('./examCertif/exam-create/exam-create.component').then((m) => m.ExamCreateComponent)
      },
      {
        path: 'exams/grade/:id', // Attribution des notes
        loadComponent: () =>
          import('./examCertif/exam-grade/exam-grade.component').then((m) => m.ExamGradeComponent)
      }
      // Les autres routes déjà définies restent inchangées
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