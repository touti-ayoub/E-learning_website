#!/bin/bash

# Create directories if they don't exist
mkdir -p src/assets/pptxjs
mkdir -p src/assets/jquery

# Download jQuery
curl -L https://code.jquery.com/jquery-3.6.0.min.js -o src/assets/jquery/jquery.min.js

# Download required libraries
curl -L https://cdnjs.cloudflare.com/ajax/libs/jszip/3.7.1/jszip.min.js -o src/assets/pptxjs/jszip.min.js
curl -L https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js -o src/assets/pptxjs/d3.min.js
curl -L https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.8.6/nv.d3.min.js -o src/assets/pptxjs/nv.d3.min.js

# Download PPTXjs specific files
curl -L https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/pptxjs.js -o src/assets/pptxjs/pptxjs.js
curl -L https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/divs2slides.js -o src/assets/pptxjs/divs2slides.js
curl -L https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/jquery.fullscreen-min.js -o src/assets/pptxjs/jquery.fullscreen-min.js
curl -L https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/filereader.js -o src/assets/pptxjs/filereader.js

echo "PPTXjs setup completed!" 