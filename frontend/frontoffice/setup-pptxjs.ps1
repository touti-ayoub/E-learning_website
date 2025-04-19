# Create directories if they don't exist
New-Item -ItemType Directory -Force -Path src/assets/pptxjs
New-Item -ItemType Directory -Force -Path src/assets/jquery

# Download jQuery
Invoke-WebRequest -Uri "https://code.jquery.com/jquery-3.6.0.min.js" -OutFile "src/assets/jquery/jquery.min.js"

# Download required libraries
Invoke-WebRequest -Uri "https://cdnjs.cloudflare.com/ajax/libs/jszip/3.7.1/jszip.min.js" -OutFile "src/assets/pptxjs/jszip.min.js"
Invoke-WebRequest -Uri "https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js" -OutFile "src/assets/pptxjs/d3.min.js"
Invoke-WebRequest -Uri "https://cdnjs.cloudflare.com/ajax/libs/nvd3/1.8.6/nv.d3.min.js" -OutFile "src/assets/pptxjs/nv.d3.min.js"

# Download PPTXjs specific files
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/pptxjs.js" -OutFile "src/assets/pptxjs/pptxjs.js"
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/divs2slides.js" -OutFile "src/assets/pptxjs/divs2slides.js"
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/jquery.fullscreen-min.js" -OutFile "src/assets/pptxjs/jquery.fullscreen-min.js"
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/meshesha/PPTXjs/master/js/filereader.js" -OutFile "src/assets/pptxjs/filereader.js"

Write-Host "PPTXjs setup completed!" 