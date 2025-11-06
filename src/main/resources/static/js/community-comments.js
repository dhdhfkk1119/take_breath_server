document.addEventListener("DOMContentLoaded", () => {
    loadComments();

});

async function loadComments(keyword = "") {
    try {
        const url = keyword
            ? `/api/admin/community/comments?keyword=${encodeURIComponent(keyword)}`
            : "/api/admin/community/comments";

        const res = await fetch(url);
        if (!res.ok) throw new Error("데이터를 불러오지 못했습니다.");
        const comments = await res.json();

        const tbody = document.getElementById("commentList");
        if (comments.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="7" style="text-align:center; padding:1.5rem; color:#999;">댓글이 없습니다.</td></tr>
            `;
            return;
        }

        tbody.innerHTML = comments.map(c => `
            <tr>
                <td>${c.id}</td>
                <td class="introduction-cell">${c.content}</td>
                <td>${c.memberName}</td>
                <td>-</td>
                <td>-</td>
                <td>${c.createdAt || '-'}</td>
                <td>
                    <button class="btn btn-danger" onclick="deleteComment(${c.id})">삭제</button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error(err);
        alert("댓글 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

async function deleteComment(id) {
    if (!confirm("정말 이 댓글을 강제 삭제하시겠습니까?")) return;

    try {
        const res = await fetch(`/api/admin/community/comments/${id}`, { method: "DELETE" });
        const text = await res.text();

        await loadComments();

        if (res.ok) {
            alert("댓글이 삭제되었습니다.");
        } else if (text.includes("이미 삭제")) {
            alert("이미 삭제된 댓글입니다. 목록을 새로고침했습니다.");
        } else {
            throw new Error(text || "삭제 실패");
        }

    } catch (err) {
        console.error(err);
        alert("삭제 중 오류가 발생했습니다.");
    }
}
