let memberGrowthChart = null;
let reportProcessChart = null;

function initCharts(stats) {
    console.log('initCharts 함수 실행됨', stats);

    // ─────────────── 막대 그래프 - 회원 및 상담사 현황 ───────────────
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
                plugins: { legend: { display: false } },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                }
            }
        });
    }

    // ─────────────── 원형 차트 - 역할별 비율 ───────────────
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
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }

    // ─────────────── 회원 증가 추이 차트 초기화 ───────────────
    initMemberGrowthChart(6);

    // 회원 증가 추이 기간 선택 버튼 이벤트
    const memberChartButtons = document.querySelectorAll('.member-chart-btn');
    memberChartButtons.forEach(button => {
        button.addEventListener('click', function() {
            memberChartButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            const period = parseInt(this.dataset.period);
            initMemberGrowthChart(period);
        });
    });

    // ─────────────── 신고 처리 추이 차트 초기화 ───────────────
    initReportProcessChart(6);

    // 신고 처리 추이 기간 선택 버튼 이벤트
    const reportChartButtons = document.querySelectorAll('.report-chart-btn');
    reportChartButtons.forEach(button => {
        button.addEventListener('click', function() {
            reportChartButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            const period = parseInt(this.dataset.period);
            initReportProcessChart(period);
        });
    });
}

/**
 * 회원 증가 추이 차트 초기화 (일반회원 / 상담사)
 * @param {number} months - 표시할 개월 수 (6, 12, 24)
 */
function initMemberGrowthChart(months) {
    const ctx = document.getElementById('memberGrowthChart');
    if (!ctx) return;

    if (memberGrowthChart) memberGrowthChart.destroy();

    const chartData = window.stats?.memberGrowthData;
    if (!chartData || !Array.isArray(chartData.labels) || !Array.isArray(chartData.memberData)) {
        console.warn("⚠️ 회원 증가 데이터가 없습니다.");
        return;
    }

    if (chartData.labels.length === 0) {
        console.warn("⚠️ 회원 증가 데이터가 비어있습니다.");
    }

    // 데이터셋 구성
    const datasets = [
        {
            label: '일반회원',
            data: chartData.memberData,
            borderColor: 'rgb(52, 152, 219)',
            backgroundColor: 'rgba(52, 152, 219, 0.1)',
            borderWidth: 3,
            fill: true,
            tension: 0.4,
            pointRadius: 5,
            pointHoverRadius: 8,
            pointBackgroundColor: 'rgb(52, 152, 219)',
            pointBorderColor: '#fff'
        }
    ];

    // 상담사 데이터가 있을 때만 추가
    if (chartData.counselorData && chartData.counselorData.length > 0) {
        datasets.push({
            label: '상담사',
            data: chartData.counselorData,
            borderColor: 'rgb(155, 89, 182)',
            backgroundColor: 'rgba(155, 89, 182, 0.15)',
            borderWidth: 3,
            fill: true,
            tension: 0.4,
            pointRadius: 5,
            pointHoverRadius: 8,
            pointBackgroundColor: 'rgb(155, 89, 182)',
            pointBorderColor: '#fff'
        });
    }

    // 차트 생성
    memberGrowthChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { intersect: false, mode: 'index' },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: { size: 13, weight: '500' },
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    cornerRadius: 6,
                    displayColors: true,
                    callbacks: {
                        label: function(context) {
                            const label = context.dataset.label || '';
                            return `${label}: ${context.parsed.y.toLocaleString()}명`;
                        }
                    }
                },
                title: {
                    display: true,
                    text: '회원 증가 추이'
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        font: { size: 12 },
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
                    ticks: { font: { size: 12 } },
                    grid: { display: false, drawBorder: false }
                }
            }
        }
    });
}

/**
 * 신고 처리 추이 차트 초기화 (커뮤니티 + 댓글 합산)
 * @param {number} months - 표시할 개월 수 (6, 12, 24)
 */
function initReportProcessChart(months) {
    console.log('🔍 initReportProcessChart 호출됨, months:', months);

    const ctx = document.getElementById('reportProcessChart');
    if (!ctx) {
        console.error('❌ reportProcessChart 캔버스를 찾을 수 없습니다!');
        return;
    }

    console.log('✅ reportProcessChart 캔버스 찾음');

    if (reportProcessChart) {
        console.log('🗑️ 기존 차트 삭제');
        reportProcessChart.destroy();
    }

    const chartData = window.stats?.reportProcessData;
    console.log('📊 신고 처리 데이터:', chartData);

    if (!chartData) {
        console.error("❌ reportProcessData가 없습니다!");
        console.log('window.stats:', window.stats);
        return;
    }

    if (!Array.isArray(chartData.labels) || !Array.isArray(chartData.data)) {
        console.error("❌ reportProcessData 구조가 잘못되었습니다!", chartData);
        return;
    }

    if (chartData.labels.length === 0) {
        console.warn("⚠️ 신고 처리 데이터가 비어있습니다. (빈 차트 표시)");
    }

    console.log('📈 차트 생성 시작...');
    console.log('Labels:', chartData.labels);
    console.log('Data:', chartData.data);

    // 차트 생성
    reportProcessChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: [{
                label: '신고 처리 건수',
                data: chartData.data,
                borderColor: 'rgb(231, 76, 60)',
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 5,
                pointHoverRadius: 8,
                pointBackgroundColor: 'rgb(231, 76, 60)',
                pointBorderColor: '#fff',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { intersect: false, mode: 'index' },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: { size: 13, weight: '500' },
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    cornerRadius: 6,
                    displayColors: true,
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.parsed.y}건`;
                        }
                    }
                },
                title: {
                    display: true,
                    text: '신고 처리 추이'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        font: { size: 12 },
                        callback: function(value) {
                            return value + '건';
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)',
                        drawBorder: false
                    }
                },
                x: {
                    ticks: { font: { size: 12 } },
                    grid: { display: false, drawBorder: false }
                }
            }
        }
    });

    console.log('✅ 신고 처리 차트 생성 완료!');
}

/**
 * 실제 서버 데이터를 가져오는 경우 (확장용)
 */
async function fetchMemberGrowthData(months) {
    try {
        const response = await fetch(`/api/admin/stats/member-growth?months=${months}`);
        if (!response.ok) throw new Error('데이터를 가져오는데 실패했습니다.');
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('회원 증가 데이터 로딩 실패:', error);
        return { labels: [], memberData: [], counselorData: [] };
    }
}