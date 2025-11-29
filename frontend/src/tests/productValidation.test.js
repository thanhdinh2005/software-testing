import { validateProduct } from "../utils/productValidation";

describe("validateProduct()", () => {

  test("TC1: Product name rong - nen bao loi", () => {
    const errors = validateProduct({ name: "", price: 10000, quantity: 5, categoryName: "Van phong pham" });
    expect(errors.name).toBe("Ten san pham khong duoc de trong");
  });

  test("TC2: Product name khong hop le - nen bao loi", () => {
    const errors = validateProduct({ name: "   ", price: 10000, quantity: 5, categoryName: "Van phong pham" });
    expect(errors.name).toBe("Ten san pham phai co it nhat 3 ky tu");
  });

  test("TC3: Product name < 3 ky tu - nen bao loi", () => {
    const errors = validateProduct({ name: "ab", price: 10000, quantity: 5, categoryName: "Van phong pham" });
    expect(errors.name).toBe("Ten san pham phai co it nhat 3 ky tu");
  });

  test("TC4: Product name >> 100 ky tu - nen bao loi", () => {
    const longName = "A".repeat(101);
    const errors = validateProduct({ name: longName, price: 10000, quantity: 5, categoryName: "Van phong pham" });
    expect(errors.name).toBe("Ten san pham khong duoc qua 100 ky tu");
  });

  test("TC5: Product name hop le - khong co loi", () => {
    const errors = validateProduct({ name: "But bi Thien Long", price: 10000, quantity: 5, categoryName: "Van phong pham" });
    expect(errors.name).toBeUndefined();
  });

  test("TC6: Product price rong - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: "", quantity: 10, categoryName: "Van phong pham" });
    expect(errors.price).toBe("Gia san pham phai lon hon 0");
  });

  test("TC7: Product price <= 0 - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 0, quantity: 10, categoryName: "Van phong pham" });
    expect(errors.price).toBe("Gia san pham phai lon hon 0");
  });

  test("TC8: Product price <= 0 - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: -100, quantity: 10, categoryName: "Van phong pham" });
    expect(errors.price).toBe("Gia san pham phai lon hon 0");
  });

  test("TC9: Product price hop le - khong co loi", () => {
    const errors = validateProduct({ name: "But bi", price: 15000, quantity: 10, categoryName: "Van phong pham" });
    expect(errors.price).toBeUndefined();
  });

  test("TC10: Product quatity rong - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: "", categoryName: "Van phong pham" });
    expect(errors.quantity).toBe("So luong khong duoc de trong");
  });

  test("TC11: Product quatity <= 0 - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: -2, categoryName: "Van phong pham" });
    expect(errors.quantity).toBe("So luong khong duoc am");
  });

  test("TC12: Product quatity hop le - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: 10, categoryName: "Van phong pham" });
    expect(errors.quantity).toBeUndefined();
  });

  test("TC13: Product category rong - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: 10, categoryName: "" });
    expect(errors.categoryName).toBe("Danh muc khong duoc de trong");
  });

  test("TC14: Product category hop le - khong co loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: 10, categoryName: "Hoc tap" });
    expect(errors.categoryName).toBeUndefined();
  });

  test("TC15: Product decription > 500 ky tu - nen bao loi", () => {
    const longDesc = "A".repeat(501);
    const errors = validateProduct({ name: "Sach", price: 12000, quantity: 5, categoryName: "Van hoc", description: longDesc });
    expect(errors.description).toBe("Mo ta khong duoc vuot qua 500 ky tu");
  });

  test("TC16: Product decription hop le - khong co loi", () => {
    const validDesc = "A".repeat(500);
    const errors = validateProduct({ name: "Sach", price: 12000, quantity: 5, categoryName: "Van hoc", description: validDesc });
    expect(errors.description).toBeUndefined();
  });

  test("TC17: Product price qua lon - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 1000000000, quantity: 10, categoryName: "Van phong pham" });
    expect(errors.price).toBe("Gia san pham qua lon");
  });

  test("TC18: Product quantity qua lon - nen bao loi", () => {
    const errors = validateProduct({ name: "But bi", price: 10000, quantity: 100000, categoryName: "Van phong pham" });
    expect(errors.quantity).toBe("So luong qua lon");
  });

});
