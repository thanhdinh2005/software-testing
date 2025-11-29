import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import ProductForm from "../components/ProductForm";
import { createProduct, updateProduct } from "../services/productService";

jest.mock("../services/productService");

describe("Test ProductForm component (create/edit)", () => {
    test("TC1: Tao san pham thanh cong", async () => {
        // Mock createProduct()
        createProduct.mockResolvedValue({
            id: 100,
            name: "Product A",
            price: 500,
            quantity: 15,
            categoryName: "BOOKS",
            description: "Description Product A"
        });
        
        const mockOnSuccess = jest.fn();
        render(<ProductForm onSubmitSuccess={mockOnSuccess} onCancel={jest.fn()} />);

        // Input name
        fireEvent.change(screen.getByTestId("name"), {
            target: { value: "Product A" },
        });

        // Input price
        fireEvent.change(screen.getByTestId("price"), {
            target: { value: "500" },
        });

        // Input quantity
        fireEvent.change(screen.getByTestId("quantity"), {
            target: { value: "15" },
        });

        // Select category
        fireEvent.change(screen.getByTestId("category"), {
            target: { value: "BOOKS" },
        });

        // Input description
        fireEvent.change(screen.getByTestId("description"), {
            target: { value: "Description Product A" },
        });

        // Click button Save
        fireEvent.click(screen.getByTestId("Save"));

        // Wait and check createProduct() is called with the correct data
        await waitFor(() => {
            expect(createProduct).toHaveBeenCalledWith({
                name: "Product A",
                price: "500",
                quantity: "15",
                categoryName: "BOOKS",
                description: "Description Product A"
            });
        });
        expect(mockOnSuccess).toHaveBeenCalled();
    });

    test("TC2: Form load dung du lieu khi chinh sua san pham", async () => {
        const mockProduct = {
            id: 100,
            name: "Product A",
            price: 500,
            quantity: 15,
            categoryName: "BOOKS",
            description: "Description Product A"
        };

        updateProduct.mockResolvedValue({ 
            ...mockProduct, 
            name: "Product A Edited",
            price: 600,
        });

        const mockOnSuccess = jest.fn();
        render(<ProductForm product={mockProduct} onSubmitSuccess={mockOnSuccess} onCancel={jest.fn()} />);

        fireEvent.change(screen.getByTestId("name"), {
            target: { value: "Product A Edited" },
        });
        fireEvent.change(screen.getByTestId("price"), {
            target: { value: "600" },
        });
        fireEvent.change(screen.getByTestId("quantity"), {
            target: { value: "10" },
        });
        fireEvent.change(screen.getByTestId("category"), {
            target: { value: "BOOKS" },
        });
        fireEvent.change(screen.getByTestId("description"), {
            target: { value: "Description Product A Update" },
        });
        fireEvent.click(screen.getByTestId("Save"));
        await waitFor(() => {
            expect(updateProduct).toHaveBeenCalledWith(100, {
                name: "Product A Edited",
                price: "600",
                quantity: "10",
                categoryName: "BOOKS",
                description: "Description Product A Update"
            });
        });

        expect(mockOnSuccess).toHaveBeenCalled();
    });

    test("TC3: Hien thi loi validation khi nhap du lieu khong hop le", async () => {
        const mockOnSuccess = jest.fn();
        render(<ProductForm onSubmitSuccess={mockOnSuccess} onCancel={jest.fn()} />);
        // Nhan btn Luu ma khong nhap gi
        fireEvent.click(screen.getByTestId("Save"));
        // Cho va kiem tra loi validation hien thi
        await waitFor(() => {
            expect(screen.getByText("Ten san pham khong duoc de trong")).toBeInTheDocument();
            expect(screen.getByText("Gia san pham phai lon hon 0")).toBeInTheDocument();
            expect(screen.getByText("So luong khong duoc de trong")).toBeInTheDocument();
            expect(screen.getByText("Danh muc khong duoc de trong")).toBeInTheDocument();
        });
        expect(mockOnSuccess).not.toHaveBeenCalled();
    });
});
