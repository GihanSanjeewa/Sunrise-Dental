/**
 * Sunrise Dental Clinic Management System
 * Centralized API Client, Authentication, and Granular Permission Helpers
 */

const API_BASE = '/api';

// Current Session Helpers
function getSession() {
    const sessionStr = sessionStorage.getItem('sunrise_user');
    if (!sessionStr) return null;
    try {
        return JSON.parse(sessionStr);
    } catch (e) {
        return null;
    }
}

function setSession(userData) {
    sessionStorage.setItem('sunrise_user', JSON.stringify(userData));
}

function clearSession() {
    sessionStorage.removeItem('sunrise_user');
}

/**
 * Checks if the current session user has a specific granular permission.
 * System administrators (ADMIN) always have all permissions.
 */
function hasPermission(permissionName) {
    const user = getSession();
    if (!user) return false;
    if (user.role === 'ADMIN') return true;
    if (!user.permissions || !Array.isArray(user.permissions)) return false;
    return user.permissions.includes(permissionName);
}

function checkAuth(allowedRoles = [], requiredPermission = null) {
    const user = getSession();
    if (!user) {
        window.location.href = 'index.html';
        return null;
    }

    if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
        alert('You do not have permission to access this section.');
        window.location.href = 'dashboard.html';
        return null;
    }

    if (requiredPermission && !hasPermission(requiredPermission)) {
        alert(`Access Denied: Missing required permission [${requiredPermission}].`);
        window.location.href = 'dashboard.html';
        return null;
    }

    // Populate user profile info in sidebar
    const userNameEl = document.getElementById('currentUserName');
    const userRoleEl = document.getElementById('currentUserRole');
    const userAvatarEl = document.getElementById('currentUserAvatar');

    if (userNameEl) userNameEl.textContent = user.fullName || user.username;
    if (userRoleEl) userRoleEl.textContent = user.role;
    if (userAvatarEl) {
        const initials = (user.fullName || user.username).substring(0, 2).toUpperCase();
        userAvatarEl.textContent = initials;
    }

    // Hide/show role-restricted nav links
    if (user.role === 'DENTIST') {
        const adminLinks = document.querySelectorAll('.admin-only, .receptionist-only');
        adminLinks.forEach(el => el.style.display = 'none');
    } else if (user.role === 'RECEPTIONIST') {
        const adminLinks = document.querySelectorAll('.admin-only');
        adminLinks.forEach(el => el.style.display = 'none');
    }

    // Apply granular permission restrictions to elements with data-permission
    const permElements = document.querySelectorAll('[data-permission]');
    permElements.forEach(el => {
        const perm = el.getAttribute('data-permission');
        if (perm && !hasPermission(perm)) {
            el.style.display = 'none';
        }
    });

    return user;
}

// Unified API Fetch
async function apiRequest(endpoint, options = {}) {
    const user = getSession();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (user && user.token) {
        headers['Authorization'] = `Bearer ${user.token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, config);
        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            let errorMsg = data.message || `Request failed with status ${response.status}`;
            if (data.validationErrors) {
                const details = Object.entries(data.validationErrors).map(([k, v]) => `${k}: ${v}`).join('\n');
                errorMsg += `\n${details}`;
            }
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

// Global Toast / Alert Notifications
function showNotification(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed top-0 end-0 m-3 shadow-lg`;
    alertDiv.style.zIndex = '9999';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        <strong>${type === 'error' ? 'Error' : 'Notice'}:</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(alertDiv);
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 300);
    }, 4000);
}

// Logout handler
async function handleLogout() {
    try {
        await apiRequest('/auth/logout', { method: 'POST' });
    } catch (e) {
        // Ignore logout network errors
    } finally {
        clearSession();
        window.location.href = 'index.html';
    }
}
