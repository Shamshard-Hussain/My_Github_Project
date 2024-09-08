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



// //Food menu
//     // Handle menu button clicks to filter products
//     const menuBtns = document.querySelectorAll('.menu-btn');
//     const foodItems = document.querySelectorAll('.food-item');
//
//     // Initially show all products or the default category
//     let activeCategory = 'Appetizers'; // or set this to any default category
//     showFoodMenu(activeCategory);
//
//     menuBtns.forEach((btn) => {
//     btn.addEventListener('click', () => {
//         resetActiveBtn();
//         activeCategory = btn.id;
//         showFoodMenu(activeCategory);
//         btn.classList.add('active-btn');
//     });
// });
//
//     function resetActiveBtn() {
//     menuBtns.forEach((btn) => {
//         btn.classList.remove('active-btn');
//     });
// }
//
//     function showFoodMenu(category) {
//     foodItems.forEach((item) => {
//         const itemCategory = item.querySelector('.category span').textContent.trim();
//         if (category === 'Appetizers' || itemCategory === category) {
//             item.style.display = "grid";
//         } else {
//             item.style.display = "none";
//         }
//     });
// }
//
//     // Handle the navigation arrows for category buttons
//     document.addEventListener("DOMContentLoaded", () => {
//     const btnContainer = document.querySelector(".menu-btns");
//     const btns = Array.from(btnContainer.children);
//     const leftArrow = document.querySelector(".left-arrow");
//     const rightArrow = document.querySelector(".right-arrow");
//     let startIndex = 0;
//     const visibleCount = 3;
//
//     // Function to update the visibility of the buttons
//     const updateButtons = () => {
//     btns.forEach((btn, index) => {
//     btn.style.display = (index >= startIndex && index < startIndex + visibleCount) ? "block" : "none";
// });
// };
//
//     // Event listeners for arrows
//     leftArrow.addEventListener("click", () => {
//     if (startIndex > 0) {
//     startIndex--;
//     updateButtons();
// }
// });
//
//     rightArrow.addEventListener("click", () => {
//     if (startIndex < btns.length - visibleCount) {
//     startIndex++;
//     updateButtons();
// }
// });
//
//     // Initial display update
//     updateButtons();
// });


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

document.querySelector(".payment form").addEventListener("submit", function(e) {
    e.preventDefault();

    // Transfer payment data to hidden fields in reservation form
    document.getElementById("cardHolderName").value = document.getElementById("PcardHolderName").value;
    document.getElementById("cardNumber").value = document.getElementById("PcardNumber").value;
    document.getElementById("expiryDate").value = document.getElementById("PexpiryDate").value;
    document.getElementById("cvc").value = document.getElementById("Pcvc").value;

    // Simulate successful payment (replace with actual payment logic)
    setTimeout(() => {
        alert("Reservation successful!"); // Display success message as an alert
        document.getElementById("reservation-form").submit();
    }, 1000); // Simulate a short delay for payment processing
});



//logout
function confirmLogout(event) {
    event.preventDefault(); // Prevent default link behavior

    // Confirm the logout
    if (confirm("Are you sure you want to logout?")) {
        // Redirect to log out URL using GET method
        window.location.href = '/logout';
    }
}
//payment Verify
function formatExpiryDate(input) {
    // Remove non-numeric characters except "/"
    input.value = input.value.replace(/[^\d\/]/g, '');

    // Automatically add "/" after the month part (MM)
    if (input.value.length > 2 && input.value[2] !== '/') {
        input.value = input.value.slice(0, 2) + '/' + input.value.slice(2);
    }

    // Ensure the input is no longer than "MM/YY" (5 characters)
    if (input.value.length > 5) {
        input.value = input.value.slice(0, 5);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const cardNumberInput = document.getElementById('PcardNumber');

    // Function to format card number with dashes
    function formatCardNumber(value) {
        // Remove non-digit characters
        value = value.replace(/\D/g, '');
        // Format the value with dashes
        return value.match(/.{1,4}/g)?.join('-') || '';
    }

    cardNumberInput.addEventListener('input', function(e) {
        // Get the current value and format it
        const formattedValue = formatCardNumber(e.target.value);
        // Set the formatted value back to the input
        e.target.value = formattedValue;
    });

    cardNumberInput.addEventListener('keypress', function(e) {
        const key = e.key;
        // Allow only digits and control keys (Backspace, Tab, etc.)
        if (!/\d/.test(key) && ![8, 9, 37, 39].includes(e.which)) {
            e.preventDefault();
        }
    });

    cardNumberInput.addEventListener('keydown', function(e) {
        // Prevent pasting non-digit characters
        if (e.ctrlKey && e.key === 'v') {
            e.preventDefault();
        }
    });
});

// // Show the container and disable background scrolling when the "Profile" link is clicked
// document.querySelector('#profile-button').addEventListener('click', (event) => {
//     event.preventDefault(); // Prevent default link behavior
//     document.querySelector('.profile-overlay').style.display = 'flex';
//     document.body.style.overflow = 'hidden'; // Disable background scrolling
//
//     // Fetch admin details and populate the profile container
//     fetch('/user/getDetails')
//         .then(response => response.json())
//         .then(data => {
//             if (data.firstName) {
//                 document.getElementById('firstName').textContent = data.firstName;
//                 document.getElementById('lastName').textContent = data.lastName;
//                 document.getElementById('userEmail').textContent = data.email;
//                 document.getElementById('contactNo').textContent = data.phone;
//             } else {
//                 console.error('User details not found');
//             }
//         })
//         .catch(error => console.error('Error fetching user details:', error));
//
//     // Fetch reservations and populate the reservations table
//     fetch('/user/getReservations')
//         .then(response => response.json())
//         .then(data => {
//             const reservationBody = document.getElementById('reservationBody');
//             reservationBody.innerHTML = ''; // Clear existing rows
//
//             data.forEach(reservation => {
//                 const row = document.createElement('tr');
//                 row.innerHTML = `
//               <td>${reservation.date}</td>
//               <td>${reservation.time}</td>
//               <td>${reservation.persons}</td>
//                <td>${reservation.status}</td>
//             <td><button class="profile-cancel" data-id="${reservation.id}">Cancel</button></td>
//             `;
//                 reservationBody.appendChild(row);
//             });
//         })
//     // .catch(error => console.error('Error fetching reservations:', error));
// });
//
// // Hide the container and re-enable background scrolling when the close button is clicked
// document.querySelector('.profile-close').addEventListener('click', () => {
//     document.querySelector('.profile-overlay').style.display = 'none';
//     document.body.style.overflow = ''; // Re-enable background scrolling
// });
//
// document.addEventListener('DOMContentLoaded', () => {
//     const tableContainer = document.querySelector('.table-container');
//
//     // Event delegation: Attach the event listener to the table container
//     tableContainer.addEventListener('click', function(event) {
//         if (event.target.classList.contains('profile-cancel')) {
//             const reservationId = event.target.getAttribute('data-id');
//             console.log("Cancel button clicked for reservation ID: " + reservationId);
//
//             if (confirm('Are you sure you want to cancel this reservation?')) {
//                 fetch(`/user/reservations/${reservationId}`, {
//                     method: 'DELETE',
//                     headers: {
//                         'Content-Type': 'application/json'
//                     }
//                 })
//                     .then(response => {
//                         if (response.ok) {
//                             event.target.closest('tr').remove();
//                         } else {
//                             alert('Failed to cancel reservation');
//                         }
//                     })
//                     .catch(error => {
//                         alert('Error cancelling reservation');
//                     });
//             }
//         }
//     });
// });

