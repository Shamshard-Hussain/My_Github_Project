//Header
document.addEventListener('DOMContentLoaded', function () {
    const menuBtn = document.querySelector('.home-menu-btn');
    const navigation = document.querySelector('.navigation');
    const navLinks = document.querySelectorAll('.navigation-items a'); // Select all navigation links

    menuBtn.addEventListener('click', function () {
        menuBtn.classList.toggle('active');
        navigation.classList.toggle('active');
    });

    navLinks.forEach(link => {
        link.addEventListener('click', function () {
            navigation.classList.remove('active'); // Hide the navigation
            menuBtn.classList.remove('active'); // Reset the menu button state
        });
    });
});





document.addEventListener("DOMContentLoaded", () => {
    // Handle menu button clicks to filter products
    const menuBtns = document.querySelectorAll('.menu-btn');
    const foodItems = document.querySelectorAll('.food-item');

    // Initially show the first category (Appetizers or the first in the list)
    if (menuBtns.length > 0) {
        let activeCategory = menuBtns[0].id; // Automatically set the first button as active
        showFoodMenu(activeCategory);
        menuBtns[0].classList.add('active-btn'); // Highlight the first button
    }

    menuBtns.forEach((btn) => {
        btn.addEventListener('click', () => {
            resetActiveBtn();
            activeCategory = btn.id;
            showFoodMenu(activeCategory);
            btn.classList.add('active-btn');
        });
    });

    function resetActiveBtn() {
        menuBtns.forEach((btn) => {
            btn.classList.remove('active-btn');
        });
    }

    function showFoodMenu(category) {
        foodItems.forEach((item) => {
            const itemCategory = item.querySelector('.category span').textContent.trim();
            if (category === itemCategory) {
                item.style.display = "grid";
            } else {
                item.style.display = "none";
            }
        });
    }

    // Handle the navigation arrows for category buttons
    const btnContainer = document.querySelector(".menu-btns");
    const btns = Array.from(btnContainer.children);
    const leftArrow = document.querySelector(".left-arrow");
    const rightArrow = document.querySelector(".right-arrow");
    let startIndex = 0;
    const visibleCount = 3;

    // Function to update the visibility of the buttons
    const updateButtons = () => {
        btns.forEach((btn, index) => {
            btn.style.display = (index >= startIndex && index < startIndex + visibleCount) ? "block" : "none";
        });
    };

    // Event listeners for arrows
    leftArrow.addEventListener("click", () => {
        if (startIndex > 0) {
            startIndex--;
            updateButtons();
        }
    });

    rightArrow.addEventListener("click", () => {
        if (startIndex < btns.length - visibleCount) {
            startIndex++;
            updateButtons();
        }
    });

    // Initial display update
    updateButtons();
});

