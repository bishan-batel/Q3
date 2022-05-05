const onScroll = () => document
    .querySelectorAll(".reveal")
    .forEach((ele) => {
        const windowHeight = window.innerHeight;
        const elementTop = ele.getBoundingClientRect().top;
        let elementVisible = 150;

        try {
            let offset = ele.getAttribute("reveal-offset");
            if (typeof (offset) == "number")
                elementVisible += parseFloat(offset);
        } catch (e) {
            console.error(e);
        }

        if (elementTop < windowHeight - elementVisible) {
            // randomizeSkew(ele)
            ele.classList.add("active");
        } else {
            // if (ele.classList.contains("active")) randomizeSkew(ele)
            ele.classList.remove("active");
        }
    });
onScroll();
window.addEventListener("scroll", onScroll);

function subForward(url) {
    url = "Q3/" + url;
    window.location.href = `${window.location.protocol}//${window.location.host}/${url}`;
}
