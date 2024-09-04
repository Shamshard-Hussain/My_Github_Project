
function openModal() {
    document.getElementById("productModal").style.display = "block";
}

function closeModal() {
    document.getElementById("productModal").style.display = "none";
}





// Add product to table
// document.getElementById("productForm").addEventListener("submit", function (event) {
//     // Remove event.preventDefault() to allow form submission
//     const productName = document.getElementById("productName").value;
//     const productPrice = document.getElementById("productPrice").value;
//     const productCategory = document.getElementById("productCategory").value;
//     const description = document.getElementById("productDescription").value;
//     const productImage = document.getElementById("productImage").files[0];
//
//     // Display the image in the table (for local view only)
//     const reader = new FileReader();
//     reader.onload = function (e) {
//         const productTable = document.getElementById("productTable").getElementsByTagName("tbody")[0];
//         const newRow = productTable.insertRow();
//
//         const cell1 = newRow.insertCell(0);
//         const img = document.createElement("img");
//         img.src = e.target.result;
//         img.alt = productName;
//         img.style.maxWidth = "100px"; // Bootstrap default size can be adjusted
//         img.style.borderRadius = "8px";
//         cell1.appendChild(img);
//
//         const cell2 = newRow.insertCell(1);
//         cell2.textContent = productName;
//
//         const cell3 = newRow.insertCell(2);
//         cell3.textContent = `$${productPrice}`;
//
//         const cell4 = newRow.insertCell(3);
//         cell4.textContent = productCategory;
//
//         const cell5 = newRow.insertCell(4);
//         const editButton = document.createElement("a");
//         editButton.href = "#";
//         editButton.className = "btn btn-warning btn-sm";
//         editButton.textContent = "Edit";
//         cell5.appendChild(editButton);
//
//         const deleteButton = document.createElement("a");
//         deleteButton.href = "#";
//         deleteButton.className = "btn btn-danger btn-sm";
//         deleteButton.textContent = "Delete";
//         cell5.appendChild(deleteButton);
//     };
//     reader.readAsDataURL(productImage);
//
//     // Close modal after form submission
//     closeModal();
// });


// Mobile menu toggle
const mobile = document.querySelector('.menu-toggle');
const mobileLink = document.querySelector('.sidebar');
mobile.addEventListener("click", function () {
    mobile.classList.toggle("is-active");
    mobileLink.classList.toggle("active");
});

// Close menu when clicking on a menu item
mobileLink.addEventListener("click", function () {
    const menuBars = document.querySelector(".is-active");
    if (window.innerWidth <= 768 && menuBars) {
        mobile.classList.toggle("is-active");
        mobileLink.classList.toggle("active");
    }
});


document.addEventListener("DOMContentLoaded", function () {
    // Handle image file input change event
    document.getElementById('Image').addEventListener('change', function () {
        const file = this.files[0];
        const imagePreview = document.getElementById('ImagePreview');

        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block'; // Show the image preview
            };
            reader.readAsDataURL(file);
        } else {
            imagePreview.src = '';
            imagePreview.style.display = 'none'; // Hide the image preview if no file is selected
        }
    });
});



// Function to open & close the edit product modal
function closeEditProductModal() {
    document.getElementById('editProductModal').style.display = 'none';
}





function filterTable() {
    const input = document.getElementById('searchInput').value.toLowerCase();
    const table = document.getElementById('productTable');
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    let found = false;

    // Loop through all table rows except the last one (no results row)
    for (let i = 0; i < rows.length - 1; i++) {
        const cells = rows[i].getElementsByTagName('td');
        let match = false;

        // Check if any cell contains the search input
        for (let j = 0; j < cells.length; j++) {
            if (cells[j].textContent.toLowerCase().includes(input)) {
                match = true;
                break;
            }
        }

        // Show or hide row based on match
        if (match) {
            rows[i].style.display = '';
            found = true;
        } else {
            rows[i].style.display = 'none';
        }
    }

    // Handle "No results found" row
    const noResultsRow = document.getElementById('noResultsRow');
    noResultsRow.style.display = found ? 'none' : '';
}

//logout
function confirmLogout(event) {
    event.preventDefault(); // Prevent default link behavior

    // Confirm the logout
    if (confirm("Are you sure you want to logout?")) {
        // Redirect to log out URL using GET method
        window.location.href = '/logout';
    }
}

