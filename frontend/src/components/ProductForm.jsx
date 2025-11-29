import React from "react";
import { useState, useEffect } from "react";
import { createProduct, updateProduct } from "../services/productService";
import { validateProduct } from "../utils/productValidation";

export default function ProductForm({ product, onSubmitSuccess, onCancel }) {
  const [name, setName] = useState("");
  const [price, setPrice] = useState("");
  const [quantity, setQuantity] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [description, setDescription] = useState("");
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (product) {
      setName(product.name);
      setPrice(product.price);
      setQuantity(product.quantity);
      setCategoryName(product.categoryName);
      setDescription(product.description);
    }
  }, [product]);

  const handleSave = async () => {
    const data = { name, price, quantity, categoryName, description };
    const validationErrors = validateProduct(data);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }
    console.log(data);
    if (product) await updateProduct(product.id, data);
    else await createProduct(data);

    onSubmitSuccess();
  };

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
        zIndex: 9999
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
          zIndex: 10000 
        }}
      >
        <h3>{product ? "Edit Product" : "Create Product"}</h3>

        <div><label>Tên: </label><input data-testid="name" value={name} onChange={e => setName(e.target.value)} /></div>
        {
          errors.name && <p style={{color: "red"}}>{errors.name}</p>
        }
        <div><label>Giá: </label><input data-testid="price" type="number" value={price} onChange={e => setPrice(e.target.value)} /></div>
        {
          errors.price && <p style={{color: "red"}}>{errors.price}</p>
        }
        <div><label>Số lượng: </label><input data-testid="quantity" type="number" value={quantity} onChange={e => setQuantity(e.target.value)} /></div>
        {
          errors.quantity && <p style={{color: "red"}}>{errors.quantity}</p>
        }
        <div>
          <label>Danh mục: </label>
          <select style={{cursor: "pointer"}} data-testid="category" value={categoryName} onChange={e => setCategoryName(e.target.value)}>
            <option value="">-- Chọn danh mục --</option>
            <option value="FOOD">FOOD</option>
            <option value="BOOKS">BOOKS</option>
            <option value="CLOTHING">CLOTHING</option>
            <option value="ELECTRONICS">ELECTRONICS</option>
            <option value="OTHER">OTHER</option>
          </select>
        </div>
        {
          errors.categoryName && <p style={{color: "red"}}>{errors.categoryName}</p>
        }
        <div><label>Mô tả:</label><input data-testid="description" style={{height:"100px"}} value={description} onChange={e => setDescription(e.target.value)}/></div>
        {
          errors.description && <p style={{color: "red"}}>{errors.description}</p>
        }

        <button style={{cursor: "pointer"}} data-testid="Save" onClick={handleSave}>Lưu</button>
        <button date-testid="Cancel" onClick={onCancel} style={{cursor: "pointer", marginLeft: "10px" }}>Hủy</button>
      </div>
    </div>
  );
}
