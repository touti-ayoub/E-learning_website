import { Component, AfterViewInit, ElementRef } from '@angular/core';

// Déclarer jQuery globalement
declare var $: any;

@Component({
  selector: 'app-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.css']
})
export class BannerComponent implements AfterViewInit {
  constructor(private el: ElementRef) {}

  ngAfterViewInit(): void {
    // Vérifier si jQuery et Owl Carousel sont bien chargés
    setTimeout(() => {
      const carousel = $(this.el.nativeElement).find('.header-carousel'); // Modifier ici pour cibler la bonne classe

      if (carousel.length > 0 && typeof carousel.owlCarousel === 'function') {
        carousel.owlCarousel({
          loop: true, // Active la boucle infinie
          margin: 10, // Espace entre les éléments
          nav: true, // Afficher les flèches de navigation
          dots: true, // Afficher les indicateurs
          autoplay: true, // Activer le défilement automatique
          autoplayTimeout: 3000, // Temps entre chaque slide (en ms)
          autoplayHoverPause: true, // Pause au survol
          responsive: {
            0: { items: 1 }, // 1 élément affiché sur mobile
            600: { items: 1 }, // 1 élément affiché sur tablette
            1000: { items: 1 } // 1 élément affiché sur grand écran
          }
        });
      } else {
        console.error('❌ Owl Carousel n\'est pas chargé correctement.');
      }
    }, 1000); // Petit délai pour être sûr que jQuery est bien chargé
  }
}
