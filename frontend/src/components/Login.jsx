import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { loginUser } from '../services/authService';
import { validateUsername, validatePassword } from '../utils/validation';
import "./Login.css";

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({})
  const [message, setMessage] = useState('');
  const [showPassword, setShowPassword] = useState(false);


  const handleSubmit = async (e) => {
    e.preventDefault();

    const usernameError = validateUsername(username);
    const passwordError = validatePassword(password);
    setErrors({
      username: usernameError,
      password: passwordError,
    });
    
    if (usernameError || passwordError) {
        return;
    }

    try {
        const data = await loginUser(username, password);
        setMessage("Login successful"); 
        localStorage.setItem('token', data.data.token);
        localStorage.setItem('username', username);
        navigate("/dashboard");
    } catch (error) {
        setMessage("Login Failed");
    }
  };

  return (
    <div className="login-page">
      <div className="login-box">
        <form onSubmit={handleSubmit} data-testid="login-form">
          <h1>Login</h1>
          <div className="password-wrapper">
            <input className='username-input' type="text" placeholder='Username' data-testid="username-input" value={username} onChange={(e) => setUsername(e.target.value)}/>
          </div>
            {errors.username && (
                <p className="error-text" data-testid="username-error">
                {errors.username}
                </p>
            )}
          <div className="password-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              data-testid="password-input"
              className="password-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <span
              className="eye-icon"
              data-testid="toggle-password"
              onMouseDown={() => setShowPassword(true)}
              onMouseUp={() => setShowPassword(false)}
              onMouseLeave={() => setShowPassword(false)}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
              >
                <circle cx="12" cy="12" r="3" />
                <path d="M2.5 12s3.5-7 9.5-7 9.5 7 9.5 7-3.5 7-9.5 7-9.5-7-9.5-7z" />
              </svg>
            </span>

          </div>
            {errors.password && (
              <p className="error-text" data-testid="password-error">
                {errors.password}
              </p>
            )}

          <button type='submit' data-testid="login-button" className="login-button" > Login </button>
          {message && (
            <p data-testid="login-message" className={`message ${message === "Login successful" ? "success" : "error"}`}>
                {message}
            </p>
          )}
        </form>
      </div>
    </div>
  )
}
