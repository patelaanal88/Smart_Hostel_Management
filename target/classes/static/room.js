const floorSelect = document.getElementById("floorSelect");
const roomGrid = document.getElementById("roomGrid");
const modal = document.getElementById("bedModal");
const bedContainer = document.getElementById("bedContainer");
const modalRoomTitle = document.getElementById("modalRoomTitle");

let currentRoom = null;

const floorConfig = {
    1: { beds: 3 },
    2: { beds: 2 },
    3: { beds: 3 },
    4: { beds: 3 },
    5: { beds: 3 }
};

function getData() {
    return JSON.parse(localStorage.getItem("rooms")) || {};
}

function saveData(data) {
    localStorage.setItem("rooms", JSON.stringify(data));
}

floorSelect.addEventListener("change", () => {
    const floor = floorSelect.value;
    roomGrid.innerHTML = "";
    if (!floor) return;

    const data = getData();
    for (let i = 1; i <= 5; i++) {
        const roomNo = `${floor}0${i}`;
        if (!data[roomNo]) {
            data[roomNo] = Array(floorConfig[floor].beds).fill(false);
        }

        const isFull = data[roomNo].every(bed => bed);
        const div = document.createElement("div");
        div.className = `room ${isFull ? "red" : "green"}`;
        div.innerText = roomNo;
        div.onclick = () => openRoom(roomNo, floor);
        roomGrid.appendChild(div);
    }
    saveData(data);
});

function openRoom(roomNo, floor) {
    currentRoom = roomNo;
    modalRoomTitle.innerText = `Room ${roomNo}`;
    bedContainer.innerHTML = "";

    const data = getData();
    data[roomNo].forEach((status, index) => {
        const bed = document.createElement("div");
        bed.className = `bed ${status ? "full" : "empty"}`;
        bed.innerText = String.fromCharCode(65 + index);
        bed.onclick = () => toggleBed(index);
        bedContainer.appendChild(bed);
    });

    modal.style.display = "flex";
}

function toggleBed(index) {
    const data = getData();
    data[currentRoom][index] = !data[currentRoom][index];
    saveData(data);
    openRoom(currentRoom, currentRoom[0]);
    floorSelect.dispatchEvent(new Event("change"));
}

function closeModal() {
    modal.style.display = "none";
}
