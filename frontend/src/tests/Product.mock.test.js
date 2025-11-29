import React from "react";
import { render, screen, fireEvent, waitFor, act } from "@testing-library/react";
import * as productService from "../services/productService";

jest.mock("../services/productService");

describe("Product  Mock Tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("TC1: Mock getProduct() ", async () =>{
    productService.getProducts.mockResolvedValue({
        content: [
            { id: 1, name: "Product A", price: 100, quantity: 10, categoryName: "Category 1", description: "A" },
            { id: 2, name: "Product B", price: 200, quantity: 20, categoryName: "Category 2", description: "AA" },
            { id: 3, name: "Product C", price: 300, quantity: 30, categoryName: "Category 3", description: "AAA" },
            { id: 4, name: "Product D", price: 400, quantity: 40, categoryName: "Category 1", description: "AAAA" },
            { id: 5, name: "Product E", price: 400, quantity: 60, categoryName: "Category 4", description: "AAAAA" },
            { id: 6, name: "Product F", price: 400, quantity: 40, categoryName: "Category 2", description: "AAAAAA" },
            { id: 7, name: "Product G", price: 400, quantity: 40, categoryName: "Category 2", description: "AAAAAAA" },
            { id: 8, name: "Product H", price: 400, quantity: 40, categoryName: "Category 1", description: "AAAAAAAA" },
        ],
        number: 0,
        size: 10,
        totalPages: 1,
        totalElements: 2
    });

    const res = await productService.getProducts();
    expect(res.content[0].name).toBe("Product A");
    expect(res.content[1].name).toBe("Product B");
    expect(res.content[3].price).toBe(400);
    expect(res.content[4].quantity).toBe(60);
    expect(res.content[5].categoryName).toBe("Category 2");
    expect(res.content[6].description).toBe("AAAAAAA");
    expect(productService.getProducts).toHaveBeenCalledTimes(1);
  })

  test("TC2: Mock createProduct() ", async () => {
    productService.createProduct.mockResolvedValue({
        id: 100,
        name: "Product A",
        price: 500,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A"
    });

    const res = await productService.createProduct({
        name: "Product A",
        price: 500,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A"
    });
    expect(res.id).toBe(100);
    expect(res.name).toBe("Product A");
    expect(res.price).toBe(500);
    expect(res.quantity).toBe(15);
    expect(res.categoryName).toBe("Fashion");
    expect(res.description).toBe("Decription Product A");

    expect(productService.createProduct).toHaveBeenCalledWith({
        name: "Product A",
        price: 500,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A"
    });
  })

  test("TC3: Mock updateProduct() ", async () =>{
    productService.updateProduct.mockResolvedValue({
        id: 100,
        name: "Product A Update",
        price: 1000,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A Update"
    });
    
    const res = await productService.updateProduct(100, {
        name: "Product A Update",
        price: 1000,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A Update"
    });

    expect(res.name).toBe("Product A Update");
    expect(res.price).toBe(1000);
    expect(res.description).toBe("Decription Product A Update")

    expect(productService.updateProduct).toHaveBeenCalledWith(100, {
        name: "Product A Update",
        price: 1000,
        quantity: 15,
        categoryName: "Fashion",
        description: "Decription Product A Update"
    });
  })

  test("TC4: Mock deleteProduct() ", async () => {
    productService.deleteProduct.mockResolvedValue({
        "success": true,
        "message": "OK",
        "data": null
    });
    const res = await productService.deleteProduct(3);
    expect(res.message).toBe("OK");
    expect(productService.deleteProduct).toHaveBeenCalledWith(3);
  })

  test("TC5: createProduct() failure scenarios", async () => {
    productService.createProduct.mockRejectedValue(new Error("Failed to create"));

    await expect(productService.createProduct({})).rejects.toThrow("Failed to create");

    expect(productService.createProduct).toHaveBeenCalledTimes(1);
  })
    
  test("TC6: Verify all CRUD functions were mocked correctly", async () => {
    productService.getProducts.mockResolvedValue({ content: [] });
    productService.createProduct.mockResolvedValue({
        id: 10,
        name: "A",
    });
    productService.updateProduct.mockResolvedValue({
        id: 10,
        name: "Updated",
    });
    productService.deleteProduct.mockResolvedValue({
        message: "OK",
    });

    const list = await productService.getProducts();
    const created = await productService.createProduct({ name: "A" });
    const updated = await productService.updateProduct(10, { name: "Updated" });
    const deleted = await productService.deleteProduct(10);

    expect(productService.getProducts).toHaveBeenCalledTimes(1);
    expect(productService.createProduct).toHaveBeenCalledWith({
        name: "A",
    });
    expect(productService.updateProduct).toHaveBeenCalledWith(10, {
        name: "Updated",
    });
    expect(productService.deleteProduct).toHaveBeenCalledWith(10);

    expect(list.content).toEqual([]);
    expect(created.name).toBe("A");
    expect(updated.name).toBe("Updated");
    expect(deleted.message).toBe("OK");
  })

});
