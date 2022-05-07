document.querySelectorAll(".classroom").forEach(classroom => {
    const id = classroom.querySelector(".classroomId").textContent;

    classroom.onclick = () => {
        window.location.href = `/Q3/classroom?id=${id}`;
    }
});