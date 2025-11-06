document.addEventListener("DOMContentLoaded", () => {
    loadCategories();
    document.getElementById("addCategoryBtn").addEventListener("click", addCategory);
});

async function loadCategories() {
    try {
        const res = await fetch("/api/admin/view/community/categories");
        if (!res.ok) throw new Error("데이터를 불러오지 못했습니다.");
        const categories = await res.json();

        const tbody = document.getElementById("categoryList");
        if (categories.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="4" style="text-align:center; padding:1.5rem; color:#999;">카테고리가 없습니다.</td></tr>
            `;
            return;
        }

        tbody.innerHTML = categories.map(cat => `
            <tr>
                <td>${cat.id}</td>
                <td>${cat.name}</td>
                <td>${cat.createdAt || '-'}</td>
                <td>
                    <button class="btn btn-danger" onclick="deleteCategory(${cat.id})">삭제</button>
                </td>
            </tr>
        `).join("");
    } catch (err) {
        console.error(err);
        alert("카테고리 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

async function addCategory() {
    const nameInput = document.getElementById("newCategoryName");
    const name = nameInput.value.trim();
    if (!name) return alert("카테고리 이름을 입력하세요.");

    try {
        const res = await fetch("/api/admin/community/categories", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name })
        });

        if (!res.ok) throw new Error("추가 실패");

        nameInput.value = "";
        await loadCategories();
    } catch (err) {
        console.error(err);
        alert("카테고리를 추가하는 중 오류가 발생했습니다.");
    }
}

async function deleteCategory(id) {
    if (!confirm("정말 이 카테고리를 삭제하시겠습니까?")) return;

    try {
        const res = await fetch(`/api/admin/community/categories/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error("삭제 실패");
        await loadCategories();
    } catch (err) {
        console.error(err);
        alert("삭제 중 오류가 발생했습니다.");
    }
}
