console.log("bruh")
document
        .querySelector("#authform")
        .addEventListener('submit', ev => {
            console.log(ev);
            // ev.preventDefault();
        });
