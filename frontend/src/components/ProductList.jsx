import React from "react";
export default function ProductList({ products, onSelect, selectedId, onDetail }) {
  

  return (
    <table data-testid="product-list" border="1" cellPadding="6" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên</th>
          <th>Giá</th>
          <th>Số lượng</th>
          <th>Danh mục</th>
          <th>Chi tiết</th>
        </tr>
      </thead>

      <tbody>
        {products.map((p) => (
          <tr
            data-testid="product-item"
            key={p.id}
            onClick={() => onSelect(p)}
            style={{
              background: selectedId === p.id ? "rgb(31, 192, 204)" : "",
              cursor: "pointer"
            }}
          >
            <td>{p.id}</td>
            <td>{p.name}</td>
            <td>{p.price}</td>
            <td>{p.quantity}</td>
            <td>{p.categoryName}</td>
            <td>
              <button
                data-testid="product-item-detail"
                style={{cursor: "pointer"}}
                onClick={(e) => {
                  e.stopPropagation(); // ĐỂ KHÔNG SELECT KHI CLICK DETAIL
                  onDetail(p);
                }}
              >
                Detail
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
