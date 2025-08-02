const API_BASE = "http://localhost:8080/api/tokens";

document.getElementById("tokenForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const name = document.getElementById("patientName").value;
  const res = await fetch(`${API_BASE}?name=${name}`, { method: "POST" });
  const token = await res.json();
  alert(`Your token number is: ${token.tokenNumber}`);
  loadQueue();
});

async function loadQueue() {
  const res = await fetch(API_BASE);
  const queue = await res.json();
  const list = document.getElementById("queueList");
  list.innerHTML = "";
  queue.forEach(t => {
    const li = document.createElement("li");
    li.textContent = `Token #${t.tokenNumber} - ${t.patientName}`;
    list.appendChild(li);
  });
}

loadQueue();
setInterval(loadQueue, 5000);
