export default function ActionBar({
  searchData, setSearchData,
  onSearch,
  onCreate, onEdit, onDelete,
  selectedProduct
}) {
  return (
    <div data-testid="action-bar" style={{ display: "flex", gap: "10px", margin: "20px 0" }}>

      {/* NAME */}
      <input
        data-testid="search-input"
        placeholder="Tên sản phẩm..."
        value={searchData.name}
        onChange={(e) =>
          setSearchData({ ...searchData, name: e.target.value })
        }
      />

      {/* CATEGORY */}
      <select
        style={{cursor: "pointer"}}
        data-testid="filter-category"
        value={searchData.categoryName}
        onChange={(e) =>
          setSearchData({ ...searchData, categoryName: e.target.value })
        }
      >
        <option value="">Tất cả</option>
        <option value="FOOD">FOOD</option>
        <option value="BOOKS">BOOKS</option>
        <option value="CLOTHING">CLOTHING</option>
        <option value="ELECTRONICS">ELECTRONICS</option>
        <option value="OTHER">OTHER</option>
      </select>

      {/* PRICE RANGE */}
      <input
        data-testid="price-min-input"
        type="number"
        placeholder="Giá min"
        onChange={(e) =>
          setSearchData({ ...searchData, priceMin: e.target.value })
        }
      />

      <input
        data-testid="price-max-input"
        type="number"
        placeholder="Giá max"
        onChange={(e) =>
          setSearchData({ ...searchData, priceMax: e.target.value })
        }
      />

      {/* QUANTITY RANGE */}
      <input
        data-testid="quantity-min-input"
        type="number"
        placeholder="SL min"
        onChange={(e) =>
          setSearchData({ ...searchData, quantityMin: e.target.value })
        }
      />

      <input
        data-testid="quantity-max-input"
        type="number"
        placeholder="SL max"
        onChange={(e) =>
          setSearchData({ ...searchData, quantityMax: e.target.value })
        }
      />

      {/* BUTTON SEARCH */}
      <button data-testid="search-button" style={{cursor: "pointer"}} onClick={onSearch}>
        SEARCH
      </button>

      <button data-testid="create-button" style={{cursor: "pointer"}} onClick={onCreate}>Create</button>
      <button
        style={{cursor: "pointer"}}
        data-testid="edit-button"
        onClick={() => onEdit(selectedProduct)}
        disabled={!selectedProduct}
      >
        Edit
      </button>
      <button
        style={{cursor: "pointer"}}
        data-testid="delete-button"
        onClick={() => onDelete(selectedProduct)}
        disabled={!selectedProduct}
      >
        Delete
      </button>
    </div>
  );
}
