// EwuarSoft Application Scripts

document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss alert messages after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });

    // Mobile sidebar toggle
    const toggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.app-sidebar');
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }

    // Format currency inputs or elements if needed
    const currencyElements = document.querySelectorAll('.format-currency');
    currencyElements.forEach(el => {
        const val = parseFloat(el.textContent.replace(/[^0-9.-]+/g, ''));
        if (!isNaN(val)) {
            el.textContent = new Intl.NumberFormat('es-CO', {
                style: 'currency',
                currency: 'COP',
                minimumFractionDigits: 0
            }).format(val);
        }
    });
});
