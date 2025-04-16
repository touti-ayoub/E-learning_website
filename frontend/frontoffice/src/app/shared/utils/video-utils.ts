/**
 * Transforms a YouTube, Vimeo, or other video URL into an embeddable URL format
 */
export function getEmbedUrl(url: string | undefined, type: string | undefined): string {
  if (!url) return '';
  
  switch (type?.toLowerCase()) {
    case 'youtube': {
      // Handle YouTube URLs
      // Convert various YouTube URL formats to embed format
      const youtubeRegExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
      const match = url.match(youtubeRegExp);
      
      if (match && match[2].length === 11) {
        return `https://www.youtube.com/embed/${match[2]}`;
      }
      return url; // Return original if not a valid YouTube URL
    }
    
    case 'vimeo': {
      // Handle Vimeo URLs
      const vimeoRegExp = /vimeo\.com\/(?:.*\/)?(?:videos\/)?([0-9]+)/;
      const match = url.match(vimeoRegExp);
      
      if (match) {
        return `https://player.vimeo.com/video/${match[1]}`;
      }
      return url; // Return original if not a valid Vimeo URL
    }
    
    case 'url':
    default:
      return url; // For other URLs, just return as is
  }
} 