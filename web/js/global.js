const onScroll = () => document
    .querySelectorAll(".reveal")
    .forEach((ele) => {
        const windowHeight = window.innerHeight;
        const elementTop = ele.getBoundingClientRect().top;
        let margin = 150;

        try {
            let offset = ele.getAttribute("reveal-offset");
            if (typeof (offset) == "number") margin += parseFloat(offset);
        } catch (e) {
            console.error(e);
        }

        if (elementTop < windowHeight - margin) {
            ele.classList.add("active");
        } else {
            ele.classList.remove("active");
        }
    });
onScroll();
window.addEventListener("scroll", onScroll);
