document.querySelector("#get-started").onclick = () => {
    document.querySelector("#transition").classList.add("active");

    setTimeout(() => {
        subForward("account");
    }, 900);
}

