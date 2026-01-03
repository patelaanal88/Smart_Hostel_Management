/**
 * UI FUNCTION: updateLabels
 * Handles the visual toggle between Student and Admin on the Sign-Up page.
 */
function updateLabels(role) {
    const authForm = document.getElementById('authForm');
    if (authForm) authForm.reset(); // Mandatory clear on toggle to prevent data mixing

    const idLabel = document.getElementById('id-label');
    const idField = document.getElementById('id-field');
    const loginBtn = document.querySelector('.login-btn');

    if (role === 'admin') {
        idLabel.innerText = "Identification Number (ADxxxx)";
        idField.placeholder = "e.g. AD1234";
        if (loginBtn) {
            loginBtn.innerText = "Register Admin";
            loginBtn.style.background = "#3b82f6"; // Admin Blue
        }
    } else {
        idLabel.innerText = "Registration Number (STxxxx)";
        idField.placeholder = "e.g. ST1234";
        if (loginBtn) {
            loginBtn.innerText = "Register Student";
            loginBtn.style.background = "#10b981"; // Student Green
        }
    }
}

/**
 * SIGN-UP FUNCTION: handleAuth
 * Validates inputs using strict Regex and sends data to Spring Boot.
 */
async function handleAuth(event) {
    event.preventDefault();

    const isStudent = document.getElementById('student-btn').checked;
    const idValue = document.getElementById('id-field').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const username = document.getElementById('username').value.trim();

    // 1. Mandatory Check
    if (!idValue || !email || !password || !username) {
        alert("All fields are mandatory. Please fill in all details.");
        return;
    }

    // 2. ID Check Rules (STxxxx or ADxxxx)
    const stRegex = /^ST\d{4}$/;
    const adRegex = /^AD\d{4}$/;
    if (isStudent && !stRegex.test(idValue)) {
        alert("Invalid Student ID! Must be ST followed by 4 digits (e.g., ST1234)");
        return;
    }
    if (!isStudent && !adRegex.test(idValue)) {
        alert("Invalid Admin ID! Must be AD followed by 4 digits (e.g., AD1234)");
        return;
    }

    // 3. Email Check (Starts with letters, must follow x@gmail.com)
    const emailRegex = /^[a-zA-Z][a-zA-Z0-9]*@gmail\.com$/;
    if (!emailRegex.test(email)) {
        alert("Invalid Email! Must start with a letter and end with @gmail.com");
        return;
    }

    // 4. Password Check (Min 6 characters)
    if (password.length < 6) {
        alert("Password too short! Minimum 6 characters required.");
        return;
    }

    // Prepare User Object for Backend Signup
    const userData = {
        username: username,
        email: email,
        password: password,
        role: isStudent ? "STUDENT" : "ADMIN",
        registrationNumber: isStudent ? idValue : null,
        identificationNumber: isStudent ? null : idValue
    };

    try {
        const res = await fetch('http://localhost:8080/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (res.ok) {
            const savedUser = await res.json();
            alert("Account created successfully! Please login now.");
            window.location.href = "login.html"; // Redirect to login page after signup
        } else {
            const errorText = await res.text();
            alert("Registration failed: " + (errorText || "ID or Username already exists."));
        }
    } catch (err) {
        console.error("Signup error:", err);
        alert("Error connecting to server. Ensure Spring Boot is running.");
    }
}

/**
 * LOGIN FUNCTION: handleLogin
 * Verifies the Registration/Identification Number and Password against the database.
 */
async function handleLogin(event) {
    event.preventDefault();

    const loginId = document.getElementById('login-id').value.trim();
    const loginPassword = document.getElementById('login-password').value;

    if (!loginId || !loginPassword) {
        alert("Please enter both ID and Password.");
        return;
    }

    // We send the ID into the 'username' field of the JSON request
    // The Backend uses findByRegistrationNumberOrIdentificationNumber
    const loginRequest = {
        username: loginId,
        password: loginPassword
    };



    try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginRequest)
        });

        if (res.ok) {
            const user = await res.json();

            // Store the logged-in user details in localStorage for dashboard access
            localStorage.removeItem('currentUser');
            localStorage.setItem('currentUser', JSON.stringify(user));

            alert("Login Successful! Welcome " + user.username);

            // Redirect based on the role stored in the Database
            if (user.role === "STUDENT") {
                window.location.href = "student_dashboard.html";
            } else if (user.role === "ADMIN") {
                window.location.href = "admin_dashboard.html";
            }
        } else {
            alert("Invalid Details! Please check your ID and Password.");
        }
    } catch (err) {
        console.error("Login Connection error:", err);
        alert("Error connecting to server. Ensure Spring Boot is running on port 8080.");
    }
}           