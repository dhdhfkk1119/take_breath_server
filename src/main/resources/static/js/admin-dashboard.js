function initChart(stats) {
    const ctx = document.getElementById('statsChart');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['전체 회원', '전체 상담사', '활성 상담사', '승인 대기'],
            datasets: [{
                label: '통계',
                data: [
                    stats.totalMembers,
                    stats.totalCounselors,
                    stats.activeCounselors,
                    stats.pendingCounselors
                ],
                backgroundColor: [
                    'rgba(54, 162, 235, 0.8)',
                    'rgba(75, 192, 192, 0.8)',
                    'rgba(153, 102, 255, 0.8)',
                    'rgba(255, 159, 64, 0.8)'
                ],
                borderColor: [
                    'rgb(54, 162, 235)',
                    'rgb(75, 192, 192)',
                    'rgb(153, 102, 255)',
                    'rgb(255, 159, 64)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: '시스템 통계 현황',
                    font: { size: 18 }
                },
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}