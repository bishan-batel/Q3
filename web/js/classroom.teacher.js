{
    document.querySelector("#delete-classroom").onsubmit = (ev) => {
        if (!window.confirm("Are you sure you want to delete this classroom"))
            ev.preventDefault();
    };

    const createAssignment = {
        accuracy: document.querySelector("#create-assignment-accuracy"),
        accuracyMarker: document.querySelector("#create-assignment-accuracy+span")
    };
    const updateMarker = () => {
        createAssignment.accuracyMarker.textContent = `${Math.round(100 * createAssignment.accuracy.value)}%`;

    };
    createAssignment.accuracy.oninput = updateMarker;
    updateMarker();
}