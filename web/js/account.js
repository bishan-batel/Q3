const dialog = document.querySelector("dialog#error");
setTimeout(() => {
    if (dialog) dialog.showModal();
}, 1);

function closeDialog() {
    dialog.close();
}