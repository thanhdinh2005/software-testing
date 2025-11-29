export const validateProduct = (product) => {
  const errors = {};
    
  if (!product.name || product.name.trim().length < 3){
    if (!product.name){
        errors.name = "Ten san pham khong duoc de trong";
    } else {
        errors.name = "Ten san pham phai co it nhat 3 ky tu";
    }
  }
  if (product.name && product.name.length > 100)
    errors.name = "Ten san pham khong duoc qua 100 ky tu";

  if (product.price <= 0)
    errors.price = "Gia san pham phai lon hon 0";
  if (product.price > 999999999)
    errors.price = "Gia san pham qua lon";

  if (product.quantity === "" || product.quantity === null || product.quantity === undefined)
    errors.quantity = "So luong khong duoc de trong";
  if (product.quantity < 0)
    errors.quantity = "So luong khong duoc am";
  if (product.quantity > 99999)
    errors.quantity = "So luong qua lon";

  if (product.description && product.description.length > 500)
    errors.description = "Mo ta khong duoc vuot qua 500 ky tu";

  if (!product.categoryName)
    errors.categoryName = "Danh muc khong duoc de trong";

  return errors;
};
