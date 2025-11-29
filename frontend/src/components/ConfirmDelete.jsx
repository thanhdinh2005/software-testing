export default function ConfirmDelete({ product, onCancel, onConfirm }) {
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
        <h2
          style={{
            fontSize: "1.25rem",     
            fontWeight: "600",        
            marginBottom: "12px",    
            color: "#dc2626"          
          }}
        >
          Xác nhận xóa
        </h2>

        <p>Bạn có chắc chắn muốn xóa:</p>
        <p
          style={{
            fontWeight: "700",        
            fontSize: "1.125rem",    
            marginTop: "4px",        
            color: "#1f2937"        
          }}
        >
          {product.name}
        </p>

        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            gap: "12px",      
            marginTop: "24px" 
          }}
        >
          <button
            data-testid="cancel-delete"
            onClick={onCancel}
            style={{
              padding: "8px 16px",          
              borderRadius: "6px",          
              backgroundColor: "#d1d5db",   
              border: "none",
              cursor: "pointer"
            }}
          >
            Hủy
          </button>
          <button
            data-testid="confirm-delete"
            onClick={onConfirm}
            style={{
              padding: "8px 16px",
              borderRadius: "6px",
              backgroundColor: "#ef4444", 
              color: "white",
              border: "none",
              cursor: "pointer"
            }}
          >
            Xóa
          </button>
        </div>
      </div>
    </div>
  );
}
