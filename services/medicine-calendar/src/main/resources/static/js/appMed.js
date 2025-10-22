    // Configuration
    const API_BASE_URL = window.location.origin +'/calendar/calendar';
    const USER_ID = 'Patient01'; // Change this to your actual user ID or get from URL/session

    const tokenName = 'keycloakToken';
    let currentWeekOffset = 0;
    let calendarData = null;

    // Initialize
    document.addEventListener('keycloakInitialized', () => {
        loadCalendar();
        setupEventListeners();
    });

    function setupEventListeners() {
        document.getElementById('prevWeek').addEventListener('click', () => {
            currentWeekOffset--;
            loadCalendar();
        });

        document.getElementById('currentWeek').addEventListener('click', () => {
            currentWeekOffset = 0;
            loadCalendar();
        });

        document.getElementById('nextWeek').addEventListener('click', () => {
            currentWeekOffset++;
            loadCalendar();
        });

        document.getElementById('addMedicineForm').addEventListener('submit', handleAddMedicine);
    }

    async function loadCalendar() {
        try {
            const response = await fetch(`${API_BASE_URL}?week=${currentWeekOffset}`,{
              headers: {
                'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
              }
            });

            if (!response.ok) {
                throw new Error('Failed to load calendar data');
            }

            calendarData = await response.json();
            renderCalendar();
            renderMedicineList();
        } catch (error) {
            console.error('Error loading calendar:', error);
            showError('Failed to load calendar. Please try again.');
        }
    }

    function renderCalendar() {
        const { weekRange, weekDays, timeSlots, medicineSchedule, today } = calendarData;

        // Update week header
        document.getElementById('weekHeader').textContent = weekRange;

        // Render table header
        const thead = document.getElementById('calendarHead');
        thead.innerHTML = `
            <tr>
                <th>Day</th>
                ${timeSlots.map(slot => `<th>${slot}</th>`).join('')}
            </tr>
        `;

        // Render table body
        const tbody = document.getElementById('calendarBody');
        tbody.innerHTML = weekDays.map(day => {
            const isToday = day === today;
            const dateObj = new Date(day);
            const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'long' });
            const dateFormatted = dateObj.toLocaleDateString('en-GB');

            return `
                <tr class="${isToday ? 'today-row' : ''}">
                    <td class="day-col">
                        <strong>${dayName}</strong><br>
                        <span>${dateFormatted}</span>
                    </td>
                    ${timeSlots.map(slot => {
                        const medicines = medicineSchedule[day]?.[slot] || [];
                        return `
                            <td>
                                ${medicines.map(med => `
                                    <div class="medicine-note">
                                        <strong>${med.name}</strong>
                                        <small>${med.dosage}</small>
                                    </div>
                                `).join('')}
                            </td>
                        `;
                    }).join('')}
                </tr>
            `;
        }).join('');
    }

    function renderMedicineList() {
        const { allMedicines } = calendarData;
        const container = document.getElementById('medicineListContainer');

        if (allMedicines.length === 0) {
            container.innerHTML = '<p>No medicines added yet.</p>';
            return;
        }

        container.innerHTML = allMedicines.map(medicine => {
            const startDate = new Date(medicine.start_date).toLocaleDateString('en-GB');
            const endDate = medicine.end_date ? new Date(medicine.end_date).toLocaleDateString('en-GB') : null;
            const time = medicine.time.substring(0, 5); // Format HH:mm

            return `
                <div class="medicine-item">
                    <div class="medicine-info">
                        <strong>${medicine.name}</strong> - <span>${medicine.dosage}</span><br>
                        <small>
                            ${medicine.frequency} at ${time} |
                            From ${startDate}
                            ${endDate ? `to ${endDate}` : ''}
                        </small>
                        ${medicine.notes ? `<br><small>Notes: ${medicine.notes}</small>` : ''}
                    </div>
                    <button class="btn btn-danger" onclick="deleteMedicine(${medicine.id})">Delete</button>
                </div>
            `;
        }).join('');
    }

    async function handleAddMedicine(e) {
        e.preventDefault();

        const formData = {
            name: document.getElementById('name').value,
            dosage: document.getElementById('dosage').value,
            frequency: document.getElementById('frequency').value,
            time: document.getElementById('time').value,
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value,
            notes: document.getElementById('notes').value
        };

        try {
            const response = await fetch(`${API_BASE_URL}/add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error('Failed to add medicine');
            }

            // Reset form and reload calendar
            document.getElementById('addMedicineForm').reset();
            await loadCalendar();
            alert('Medicine added successfully!');
        } catch (error) {
            console.error('Error adding medicine:', error);
            alert('Failed to add medicine. Please try again.');
        }
    }

    async function deleteMedicine(medicineId) {
        if (!confirm('Are you sure you want to delete this medicine?')) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/delete/${medicineId}`, {
                method: 'DELETE',
                 headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
                 }
            });

            if (!response.ok) {
                throw new Error('Failed to delete medicine');
            }

            await loadCalendar();
            alert('Medicine deleted successfully!');
        } catch (error) {
            console.error('Error deleting medicine:', error);
            alert('Failed to delete medicine. Please try again.');
        }
    }

    function showError(message) {
        const container = document.querySelector('.container');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error';
        errorDiv.textContent = message;
        container.insertBefore(errorDiv, container.firstChild);
    }