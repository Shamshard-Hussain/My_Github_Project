/// Cart
document.addEventListener('DOMContentLoaded', function() {
    // Initial load of the cart count
    updateCartCount();

    // Add event listeners to all cart buttons
    const cartButtons = document.querySelectorAll('.cart-btn');

    cartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            const productName = this.closest('.food-item').querySelector('.food-name').textContent;
            let image = this.closest('.food-item').querySelector('img').src;
            image = decodeURIComponent(image); // Decode the URL
            const price = parseFloat(this.closest('.food-item').querySelector('.food-price').textContent.replace('Rs ', '').replace('0', ''));

            // First, check if the product is already in the cart
            fetch('/user/isProductInCart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'productId': productId
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.exists) {
                        alert('Product is already in the cart.');
                    } else {
                        // If the product is not in the cart, proceed to add it
                        return fetch('/user/addToCart', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: new URLSearchParams({
                                'productId': productId,
                                'productName': productName,
                                'image': image,
                                'price': price
                            })
                        });
                    }
                })
                .then(response => {
                    if (response) {
                        return response.json(); // Parse the response from addToCart
                    }
                })
                .then(data => {
                    if (data && data.message) {
                        alert(data.message); // Show the response message
                        updateCartCount(); // Update cart count after adding the product
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An unexpected error occurred. Please try again.');
                });
        });
    });

    // Function to update the cart item count
    function updateCartCount() {
        fetch('/user/cart', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(cartItems => {
                let itemCount = 0;
                cartItems.forEach(item => {
                    itemCount += item.quantity;
                });
                document.querySelector('.cart-count').textContent = itemCount;
            })
            .catch(error => {
                console.error('Error fetching cart items:', error);
            });
    }
});


document.addEventListener('DOMContentLoaded', function() {
    // Initial load of the cart
    loadCart();

    // Add event listener to the cart button
    document.getElementById('cart-button').addEventListener('click', function() {
        loadCart();
    });
});

function loadCart() {
    fetch('/user/cart', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(cartItems => {
            const cartItemsBody = document.getElementById('cart-items-body');
            cartItemsBody.innerHTML = ''; // Clear the table body

            let subtotal = 0;
            let itemCount = 0;

            cartItems.forEach(item => {
                const itemTotal = item.price * item.quantity;
                subtotal += itemTotal;
                itemCount += item.quantity;

                const row = document.createElement('tr');
                const quantityInputId = `quantity-input-${item.productId}`; // Unique ID for each quantity input

                row.innerHTML = `
                <td><img src="${item.image}" alt="${item.productName}" style="width: 50px; height: 50px;"></td>
                <td>${item.productName}</td>
                <td>${item.price.toFixed(2)}</td>
                <td>
                    <div class="number-input-container">
                        <button class="spin-button decrement" data-product-id="${item.productId}">-</button>
                        <input type="number" id="${quantityInputId}" value="${item.quantity}" min="1">
                        <button class="spin-button increment" data-product-id="${item.productId}">+</button>
                    </div>
                </td>
                <td id="total-${item.productId}">${itemTotal.toFixed(2)}</td>
                <td><button class="user-cancel-btn" data-product-id="${item.productId}">Remove</button></td>
            `;
                cartItemsBody.appendChild(row);
            });

            // Update the cart item count
            document.querySelector('.cart-count').textContent = itemCount;

            // Calculate and update totals
            updateTotals();

            // Add event listeners to the increment and decrement buttons
            document.querySelectorAll('.increment').forEach(button => {
                button.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    updateQuantity(productId, 1);
                });
            });

            document.querySelectorAll('.decrement').forEach(button => {
                button.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    updateQuantity(productId, -1);
                });
            });

            // Add event listeners to the remove buttons
            document.querySelectorAll('.user-cancel-btn').forEach(button => {
                button.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    removeFromCart(productId);
                });
            });
        })
        .catch(error => {
            console.error('Error fetching cart items:', error);
        });
}

function updateQuantity(productId, delta) {
    const input = document.getElementById(`quantity-input-${productId}`);
    let currentQuantity = parseInt(input.value, 10);

    if (delta > 0 || currentQuantity > 1) {
        input.value = currentQuantity + delta;
        updateItemTotal(productId);
        updateTotals();
    }
}

function updateItemTotal(productId) {
    const input = document.getElementById(`quantity-input-${productId}`);
    const quantity = parseInt(input.value, 10);
    const price = parseFloat(document.querySelector(`#cart-items-body tr td:nth-child(3)`).textContent.replace('', ''));
    const itemTotal = price * quantity;
    const row = document.querySelector(`#cart-items-body tr td#total-${productId}`);
    if (row) {
        row.textContent = `${itemTotal.toFixed(2)}`;
    }
}

