let memberGrowthChart = null;

function initCharts(stats) {
    console.log('initCharts 함수 실행됨', stats);

    // 막대 그래프 - 회원 및 상담사 현황
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
                maintainAspectRatio: false,
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

    // 원형 차트 - 역할별 비율
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
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // 회원 증가 추이 차트 초기화
    initMemberGrowthChart(6);

    // 기간 선택 버튼 이벤트 리스너
    const chartButtons = document.querySelectorAll('.chart-btn');
    chartButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 버튼 활성화 상태 변경
            chartButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            // 차트 업데이트
            const period = parseInt(this.dataset.period);
            initMemberGrowthChart(period);
        });
    });
}

/**
 * 회원 증가 추이 차트 초기화
 * @param {number} months - 표시할 개월 수 (6, 12, 24)
 */
function initMemberGrowthChart(months) {
    const ctx = document.getElementById('memberGrowthChart');
    if (!ctx) return;

    // 기존 차트가 있으면 제거
    if (memberGrowthChart) {
        memberGrowthChart.destroy();
    }

    // 실제 데이터 사용 (백엔드에서 전달받은 데이터)
    // 데이터가 없으면 샘플 데이터 생성
    const chartData = window.stats?.memberGrowthData;
    if (!chartData || !chartData.labels || !chartData.data) {
            console.warn("⚠️ 회원 증가 데이터가 없습니다. 차트를 표시하지 않습니다.");
            return;
        }


    // 차트 생성
    memberGrowthChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: [{
                label: '총 회원수',
                data: chartData.data,
                borderColor: 'rgb(52, 152, 219)',
                backgroundColor: 'rgba(52, 152, 219, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 5,
                pointHoverRadius: 8,
                pointBackgroundColor: 'rgb(52, 152, 219)',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointHoverBackgroundColor: 'rgb(52, 152, 219)',
                pointHoverBorderColor: '#fff',
                pointHoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: {
                            size: 13,
                            weight: '500'
                        },
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    cornerRadius: 6,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return '총 회원수: ' + context.parsed.y.toLocaleString() + '명';
                        },
                        afterLabel: function(context) {
                            if (context.dataIndex > 0) {
                                const currentValue = context.parsed.y;
                                const previousValue = context.dataset.data[context.dataIndex - 1];
                                const increase = currentValue - previousValue;
                                const percentage = ((increase / previousValue) * 100).toFixed(1);
                                return `증가: +${increase.toLocaleString()}명 (${percentage}%)`;
                            }
                            return null;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        font: {
                            size: 12
                        },
                        callback: function(value) {
                            return value.toLocaleString() + '명';
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)',
                        drawBorder: false
                    }
                },
                x: {
                    ticks: {
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        display: false,
                        drawBorder: false
                    }
                }
            }
        }
    });
}

/**
 * 회원 증가 추이 데이터 생성 (샘플 데이터)
 * 실제 구현 시 서버에서 실제 데이터를 받아와야 합니다.
 * @param {number} months - 개월 수
 * @returns {Object} - labels와 data를 포함한 객체
 */
function generateMemberGrowthData(months) {
    const labels = [];
    const data = [];

    // 현재 회원수를 기준으로 역산
    const currentMembers = window.stats?.totalMembers || 1000;

    // 평균 월별 증가율 설정 (5~15%)
    const avgGrowthRate = 0.08;

    const now = new Date();
    let memberCount = currentMembers;

    // 과거부터 현재까지의 데이터 생성
    const dataPoints = [];
    for (let i = months - 1; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
        const label = date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'short'
        });

        labels.push(label);

        // 역산하여 과거 회원수 계산
        if (i === 0) {
            // 현재 달은 실제 회원수 사용
            dataPoints.push(currentMembers);
        } else {
            // 과거 회원수는 랜덤한 성장률을 적용하여 역산
            const growthRate = avgGrowthRate * (0.8 + Math.random() * 0.4);
            memberCount = Math.floor(memberCount / (1 + growthRate));
            dataPoints.push(memberCount);
        }
    }

    // 역순으로 저장했으므로 다시 정렬
    dataPoints.reverse();

    return { labels, data: dataPoints };
}

/**
 * 실제 서버 데이터를 사용하는 경우의 함수 예시
 * API 엔드포인트를 만들어 실제 데이터를 가져옵니다.
 */
async function fetchMemberGrowthData(months) {
    try {
        const response = await fetch(`/api/admin/stats/member-growth?months=${months}`);
        if (!response.ok) {
            throw new Error('데이터를 가져오는데 실패했습니다.');
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('회원 증가 데이터 로딩 실패:', error);
        // 에러 시 샘플 데이터 반환
        return generateMemberGrowthData(months);
    }
}