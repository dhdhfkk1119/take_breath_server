function loadMembers(url) {
  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error("회원 목록 불러오기 실패");
      return res.text();
    })
    .then(html => {
      const container = document.getElementById("memberTableContainer");
      container.innerHTML = html;
      window.scrollTo({
        top: container.offsetTop,
        behavior: "smooth"
      });
    })
    .catch(err => {
      console.error(err);
      alert("회원 목록을 불러오는 중 오류가 발생했습니다.");
    });
}
