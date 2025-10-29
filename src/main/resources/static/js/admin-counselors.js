function openRejectModal(counselorId) {
    const modal = document.getElementById('rejectModal');
    const form = document.getElementById('rejectForm');
    form.action = '/api/admin/view/counselors/' + counselorId + '/reject';
    modal.style.display = 'block';
}

function closeRejectModal() {
    const modal = document.getElementById('rejectModal');
    modal.style.display = 'none';
}

function openImageModal(licenseName, licenseNumber, licenseRegiNumber, licenseImage) {
    const modal = document.getElementById('imageModal');
    document.getElementById('licenseNameDisplay').textContent = licenseName;
    document.getElementById('licenseNumberDisplay').textContent = licenseNumber;
    document.getElementById('licenseRegiNumberDisplay').textContent = licenseRegiNumber;

    const imagePath = '/uploads/counselor-images/' + licenseImage;
    document.getElementById('licenseImageDisplay').src = imagePath;

    console.log('이미지 경로:', imagePath); // 디버깅용

    modal.style.display = 'block';
}

function closeImageModal() {
    const modal = document.getElementById('imageModal');
    modal.style.display = 'none';
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const rejectModal = document.getElementById('rejectModal');
    const imageModal = document.getElementById('imageModal');
    if (event.target === rejectModal) {
        closeRejectModal();
    }
    if (event.target === imageModal) {
        closeImageModal();
    }
}