function updateTotals() {
    const rows = document.querySelectorAll('#cart-items-body tr');
    let subtotal = 0;

    rows.forEach(row => {
        const totalCell = row.querySelector('td[id^="total-"]');
        if (totalCell) {
            subtotal += parseFloat(totalCell.textContent.replace('Rs ', ''));
        }
    });

    const tax = subtotal * 0;
    const grandTotal = subtotal + tax;

    document.getElementById('subtotal').textContent = `Rs ${subtotal.toFixed(2)}`;
    document.getElementById('tax').textContent = `Rs ${tax.toFixed(2)}`;
    document.getElementById('grand-total').textContent = `Rs ${grandTotal.toFixed(2)}`;
}

function removeFromCart(productId) {
    const row = document.querySelector(`#cart-items-body tr td#total-${productId}`).parentNode;
    if (row) {
        row.remove();
        updateTotals();
        // Update the cart item count after removal
        updateCartCount();
    }
}

function updateCartCount() {
    fetch('/user/cart', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(cartItems => {
            let itemCount = 0;
            cartItems.forEach(item => {
                itemCount += item.quantity;
            });
            document.querySelector('.cart-count').textContent = itemCount;
        })
        .catch(error => {
            console.error('Error fetching cart items for count update:', error);
        });
}



// Function to remove item from cart
function removeFromCart(productId) {
    fetch(`/user/removeFromCart?productId=${productId}`, {
        method: 'DELETE'
    })
        .then(response => response.text())
        .then(data => {
            alert(data); // Show response message
            location.reload(); // Reload the page to update the cart
        })
        .catch(error => console.error('Error removing item from cart:', error));
}


document.addEventListener('DOMContentLoaded', () => {
    const cartButton = document.getElementById('cart-button');
    const profileButton = document.getElementById('profile-button');
    const userPopup = document.getElementById('user-popup');
    const userPopupClose = document.querySelector('.user-popup-close');
    const userTabs = document.querySelectorAll('.user-tab');
    const userSections = document.querySelectorAll('.user-section');

    const showPopup = (targetSection) => {
        userPopup.style.display = 'flex';
        userTabs.forEach(tab => {
            tab.classList.remove('active');
            if (tab.getAttribute('data-target') === targetSection) {
                tab.classList.add('active');
            }
        });
        userSections.forEach(section => {
            section.classList.remove('active');
            if (section.id === targetSection) {
                section.classList.add('active');
            }
        });

        // Load content based on the active tab
        if (targetSection === 'user-profile') {
            loadUserProfile();
        } else if (targetSection === 'user-reservations') {
            loadUserReservations();
        }
    };

    cartButton.addEventListener('click', () => {
        showPopup('user-cart');
    });

    profileButton.addEventListener('click', () => {
        showPopup('user-profile');
    });

    userPopupClose.addEventListener('click', () => {
        userPopup.style.display = 'none';
    });

    // Handle tab switching within the popup
    userTabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            const targetSection = e.target.getAttribute('data-target');
            showPopup(targetSection);
        });
    });
});

function loadUserProfile() {
    fetch('/user/getDetails')
        .then(response => response.json())
        .then(data => {
            document.getElementById('user-name').textContent = `${data.firstName} ${data.lastName}`;
            document.getElementById('user-email').textContent = data.email;
            document.getElementById('user-phone').textContent = data.phone;
        })
        .catch(error => console.error('Error fetching user details:', error));
}

function loadUserReservations() {
    fetch('/user/getReservations')
        .then(response => response.json())
        .then(data => {
            const reservationBody = document.getElementById('reservationBody');
            reservationBody.innerHTML = ''; // Clear existing rows

            data.forEach(reservation => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${reservation.id}</td>
                    <td>${reservation.date}</td>
                    <td>${reservation.time}</td>
                    <td>${reservation.status}</td>
                    <td><button class="user-cancel-btn" data-id="${reservation.id}">Cancel</button></td>
                `;
                reservationBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching reservations:', error));
}


document.addEventListener('DOMContentLoaded', () => {
    const reservationsContainer = document.querySelector('.reservations-container');

    // Event delegation: Attach the event listener to the reservations container
    reservationsContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('user-cancel-btn')) {
            const reservationId = event.target.closest('tr').querySelector('td').innerText; // Assuming the reservation ID is in the first cell
            console.log("Cancel button clicked for reservation ID: " + reservationId);

            if (confirm('Are you sure you want to cancel this reservation?')) {
                fetch(`/user/reservations/${reservationId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            event.target.closest('tr').remove();
                            alert('Reservation canceled successfully.');
                        } else {
                            alert('Failed to cancel reservation');
                        }
                    })
                    .catch(error => {
                        alert('Error cancelling reservation');
                    });
            }
        }
    });
});

