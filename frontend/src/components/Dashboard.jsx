import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import ActionBar from "../components/ActionBar";
import ProductList from "../components/ProductList";
import ProductForm from "../components/ProductForm";
import ProductDetail from "../components/ProductDetail";
import ConfirmDelete from "./ConfirmDelete";
import { getProducts, deleteProduct, getProductById, searchProducts } from "../services/productService";

export default function Dashboard() {
  const navigate = useNavigate();

  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [detailProduct, setDetailProduct] = useState(null);
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [productToDelete, setProductToDelete] = useState(null);

  const [isSearching, setIsSearching] = useState(false);

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  // Pagination data
  const [pageData, setPageData] = useState({
    content: [],
    number: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0
  });

  const username = localStorage.getItem("username");

  // data Search
  const [searchData, setSearchData] = useState({
    name: "",
    quantityMin: null,
    quantityMax: null,
    priceMin: null,
    priceMax: null,
    categoryName: ""
  });
  // Toast
  const [toastMessage, setToastMessage] = useState("");
  const [showToast, setShowToast] = useState(false);

  const showSuccessToast = (msg) => {
    setToastMessage(msg);
    setShowToast(true);

    setTimeout(() => {
      setShowToast(false);
      setToastMessage("");
    }, 2000);
  };

  // Fetch products from API
  const fetchData = async () => {
    if (!localStorage.getItem("token")) 
      navigate("/login");
    const data = await getProducts(page, size);
    setPageData(data);
    console.log("Fetched products:", data);
  };

  // On component mount
  useEffect(() => {
    if (isSearching) {
      handleSearch();
    } else {
      fetchData();
    }
  }, [page, size]);

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    navigate("/login");
  };

  const handleSearch = async () => {
    if(searchData){
      setIsSearching(true);
      setPage(0);

      const data = await searchProducts(searchData, 0, size);
      setPageData(data);
    }
    setIsSearching(false);
    return;
  };

  // Handle delete product
  const handleDelete = async (product) => {
    if (!product) return showSuccessToast("Hãy chọn sản phẩm muốn xóa!");
    setProductToDelete(product);   // lưu sản phẩm cần xóa
    setShowDeletePopup(true); 
  };

  const confirmDelete = async () => {
    if (!productToDelete) return;

    await deleteProduct(productToDelete.id);
    await fetchData();

    showSuccessToast("Xóa sản phẩm thành công!");

    setShowDeletePopup(false);
    setProductToDelete(null);
  };


  // Handle edit product
  const handleEdit = (product) => {
    if (!product) return showSuccessToast("Hãy chọn sản phẩm muốn sửa");
    setShowForm(true);
  };

  // Handle detail product
  const handleDetail = async (product) => {
    console.log("Detail product:", product.id);
    try {
      const data = await getProductById(product.id); 
      setDetailProduct(data.data);
      console.log("Detail data:", data.data);
    } catch (error) {
      console.error("Error fetching detail:", error);
    }
  }

  // Render component
  return (
    <div style={{ padding: "20px" }}>
        {/* Thanh TopBar */}
      <TopBar username={username} onLogout={handleLogout} />
        {/* Thanh ActionBar */}
      <ActionBar
        searchData={searchData}
        setSearchData={setSearchData}
        onSearch={handleSearch}
        onCreate={() => { setSelectedProduct(null); setShowForm(true); }}
        onEdit={() => handleEdit(selectedProduct)}
        onDelete={() => handleDelete(selectedProduct)}
        selectedProduct={selectedProduct}
      />
      {/* Danh sách sản phẩm lấy từ pageData.content */}
      <ProductList
        products={pageData.content}
        selectedId={selectedId}
        onSelect={(p) => {
          if (selectedId === p.id) {
            // nếu click lại vào sản phẩm đang chọn → bỏ chọn
            setSelectedId(null);
            setSelectedProduct(null);
          } else {
            // nếu click sản phẩm khác → chọn
            setSelectedId(p.id);
            setSelectedProduct(p);
          }
        }}
        onDetail={(p) => handleDetail(p)}
      />

      {/* PHÂN TRANG */}
      <div style={{ marginTop: "20px", textAlign: "center" }}>
        <button
          data-testid="prev-button"
          style={{cursor: "pointer"}}
          onClick={() => {setPage((p) => Math.max(0, p - 1)), setSelectedProduct(null)}}
          disabled={pageData.number === 0}
        >
          Prev
        </button>

        <span data-testid="page-txt" style={{ margin: "0 10px" }}>
          Trang {pageData.number + 1}/{pageData.totalPages}
        </span>

        <button
          data-testid="next-button"
          style={{cursor: "pointer"}}
          onClick={() => {setPage((p) => Math.min(pageData.totalPages - 1, p + 1)), setSelectedProduct(null)}}
          disabled={pageData.number + 1 >= pageData.totalPages}
        >
          Next
        </button>

        {/* Chọn size */}
        {/* <select
          value={size}
          onChange={(e) => { setSize(Number(e.target.value)); setPage(0); }}
          style={{ marginLeft: "20px" }}
        >
          <option value={5}>5 / trang</option>
          <option value={10}>10 / trang</option>
          <option value={20}>20 / trang</option>
        </select> */}
      </div>

      {showToast && (
        <div
          data-testid="toast-success"
          style={{
            position: "fixed",
            bottom: "16px",        // bottom-4
            right: "16px",         // right-4
            zIndex: 2000,          // z-[2000]
            padding: "12px 16px",  // px-4 py-3
            borderRadius: "8px",   // rounded-lg
            display: "flex",       // flex
            alignItems: "center",  // items-center
            gap: "8px",            // gap-2
            backgroundColor: "#22c55e", // bg-green-500
            color: "white",        // text-white
            boxShadow: "0 4px 6px rgba(0,0,0,0.2)", // shadow-lg
            animation: "slide-in 0.3s ease-out" 

          }}
        >
          <span>{toastMessage}</span>
        </div>
      )}

      {showDeletePopup && (
        <ConfirmDelete
          product={productToDelete}
          onCancel={() => setShowDeletePopup(false)}
          onConfirm={confirmDelete}
        />
      )}

      {showForm && (
        <ProductForm
          product={selectedProduct}
          onSubmitSuccess={() => { setShowForm(false); fetchData(); showSuccessToast("Lưu sản phẩm thành công!");}}
          onCancel={() => setShowForm(false)}
        />
      )}

      {detailProduct && (
        <ProductDetail
          product={detailProduct}
          onClose={() => setDetailProduct(null)}
        />
      )}
    </div>
  );
}
