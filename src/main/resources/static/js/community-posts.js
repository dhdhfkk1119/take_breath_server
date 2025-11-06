document.addEventListener("DOMContentLoaded", () => {
    loadPosts();

});

async function loadPosts(keyword = "") {
    try {
        const url = keyword
            ? `/api/admin/community/posts?keyword=${encodeURIComponent(keyword)}`
            : "/api/admin/community/posts";

        const res = await fetch(url);
        if (!res.ok) throw new Error("데이터를 불러오지 못했습니다.");
        const posts = await res.json();

        const tbody = document.getElementById("postList");
        if (posts.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="9" style="text-align:center; padding:1.5rem; color:#999;">게시글이 없습니다.</td></tr>
            `;
            return;
        }

        tbody.innerHTML = posts.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.title}</td>
                <td>${p.author}</td>
                <td>${p.categoryName}</td>
                <td>${p.viewCount}</td>
                <td>${p.likeCount}</td>
                <td>${p.reportCount}</td>
                <td>${p.createdAt || '-'}</td>
                <td>
                    <button class="btn btn-danger" onclick="deletePost(${p.id})">삭제</button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error(err);
        alert("게시글 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

async function deletePost(id) {
    if (!confirm("정말 이 게시글을 강제 삭제하시겠습니까?")) return;
    try {
        const res = await fetch(`/api/admin/community/posts/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error("삭제 실패");
        await loadPosts();
    } catch (err) {
        console.error(err);
        alert("삭제 중 오류가 발생했습니다.");
    }
}
