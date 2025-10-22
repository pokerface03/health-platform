// Configuration
const API_BASE_URL = window.location.origin + '/vitals/vitals';
const REFRESH_INTERVAL = 30000; // 30 seconds
const HOURS_TO_FETCH = 24;


const tokenName = 'keycloakToken';


let charts = {};
let refreshTimer = null;

// API Client
class VitalsAPIClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    async getUserId() {
            try {
                const response = await fetch(`${this.baseUrl}`,{
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
                    }
                });
                if (!response.ok) throw new Error(`HTTP ${response.status}`);

            } catch (error) {
                console.error('Error with get user Id:', error);
                throw error;
            }
    }
    async getLatestVitals() {
        try {
            const response = await fetch(`${this.baseUrl}/latest`,{
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
                }
            });
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching latest vitals:', error);
            throw error;
        }
    }

    async getVitalsByMetric(metricName, hours = 24) {
        try {
            const now = new Date();
            const from = new Date(now.getTime() - hours * 60 * 60 * 1000);

            const params = new URLSearchParams({
                from: from.toISOString(),
                to: now.toISOString()
            });

            const response = await fetch(`${this.baseUrl}/${metricName}?${params}`,{
                headers: {
                     'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
                }
            });
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error(`Error fetching ${metricName}:`, error);
            throw error;
        }
    }

    async healthCheck() {
        try {
            const response = await fetch(`${this.baseUrl}/health`,{
              headers: {
                'Authorization': 'Bearer ' + localStorage.getItem(tokenName)
              }
            });
            return response.ok;
        } catch (error) {
            console.error('Health check failed:', error);
            return false;
        }
    }
}

// Initialize API client
const apiClient = new VitalsAPIClient(API_BASE_URL);

// UI Functions
function updateStatus(isHealthy) {
    const indicator = document.getElementById('statusIndicator');
    const statusText = document.getElementById('statusText');

    if (isHealthy) {
        indicator.classList.remove('disconnected');
        statusText.textContent = 'Connected';
    } else {
        indicator.classList.add('disconnected');
        statusText.textContent = 'Disconnected';
    }
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000);
}

// Load latest vitals and update stat cards
async function loadLatestVitals() {
    try {
        const vitals = await apiClient.getLatestVitals();

        // Create a map for easy lookup
        const vitalsMap = {};
        vitals.forEach(vital => {
            vitalsMap[vital.metric_name] = vital.value;
        });

        // Update stat cards
        document.getElementById('hrValue').textContent =
            vitalsMap['hr'] ? Math.round(vitalsMap['hr']) : '--';

        const bpSys = vitalsMap['bp_sys'] ? Math.round(vitalsMap['bp_sys']) : '--';
        const bpDia = vitalsMap['bp_dia'] ? Math.round(vitalsMap['bp_dia']) : '--';
        document.getElementById('bpValue').textContent = `${bpSys}/${bpDia}`;

        document.getElementById('glucoseValue').textContent =
            vitalsMap['glucose'] ? Math.round(vitalsMap['glucose']) : '--';

        document.getElementById('weightValue').textContent =
            vitalsMap['weight'] ? vitalsMap['weight'].toFixed(1) : '--';

        updateStatus(true);
    } catch (error) {
        console.error('Error loading latest vitals:', error);
        updateStatus(false);
        showError('Failed to load latest vitals. Check if Subscriber API is running on port 8082.');
    }
}

// Create a chart for a specific metric
async function createChart(canvasId, metricName, label, color) {
    try {
        const data = await apiClient.getVitalsByMetric(metricName, HOURS_TO_FETCH);

        const labels = data.map(item => {
            const date = new Date(item.timestamp);
            return date.toLocaleTimeString();
        });

        const values = data.map(item => item.value);

        const ctx = document.getElementById(canvasId).getContext('2d');

        // Destroy existing chart
        if (charts[canvasId]) {
            charts[canvasId].destroy();
        }

        charts[canvasId] = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: label,
                    data: values,
                    borderColor: color,
                    backgroundColor: color + '33',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 2,
                    pointHoverRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        ticks: {
                            callback: function(value) {
                                return Math.round(value);
                            }
                        }
                    },
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 45
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error(`Error creating chart for ${metricName}:`, error);
        showError(`Failed to load ${label} chart`);
    }
}

// Create blood pressure chart (dual line)
async function createBPChart() {
    try {
        const sysData = await apiClient.getVitalsByMetric('bp_sys', HOURS_TO_FETCH);
        const diaData = await apiClient.getVitalsByMetric('bp_dia', HOURS_TO_FETCH);

        const labels = sysData.map(item => {
            const date = new Date(item.timestamp);
            return date.toLocaleTimeString();
        });

        const ctx = document.getElementById('bpChart').getContext('2d');

        if (charts['bpChart']) {
            charts['bpChart'].destroy();
        }

        charts['bpChart'] = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Systolic',
                        data: sysData.map(item => item.value),
                        borderColor: '#ef4444',
                        backgroundColor: '#ef444433',
                        tension: 0.4,
                        pointRadius: 2,
                        pointHoverRadius: 5
                    },
                    {
                        label: 'Diastolic',
                        data: diaData.map(item => item.value),
                        borderColor: '#3b82f6',
                        backgroundColor: '#3b82f633',
                        tension: 0.4,
                        pointRadius: 2,
                        pointHoverRadius: 5
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    tooltip: {
                        mode: 'index',
                        intersect: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: false,
                        ticks: {
                            callback: function(value) {
                                return Math.round(value);
                            }
                        }
                    },
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 45
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error creating BP chart:', error);
        showError('Failed to load Blood Pressure chart');
    }
}

// Load all charts
async function loadAllCharts() {
    await Promise.all([
        createChart('hrChart', 'hr', 'Heart Rate (bpm)', '#ec4899'),
        createBPChart(),
        createChart('glucoseChart', 'glucose', 'Glucose (mg/dL)', '#f59e0b'),
        createChart('weightChart', 'weight', 'Weight (kg)', '#10b981')
    ]);
}

// Refresh all data
async function refreshAllData() {
    await loadLatestVitals();
    await loadAllCharts();
    await apiClient.getUserId();
}

// Initialize the dashboard
async function init() {

   //document.getElementById('calendarBtn').href = `http://localhost:8083/calendar/${currentUserId}`;

   const isHealthy = await apiClient.healthCheck();
   updateStatus(isHealthy);

    if (!isHealthy) {
         showError('Cannot connect to Subscriber API. Make sure it is running on port 8082.');
         return;
    }

    await refreshAllData();

    refreshTimer = setInterval(refreshAllData, REFRESH_INTERVAL);


}

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (refreshTimer) {
        clearInterval(refreshTimer);
    }
});

// Start the application when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('keycloakInitialized', init);
} else {
    init();
}