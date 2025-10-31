let currentReportId = null;
let currentReportType = null;  // 'POST' 또는 'COMMENT'
let currentAction = null;

/**
 * 신고 처리 모달 열기
 */
function openProcessModal(reportId, reportType, action) {
    currentReportId = reportId;
    currentReportType = reportType;
    currentAction = action;

    const modal = document.getElementById('processModal');
    const title = document.getElementById('modalTitle');
    const description = document.getElementById('modalDescription');

    if (action === 'approve') {
        title.textContent = '신고 승인';
        description.textContent = '제재 사유를 입력하세요:';
    } else {
        title.textContent = '신고 반려';
        description.textContent = '반려 사유를 입력하세요:';
    }

    modal.classList.add('show');
}

/**
 * 모달 닫기
 */
function closeModal() {
    const modal = document.getElementById('processModal');
    modal.classList.remove('show');
    document.getElementById('reasonInput').value = '';
    currentReportId = null;
    currentReportType = null;
    currentAction = null;
}

/**
 * 신고 처리 요청
 */
function processReport() {
    const adminComment = document.getElementById('reasonInput').value.trim();

    if (!adminComment) {
        alert('사유를 입력해주세요.');
        return;
    }

    // API URL 결정
    const baseUrl = currentReportType === 'POST'
        ? `/api/admin/community/reports/${currentReportId}/status`
        : `/api/admin/comment/reports/${currentReportId}/status`;

    const status = currentAction === 'approve' ? 'APPROVED' : 'REJECTED';

    fetch(baseUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            status: status,
            adminComment: adminComment
        })
    })
    .then(response => {
        if (response.ok) {
            alert('처리되었습니다.');
            location.reload();
        } else {
            return response.json().then(data => {
                alert(data.message || '처리 중 오류가 발생했습니다.');
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('처리 중 오류가 발생했습니다.');
    });
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('confirmBtn').addEventListener('click', processReport);
    document.getElementById('cancelBtn').addEventListener('click', closeModal);

    document.getElementById('processModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
});