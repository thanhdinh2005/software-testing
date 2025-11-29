import React from "react";
export default function ProductDetail({ product, onClose }) {
  if (!product) return null;

  return (
    <div 
      style={{
        position: "fixed",
        top: 0, left: 0,
        width: "100vw",
        height: "100vh",
        background: "rgba(0,0,0,0.4)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        zIndex: 9999   // ⚡ z-index cho overlay
      }}
    >
      <div 
        style={{
          padding: "20px",
          border: "1px solid black",
          background: "white",
          width: "350px",
          borderRadius: "8px",
          boxShadow: "0 4px 10px rgba(0,0,0,0.3)",
          zIndex: 10000  // ⚡ popup cao hơn overlay
        }}
      >
        <h3>Thông tin chi tiết</h3>

        <p data-testid="detail-id"><strong>ID:</strong> {product.id}</p>
        <p data-testid="detail-name"><strong>Tên:</strong> {product.name}</p>
        <p data-testid="detail-price"><strong>Giá:</strong> {product.price}</p>
        <p data-testid="detail-quantity"><strong>Số lượng:</strong> {product.quantity}</p>
        <p data-testid="detail-category"><strong>Danh mục:</strong> {product.categoryName}</p>
        <p data-testid="detail-description"><strong>Mô tả:</strong> {product.description}</p>

        <button data-testid="close-button" onClick={onClose} style={{cursor: "pointer", marginTop: "15px" }}>
          Đóng
        </button>
      </div>
    </div>
  );
}