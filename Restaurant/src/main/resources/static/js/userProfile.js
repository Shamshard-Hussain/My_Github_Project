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
            const price = parseFloat(this.closest('.food-item').querySelector('.food-price').textContent.replace('Rs ', '').replace('', ''));

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


document.addEventListener('DOMContentLoaded', function () {
    loadCart();

    document.getElementById('cart-button').addEventListener('click', function () {
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
                const quantityInputId = `quantity-input-${item.productId}`;

                row.innerHTML = `
                    <td id="productId" style="display: none;">${item.productId}</td> <!-- Hidden ID column -->
                    <td><img src="${item.image}" alt="${item.productName}" style="width: 50px; height: 50px;"></td>
                    <td id="productName">${item.productName}</td>
                    <td id="price-${item.productId}">${item.price.toFixed(2)}</td>
                    <td>
                        <div class="number-input-container">
                            <button class="spin-button decrement" data-product-id="${item.productId}">-</button>
                            <input type="text" id="${quantityInputId}" value="${item.quantity}" min="1" readonly>
                            <button class="spin-button increment" data-product-id="${item.productId}">+</button>
                        </div>
                    </td>
                    <td id="total-${item.productId}">${itemTotal.toFixed(2)}</td>
                    <td><button class="user-cancel-btn" data-product-id="${item.productId}">Remove</button></td>
                `;
                cartItemsBody.appendChild(row);
            });

            updateTotals();

            // Add event listeners to increment and decrement buttons
            document.querySelectorAll('.increment').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-product-id');
                    updateQuantity(productId, 1);
                });
            });

            document.querySelectorAll('.decrement').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-product-id');
                    updateQuantity(productId, -1);
                });
            });

            // Add event listeners to remove buttons
            document.querySelectorAll('.user-cancel-btn').forEach(button => {
                button.addEventListener('click', function () {
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
        currentQuantity += delta;
        input.value = currentQuantity;
        updateItemTotal(productId, currentQuantity);
        updateTotals();
    }
}

function updateItemTotal(productId, quantity) {
    const price = parseFloat(document.querySelector(`#price-${productId}`).textContent);
    const itemTotal = price * quantity;
    document.getElementById(`total-${productId}`).textContent = itemTotal.toFixed(2);
}

function removeFromCart(productId) {
    fetch(`/user/removeFromCart?productId=${productId}`, {
        method: 'DELETE'
    })
        .then(response => response.text())
        .then(data => {
            const row = document.querySelector(`#cart-items-body tr td#total-${productId}`).parentNode;
            if (row) {
                row.remove();
                updateTotals();
                updateCartCount();
            }
            alert(data);
        })
        .catch(error => console.error('Error removing item from cart:', error));
}

function updateTotals() {
    const rows = document.querySelectorAll('#cart-items-body tr');
    let subtotal = 0;

    rows.forEach(row => {
        const totalCell = row.querySelector('td[id^="total-"]');
        if (totalCell) {
            subtotal += parseFloat(totalCell.textContent);
        }
    });

    const tax = subtotal * 0; // Adjust tax calculation as needed
    const grandTotal = subtotal + tax;

    document.getElementById('subtotal').textContent = `Rs ${subtotal.toFixed(2)}`;
    document.getElementById('tax').textContent = `Rs ${tax.toFixed(2)}`;
    document.getElementById('grand-total').textContent = `Rs ${grandTotal.toFixed(2)}`;
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
                `;
                reservationBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching reservations:', error));
}

//remove all from cart
function removeAllFromCart(productId) {
    fetch(`/user/removeAllFromCart?productId=${productId}`, {
        method: 'DELETE'
    })
        .then(response => response.text())
        .then(data => {
            const row = document.querySelector(`#cart-items-body tr td#total-${productId}`).parentNode;
            if (row) {
                row.remove();
                updateTotals();
                updateCartCount();
            }

        })
        .catch(error => console.error('Error removing item from cart:', error));
}

// popup cart payment
document.addEventListener('DOMContentLoaded', function() {
    const checkoutBtn = document.querySelector('.user-checkout');
    const paymentPopup = document.getElementById('popup');
    const closePaymentPopup = document.getElementById('close-popup');
    const payBtn = document.querySelector('.cart-payment-checkout');
    const grandTotalElement = document.getElementById('grand-total');
    const amountInput = document.getElementById('amount');

    function isCartEmpty() {
        // Check if there are no rows in the cart
        const rows = document.querySelectorAll('#cart-items-body tr');
        return rows.length === 0;
    }

    function openPopup() {
        if (isCartEmpty()) {
            alert('Your cart is empty. Add items to the cart before proceeding to payment.');
            return; // Do not open the popup if the cart is empty
        }

        // Update the amount input with the grand total from the cart
        const grandTotal = grandTotalElement.textContent.trim().replace('Rs ', '');
        amountInput.value = `Rs ${grandTotal}`;

        // Show the payment popup
        paymentPopup.style.display = 'flex';
        document.body.classList.add('no-scroll');
    }

    function closePopup() {
        paymentPopup.style.display = 'none';
        document.body.classList.remove('no-scroll');
    }

    // Attach event listeners
    checkoutBtn.addEventListener('click', openPopup);
    closePaymentPopup.addEventListener('click', closePopup);
    payBtn.addEventListener('click', checkout);
});

