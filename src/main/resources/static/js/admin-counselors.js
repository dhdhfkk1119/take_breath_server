function openRejectModal(counselorId) {
    const modal = document.getElementById('rejectModal');
    const form = document.getElementById('rejectForm');
    form.action = '/api/admin/view/counselors/' + counselorId + '/reject';
    modal.style.display = 'block';
}

function closeRejectModal() {
    document.getElementById('rejectModal').style.display = 'none';
}

function openImageModal(licenseName, licenseNumber, licenseRegiNumber, licenseImage) {
    const modal = document.getElementById('imageModal');
    document.getElementById('licenseNameDisplay').textContent = licenseName;
    document.getElementById('licenseNumberDisplay').textContent = licenseNumber;
    document.getElementById('licenseRegiNumberDisplay').textContent = licenseRegiNumber;

    const imagePath = '/uploads/counselor-images/' + licenseImage;
    document.getElementById('licenseImageDisplay').src = imagePath;
    modal.style.display = 'block';
}

function closeImageModal() {
    document.getElementById('imageModal').style.display = 'none';
}

document.addEventListener('click', (e) => {
    if (e.target.classList.contains('license-badge')) {
        const name = e.target.dataset.name;
        const number = e.target.dataset.number;
        const regi = e.target.dataset.regi;
        const img = e.target.dataset.img;
        openImageModal(name, number, regi, img);
    }
});

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const rejectModal = document.getElementById('rejectModal');
    const imageModal = document.getElementById('imageModal');
    if (event.target === rejectModal) closeRejectModal();
    if (event.target === imageModal) closeImageModal();
};
