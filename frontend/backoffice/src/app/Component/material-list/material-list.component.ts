// src/app/Component/material-list/material-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MaterialService, MaterialDTO } from '../../../service/mic5/material.service';
import { EventService } from '../../../service/mic5/event.service';

// Extended interface to include event title
interface MaterialWithEvent extends MaterialDTO {
  eventTitle?: string;
}

@Component({
  selector: 'app-material-list',
  templateUrl: './material-list.component.html',
  styleUrls: ['./material-list.component.scss'],
  standalone: true,
  imports: [CommonModule, MatIconModule]
})
export class MaterialListComponent implements OnInit {
  materials: MaterialWithEvent[] = [];
  errorMessage: string = '';

  constructor(
    private materialService: MaterialService,
    private eventService: EventService
  ) {}

  ngOnInit(): void {
    this.loadMaterials();
  }

  loadMaterials(): void {
    this.materialService.getAllMaterials().subscribe({
      next: (materials) => {
        // First get materials
        if (materials.length === 0) {
          this.materials = [];
          return;
        }

        // Then get all events to map titles
        this.eventService.getAllEvents().subscribe({
          next: (events) => {
            // Create map for quick lookups
            const eventMap = new Map<number, string>();
            events.forEach(event => {
              eventMap.set(event.eventId, event.title);
            });

            // Add event titles to materials
            this.materials = materials.map(material => ({
              ...material,
              eventTitle: eventMap.get(material.eventId) || 'Unknown Event'
            }));
          },
          error: (error) => {
            // Still show materials even if event data fails
            this.materials = materials.map(material => ({
              ...material,
              eventTitle: 'Event Data Unavailable'
            }));
            console.error('Error loading events:', error);
          }
        });
      },
      error: (error) => {
        this.errorMessage = 'Failed to load materials. Please try again later.';
        console.error('Error loading materials:', error);
      }
    });
  }

  deleteMaterial(material: MaterialDTO): void {
    if (confirm(`Are you sure you want to delete the material "${material.title}"?`)) {
      this.materialService.deleteMaterial(material.id).subscribe({
        next: () => {
          this.materials = this.materials.filter(m => m.id !== material.id);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete material. Please try again later.';
          console.error('Error deleting material:', error);
        }
      });
    }
  }
}
