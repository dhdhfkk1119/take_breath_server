function initCharts(stats) {
    console.log('initCharts 함수 실행됨', stats);

    // 1. 막대 그래프 - 회원 및 상담사 현황
    const ctx1 = document.getElementById('statsChart');
    if (ctx1) {
        new Chart(ctx1, {
            type: 'bar',
            data: {
                labels: ['전체 회원', '활성 회원', '전체 상담사', '활성 상담사', '승인 대기'],
                datasets: [{
                    label: '통계',
                    data: [
                        stats.totalMembers,
                        stats.activeMembers,
                        stats.totalCounselors,
                        stats.activeCounselors,
                        stats.pendingCounselors
                    ],
                    backgroundColor: [
                        'rgba(52, 152, 219, 0.8)',
                        'rgba(46, 204, 113, 0.8)',
                        'rgba(155, 89, 182, 0.8)',
                        'rgba(26, 188, 156, 0.8)',
                        'rgba(243, 156, 18, 0.8)'
                    ],
                    borderColor: [
                        'rgb(52, 152, 219)',
                        'rgb(46, 204, 113)',
                        'rgb(155, 89, 182)',
                        'rgb(26, 188, 156)',
                        'rgb(243, 156, 18)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    // 2. 원형 차트 - 역할별 비율
    const ctx2 = document.getElementById('roleChart');
    if (ctx2) {
        new Chart(ctx2, {
            type: 'doughnut',
            data: {
                labels: ['일반 회원', '상담사', '관리자'],
                datasets: [{
                    data: [
                        stats.userCount,
                        stats.counselorCount,
                        stats.adminCount
                    ],
                    backgroundColor: [
                        'rgba(52, 152, 219, 0.8)',
                        'rgba(155, 89, 182, 0.8)',
                        'rgba(231, 76, 60, 0.8)'
                    ],
                    borderColor: [
                        'rgb(52, 152, 219)',
                        'rgb(155, 89, 182)',
                        'rgb(231, 76, 60)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }
}