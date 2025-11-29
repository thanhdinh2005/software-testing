import React from "react";

export default function TopBar({ username, onLogout }) {
  return (
    <div 
    data-testid="top-bar"
    style={{ 
      height: "60px", background: "#f5f5f5",
      display: "flex", justifyContent: "space-between",
      padding: "0 20px", alignItems: "center"
    }}>
      <h2>Dashboard</h2>
      <div>
        <span data-testid="username-login" style={{ marginRight: "20px" }}>Xin chào, {username}</span>
        <button data-testid="logout-button" style={{cursor: "pointer"}} onClick={onLogout}>Đăng xuất</button>
      </div>
    </div>
  );
}
