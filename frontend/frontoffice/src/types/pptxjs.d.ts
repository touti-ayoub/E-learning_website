import 'jquery';

declare global {
    interface JQuery {
        pptxToHtml(options: {
            pptxFileUrl: string;
            slidesScale?: string;
            slideMode?: boolean;
            keyBoardShortCut?: boolean;
            slideModeConfig?: {
                first?: number;
                nav?: boolean;
                navTxtColor?: string;
                navBgColor?: string;
                showPlayPauseBtn?: boolean;
                showSlideNum?: boolean;
                showTotalSlideNum?: boolean;
                autoSlide?: boolean;
                randomAutoSlide?: boolean;
                loop?: boolean;
                background?: string;
                transition?: string;
                transitionSpeed?: number;
            };
        }): JQuery;
    }
} 