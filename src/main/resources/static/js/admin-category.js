// 관리자 커뮤니티 - 카테고리 삭제 전용 JS
async function deleteCategory(id) {
    if (!confirm("정말 이 카테고리를 삭제하시겠습니까?")) return;

    try {
        const res = await fetch(`/api/admin/view/community/categories/${id}/delete`, {
            method: "POST" // 컨트롤러가 POST 매핑이므로 DELETE 아님
        });

        // 정상 삭제 시
        if (res.ok) {
            alert("카테고리가 삭제되었습니다.");
            location.reload();
            return;
        }

        // 실패 시 서버에서 온 에러 메시지 표시
        const data = await res.json();
        const msg = data?.error?.message || "삭제 중 오류가 발생했습니다.";
        alert(msg);

    } catch (err) {
        console.error("삭제 오류:", err);
        alert("삭제 중 예기치 못한 오류가 발생했습니다.");
    }
}
