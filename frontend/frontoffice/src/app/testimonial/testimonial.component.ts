import { Component, AfterViewInit, ElementRef } from '@angular/core';

// Déclare jQuery globalement pour éviter les conflits avec Webpack
declare var $: any;

@Component({
  selector: 'app-testimonial',
  templateUrl: './testimonial.component.html',
  styleUrls: ['./testimonial.component.css']
})
export class TestimonialComponent implements AfterViewInit {
  constructor(private el: ElementRef) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      const carousel = $(this.el.nativeElement).find('.testimonial-carousel');

      if (carousel.length > 0 && typeof carousel.owlCarousel === 'function') {
        carousel.owlCarousel({
          loop: true,
          margin: 20,
          nav: true,
          dots: true,
          autoplay: true,
          autoplayTimeout: 3000,
          autoplayHoverPause: true,
          responsive: {
            0: { items: 1 },
            600: { items: 2 },
            1000: { items: 3 }
          }
        });
      } else {
        console.error('❌ Owl Carousel ne s\'est pas chargé correctement.');
      }
    }, 1000); // Délai pour s'assurer que jQuery est bien chargé
  }
}
