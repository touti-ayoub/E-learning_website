import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { PointHistoryComponent } from './point-history/point-history.component';
import { MaterialModule } from 'src/app/shared/material.modul';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'history',
    pathMatch: 'full'
  },
  {
    path: 'history',
    component: PointHistoryComponent
  }
];

@NgModule({
  declarations: [
    PointHistoryComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule.forChild(routes)
  ]
})
export class PointModule { }