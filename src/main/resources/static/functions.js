function toggleRow(row) {
    const isHidden = row.style.display === "none";
    
    document.querySelectorAll('.description-row').forEach(descriptionRow => {
        descriptionRow.style.display = "none";
    });
    
    if (isHidden) {
        row.style.display = "table-row";
    }
}
        
function showNotification() {
    const notification = document.querySelector(".notification");

    if (notification) {
        notification.classList.add('show');
        setTimeout(() => {
            notification.classList.add('fade-out');
        }, 4000);
    }
}

/*
This is necessary to guarantee that the external resources are fully loaded before the script
is executed. Without this, showNotification() wasn't working, because `defer` delays the script
execution up to the point where the HTML is fully parsed.
*/
window.onload = function() {
    showNotification();
};
