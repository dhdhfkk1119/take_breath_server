document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('feeChart');
    if (!canvas) return;

    const rawLabels = canvas.dataset.labels || '';
    const rawValues = canvas.dataset.values || '';

    const labels = rawLabels.split(',').filter(Boolean).reverse();
    const data = rawValues.split(',').filter(Boolean).map(Number).reverse();

    if (labels.length === 0) return;

    const ctx = canvas.getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: '월별 수수료 금액 (원)',
                data,
                backgroundColor: 'rgba(52, 152, 219, 0.7)',
                borderColor: 'rgba(41, 128, 185, 1)',
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (context) => context.parsed.y.toLocaleString() + '원'
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: value => value.toLocaleString() + '원'
                    }
                }
            }
        }
    });
});
