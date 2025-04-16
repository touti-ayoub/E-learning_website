import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-pptx-viewer',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  template: `
    <div class="pptx-container">
      <!-- Loading state -->
      <div *ngIf="loading" class="text-center p-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">{{ loadingMessage }}</p>
      </div>
      
      <!-- Server-side converted presentation view -->
      <div *ngIf="!loading && conversionStatus === 'COMPLETED'" class="embed-responsive">
        <iframe 
          [src]="getViewerUrl()" 
          class="embed-responsive-item" 
          frameborder="0" 
          width="100%" 
          height="600"
          allowfullscreen>
        </iframe>
      </div>
      
      <!-- Conversion in progress -->
      <div *ngIf="!loading && conversionStatus === 'PENDING'" class="alert alert-info">
        <div class="text-center">
          <div class="spinner-border text-primary mb-3" role="status">
            <span class="visually-hidden">Converting...</span>
          </div>
          <h5>Converting Presentation</h5>
          <p>The presentation is being processed. This may take a few moments...</p>
        </div>
      </div>
      
      <!-- Conversion failed -->
      <div *ngIf="!loading && conversionStatus === 'FAILED'" class="alert alert-danger">
        <div class="text-center">
          <i class="fas fa-exclamation-triangle fa-3x mb-3"></i>
          <h5>Conversion Failed</h5>
          <p>Sorry, we couldn't convert this presentation. Trying alternative viewer...</p>
          <div *ngIf="!alternativeViewerUrl" class="mt-3">
            <div class="spinner-border spinner-border-sm me-2" role="status"></div>
            Loading alternative viewer...
          </div>
        </div>
      </div>
      
      <!-- Alternative viewer -->
      <div *ngIf="!loading && conversionStatus === 'FAILED' && alternativeViewerUrl" class="embed-responsive mt-3">
        <iframe 
          [src]="alternativeViewerUrl" 
          class="embed-responsive-item" 
          frameborder="0" 
          width="100%" 
          height="600"
          allowfullscreen>
        </iframe>
      </div>
      
      <!-- Not converted yet with conversion button -->
      <div *ngIf="!loading && !conversionStatus" class="local-file-viewer">
        <div class="viewer-header mb-3">
          <h4 class="m-0">Presentation Preview</h4>
          <p class="text-muted small mb-0">Uploaded presentation</p>
        </div>
        
        <div class="preview-container">
          <div class="preview-placeholder">
            <!-- PowerPoint SVG Icon -->
            <svg class="pptx-icon mb-3" xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 384 512">
              <!-- PowerPoint Icon Background -->
              <path fill="#D24726" d="M64 0C28.7 0 0 28.7 0 64V448c0 35.3 28.7 64 64 64H320c35.3 0 64-28.7 64-64V160H256c-17.7 0-32-14.3-32-32V0H64zM256 0V128H384L256 0z"/>
              <!-- PowerPoint Icon Letter P -->
              <path fill="#FFFFFF" d="M112 192H176c35.3 0 64 28.7 64 64v32c0 35.3-28.7 64-64 64H144v64c0 8.8-7.2 16-16 16s-16-7.2-16-16V208c0-8.8 7.2-16 16-16zm32 128h32c17.7 0 32-14.3 32-32v-32c0-17.7-14.3-32-32-32H144v96z"/>
            </svg>
            <h5>PowerPoint Presentation</h5>
            <p class="text-muted mb-4">This presentation needs to be converted for viewing in the browser.</p>
            
            <button (click)="startConversion()" class="btn btn-primary" [disabled]="conversionInProgress">
              <span *ngIf="!conversionInProgress">View Presentation</span>
              <span *ngIf="conversionInProgress">
                <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Converting...
              </span>
            </button>
          </div>
        </div>
        
        <div class="text-center mt-3">
          <a [href]="downloadUrl" class="btn btn-outline-secondary" download>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download me-2" viewBox="0 0 16 16">
              <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5"/>
              <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708z"/>
            </svg>
            Download Instead
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .pptx-container {
      width: 100%;
      min-height: 200px;
      background-color: #f8f9fa;
      border-radius: 5px;
      padding: 15px;
      margin-bottom: 20px;
    }
    .embed-responsive {
      position: relative;
      width: 100%;
      height: 0;
      padding-bottom: 60%;
    }
    .embed-responsive-item {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
    .local-file-viewer {
      border: 1px solid #ddd;
      border-radius: 4px;
      padding: 20px;
      background-color: white;
    }
    .viewer-header {
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
    }
    .preview-container {
      min-height: 300px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #fafafa;
      border-radius: 4px;
      margin: 20px 0;
    }
    .preview-placeholder {
      text-align: center;
      padding: 30px;
    }
    .pptx-icon {
      display: block;
      margin: 0 auto 15px;
    }
  `]
})
export class PptxViewerComponent implements OnInit {
  @Input() presentationUrl: string = '';
  @Input() isBase64: boolean = false;
  @Input() lessonId: number = 0;
  
  loading: boolean = true;
  conversionStatus: string | null = null;
  convertedUrl: SafeResourceUrl | null = null;
  alternativeViewerUrl: SafeResourceUrl | null = null;
  downloadUrl: string = '';
  loadingMessage: string = 'Loading presentation...';
  conversionInProgress: boolean = false;
  private pollingInterval: any = null;
  private apiBaseUrl = environment.apiBaseUrl || 'http://localhost:8088/api';
  
  constructor(
    private sanitizer: DomSanitizer,
    private http: HttpClient
  ) {}
  
  ngOnInit(): void {
    this.downloadUrl = `${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/download`;
    
    setTimeout(() => {
      this.checkConversionStatus();
    }, 500);
  }
  
  ngOnDestroy(): void {
    // Clear any polling intervals when component is destroyed
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }
  
  startConversion(): void {
    this.conversionInProgress = true;
    
    this.http.post<any>(`${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/convert`, {}).subscribe({
        next: (response) => {
          console.log('Conversion started:', response);
          if (response.success) {
            this.loadingMessage = 'Converting presentation...';
            this.loading = true;
            
            // Start polling for status
            this.startStatusPolling();
          } else {
            console.error('Failed to start conversion:', response.message);
            this.conversionInProgress = false;
          }
        },
        error: (err) => {
          console.error('Error starting conversion:', err);
          this.conversionInProgress = false;
        }
      });
  }
  
  getViewerUrl(): SafeResourceUrl {
    if (this.convertedUrl) {
      return this.convertedUrl;
    }
    
    if (this.alternativeViewerUrl) {
      return this.alternativeViewerUrl;
    }
    
    // If conversion failed, try using Google Docs Viewer
    const rawUrl = `${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/raw`;
    const googleDocsUrl = `https://docs.google.com/viewer?url=${encodeURIComponent(rawUrl)}&embedded=true`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(googleDocsUrl);
  }
  
  private checkConversionStatus(): void {
    this.http.get<any>(`${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/status`).subscribe({
      next: (response) => {
        console.log('Conversion status:', response);
        this.conversionStatus = response.status;
        
        if (response.status === 'COMPLETED') {
          const viewUrl = `${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/view`;
          this.convertedUrl = this.sanitizer.bypassSecurityTrustResourceUrl(viewUrl);
          this.loading = false;
          
          if (this.pollingInterval) {
            clearInterval(this.pollingInterval);
            this.pollingInterval = null;
          }
        } else if (response.status === 'PENDING') {
          this.conversionStatus = 'PENDING';
          this.loading = false;
          
          if (!this.pollingInterval) {
            this.startStatusPolling();
          }
        } else if (response.status === 'FAILED') {
          this.conversionStatus = 'FAILED';
          this.loading = false;
          
          // Try alternative viewer
          this.setupAlternativeViewer();
          
          if (this.pollingInterval) {
            clearInterval(this.pollingInterval);
            this.pollingInterval = null;
          }
        } else {
          this.conversionStatus = null;
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Error checking conversion status:', err);
        this.loading = false;
        this.conversionStatus = 'FAILED';
        this.setupAlternativeViewer();
      }
    });
  }
  
  private setupAlternativeViewer(): void {
    // Try using Google Docs Viewer as fallback
    const rawUrl = `${this.apiBaseUrl}/lessons/${this.lessonId}/presentation/raw`;
    const googleDocsUrl = `https://docs.google.com/viewer?url=${encodeURIComponent(rawUrl)}&embedded=true`;
    this.alternativeViewerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(googleDocsUrl);
  }
  
  private startStatusPolling(): void {
    // Clear any existing interval
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
    
    // Start a new polling interval
    this.pollingInterval = setInterval(() => {
      console.log('Polling for conversion status...');
      this.checkConversionStatus();
    }, 3000);
    
    // Set a maximum polling time (2 minutes)
    setTimeout(() => {
      if (this.pollingInterval) {
        console.log('Maximum polling time reached, stopping polls');
        clearInterval(this.pollingInterval);
        this.pollingInterval = null;
        
        // If still loading or pending, show error
        if (this.loading || this.conversionStatus === 'PENDING') {
          this.loading = false;
          this.conversionStatus = 'FAILED';
        }
      }
    }, 120000);
  }
  
  /**
   * Injects CSS into the iframe to handle missing images gracefully
   */
  private injectImageErrorHandling(): void {
    try {
      // Get the iframe element
      const iframe = document.querySelector('iframe');
      if (!iframe || !iframe.contentWindow) return;
      
      // Attempt to access iframe content (may fail due to CORS)
      try {
        const iframeDoc = iframe.contentWindow.document;
        
        // Create a style element
        const style = iframeDoc.createElement('style');
        style.textContent = `
          img {
            max-width: 100%;
            height: auto;
          }
          img::before {
            content: "";
            display: block;
            width: 100%;
            height: 100px;
            background-color: #f0f0f0;
          }
          img::after {
            content: "Image not available";
            display: block;
            font-style: italic;
            color: #888;
            text-align: center;
            padding: 10px;
          }
        `;
        
        // Append style to the head
        iframeDoc.head.appendChild(style);
        
        console.log('Image error handling CSS injected');
      } catch (e) {
        console.warn('Could not access iframe content due to CORS restrictions', e);
      }
    } catch (e) {
      console.error('Error injecting image error handling', e);
    }
  }
} 