document.addEventListener('DOMContentLoaded', function() {
    const reportFilterCategory = document.getElementById('report-filterCategory');
    const reportFilterFields = document.getElementById('report-filterFields');
    const reportGridContainer = document.getElementById('paymentGridContainer');
    const generateReportButton = document.getElementById('generateReportButton');

    // Mock data for grid view example
    const mockData = [
        { title: "Reservations", description: "Details of Reservations Payment", type: "Reservation" },
        { title: "Orders", description: "Details of Orders Payment", type: "Food-Order" },
        { title: "Other", description: "Details of Other Payment", type: "Other" }
    ];

    // Function to populate the grid with mock data
    function populateGridWithMockData() {
        reportGridContainer.innerHTML = '';
        mockData.forEach(item => {
            const card = document.createElement('div');
            card.className = 'report-card';
            card.innerHTML = `
          <h3>${item.title}</h3>
          <p>${item.description}</p>
          <button class="report-card-btn" data-type="${item.type}">View Details</button>
        `;
            reportGridContainer.appendChild(card);
        });
        reportGridContainer.style.display = 'grid';
    }

    // Handle category change to show appropriate filter fields
    reportFilterCategory.addEventListener('change', function() {
        const category = reportFilterCategory.value;
        reportFilterFields.innerHTML = '';

        if (category === 'payments') {
            reportFilterFields.innerHTML = `
          <label for="report-paymentDateFrom">From Date:</label>
          <input type="date" id="report-paymentDateFrom" required>
          <label for="report-paymentDateTo">To Date:</label>
          <input type="date" id="report-paymentDateTo" required>
        `;
            reportGridContainer.style.display = 'none';

        } else if (category === 'reservations') {
            reportFilterFields.innerHTML = `
          <label for="report-reservationDateFrom">From Date:</label>
          <input type="date" id="report-reservationDateFrom" required>
          <label for="report-reservationDateTo">To Date:</label>
          <input type="date" id="report-reservationDateTo" required>
        `;
            reportGridContainer.style.display = 'none';

        } else if (category === 'products') {
            reportFilterFields.innerHTML = `
          <label for="report-productCategory">Category:</label>
          <select id="report-productCategory">
            <option value="All">All</option>
            <option value="Appetizers">Appetizers</option>
             <option value="Salads">Salads</option>
                <option value="Soup">Soup</option>
                <option value="Chicken">Chicken</option>
                <option value="Seafood">Seafood</option>
                <option value="Prawns">Prawns</option>
                <option value="Cuttlefish">Cuttlefish</option>
                <option value="Beef">Beef</option>
                <option value="Whole Fish">Whole Fish</option>
                <option value="Vegetable">Vegetable</option>
                <option value="Grilled Items">Grilled Items</option>
                <option value="Rice">Rice</option>
                <option value="Kottu">Kottu</option>
                <option value="Noodles">Noodles</option>
                <option value="Desserts">Desserts</option>
                <option value="Soft Drinks">Soft Drinks</option>
                <option value="Energy Drinks">Energy Drinks</option>
                <option value="Milkshake">Milkshake</option>
                <option value="Fresh Juice">Fresh Juice</option>
                <option value="Mocktail">Mocktail</option>
                <option value="Water">Water</option>
          </select>
        `;
            reportGridContainer.style.display = 'none';

        } else {
            reportGridContainer.style.display = 'none';
        }
    });

    // Handle report generation
    generateReportButton.addEventListener('click', function(event) {
        event.preventDefault();
        const category = reportFilterCategory.value;
        const filters = {};

        if (category === 'payments') {
            filters.dateFrom = document.getElementById('report-paymentDateFrom').value;
            filters.dateTo = document.getElementById('report-paymentDateTo').value;

            if (!filters.dateFrom || !filters.dateTo) {
                alert('Please select both date ranges.');
                return;
            }


            fetch('/admin/filterPayments', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(filters),
            })
                .then(response => response.json().then(data => ({ status: response.status, data })))
                .then(({ status, data }) => {
                    if (status === 200) {
                        if (data.length === 0) {
                            alert('No data available for the selected filters.');
                            reportGridContainer.style.display = 'none';
                        } else {
                            populateGridWithMockData();  // Only display mock data if records exist
                            reportGridContainer.style.display = 'grid';
                        }
                    } else {
                        alert('Error: ' + data);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while fetching data.');
                });

        } else if (category === 'reservations') {
            filters.dateFrom = document.getElementById('report-reservationDateFrom').value;
            filters.dateTo = document.getElementById('report-reservationDateTo').value;

            if (!filters.dateFrom || !filters.dateTo) {
                alert('Please select both date ranges.');
                return;
            }

            downloadExcel(filters, 'reservations');

        } else if (category === 'products') {
            filters.category = document.getElementById('report-productCategory').value;

            fetch('/admin/filterProducts', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(filters),
            })
                .then(response => response.json().then(data => ({ status: response.status, data })))
                .then(({ status, data }) => {
                    if (status === 200) {
                        if (data.length === 0) {
                            alert(`No data available for the selected category.`);
                        } else {
                            downloadExcel(filters, 'products');
                        }
                    } else {
                        alert('Error: ' + data);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('ANo data available for the selected category.');
                });
        }
    });

    // Function to download Excel
    function downloadExcel(filters, category) {
        const endpoint = category === 'reservations' ? '/admin/downloadReservationsExcel' :
            category === 'products' ? '/admin/downloadProductsExcel' :
                '/admin/downloadPaymentsExcel';



        fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(filters),
        })
            .then(response => {
                if (response.status === 204) {
                    alert('No data available for download.');
                    return;
                }
                return response.blob();
            })
            .then(blob => {
                if (blob) {
                    const url = window.URL.createObjectURL(new Blob([blob]));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute('download', `${category}.xlsx`);
                    document.body.appendChild(link);
                    link.click();
                    link.parentNode.removeChild(link);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while downloading the file.');
            });
    }


    // Handle view details button click to download Excel
    reportGridContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('report-card-btn')) {
            const type = event.target.getAttribute('data-type');
            const dateFrom = document.getElementById('report-paymentDateFrom')?.value;
            const dateTo = document.getElementById('report-paymentDateTo')?.value;

            // Check if date range inputs are available
            if (!dateFrom || !dateTo) {
                alert('Please select date ranges.');
                return;
            }

            const filters = { type, dateFrom, dateTo };

            fetch('/admin/downloadPaymentsExcel', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(filters),
            })
                .then(response => {
                    if (response.status === 204) {
                        alert('No data available for download.');
                        return;
                    }
                    return response.blob();
                })
                .then(blob => {
                    if (blob) {
                        const url = window.URL.createObjectURL(new Blob([blob]));
                        const link = document.createElement('a');
                        link.href = url;
                        link.setAttribute('download', `${type}.xlsx`);
                        document.body.appendChild(link);
                        link.click();
                        link.parentNode.removeChild(link);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while downloading the file.');
                });
        }
    });

    // Trigger change event to show the initial filter fields
    reportFilterCategory.dispatchEvent(new Event('change'));
});