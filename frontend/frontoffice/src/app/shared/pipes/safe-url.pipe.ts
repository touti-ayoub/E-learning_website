import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Pipe({
  name: 'safeUrl',
  standalone: true
})
export class SafeUrlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(url: string | undefined): SafeResourceUrl {
    if (!url) return this.sanitizer.bypassSecurityTrustResourceUrl('');
    
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
} 