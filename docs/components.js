// Component loader utility
function loadComponent(elementId, filePath) {
    fetch(filePath)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {
            const element = document.getElementById(elementId);
            if (element) {
                element.innerHTML = data;
            } else {
                console.error(`Element with ID '${elementId}' not found`);
            }
        })
        .catch(error => {
            console.error('Error loading component:', error);
        });
}

// Load all page components
function loadPageComponents() {
    loadComponent('sidebar-container', 'sidebar.html');
    loadComponent('grid-container', 'grid.html');
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', loadPageComponents);