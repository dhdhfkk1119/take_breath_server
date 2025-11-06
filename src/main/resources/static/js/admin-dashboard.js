let memberGrowthChart = null;
let reportStatusChart = null;

/**
 * 메인 차트 초기화
 */
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
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    }

    // ─────────────── 회원 증가 추이 ───────────────
    initMemberGrowthChart(6);

    // 버튼 이벤트 (6/12/24개월)
    const memberChartButtons = document.querySelectorAll('.member-chart-btn');
    memberChartButtons.forEach(button => {
        button.addEventListener('click', function () {
            memberChartButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            const period = parseInt(this.dataset.period);
            initMemberGrowthChart(period);
        });
    });

    // ─────────────── 신고 처리 현황 ───────────────
    initReportStatusChart();

    // ─────────────── 결제 수수료 추이 ───────────────
    initFeeChart();
}

/**
 * 회원 증가 추이 차트 (일반회원 / 상담사)
 */
function initMemberGrowthChart(months) {
    const ctx = document.getElementById('memberGrowthChart');
    if (!ctx) return;

    if (memberGrowthChart) memberGrowthChart.destroy();

    const chartData = window.stats?.memberGrowthData;
    if (!chartData || !Array.isArray(chartData.labels) || !Array.isArray(chartData.memberData)) {
        console.warn("회원 증가 데이터가 없습니다.");
        return;
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
                    callbacks: {
                        label: function (context) {
                            const label = context.dataset.label || '';
                            return `${label}: ${context.parsed.y.toLocaleString()}명`;
                        }
                    }
                },
                title: { display: true, text: '회원 증가 추이' }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        font: { size: 12 },
                        callback: value => value.toLocaleString() + '명'
                    },
                    grid: { color: 'rgba(0, 0, 0, 0.05)', drawBorder: false }
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
 * 신고 처리 현황 차트 (막대 그래프)
 */
function initReportStatusChart() {
    const ctx = document.getElementById('reportStatusChart');
    if (!ctx) return;

    if (reportStatusChart) reportStatusChart.destroy();

    const statusData = window.stats?.reportStatusData;
    if (!statusData) return;

    const pending = statusData.PENDING || 0;
    const approved = statusData.APPROVED || 0;
    const rejected = statusData.REJECTED || 0;

    reportStatusChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['대기', '승인', '반려'],
            datasets: [{
                label: '신고 건수',
                data: [pending, approved, rejected],
                backgroundColor: [
                    'rgba(243, 156, 18, 0.8)',
                    'rgba(46, 204, 113, 0.8)',
                    'rgba(231, 76, 60, 0.8)'
                ],
                borderColor: [
                    'rgb(243, 156, 18)',
                    'rgb(46, 204, 113)',
                    'rgb(231, 76, 60)'
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                title: { display: true, text: '신고 상태별 처리 건수' },
                tooltip: {
                    callbacks: {
                        label: ctx => `${ctx.label}: ${ctx.parsed.y}건`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        callback: value => value + '건'
                    },
                    grid: { color: 'rgba(0,0,0,0.05)', drawBorder: false }
                },
                x: { grid: { display: false } }
            }
        }
    });
}

/**
 * 월별 결제 수수료 추이 차트
 */
function initFeeChart() {
    const canvas = document.getElementById("feeChart");
    if (!canvas) return;

    const labelsRaw = canvas.dataset.labels?.split(",").filter(l => l.trim() !== "");
    const valuesRaw = canvas.dataset.values?.split(",").filter(v => v.trim() !== "");
    const container = canvas.closest(".chart-container");

    if (!labelsRaw || labelsRaw.length === 0 || !valuesRaw || valuesRaw.length === 0) {
        container.innerHTML = `
            <div style="text-align:center; padding:2rem; color:#777;">
                결제 데이터가 없습니다.
            </div>
        `;
        return;
    }

    new Chart(canvas, {
        type: "bar",
        data: {
            labels: labelsRaw,
            datasets: [{
                label: "월별 결제 수수료 추이",
                data: valuesRaw,
                backgroundColor: "rgba(52, 152, 219, 0.7)",
                borderColor: "rgb(41, 128, 185)",
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false },
                title: { display: true, text: "월별 결제 수수료 추이" },
                tooltip: {
                    callbacks: { label: ctx => `${ctx.parsed.y.toLocaleString()}원` }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: value => value.toLocaleString() + "원"
                    }
                }
            }
        }
    });
}

/**
 * 서버 데이터 동적 로딩용 (확장 가능)
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

/**
 * DOM 로드 후 실행
 */
document.addEventListener("DOMContentLoaded", () => {
    if (window.stats) {
        initCharts(window.stats);
    } else {
        console.warn("⚠️ window.stats 데이터가 없습니다!");
    }
});
