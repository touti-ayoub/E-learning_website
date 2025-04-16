import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MaterialDTO {
  id: number;
  eventId: number;
  title: string;
  description: string;
  fileUrl: string;
  fileType: string;
}

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  private apiUrl = 'http://localhost:8081/mic5/materials';

  constructor(private http: HttpClient) {}

  getAllMaterials(): Observable<MaterialDTO[]> {
    return this.http.get<MaterialDTO[]>(this.apiUrl);
  }

  getMaterialById(id: number): Observable<MaterialDTO> {
    return this.http.get<MaterialDTO>(`${this.apiUrl}/${id}`);
  }

  createMaterial(material: MaterialDTO): Observable<MaterialDTO> {
    return this.http.post<MaterialDTO>(this.apiUrl, material);
  }

  updateMaterial(id: number, material: MaterialDTO): Observable<MaterialDTO> {
    return this.http.put<MaterialDTO>(`${this.apiUrl}/${id}`, material);
  }

  deleteMaterial(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
