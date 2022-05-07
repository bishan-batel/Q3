{
    const cookies = document.querySelector("#cookiePrompt");
    if (cookies) {
        cookies.showModal();
        document.querySelector("#cookiePrompt-yes").onclick = () => {
            document.cookie = "acceptedCookies=true; SameSite=Lax; Secure";
            cookies.close();
            cookies.remove()
        }
    }

    const err = document.querySelector("#error");
    if (err) {
        err.showModal();
        document.querySelector("#error button").onclick = () => {
            err.close();
            err.remove();
        }
    }
}