document.addEventListener('DOMContentLoaded', function () {
    const viewButtons = document.querySelectorAll('.view-btn');
    const modal = document.getElementById('descriptionModal');
    const closeBtn = document.querySelector('.product-close-btn');
    const modalProductDescription = document.getElementById('modalProductDescription');

    viewButtons.forEach(button => {
        button.addEventListener('click', function () {
            const productDescription = this.getAttribute('data-description');

            modalProductDescription.textContent = productDescription;
            modal.style.display = 'block';
        });
    });

    closeBtn.addEventListener('click', function () {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function (event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    });
});













// contact Us popup
function togglePopup() {
    var popup = document.getElementById("contact-popup");
    popup.classList.toggle("show");

    if (popup.classList.contains("show")) {
        document.body.style.overflow = "hidden"; // Disable scrolling on the body
    } else {
        document.body.style.overflow = ""; // Re-enable scrolling on the body
    }
}






//service card
document.addEventListener('DOMContentLoaded', function () {
    let currentIndex = 0;
    const slides = document.querySelectorAll('.slide');
    const slider = document.querySelector('.slider');

    function showNextSlide() {
        currentIndex = (currentIndex + 1) % slides.length;
        slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }

    setInterval(showNextSlide, 3000);
});


//Home Slider
    document.addEventListener('DOMContentLoaded', () => {
    const slides = document.querySelectorAll('.image-slide');
    const contentBlocks = document.querySelectorAll('.home-content');
    const navBtns = document.querySelectorAll('.nav-btn');
    let currentIndex = 0;

    function showSlide(index) {
    slides.forEach((slide, i) => {
    slide.classList.toggle('active', i === index);
});
    contentBlocks.forEach((block, i) => {
    block.classList.toggle('active', i === index);
});
    navBtns.forEach((btn, i) => {
    btn.classList.toggle('active', i === index);
});
}

    navBtns.forEach((btn, index) => {
    btn.addEventListener('click', () => {
    currentIndex = index;
    showSlide(currentIndex);
});
});

    // Automatic slide change
    setInterval(() => {
    currentIndex = (currentIndex + 1) % slides.length;
    showSlide(currentIndex);
}, 5000); // Change slide every 5 seconds
});

//Location

    $(".location-slider").owlCarousel({
    loop: true,
    autoplay: true,
    autoplayTimeout: 3000, //2000ms = 2s;
    autoplayHoverPause: true,
});


    //image cards
let allImages = []; // Store all available image filenames
let currentImages = []; // Store currently displayed images
const imagesToShow = 6; // Number of images to display at once

async function fetchImagesFromServer() {
    try {
        const response = await fetch('/api/images'); // Your API endpoint
        if (!response.ok) {
            throw new Error('Network response was not ok.');
        }
        return await response.json(); // Array of all image filenames
    } catch (error) {
        console.error('Error fetching images:', error);
        return [];
    }
}

function getRandomImages() {
    let shuffled = allImages.slice().sort(() => 0.5 - Math.random());
    return shuffled.slice(0, imagesToShow);
}

function updateGallery(images) {
    currentImages = images;
    const galleryContainer = document.querySelector('.gallery-container');
    galleryContainer.innerHTML = '';
    currentImages.forEach((image, index) => {
        const galleryItem = document.createElement('div');
        galleryItem.classList.add('gallery-item');
        galleryItem.setAttribute('data-index', index);

        const img = document.createElement('img');
        img.setAttribute('src', `/assets/${image}`); // Ensure this path is correct

        galleryItem.appendChild(img);
        galleryContainer.appendChild(galleryItem);
    });

    const galleryItems = document.querySelectorAll('.gallery-item');
    galleryItems.forEach(item => {
        item.addEventListener('click', function () {
            showLightBox(parseInt(this.getAttribute('data-index')));
        });
    });
}

function showLightBox(n) {
    if (n >= currentImages.length) {
        n = 0;
    } else if (n < 0) {
        n = currentImages.length - 1;
    }
    const imageLocation = `/assets/${currentImages[n]}`;
    document.getElementById('lightbox-image').setAttribute('src', imageLocation);
    document.querySelector('.lightbox').style.display = 'block';
    disableScroll(); // Disable scrollbar
}

function slideImage(n) {
    showLightBox((currentImages.indexOf(document.getElementById('lightbox-image').getAttribute('src').split('/').pop())) + n);
}

document.querySelector('.lightbox-prev').addEventListener('click', () => slideImage(-1));
document.querySelector('.lightbox-next').addEventListener('click', () => slideImage(1));
document.querySelector('.lightbox').addEventListener('click', (event) => {
    if (event.target === document.querySelector('.lightbox')) {
        document.querySelector('.lightbox').style.display = 'none';
        enableScroll(); // Enable scrollbar
    }
});

document.addEventListener('DOMContentLoaded', async function () {
    allImages = await fetchImagesFromServer(); // Fetch all available images
    updateGallery(getRandomImages()); // Show 6 random images

    setInterval(() => {
        updateGallery(getRandomImages()); // Change images every 5 seconds (adjust as needed)
    }, 5000); // Change interval time if needed
});

// Function to disable the scrollbar
function disableScroll() {
    document.body.style.overflow = 'hidden';
}

// Function to enable the scrollbar
function enableScroll() {
    document.body.style.overflow = '';

}


//Disable previous dates
document.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('inputdate');
    const today = new Date();
    const day = String(today.getDate()).padStart(2, '0');
    const month = String(today.getMonth() + 1).padStart(2, '0'); // January is 0
    const year = today.getFullYear();

    const currentDate = `${year}-${month}-${day}`;

    dateInput.value = currentDate; // Set default value to today's date
    dateInput.min = currentDate; // Disable previous dates
});

//set the time
document.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('inputdate');
    const timeInput = document.getElementById('inputtime');

    // Function to set the minimum time for today
    function setMinTime() {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const minTime = `${hours}:${minutes}`;

        // Set the minimum time attribute
        timeInput.setAttribute('min', minTime);
    }

    // Set min time on date change
    dateInput.addEventListener('change', function() {
        const selectedDate = new Date(dateInput.value);
        const today = new Date();

        if (selectedDate.toDateString() === today.toDateString()) {
            setMinTime();
        } else {
            // No min time restriction for future dates
            timeInput.removeAttribute('min');
        }
    });

    // Initialize on page load
    setMinTime();
});

//Reservation popup
function openPopup() {
    event.preventDefault();
    document.getElementById("reservation-popup").style.display = "flex";
    document.body.style.overflow = "hidden"; // Disable scrolling on the body
    return false; // Prevent default form submission
}

function closePopup() {
    document.getElementById("reservation-popup").style.display = "none";
    document.body.style.overflow = ""; // Re-enable scrolling on the body
}




///---contact-us----///
document.getElementById('contactForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Prevent the default form submission

    const formData = new FormData(this);

    try {
        const response = await fetch('/user/contact', {
            method: 'POST',
            body: formData,
        });

        const result = await response.text();
        alert(result);

        // Clear the form data
        this.reset();

        // Optional: Close the popup after form submission
        togglePopup();
    } catch (error) {
        alert('An error occurred while sending your message. Please try again later.');
    }
});

