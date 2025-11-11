// ──────────────────────────────────────────────
// 📊 Admin Dashboard Chart Script (Stable & Optimized)
// ──────────────────────────────────────────────
window.addEventListener('error', function (e) {
  console.error('💥 JS 전역 오류 발생:', e.message, e.filename, e.lineno);
});
console.log('✅ admin-dashboard.js 파일 진입 확인');

// ✅ Chart 전역 설정 (성능 최적화)
Chart.defaults.responsive = false;
Chart.defaults.maintainAspectRatio = false;
Chart.defaults.responsiveAnimationDuration = 0;
Chart.defaults.resizeDelay = 1000;

// ───────────────────────────────
// 전역 인스턴스 (중복 생성 방지)
// ───────────────────────────────
let feeChartInstance,
    memberGrowthChartInstance,
    statsChartInstance,
    roleChartInstance;

let isInitialized = false;

// ───────────────────────────────
// 초기 실행 (한 번만)
// ───────────────────────────────
window.addEventListener('load', function () {
    console.log('✅ admin-dashboard.js fully loaded');
    if (!window.stats) {
        console.error('❌ window.stats 없음');
        return;
    }

    // 순차적으로 차트 렌더링
    setTimeout(() => initFeeChart(), 0);
    setTimeout(() => initMemberGrowthChart(window.stats, 6), 50);
    setTimeout(() => initStatsChart(window.stats), 100);
    setTimeout(() => initRoleChart(window.stats), 150);

    setupEventListeners();
});

// ───────────────────────────────
// 회원 증가 추이 버튼 리스너
// ───────────────────────────────
function setupEventListeners() {
    const memberBtns = document.querySelectorAll('.member-chart-btn');
    memberBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const period = parseInt(this.dataset.period);
            memberBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            recreateMemberGrowthChart(period);
        });
    });
}

// ───────────────────────────────
// Canvas 재생성 (회원 차트만)
// ───────────────────────────────
function recreateMemberGrowthChart(period) {
    if (memberGrowthChartInstance) {
        memberGrowthChartInstance.destroy();
        memberGrowthChartInstance = null;
    }

    const oldCanvas = document.getElementById('memberGrowthChart');
    if (oldCanvas) oldCanvas.remove();

    const wrapper = document.getElementById('memberGrowthChartWrapper');
    if (wrapper) {
        const newCanvas = document.createElement('canvas');
        newCanvas.id = 'memberGrowthChart';
        newCanvas.height = 350;
        newCanvas.width = wrapper.offsetWidth;
        wrapper.appendChild(newCanvas);

        initMemberGrowthChart(window.stats, period);
    }
}

// ───────────────────────────────
// 💰 월별 결제 수수료 차트
// ───────────────────────────────
function initFeeChart() {
    const canvas = document.getElementById('feeChart');
    if (!canvas) return;

    const labels = canvas.dataset.labels?.split(',').filter(l => l.trim()) || [];
    const values = canvas.dataset.values?.split(',').map(Number) || [];

    if (labels.length === 0 || values.length === 0) {
        const wrapper = document.getElementById('feeChartWrapper');
        if (wrapper) {
            wrapper.innerHTML = '<p style="text-align:center; padding:2rem; color:#888;">📉 결제 데이터가 없습니다.</p>';
        }
        return;
    }

    const ctx = canvas.getContext('2d');
    canvas.height = 350;
    canvas.width = canvas.offsetWidth;

    feeChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: '월별 결제 수수료',
                data: values,
                backgroundColor: '#5dade2',
                borderColor: '#3498db',
                borderWidth: 1
            }]
        },
        options: {
            animation: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => ctx.parsed.y.toLocaleString() + '원'
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { callback: v => v.toLocaleString() + '원' }
                }
            }
        }
    });
}

// ───────────────────────────────
// 📈 회원 증가 추이
// ───────────────────────────────
function initMemberGrowthChart(stats, months = 6) {
    const canvas = document.getElementById('memberGrowthChart');
    if (!canvas) return;

    const data = stats?.memberGrowthData || {};
    let labels = data.labels || [];
    let memberData = data.memberData || [];
    let counselorData = data.counselorData || [];

    if (labels.length > months) {
        labels = labels.slice(-months);
        memberData = memberData.slice(-months);
        counselorData = counselorData.slice(-months);
    }

    const ctx = canvas.getContext('2d');
    canvas.height = 350;
    canvas.width = canvas.offsetWidth;

    memberGrowthChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [
                {
                    label: '일반회원',
                    data: memberData,
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: '상담사',
                    data: counselorData,
                    borderColor: '#9b59b6',
                    backgroundColor: 'rgba(155, 89, 182, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            animation: false,
            plugins: {
                legend: { display: true, position: 'top' },
                tooltip: {
                    callbacks: {
                        label: ctx => `${ctx.dataset.label}: ${ctx.parsed.y}명`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { callback: v => v + '명' }
                }
            }
        }
    });
}

// ───────────────────────────────
// 👥 회원/상담사 현황
// ───────────────────────────────
function initStatsChart(stats) {
    const canvas = document.getElementById('statsChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    canvas.height = 350;
    canvas.width = canvas.offsetWidth;

    statsChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['일반회원', '상담사'],
            datasets: [
                {
                    label: '활성',
                    data: [stats?.activeMembers || 0, stats?.activeCounselors || 0],
                    backgroundColor: '#2ecc71'
                },
                {
                    label: '정지',
                    data: [stats?.suspendedMembers || 0, 0],
                    backgroundColor: '#e74c3c'
                },
                {
                    label: '대기',
                    data: [0, stats?.pendingCounselors || 0],
                    backgroundColor: '#f39c12'
                }
            ]
        },
        options: {
            animation: false,
            plugins: {
                legend: { position: 'top' }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { callback: v => v + '명' }
                }
            }
        }
    });
}

// ───────────────────────────────
// 🧩 역할별 비율
// ───────────────────────────────
function initRoleChart(stats) {
    const canvas = document.getElementById('roleChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    canvas.height = 350;
    canvas.width = canvas.offsetWidth;

    roleChartInstance = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['일반회원', '상담사', '관리자'],
            datasets: [{
                data: [
                    stats?.totalMembers || 0,
                    stats?.totalCounselors || 0,
                    1
                ],
                backgroundColor: ['#3498db', '#9b59b6', '#e74c3c'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            animation: false,
            plugins: {
                legend: { position: 'bottom' },
                tooltip: {
                    callbacks: {
                        label: ctx => {
                            const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((ctx.parsed / total) * 100).toFixed(1);
                            return `${ctx.label}: ${ctx.parsed}명 (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}