//Filter image card
function filterCard() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const galleryItems = document.querySelectorAll('.detail-card');

    galleryItems.forEach(item => {
        const heading = item.querySelector('.detail-name h4').textContent.toLowerCase();
        const id = item.getAttribute('data-id').toLowerCase();

        if (heading.includes(searchInput) || id.includes(searchInput)) {
            item.style.display = ''; // Show the item
        } else {
            item.style.display = 'none'; // Hide the item
        }
    });
}

// notification for admin
$(document).ready(function() {
    // Get query parameters from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get('message');
    const operation = urlParams.get('operation');

    // Display the modal based on the message and operation parameters
    if (message === 'success') {
        if (operation === 'add') {
            $('#modalMessage').text('Added successfully!');
        } else if (operation === 'update') {
            $('#modalMessage').text('Updated successfully!');
        } else {
            $('#modalMessage').text('Operation completed successfully!');
        }
        $('#messageModal').modal('show');
    } else if (message === 'duplicate') {
        if (operation === 'add') {
            $('#modalMessage').text('ID already exists in the database.');
        } else if (operation === 'update') {
            $('#modalMessage').text('Operation failed.');
        } else {
            $('#modalMessage').text('Operation failed.');
        }
        $('#messageModal').modal('show');
    }
    else if (message === 'error') {
        if (operation === 'add') {
            $('#modalMessage').text('Failed to add.');
        } else if (operation === 'update') {
            $('#modalMessage').text('Operation failed.');
        } else {
            $('#modalMessage').text('Operation failed.');
        }
        $('#messageModal').modal('show');
    }
    // Manually close the modal when the "OK" button is clicked
    $('#close').click(function() {
        $('#messageModal').modal('hide');
    });

});

// update image cards
document.addEventListener("DOMContentLoaded", function () {
    // Handle edit button click
    document.querySelectorAll('.detail-favorites').forEach(button => {
        button.addEventListener('click', function () {
            // Get the selected gallery item data
            const galleryItem = this.closest('.detail-card');
            const galleryId = galleryItem.getAttribute('data-id'); // Retrieve ID from data attribute
            const headingName = galleryItem.querySelector('.detail-name h4').textContent;
            const description = galleryItem.querySelector('.detail-sub').textContent;
            const imageUrl = galleryItem.querySelector('.detail-img').src;

            // Ensure ID is available
            if (galleryId) {
                // Set modal values
                document.getElementById('editGalleryId').value = galleryId;
                document.getElementById('editHeadingName').value = headingName;
                document.getElementById('editDescription').value = description;
                document.getElementById('editImagePreview').src = imageUrl;
                document.getElementById('editImagePreview').style.display = 'block';

                // Show the modal
                document.getElementById('editGalleryModal').style.display = 'block';
            } else {
                console.error("Gallery ID not found.");
            }
        });
    });

    // Close the modal
    document.getElementById('closeEditGalleryModal').addEventListener('click', function () {
        document.getElementById('editGalleryModal').style.display = 'none';
    });
});

//edit modal image preview
function previewImage(event) {
        const file = event.target.files[0];
        const imagePreview = document.getElementById('editImagePreview');

        if (file) {
            const reader = new FileReader();

            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block'; // Show the image preview
            };

            reader.readAsDataURL(file);
        } else {
            imagePreview.src = '';
            imagePreview.style.display = 'none'; // Hide the image preview if no file selected
        }
     }

<!-- Access Denied Message -->
function handleAccessDenied() {
    alert('Access denied: You do not have permission to view this page.');
    window.history.back(); // Go to the previous page
}

// function filterTable() {
//     // Get the search input value and convert it to lowercase
//     const searchInput = document.getElementById('searchInput').value.toLowerCase();
//
//     // Get all table rows
//     const tableRows = document.querySelectorAll('#productTable tbody tr');
//
//     // Loop through all rows
//     tableRows.forEach(row => {
//         // Get the text content of the row
//         const rowText = row.textContent.toLowerCase();
//
//         // Check if the row contains the search input text
//         if (rowText.includes(searchInput)) {
//             row.style.display = ''; // Show the row
//         } else {
//             row.style.display = 'none'; // Hide the row
//         }
//     });
// }
//
// // Optionally, add event listener to filter as you type
// document.getElementById('searchInput').addEventListener('input', filterTable);

