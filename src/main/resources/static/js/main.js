// Smart Waste Management System - JS Logic

document.addEventListener("DOMContentLoaded", function () {
    
    // 1. Geolocation API Logic for Raise Complaint Page
    const locateBtn = document.getElementById("btn-get-location");
    const latInput = document.getElementById("latitude");
    const lngInput = document.getElementById("longitude");
    const locationStatus = document.getElementById("location-status");

    if (latInput && lngInput) {
        // Automatically try to get location on page load
        getLocation();
    }

    if (locateBtn) {
        locateBtn.addEventListener("click", function (e) {
            e.preventDefault();
            getLocation();
        });
    }

    function getLocation() {
        if (locationStatus) {
            locationStatus.innerHTML = '<span class="text-warning"><i class="bi bi-geo-alt-fill"></i> Acquiring GPS coordinates...</span>';
        }

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition, showError, {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            });
        } else {
            if (locationStatus) {
                locationStatus.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle-fill"></i> Geolocation is not supported by this browser.</span>';
            }
        }
    }

    function showPosition(position) {
        if (latInput && lngInput) {
            latInput.value = position.coords.latitude;
            lngInput.value = position.coords.longitude;
        }
        if (locationStatus) {
            locationStatus.innerHTML = `<span class="text-success"><i class="bi bi-check-circle-fill"></i> Location Captured: Lat ${position.coords.latitude.toFixed(6)}, Lng ${position.coords.longitude.toFixed(6)}</span>`;
        }
    }

    function showError(error) {
        let errorMsg = "Unable to retrieve location.";
        switch(error.code) {
            case error.PERMISSION_DENIED:
                errorMsg = "Location permission denied by user. Please enter coordinates manually or enable location.";
                break;
            case error.POSITION_UNAVAILABLE:
                errorMsg = "Location information is unavailable.";
                break;
            case error.TIMEOUT:
                errorMsg = "The request to get user location timed out.";
                break;
            case error.UNKNOWN_ERROR:
                errorMsg = "An unknown error occurred.";
                break;
        }
        if (locationStatus) {
            locationStatus.innerHTML = `<span class="text-danger"><i class="bi bi-exclamation-triangle-fill"></i> ${errorMsg}</span>`;
        }
    }

    // 2. Client Side Password Validation
    const regForm = document.getElementById("registerForm");
    if (regForm) {
        regForm.addEventListener("submit", function (e) {
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            if (password !== confirmPassword) {
                e.preventDefault();
                alert("Passwords do not match!");
            }
        });
    }

    const profileForm = document.getElementById("profileForm");
    if (profileForm) {
        profileForm.addEventListener("submit", function (e) {
            const newPassword = document.getElementById("newPassword").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            if (newPassword && newPassword !== confirmPassword) {
                e.preventDefault();
                alert("New passwords do not match!");
            }
        });
    }

    // 3. Image File Upload Constraints (Size & Extension)
    const fileInputs = document.querySelectorAll("input[type='file']");
    fileInputs.forEach(input => {
        input.addEventListener("change", function () {
            if (this.files && this.files[0]) {
                const file = this.files[0];
                const sizeInMB = file.size / (1024 * 1024);
                
                if (sizeInMB > 10) {
                    alert("Maximum image upload size is 10MB!");
                    this.value = "";
                    return;
                }

                const allowedExtensions = /(\.jpg|\.jpeg|\.png|\.webp)$/i;
                if (!allowedExtensions.exec(file.name)) {
                    alert("Only image files (.jpg, .jpeg, .png, .webp) are allowed!");
                    this.value = "";
                    return;
                }
            }
        });
    });
});
