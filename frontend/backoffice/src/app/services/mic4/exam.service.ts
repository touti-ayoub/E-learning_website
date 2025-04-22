import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Exam } from '../../../model/mic4/exam.model';

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private apiUrl = `${environment.apiUrl}/api/exams`;

  constructor(private http: HttpClient) {}

  private cleanJsonResponse(text: string): string {
    console.log('Original response:', text);
    
    // First, try to parse the response as is
    try {
      const parsed = JSON.parse(text);
      console.log('Successfully parsed original response');
      return text;
    } catch (error) {
      console.log('Initial parse failed, starting cleaning process');
    }

    // Remove any extra closing braces at the end
    let cleaned = text.replace(/}\s*]+$/, '}]');
    
    // Fix certificate data by replacing it with null
    cleaned = cleaned.replace(/"certificate":\s*{[^}]*}\s*}+/g, '"certificate": null');
    
    // Remove any remaining extra closing braces
    cleaned = cleaned.replace(/}\s*}+/g, '}');
    
    // Ensure proper array format
    if (!cleaned.startsWith('[')) {
      cleaned = '[' + cleaned;
    }
    if (!cleaned.endsWith(']')) {
      cleaned = cleaned + ']';
    }

    // Final validation
    try {
      const testParse = JSON.parse(cleaned);
      console.log('Successfully parsed cleaned response');
      return cleaned;
    } catch (error) {
      console.error('Failed to parse cleaned response:', error);
      console.error('Cleaned response:', cleaned);
      throw new Error('Failed to clean JSON response');
    }
  }

  getAllExams(): Observable<Exam[]> {
    return this.http.get(this.apiUrl, { 
      responseType: 'text',
      observe: 'response',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    }).pipe(
      map(response => {
        if (!response.body) {
          throw new Error('Empty response from server');
        }

        console.log('Raw response body:', response.body);
        const cleanedResponse = this.cleanJsonResponse(response.body);
        console.log('Cleaned response:', cleanedResponse);
        
        try {
          const parsedData = JSON.parse(cleanedResponse);
          if (!Array.isArray(parsedData)) {
            throw new Error('Response is not an array');
          }
          console.log('Parsed data:', parsedData);
          return parsedData as Exam[];
        } catch (error) {
          console.error('Error parsing exam data:', error);
          throw new Error(`Failed to parse exam data: ${error.message}`);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('HTTP Error:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          error: error.error
        });
        return throwError(() => new Error(`Failed to fetch exams: ${error.message}`));
      })
    );
  }

  createExam(formData: FormData): Observable<Exam> {
    return this.http.post<Exam>(this.apiUrl, formData);
  }

  assignScore(examId: number, score: number): Observable<Exam> {
    console.log('Assigning score:', { examId, score });
    
    const scoreData = { score };
    console.log('Score data being sent:', scoreData);

    return this.http.post<Exam>(`${this.apiUrl}/${examId}/score`, scoreData, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error assigning score:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          error: error.error
        });
        
        // If the score was actually saved (status 500 but operation succeeded)
        if (error.status === 500) {
          console.log('Score might have been saved despite 500 error');
          // Return a success response with the score
          return of({
            id: examId,
            score: score,
            // Add other required fields with default values
            title: '',
            description: '',
            date: new Date().toISOString(),
            userId: 0
          } as Exam);
        }
        
        return throwError(() => new Error(error.error?.message || 'Failed to assign score'));
      })
    );
  }

  getExamById(id: number): Observable<Exam> {
    console.log('Fetching exam by ID:', id);
    return this.http.get<Exam>(`${this.apiUrl}/${id}`, {
      headers: {
        'Accept': 'application/json'
      }
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching exam:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          error: error.error
        });
        return throwError(() => new Error(error.error?.message || 'Failed to fetch exam'));
      })
    );
  }

  downloadFile(fileUrl: string): Observable<Blob> {
    if (!fileUrl) {
      console.error('No file URL provided to downloadFile');
      return throwError(() => new Error('URL du fichier manquante'));
    }

    // Nettoyer l'URL du fichier
    let cleanFileUrl = fileUrl.trim();
    
    // Si c'est un chemin Windows, extraire juste le nom du fichier
    if (cleanFileUrl.includes('\\')) {
      const parts = cleanFileUrl.split('\\');
      cleanFileUrl = parts[parts.length - 1];
    }
    
    // Nettoyer le nom du fichier des caractères invalides
    cleanFileUrl = cleanFileUrl.replace(/[\[\]]/g, '');
    
    // Utiliser l'endpoint de téléchargement
    const downloadUrl = `${environment.apiUrl}/api/exams/download/${cleanFileUrl}`;
    console.log('Attempting to download file from URL:', downloadUrl);
    
    return this.http.get(downloadUrl, {
      responseType: 'blob',
      headers: {
        'Accept': 'application/octet-stream'
      },
      observe: 'response'
    }).pipe(
      map(response => {
        if (!response.body) {
          throw new Error('Réponse vide du serveur');
        }
        return response.body;
      }),
      catchError((error: any) => {
        console.error('Detailed error in downloadFile:', {
          error,
          status: error?.status,
          statusText: error?.statusText,
          url: error?.url || downloadUrl,
          message: error?.message,
          errorText: error?.error instanceof Blob ? 'Blob error' : error?.error
        });

        let errorMessage = 'Erreur lors du téléchargement du fichier';
        if (error?.status === 404) {
          errorMessage = `Fichier non trouvé sur le serveur. URL: ${downloadUrl}`;
        } else if (error?.status === 403) {
          errorMessage = 'Accès non autorisé au fichier';
        } else if (error?.status === 500) {
          errorMessage = 'Erreur serveur lors du téléchargement';
        } else if (error?.status === 0) {
          errorMessage = 'Erreur CORS ou connexion impossible au serveur';
        }

        return throwError(() => new Error(errorMessage));
      })
    );
  }
} 